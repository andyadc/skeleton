package com.andyadc.skeleton.redis.cache;

import com.andyadc.skeleton.redis.client.RedisClient;
import com.andyadc.skeleton.redis.serializer.JsonSerializer;
import com.andyadc.skeleton.redis.serializer.RedisSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Redis-based cache manager implementation.
 */
public class RedisCacheManager implements CacheManager {

    private static final Logger logger = LoggerFactory.getLogger(RedisCacheManager.class);

    private static final String CACHE_PREFIX = "cache:";
    private static final Duration DEFAULT_TTL = Duration.ofHours(1);

    private final RedisClient redisClient;
    private final RedisSerializer<Object> serializer;
    private final Duration defaultTtl;

    public RedisCacheManager(RedisClient redisClient) {
        this(redisClient, DEFAULT_TTL);
    }

    public RedisCacheManager(RedisClient redisClient, Duration defaultTtl) {
        this.redisClient = redisClient;
        this.serializer = new JsonSerializer<>();
        this.defaultTtl = defaultTtl;
    }

    private String cacheKey(String key) {
        return CACHE_PREFIX + key;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String key, Class<T> type) {
        try {
            Optional<String> value = redisClient.get(cacheKey(key));
            if (value.isPresent()) {
                T result = (T) serializer.deserializeFromString(value.get(), type);
                logger.debug("Cache hit for key: {}", key);
                return Optional.ofNullable(result);
            }
            logger.debug("Cache miss for key: {}", key);
            return Optional.empty();
        } catch (Exception e) {
            logger.warn("Failed to get from cache: {}", key, e);
            return Optional.empty();
        }
    }

    @Override
    public <T> void put(String key, T value) {
        put(key, value, defaultTtl);
    }

    @Override
    public <T> void put(String key, T value, Duration ttl) {
        try {
            String serialized = serializer.serializeToString(value);
            redisClient.set(cacheKey(key), serialized, ttl);
            logger.debug("Cached key: {} with TTL: {}s", key, ttl.toSeconds());
        } catch (Exception e) {
            logger.warn("Failed to put into cache: {}", key, e);
        }
    }

    @Override
    public <T> T getOrCompute(String key, Class<T> type, Supplier<T> supplier) {
        return getOrCompute(key, type, supplier, defaultTtl);
    }

    @Override
    public <T> T getOrCompute(String key, Class<T> type, Supplier<T> supplier, Duration ttl) {
        Optional<T> cached = get(key, type);
        if (cached.isPresent()) {
            return cached.get();
        }

        T computed = supplier.get();
        if (computed != null) {
            put(key, computed, ttl);
        }
        return computed;
    }

    @Override
    public void evict(String key) {
        try {
            redisClient.del(cacheKey(key));
            logger.debug("Evicted cache key: {}", key);
        } catch (Exception e) {
            logger.warn("Failed to evict cache key: {}", key, e);
        }
    }

    @Override
    public void evict(String... keys) {
        if (keys.length == 0) return;

        try {
            String[] cacheKeys = Arrays.stream(keys)
                    .map(this::cacheKey)
                    .toArray(String[]::new);
            redisClient.del(cacheKeys);
            logger.debug("Evicted {} cache keys", keys.length);
        } catch (Exception e) {
            logger.warn("Failed to evict cache keys", e);
        }
    }

    @Override
    public void evictByPattern(String pattern) {
        try {
            Set<String> keys = redisClient.scan(cacheKey(pattern), 1000);
            if (!keys.isEmpty()) {
                redisClient.del(keys.toArray(new String[0]));
                logger.debug("Evicted {} cache keys matching pattern: {}", keys.size(), pattern);
            }
        } catch (Exception e) {
            logger.warn("Failed to evict cache keys by pattern: {}", pattern, e);
        }
    }

    @Override
    public void clear() {
        evictByPattern("*");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> multiGet(Class<T> type, String... keys) {
        if (keys.length == 0) return Map.of();

        try {
            String[] cacheKeys = Arrays.stream(keys)
                    .map(this::cacheKey)
                    .toArray(String[]::new);

            List<String> values = redisClient.mget(cacheKeys);
            Map<String, T> result = new HashMap<>();

            for (int i = 0; i < keys.length; i++) {
                String value = values.get(i);
                if (value != null) {
                    result.put(keys[i], (T) serializer.deserializeFromString(value, type));
                }
            }

            return result;
        } catch (Exception e) {
            logger.warn("Failed to multi-get from cache", e);
            return Map.of();
        }
    }

    @Override
    public <T> void multiPut(Map<String, T> entries) {
        multiPut(entries, defaultTtl);
    }

    @Override
    public <T> void multiPut(Map<String, T> entries, Duration ttl) {
        if (entries.isEmpty()) return;

        try {
            Map<String, String> serializedEntries = entries.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> cacheKey(e.getKey()),
                            e -> serializer.serializeToString(e.getValue())
                    ));

            redisClient.mset(serializedEntries);

            // Set TTL for each key
            for (String key : serializedEntries.keySet()) {
                redisClient.expire(key, ttl);
            }

            logger.debug("Multi-put {} cache entries with TTL: {}s",
                    entries.size(), ttl.toSeconds());
        } catch (Exception e) {
            logger.warn("Failed to multi-put into cache", e);
        }
    }

    @Override
    public boolean exists(String key) {
        try {
            return redisClient.exists(cacheKey(key));
        } catch (Exception e) {
            logger.warn("Failed to check cache existence: {}", key, e);
            return false;
        }
    }

    @Override
    public Duration getTtl(String key) {
        try {
            return redisClient.ttl(cacheKey(key));
        } catch (Exception e) {
            logger.warn("Failed to get TTL for cache key: {}", key, e);
            return Duration.ofSeconds(-1);
        }
    }

}
