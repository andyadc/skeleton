package com.andyadc.skeleton.redis.cache;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Interface for cache operations.
 */
public interface CacheManager {

    /**
     * Gets a value from cache.
     */
    <T> Optional<T> get(String key, Class<T> type);

    /**
     * Puts a value into cache.
     */
    <T> void put(String key, T value);

    /**
     * Puts a value into cache with TTL.
     */
    <T> void put(String key, T value, Duration ttl);

    /**
     * Gets or computes a value.
     */
    <T> T getOrCompute(String key, Class<T> type, Supplier<T> supplier);

    /**
     * Gets or computes a value with TTL.
     */
    <T> T getOrCompute(String key, Class<T> type, Supplier<T> supplier, Duration ttl);

    /**
     * Evicts a cache entry.
     */
    void evict(String key);

    /**
     * Evicts multiple cache entries.
     */
    void evict(String... keys);

    /**
     * Evicts entries matching pattern.
     */
    void evictByPattern(String pattern);

    /**
     * Clears all cache entries.
     */
    void clear();

    /**
     * Gets multiple values.
     */
    <T> Map<String, T> multiGet(Class<T> type, String... keys);

    /**
     * Puts multiple values.
     */
    <T> void multiPut(Map<String, T> entries);

    /**
     * Puts multiple values with TTL.
     */
    <T> void multiPut(Map<String, T> entries, Duration ttl);

    /**
     * Checks if key exists in cache.
     */
    boolean exists(String key);

    /**
     * Gets TTL of a cached entry.
     */
    Duration getTtl(String key);
}
