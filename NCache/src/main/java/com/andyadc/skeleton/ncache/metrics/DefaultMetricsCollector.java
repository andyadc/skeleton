package com.andyadc.skeleton.ncache.metrics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * Default in-memory metrics collector.
 */
public class DefaultMetricsCollector implements MetricsCollector {

    private final Map<String, CacheMetricsHolder> cacheMetrics = new ConcurrentHashMap<>();

    private CacheMetricsHolder getOrCreate(String cacheName) {
        return cacheMetrics.computeIfAbsent(cacheName, k -> new CacheMetricsHolder());
    }

    @Override
    public void recordHit(String cacheName, String level) {
        CacheMetricsHolder holder = getOrCreate(cacheName);
        holder.hitCount.increment();
        if ("L1".equals(level)) {
            holder.l1HitCount.increment();
        } else if ("L2".equals(level)) {
            holder.l2HitCount.increment();
        }
    }

    @Override
    public void recordMiss(String cacheName, String level) {
        CacheMetricsHolder holder = getOrCreate(cacheName);
        holder.missCount.increment();
    }

    @Override
    public void recordPut(String cacheName) {
        getOrCreate(cacheName).putCount.increment();
    }

    @Override
    public void recordRemove(String cacheName) {
        getOrCreate(cacheName).removeCount.increment();
    }

    @Override
    public void recordEviction(String cacheName, String level) {
        getOrCreate(cacheName).evictionCount.increment();
    }

    @Override
    public void recordLoadTime(String cacheName, long timeNanos, boolean success) {
        CacheMetricsHolder holder = getOrCreate(cacheName);
        holder.loadTimeTotal.add(timeNanos);
        holder.loadCount.increment();
        if (success) {
            holder.loadSuccessCount.increment();
        } else {
            holder.loadFailureCount.increment();
        }
    }

    @Override
    public void recordOperationLatency(String cacheName, String operation, long timeNanos) {
        // Could be extended for per-operation metrics
    }

    @Override
    public void updateCacheSize(String cacheName, String level, long size) {
        CacheMetricsHolder holder = getOrCreate(cacheName);
        if ("L1".equals(level)) {
            holder.l1Size = size;
        }
    }

    @Override
    public Map<String, Object> getMetrics() {
        Map<String, Object> result = new ConcurrentHashMap<>();

        for (Map.Entry<String, CacheMetricsHolder> entry : cacheMetrics.entrySet()) {
            String cacheName = entry.getKey();
            CacheMetricsHolder holder = entry.getValue();

            Map<String, Object> cacheMap = new ConcurrentHashMap<>();
            cacheMap.put("hitCount", holder.hitCount.sum());
            cacheMap.put("missCount", holder.missCount.sum());
            cacheMap.put("l1HitCount", holder.l1HitCount.sum());
            cacheMap.put("l2HitCount", holder.l2HitCount.sum());
            cacheMap.put("putCount", holder.putCount.sum());
            cacheMap.put("removeCount", holder.removeCount.sum());
            cacheMap.put("evictionCount", holder.evictionCount.sum());
            cacheMap.put("l1Size", holder.l1Size);

            long hits = holder.hitCount.sum();
            long misses = holder.missCount.sum();
            double hitRate = (hits + misses) > 0 ? (double) hits / (hits + misses) : 0.0;
            cacheMap.put("hitRate", hitRate);

            result.put(cacheName, cacheMap);
        }

        return result;
    }

    @Override
    public CacheMetrics getCacheMetrics(String cacheName) {
        CacheMetricsHolder holder = cacheMetrics.get(cacheName);
        if (holder == null) {
            return null;
        }

        return new CacheMetrics() {
            @Override
            public long getHitCount() {
                return holder.hitCount.sum();
            }

            @Override
            public long getMissCount() {
                return holder.missCount.sum();
            }

            @Override
            public double getHitRate() {
                long hits = getHitCount();
                long total = hits + getMissCount();
                return total > 0 ? (double) hits / total : 0.0;
            }

            @Override
            public long getL1HitCount() {
                return holder.l1HitCount.sum();
            }

            @Override
            public long getL2HitCount() {
                return holder.l2HitCount.sum();
            }

            @Override
            public long getPutCount() {
                return holder.putCount.sum();
            }

            @Override
            public long getRemoveCount() {
                return holder.removeCount.sum();
            }

            @Override
            public long getEvictionCount() {
                return holder.evictionCount.sum();
            }

            @Override
            public double getAverageLoadTimeMs() {
                long count = holder.loadCount.sum();
                if (count == 0) return 0.0;
                return holder.loadTimeTotal.sum() / count / 1_000_000.0;
            }

            @Override
            public long getL1Size() {
                return holder.l1Size;
            }
        };
    }

    private static class CacheMetricsHolder {
        final LongAdder hitCount = new LongAdder();
        final LongAdder missCount = new LongAdder();
        final LongAdder l1HitCount = new LongAdder();
        final LongAdder l2HitCount = new LongAdder();
        final LongAdder putCount = new LongAdder();
        final LongAdder removeCount = new LongAdder();
        final LongAdder evictionCount = new LongAdder();
        final LongAdder loadTimeTotal = new LongAdder();
        final LongAdder loadCount = new LongAdder();
        final LongAdder loadSuccessCount = new LongAdder();
        final LongAdder loadFailureCount = new LongAdder();
        volatile long l1Size = 0;
    }
}
