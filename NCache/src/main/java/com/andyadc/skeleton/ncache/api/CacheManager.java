package com.andyadc.skeleton.ncache.api;

import com.andyadc.skeleton.ncache.config.CacheConfig;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Cache manager for creating and managing cache instances.
 */
public interface CacheManager {

    /**
     * Get or create cache with default config.
     */
    <K, V> Cache<K, V> getCache(String name);

    /**
     * Get or create cache with custom config.
     */
    <K, V> Cache<K, V> getCache(String name, CacheConfig<K, V> config);

    /**
     * Get existing cache if present.
     */
    <K, V> Optional<Cache<K, V>> getCacheIfPresent(String name);

    /**
     * Get all cache names.
     */
    Collection<String> getCacheNames();

    /**
     * Destroy a cache.
     */
    void destroyCache(String name);

    /**
     * Shutdown all caches.
     */
    void shutdown();

    /**
     * Get aggregated stats for all caches.
     */
    Map<String, CacheStats> getAllStats();
}
