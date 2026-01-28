package com.andyadc.skeleton.jedis.repository;

import com.andyadc.skeleton.jedis.connection.RedisConnectionManager;
import com.andyadc.skeleton.jedis.util.JsonUtil;
import com.andyadc.skeleton.jedis.util.RedisKeyUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.params.SetParams;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

public class CacheRepository {

    private static final Logger logger = LoggerFactory.getLogger(CacheRepository.class);

    private final RedisConnectionManager connectionManager;
    private final Duration defaultTtl;

    public CacheRepository(RedisConnectionManager connectionManager, Duration defaultTtl) {
        this.connectionManager = connectionManager;
        this.defaultTtl = defaultTtl;
    }

    public <T> void put(String namespace, String key, T value) {
        put(namespace, key, value, defaultTtl);
    }

    public <T> void put(String namespace, String key, T value, Duration ttl) {
        JedisPooled jedis = connectionManager.getPooled();
        String cacheKey = RedisKeyUtil.cacheKey(namespace, key);
        String json = JsonUtil.toJson(value);

        jedis.set(cacheKey, json, SetParams.setParams().ex(ttl.toSeconds()));
        logger.debug("Cached {}:{}", namespace, key);
    }

    public <T> Optional<T> get(String namespace, String key, Class<T> type) {
        JedisPooled jedis = connectionManager.getPooled();
        String cacheKey = RedisKeyUtil.cacheKey(namespace, key);
        String json = jedis.get(cacheKey);

        return JsonUtil.fromJsonSafe(json, type);
    }

    public <T> Optional<T> get(String namespace, String key, TypeReference<T> typeReference) {
        JedisPooled jedis = connectionManager.getPooled();
        String cacheKey = RedisKeyUtil.cacheKey(namespace, key);
        String json = jedis.get(cacheKey);

        if (json == null || json.isBlank()) {
            return Optional.empty();
        }

        try {
            return Optional.of(JsonUtil.fromJson(json, typeReference));
        } catch (Exception e) {
            logger.warn("Failed to deserialize cached value", e);
            return Optional.empty();
        }
    }

    /**
     * Get or compute pattern - returns cached value or computes and caches new value
     */
    public <T> T getOrCompute(String namespace, String key, Class<T> type,
                              Supplier<T> supplier) {
        return getOrCompute(namespace, key, type, supplier, defaultTtl);
    }

    public <T> T getOrCompute(String namespace, String key, Class<T> type,
                              Supplier<T> supplier, Duration ttl) {
        Optional<T> cached = get(namespace, key, type);
        if (cached.isPresent()) {
            logger.debug("Cache hit for {}:{}", namespace, key);
            return cached.get();
        }

        logger.debug("Cache miss for {}:{}", namespace, key);
        T value = supplier.get();
        if (value != null) {
            put(namespace, key, value, ttl);
        }
        return value;
    }

    public void evict(String namespace, String key) {
        JedisPooled jedis = connectionManager.getPooled();
        String cacheKey = RedisKeyUtil.cacheKey(namespace, key);
        jedis.del(cacheKey);
        logger.debug("Evicted cache {}:{}", namespace, key);
    }

    public void evictByPattern(String namespace, String pattern) {
        String fullPattern = RedisKeyUtil.cacheKey(namespace, pattern);

        connectionManager.execute(jedis -> {
            String cursor = "0";
            do {
                var result = jedis.scan(cursor,
                        new redis.clients.jedis.params.ScanParams()
                                .match(fullPattern)
                                .count(100));

                if (!result.getResult().isEmpty()) {
                    jedis.del(result.getResult().toArray(new String[0]));
                }
                cursor = result.getCursor();
            } while (!cursor.equals("0"));

            return null;
        });

        logger.debug("Evicted cache pattern {}:{}", namespace, pattern);
    }

    public boolean exists(String namespace, String key) {
        JedisPooled jedis = connectionManager.getPooled();
        String cacheKey = RedisKeyUtil.cacheKey(namespace, key);
        return jedis.exists(cacheKey);
    }

    public long getTtl(String namespace, String key) {
        JedisPooled jedis = connectionManager.getPooled();
        String cacheKey = RedisKeyUtil.cacheKey(namespace, key);
        return jedis.ttl(cacheKey);
    }

    public void refresh(String namespace, String key, Duration newTtl) {
        JedisPooled jedis = connectionManager.getPooled();
        String cacheKey = RedisKeyUtil.cacheKey(namespace, key);
        jedis.expire(cacheKey, newTtl.toSeconds());
    }
}
