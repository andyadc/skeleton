package com.andyadc.skeleton.ncache.impl;

import com.andyadc.skeleton.ncache.config.CacheConfig;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.stats.CacheStats;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * L1 cache implementation using Caffeine.
 */
public class L1CaffeineCache<K, V> {

    private final String name;
    private final Cache<K, CacheEntry<V>> cache;
    private final CacheConfig.L1Config config;
    private final DefaultCacheStats stats;
    private final EvictionListener<K, V> evictionListener;

    public L1CaffeineCache(String name, CacheConfig.L1Config config,
                           DefaultCacheStats stats,
                           EvictionListener<K, V> evictionListener) {
        this.name = name;
        this.config = config;
        this.stats = stats;
        this.evictionListener = evictionListener;
        this.cache = buildCache();
    }

    private Cache<K, CacheEntry<V>> buildCache() {
        Caffeine<Object, Object> builder = Caffeine.newBuilder()
                .maximumSize(config.getMaximumSize())
                .initialCapacity(config.getInitialCapacity());

        if (config.getExpireAfterWrite() != null) {
            builder.expireAfterWrite(config.getExpireAfterWrite());
        }

        if (config.getExpireAfterAccess() != null) {
            builder.expireAfterAccess(config.getExpireAfterAccess());
        }

        if (config.isWeakKeys()) {
            builder.weakKeys();
        }

        if (config.isWeakValues()) {
            builder.weakValues();
        }

        if (config.isSoftValues()) {
            builder.softValues();
        }

        if (config.isRecordStats()) {
            builder.recordStats();
        }

        builder.removalListener((key, value, cause) -> {
            if (cause.wasEvicted()) {
                stats.recordEviction();
            }
            if (evictionListener != null && key != null) {
                @SuppressWarnings("unchecked")
                K k = (K) key;
                @SuppressWarnings("unchecked")
                CacheEntry<V> v = (CacheEntry<V>) value;
                evictionListener.onEviction(k, v, cause);
            }
        });

        return builder.build();

//        Cache<K, CacheEntry<V>> result = (Cache<K, CacheEntry<V>>) builder.build();
//        return result;
    }

    public Optional<V> get(K key) {
        CacheEntry<V> entry = cache.getIfPresent(key);
        if (entry != null) {
            if (entry.isExpired()) {
                cache.invalidate(key);
                stats.recordL1Miss();
                return Optional.empty();
            }
            stats.recordL1Hit();
            return Optional.ofNullable(entry.isNullValue() ? null : entry.getValue());
        }
        stats.recordL1Miss();
        return Optional.empty();
    }

    public V get(K key, Function<K, CacheEntry<V>> loader) {
        CacheEntry<V> entry = cache.get(key, loader);
        if (entry != null && !entry.isExpired()) {
            return entry.isNullValue() ? null : entry.getValue();
        }
        return null;
    }

    public Map<K, V> getAll(Collection<K> keys) {
        Map<K, CacheEntry<V>> entries = cache.getAllPresent(keys);
        Map<K, V> result = new ConcurrentHashMap<>();

        for (Map.Entry<K, CacheEntry<V>> e : entries.entrySet()) {
            if (!e.getValue().isExpired() && !e.getValue().isNullValue()) {
                result.put(e.getKey(), e.getValue().getValue());
                stats.recordL1Hit();
            } else {
                stats.recordL1Miss();
            }
        }

        return result;
    }

    public void put(K key, CacheEntry<V> entry) {
        cache.put(key, entry);
        stats.updateL1Size(cache.estimatedSize());
    }

    public void putAll(Map<K, CacheEntry<V>> entries) {
        cache.putAll(entries);
        stats.updateL1Size(cache.estimatedSize());
    }

    public void invalidate(K key) {
        cache.invalidate(key);
        stats.updateL1Size(cache.estimatedSize());
    }

    public void invalidateAll(Collection<K> keys) {
        cache.invalidateAll(keys);
        stats.updateL1Size(cache.estimatedSize());
    }

    public void clear() {
        cache.invalidateAll();
        stats.updateL1Size(0);
    }

    public boolean containsKey(K key) {
        CacheEntry<V> entry = cache.getIfPresent(key);
        return entry != null && !entry.isExpired();
    }

    public long size() {
        return cache.estimatedSize();
    }

    public void cleanUp() {
        cache.cleanUp();
        stats.updateL1Size(cache.estimatedSize());
    }

    public CacheStats getCaffeineStats() {
        return cache.stats();
    }

    @FunctionalInterface
    public interface EvictionListener<K, V> {
        void onEviction(K key, CacheEntry<V> value, RemovalCause cause);
    }

}
