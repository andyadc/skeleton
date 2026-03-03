package com.andyadc.skeleton.ncache.impl;

import com.andyadc.skeleton.ncache.api.*;
import com.andyadc.skeleton.ncache.config.CacheConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * Two-level cache implementation combining L1 (Caffeine) and L2 (Redis).
 */
public class TwoLevelCache<K, V> implements Cache<K, V>, CacheSynchronizer.InvalidationListener {

    private static final Logger logger = LoggerFactory.getLogger(TwoLevelCache.class);
    private static final int LOCK_STRIPE_SIZE = 64;
    private final String name;
    private final CacheConfig<K, V> config;
    private final L1CaffeineCache<K, V> l1Cache;
    private final L2RedisCache<K, V> l2Cache;
    private final DefaultCacheStats stats;
    private final CacheSynchronizer synchronizer;
    private final CacheLoader<K, V> cacheLoader;
    private final CacheWriter<K, V> cacheWriter;
    private final ExecutorService asyncExecutor;
    private final String instanceId;
    // Lock striping for concurrent loading
    private final ConcurrentHashMap<K, ReentrantLock> loadingLocks = new ConcurrentHashMap<>();

    public TwoLevelCache(String name,
                         CacheConfig<K, V> config,
                         L1CaffeineCache<K, V> l1Cache,
                         L2RedisCache<K, V> l2Cache,
                         DefaultCacheStats stats,
                         CacheSynchronizer synchronizer,
                         ExecutorService asyncExecutor,
                         String instanceId) {
        this.name = name;
        this.config = config;
        this.l1Cache = l1Cache;
        this.l2Cache = l2Cache;
        this.stats = stats;
        this.synchronizer = synchronizer;
        this.cacheLoader = config.getCacheLoader();
        this.cacheWriter = config.getCacheWriter();
        this.asyncExecutor = asyncExecutor;
        this.instanceId = instanceId;

        // Register for synchronization events
        if (synchronizer != null && config.isSyncEnabled()) {
            synchronizer.subscribe(this);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Optional<V> get(K key) {
        Objects.requireNonNull(key, "Key cannot be null");

        // Try L1 first
        if (config.getL1Config().isEnabled()) {
            Optional<V> l1Value = l1Cache.get(key);
            if (l1Value.isPresent()) {
                stats.recordHit();
                return l1Value;
            }
        }

        // Try L2
        if (config.getL2Config().isEnabled()) {
            try {
                Optional<V> l2Value = l2Cache.get(key);
                if (l2Value.isPresent()) {
                    stats.recordHit();
                    // Populate L1
                    if (config.getL1Config().isEnabled()) {
                        CacheEntry<V> entry = CacheEntry.of(l2Value.get(),
                                config.getL1Config().getExpireAfterWrite().toMillis());
                        l1Cache.put(key, entry);
                    }
                    return l2Value;
                }
            } catch (Exception e) {
                logger.warn("L2 cache get failed for key: {}", key, e);
                // Continue without L2
            }
        }

        stats.recordMiss();
        return Optional.empty();
    }

    @Override
    public V get(K key, Function<K, V> loader) {
        Objects.requireNonNull(key, "Key cannot be null");
        Objects.requireNonNull(loader, "Loader cannot be null");

        // Try to get from cache first
        Optional<V> cached = get(key);
        if (cached.isPresent()) {
            return cached.get();
        }

        // Load with lock to prevent thundering herd
        ReentrantLock lock = getLockForKey(key);
        lock.lock();
        try {
            // Double-check after acquiring lock
            cached = get(key);
            if (cached.isPresent()) {
                return cached.get();
            }

            // Load value
            long startTime = System.nanoTime();
            V value;
            try {
                value = loader.apply(key);
                stats.recordLoadSuccess(System.nanoTime() - startTime);
            } catch (Exception e) {
                stats.recordLoadFailure();
                throw e;
            }

            // Handle null value
            if (value == null) {
                if (config.isNullValueCachingEnabled()) {
                    putNullValue(key);
                }
                return null;
            }

            // Store in cache
            put(key, value);

            return value;
        } finally {
            lock.unlock();
            cleanupLock(key, lock);
        }
    }

    @Override
    public Map<K, V> getAll(Collection<K> keys) {
        Objects.requireNonNull(keys, "Keys cannot be null");

        if (keys.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<K, V> result = new HashMap<>();
        Set<K> missingKeys = new HashSet<>();

        // Try L1 first
        if (config.getL1Config().isEnabled()) {
            Map<K, V> l1Values = l1Cache.getAll(keys);
            result.putAll(l1Values);

            for (K key : keys) {
                if (!l1Values.containsKey(key)) {
                    missingKeys.add(key);
                }
            }
        } else {
            missingKeys.addAll(keys);
        }

        // Try L2 for missing keys
        if (!missingKeys.isEmpty() && config.getL2Config().isEnabled()) {
            try {
                Map<K, V> l2Values = l2Cache.getAll(missingKeys);
                result.putAll(l2Values);

                // Populate L1 with L2 values
                if (config.getL1Config().isEnabled()) {
                    Map<K, CacheEntry<V>> entries = new HashMap<>();
                    for (Map.Entry<K, V> entry : l2Values.entrySet()) {
                        entries.put(entry.getKey(),
                                CacheEntry.of(entry.getValue(),
                                        config.getL1Config().getExpireAfterWrite().toMillis()));
                    }
                    l1Cache.putAll(entries);
                }

                missingKeys.removeAll(l2Values.keySet());
            } catch (Exception e) {
                logger.warn("L2 cache getAll failed", e);
            }
        }

        // Record stats
        stats.recordHit(); // Simplified - could be more granular

        return result;
    }

    @Override
    public void put(K key, V value) {
        put(key, value, config.getDefaultTtl());
    }

    @Override
    public void put(K key, V value, Duration ttl) {
        Objects.requireNonNull(key, "Key cannot be null");

        Duration effectiveTtl = ttl != null ? ttl : config.getDefaultTtl();

        // Write-through to external storage
        if (cacheWriter != null) {
            try {
                cacheWriter.write(key, value);
            } catch (Exception e) {
                logger.error("Cache writer failed for key: {}", key, e);
                throw new RuntimeException("Cache write-through failed", e);
            }
        }

        // Put in L2 first
        if (config.getL2Config().isEnabled()) {
            try {
                l2Cache.put(key, value, effectiveTtl);
            } catch (Exception e) {
                logger.warn("L2 cache put failed for key: {}", key, e);
            }
        }

        // Put in L1
        if (config.getL1Config().isEnabled()) {
            Duration l1Ttl = config.getL1Config().getExpireAfterWrite();
            // L1 TTL should not exceed L2 TTL
            if (l1Ttl.compareTo(effectiveTtl) > 0) {
                l1Ttl = effectiveTtl;
            }
            CacheEntry<V> entry = CacheEntry.of(value, l1Ttl.toMillis());
            l1Cache.put(key, entry);
        }

        // Notify other nodes
        if (synchronizer != null && config.isSyncEnabled()) {
            synchronizer.publishInvalidation(name, key);
        }

        stats.recordPut();
    }

    @Override
    public void putAll(Map<K, V> entries) {
        Objects.requireNonNull(entries, "Entries cannot be null");

        if (entries.isEmpty()) {
            return;
        }

        Duration ttl = config.getDefaultTtl();

        // Write-through
        if (cacheWriter != null) {
            try {
                cacheWriter.writeAll(entries);
            } catch (Exception e) {
                logger.error("Cache writer failed for batch", e);
                throw new RuntimeException("Cache write-through failed", e);
            }
        }

        // Put in L2
        if (config.getL2Config().isEnabled()) {
            try {
                l2Cache.putAll(entries, ttl);
            } catch (Exception e) {
                logger.warn("L2 cache putAll failed", e);
            }
        }

        // Put in L1
        if (config.getL1Config().isEnabled()) {
            Duration l1Ttl = config.getL1Config().getExpireAfterWrite();
            Map<K, CacheEntry<V>> l1Entries = new HashMap<>();
            for (Map.Entry<K, V> entry : entries.entrySet()) {
                l1Entries.put(entry.getKey(),
                        CacheEntry.of(entry.getValue(), l1Ttl.toMillis()));
            }
            l1Cache.putAll(l1Entries);
        }

        // Notify other nodes
        if (synchronizer != null && config.isSyncEnabled()) {
            synchronizer.publishBulkInvalidation(name, entries.keySet());
        }
    }

    @Override
    public boolean putIfAbsent(K key, V value) {
        return putIfAbsent(key, value, config.getDefaultTtl());
    }

    @Override
    public boolean putIfAbsent(K key, V value, Duration ttl) {
        Objects.requireNonNull(key, "Key cannot be null");

        // Check L1 first
        if (config.getL1Config().isEnabled() && l1Cache.containsKey(key)) {
            return false;
        }

        // Try atomic putIfAbsent on L2
        if (config.getL2Config().isEnabled()) {
            try {
                boolean success = l2Cache.putIfAbsent(key, value, ttl);
                if (!success) {
                    return false;
                }
            } catch (Exception e) {
                logger.warn("L2 cache putIfAbsent failed for key: {}", key, e);
                return false;
            }
        }

        // Put in L1
        if (config.getL1Config().isEnabled()) {
            Duration l1Ttl = config.getL1Config().getExpireAfterWrite();
            CacheEntry<V> entry = CacheEntry.of(value, l1Ttl.toMillis());
            l1Cache.put(key, entry);
        }

        // Notify other nodes
        if (synchronizer != null && config.isSyncEnabled()) {
            synchronizer.publishInvalidation(name, key);
        }

        return true;
    }

    @Override
    public boolean remove(K key) {
        Objects.requireNonNull(key, "Key cannot be null");

        boolean removed = false;

        // Remove from external storage
        if (cacheWriter != null) {
            try {
                cacheWriter.delete(key);
            } catch (Exception e) {
                logger.error("Cache writer delete failed for key: {}", key, e);
            }
        }

        // Remove from L2
        if (config.getL2Config().isEnabled()) {
            try {
                removed = l2Cache.remove(key);
            } catch (Exception e) {
                logger.warn("L2 cache remove failed for key: {}", key, e);
            }
        }

        // Remove from L1
        if (config.getL1Config().isEnabled()) {
            l1Cache.invalidate(key);
            removed = true;
        }

        // Notify other nodes
        if (synchronizer != null && config.isSyncEnabled()) {
            synchronizer.publishInvalidation(name, key);
        }

        if (removed) {
            stats.recordRemove();
        }

        return removed;
    }

    @Override
    public void removeAll(Collection<K> keys) {
        Objects.requireNonNull(keys, "Keys cannot be null");

        if (keys.isEmpty()) {
            return;
        }

        // Remove from external storage
        if (cacheWriter != null) {
            try {
                cacheWriter.deleteAll(keys);
            } catch (Exception e) {
                logger.error("Cache writer deleteAll failed", e);
            }
        }

        // Remove from L2
        if (config.getL2Config().isEnabled()) {
            try {
                l2Cache.removeAll(keys);
            } catch (Exception e) {
                logger.warn("L2 cache removeAll failed", e);
            }
        }

        // Remove from L1
        if (config.getL1Config().isEnabled()) {
            l1Cache.invalidateAll(keys);
        }

        // Notify other nodes
        if (synchronizer != null && config.isSyncEnabled()) {
            synchronizer.publishBulkInvalidation(name, keys);
        }
    }

    @Override
    public void clear() {
        // Clear L2
        if (config.getL2Config().isEnabled()) {
            try {
                l2Cache.clear();
            } catch (Exception e) {
                logger.warn("L2 cache clear failed", e);
            }
        }

        // Clear L1
        if (config.getL1Config().isEnabled()) {
            l1Cache.clear();
        }

        // Notify other nodes
        if (synchronizer != null && config.isSyncEnabled()) {
            synchronizer.publishClear(name);
        }
    }

    @Override
    public boolean containsKey(K key) {
        Objects.requireNonNull(key, "Key cannot be null");

        // Check L1
        if (config.getL1Config().isEnabled() && l1Cache.containsKey(key)) {
            return true;
        }

        // Check L2
        if (config.getL2Config().isEnabled()) {
            try {
                return l2Cache.containsKey(key);
            } catch (Exception e) {
                logger.warn("L2 cache containsKey failed for key: {}", key, e);
            }
        }

        return false;
    }

    @Override
    public long size() {
        // Return L1 size as approximation
        if (config.getL1Config().isEnabled()) {
            return l1Cache.size();
        }
        return 0;
    }

    @Override
    public CacheStats getStats() {
        return stats.snapshot();
    }

    // Async operations

    @Override
    public CompletableFuture<Optional<V>> getAsync(K key) {
        return CompletableFuture.supplyAsync(() -> get(key), asyncExecutor);
    }

    @Override
    public CompletableFuture<Void> putAsync(K key, V value) {
        return CompletableFuture.runAsync(() -> put(key, value), asyncExecutor);
    }

    @Override
    public CompletableFuture<Boolean> removeAsync(K key) {
        return CompletableFuture.supplyAsync(() -> remove(key), asyncExecutor);
    }

    // CacheSynchronizer.InvalidationListener implementation

    @Override
    public void onInvalidate(String cacheName, Object key) {
        if (name.equals(cacheName) && config.getL1Config().isEnabled()) {
            @SuppressWarnings("unchecked")
            K typedKey = (K) key;
            l1Cache.invalidate(typedKey);
            logger.debug("L1 cache invalidated for key: {} in cache: {}", key, cacheName);
        }
    }

    @Override
    public void onBulkInvalidate(String cacheName, Collection<?> keys) {
        if (name.equals(cacheName) && config.getL1Config().isEnabled()) {
            @SuppressWarnings("unchecked")
            Collection<K> typedKeys = (Collection<K>) keys;
            l1Cache.invalidateAll(typedKeys);
            logger.debug("L1 cache bulk invalidated for {} keys in cache: {}",
                    keys.size(), cacheName);
        }
    }

    @Override
    public void onClear(String cacheName) {
        if (name.equals(cacheName) && config.getL1Config().isEnabled()) {
            l1Cache.clear();
            logger.debug("L1 cache cleared for cache: {}", cacheName);
        }
    }

    // Private helper methods

    private void putNullValue(K key) {
        Duration nullTtl = config.getNullValueTtl();

        if (config.getL1Config().isEnabled()) {
            CacheEntry<V> nullEntry = CacheEntry.nullEntry(nullTtl.toMillis());
            l1Cache.put(key, nullEntry);
        }
    }

    private ReentrantLock getLockForKey(K key) {
        return loadingLocks.computeIfAbsent(key, k -> new ReentrantLock());
    }

    private void cleanupLock(K key, ReentrantLock lock) {
        // Only remove if no other thread is waiting
        if (!lock.hasQueuedThreads()) {
            loadingLocks.remove(key, lock);
        }
    }

    /**
     * Refresh cache entry asynchronously.
     */
    public CompletableFuture<Void> refreshAsync(K key) {
        return CompletableFuture.runAsync(() -> {
            if (cacheLoader != null) {
                try {
                    V value = cacheLoader.load(key);
                    if (value != null) {
                        put(key, value);
                    }
                } catch (Exception e) {
                    logger.error("Failed to refresh key: {}", key, e);
                }
            }
        }, asyncExecutor);
    }

    /**
     * Preload cache with specified keys.
     */
    public CompletableFuture<Void> preload(Collection<K> keys) {
        if (cacheLoader == null) {
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.runAsync(() -> {
            try {
                Map<K, V> values = cacheLoader.loadAll(keys);
                putAll(values);
                logger.info("Preloaded {} entries into cache: {}", values.size(), name);
            } catch (Exception e) {
                logger.error("Failed to preload cache: {}", name, e);
            }
        }, asyncExecutor);
    }

    /**
     * Cleanup resources.
     */
    public void destroy() {
        if (synchronizer != null) {
            synchronizer.unsubscribe(this);
        }
        l1Cache.clear();
        loadingLocks.clear();
    }

}
