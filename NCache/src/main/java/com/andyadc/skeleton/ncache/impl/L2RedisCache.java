package com.andyadc.skeleton.ncache.impl;

import com.andyadc.skeleton.ncache.config.CacheConfig;
import com.andyadc.skeleton.ncache.resilience.CircuitBreaker;
import com.andyadc.skeleton.ncache.resilience.RetryExecutor;
import com.andyadc.skeleton.ncache.serialization.Serializer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.params.SetParams;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * L2 cache implementation using Redis.
 */
public class L2RedisCache<K, V> {

    private final String name;
    private final JedisPool jedisPool;
    private final CacheConfig.L2Config config;
    private final Serializer<K> keySerializer;
    private final Serializer<V> valueSerializer;
    private final DefaultCacheStats stats;
    private final CircuitBreaker circuitBreaker;
    private final RetryExecutor retryExecutor;
    private final ExecutorService asyncExecutor;
    private final String keyPrefix;

    public L2RedisCache(String name,
                        JedisPool jedisPool,
                        CacheConfig.L2Config config,
                        Serializer<K> keySerializer,
                        Serializer<V> valueSerializer,
                        DefaultCacheStats stats,
                        CircuitBreaker circuitBreaker,
                        RetryExecutor retryExecutor,
                        ExecutorService asyncExecutor) {
        this.name = name;
        this.jedisPool = jedisPool;
        this.config = config;
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        this.stats = stats;
        this.circuitBreaker = circuitBreaker;
        this.retryExecutor = retryExecutor;
        this.asyncExecutor = asyncExecutor;
        this.keyPrefix = config.getKeyPrefix() + name + ":";
    }

    public Optional<V> get(K key) {
        return circuitBreaker.execute(() -> retryExecutor.execute(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                byte[] redisKey = buildKey(key);
                byte[] value = jedis.get(redisKey);

                if (value != null) {
                    stats.recordL2Hit();
                    return Optional.of(valueSerializer.deserialize(value));
                } else {
                    stats.recordL2Miss();
                    return Optional.empty();
                }
            }
        }));
    }

    public Map<K, V> getAll(Collection<K> keys) {
        if (keys.isEmpty()) {
            return Collections.emptyMap();
        }

        return circuitBreaker.execute(() -> retryExecutor.execute(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                byte[][] redisKeys = keys.stream()
                        .map(this::buildKey)
                        .toArray(byte[][]::new);

                List<byte[]> values = jedis.mget(redisKeys);
                Map<K, V> result = new HashMap<>();

                Iterator<K> keyIterator = keys.iterator();
                for (byte[] value : values) {
                    K key = keyIterator.next();
                    if (value != null) {
                        result.put(key, valueSerializer.deserialize(value));
                        stats.recordL2Hit();
                    } else {
                        stats.recordL2Miss();
                    }
                }

                return result;
            }
        }));
    }

    public void put(K key, V value, Duration ttl) {
        circuitBreaker.execute(() -> retryExecutor.execute(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                byte[] redisKey = buildKey(key);
                byte[] redisValue = valueSerializer.serialize(value);

                SetParams params = new SetParams();
                if (ttl != null && !ttl.isZero()) {
                    params.px(ttl.toMillis());
                }

                jedis.set(redisKey, redisValue, params);
                stats.recordPut();
                return null;
            }
        }));
    }

    public void putAll(Map<K, V> entries, Duration ttl) {
        if (entries.isEmpty()) {
            return;
        }

        circuitBreaker.execute(() -> retryExecutor.execute(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                Pipeline pipeline = jedis.pipelined();

                for (Map.Entry<K, V> entry : entries.entrySet()) {
                    byte[] redisKey = buildKey(entry.getKey());
                    byte[] redisValue = valueSerializer.serialize(entry.getValue());

                    if (ttl != null && !ttl.isZero()) {
                        pipeline.psetex(redisKey, ttl.toMillis(), redisValue);
                    } else {
                        pipeline.set(redisKey, redisValue);
                    }
                }

                pipeline.sync();
                stats.recordPut();
                return null;
            }
        }));
    }

    public boolean putIfAbsent(K key, V value, Duration ttl) {
        return circuitBreaker.execute(() -> retryExecutor.execute(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                byte[] redisKey = buildKey(key);
                byte[] redisValue = valueSerializer.serialize(value);

                SetParams params = new SetParams().nx();
                if (ttl != null && !ttl.isZero()) {
                    params.px(ttl.toMillis());
                }

                String result = jedis.set(redisKey, redisValue, params);
                boolean success = "OK".equals(result);
                if (success) {
                    stats.recordPut();
                }
                return success;
            }
        }));
    }

    public boolean remove(K key) {
        return circuitBreaker.execute(() -> retryExecutor.execute(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                byte[] redisKey = buildKey(key);
                long removed = jedis.del(redisKey);
                if (removed > 0) {
                    stats.recordRemove();
                    return true;
                }
                return false;
            }
        }));
    }

    public void removeAll(Collection<K> keys) {
        if (keys.isEmpty()) {
            return;
        }

        circuitBreaker.execute(() -> retryExecutor.execute(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                byte[][] redisKeys = keys.stream()
                        .map(this::buildKey)
                        .toArray(byte[][]::new);

                jedis.del(redisKeys);
                stats.recordRemove();
                return null;
            }
        }));
    }

    public void clear() {
        circuitBreaker.execute(() -> retryExecutor.execute(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                // Use SCAN to find and delete keys with prefix
                String pattern = keyPrefix + "*";
                String cursor = "0";

                do {
                    var result = jedis.scan(cursor.getBytes(),
                            new redis.clients.jedis.params.ScanParams()
                                    .match(pattern)
                                    .count(100));

                    cursor = result.getCursor();
                    List<byte[]> keys = result.getResult();

                    if (!keys.isEmpty()) {
                        jedis.del(keys.toArray(new byte[0][]));
                    }
                } while (!"0".equals(cursor));

                return null;
            }
        }));
    }

    public boolean containsKey(K key) {
        return circuitBreaker.execute(() -> retryExecutor.execute(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                byte[] redisKey = buildKey(key);
                return jedis.exists(redisKey);
            }
        }));
    }

    public CompletableFuture<Optional<V>> getAsync(K key) {
        return CompletableFuture.supplyAsync(() -> get(key), asyncExecutor);
    }

    public CompletableFuture<Void> putAsync(K key, V value, Duration ttl) {
        return CompletableFuture.runAsync(() -> put(key, value, ttl), asyncExecutor);
    }

    public CompletableFuture<Boolean> removeAsync(K key) {
        return CompletableFuture.supplyAsync(() -> remove(key), asyncExecutor);
    }

    private byte[] buildKey(K key) {
        if (config.isUseKeyHashTag()) {
            // For Redis Cluster - ensure keys hash to same slot
            String serializedKey = new String(keySerializer.serialize(key), StandardCharsets.UTF_8);
            return (keyPrefix + "{" + serializedKey + "}").getBytes(StandardCharsets.UTF_8);
        }

        byte[] serializedKey = keySerializer.serialize(key);
        byte[] prefix = keyPrefix.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[prefix.length + serializedKey.length];
        System.arraycopy(prefix, 0, result, 0, prefix.length);
        System.arraycopy(serializedKey, 0, result, prefix.length, serializedKey.length);
        return result;
    }

}