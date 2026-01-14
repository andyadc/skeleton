package com.andyadc.skeleton.ncache.api;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Core cache interface providing synchronous and asynchronous operations.
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public interface Cache<K, V> {

    /**
     * Get cache name.
     */
    String getName();

    /**
     * Get value by key.
     */
    Optional<V> get(K key);

    /**
     * Get value or compute if absent.
     */
    V get(K key, Function<K, V> loader);

    /**
     * Get multiple values by keys.
     */
    Map<K, V> getAll(Collection<K> keys);

    /**
     * Put value with default TTL.
     */
    void put(K key, V value);

    /**
     * Put value with custom TTL.
     */
    void put(K key, V value, Duration ttl);

    /**
     * Put multiple values.
     */
    void putAll(Map<K, V> entries);

    /**
     * Put if absent, returns true if put was successful.
     */
    boolean putIfAbsent(K key, V value);

    /**
     * Put if absent with TTL.
     */
    boolean putIfAbsent(K key, V value, Duration ttl);

    /**
     * Remove entry by key.
     */
    boolean remove(K key);

    /**
     * Remove multiple entries.
     */
    void removeAll(Collection<K> keys);

    /**
     * Clear all entries in this cache.
     */
    void clear();

    /**
     * Check if key exists.
     */
    boolean containsKey(K key);

    /**
     * Get approximate size.
     */
    long size();

    /**
     * Get cache statistics.
     */
    CacheStats getStats();

    // Async operations

    /**
     * Async get.
     */
    CompletableFuture<Optional<V>> getAsync(K key);

    /**
     * Async put.
     */
    CompletableFuture<Void> putAsync(K key, V value);

    /**
     * Async remove.
     */
    CompletableFuture<Boolean> removeAsync(K key);
}
