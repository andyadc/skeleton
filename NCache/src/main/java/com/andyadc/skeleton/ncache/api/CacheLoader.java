package com.andyadc.skeleton.ncache.api;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Cache loader for loading values on cache miss.
 */
@FunctionalInterface
public interface CacheLoader<K, V> {

    /**
     * Load single value.
     */
    V load(K key) throws Exception;

    /**
     * Load multiple values (batch loading).
     */
    default Map<K, V> loadAll(Collection<K> keys) throws Exception {
        Map<K, V> result = new java.util.HashMap<>();
        for (K key : keys) {
            try {
                V value = load(key);
                if (value != null) {
                    result.put(key, value);
                }
            } catch (Exception e) {
                // Skip failed loads in batch
            }
        }
        return result;
    }

    /**
     * Async load.
     */
    default CompletableFuture<V> loadAsync(K key) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return load(key);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

}
