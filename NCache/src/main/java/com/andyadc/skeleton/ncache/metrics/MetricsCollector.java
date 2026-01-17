package com.andyadc.skeleton.ncache.metrics;

import java.util.Map;

/**
 * Interface for collecting and exporting cache metrics.
 */
public interface MetricsCollector {

    /**
     * Record cache hit.
     */
    void recordHit(String cacheName, String level);

    /**
     * Record cache miss.
     */
    void recordMiss(String cacheName, String level);

    /**
     * Record put operation.
     */
    void recordPut(String cacheName);

    /**
     * Record remove operation.
     */
    void recordRemove(String cacheName);

    /**
     * Record eviction.
     */
    void recordEviction(String cacheName, String level);

    /**
     * Record load time.
     */
    void recordLoadTime(String cacheName, long timeNanos, boolean success);

    /**
     * Record operation latency.
     */
    void recordOperationLatency(String cacheName, String operation, long timeNanos);

    /**
     * Update cache size gauge.
     */
    void updateCacheSize(String cacheName, String level, long size);

    /**
     * Get all metrics as map.
     */
    Map<String, Object> getMetrics();

    /**
     * Export metrics for a specific cache.
     */
    CacheMetrics getCacheMetrics(String cacheName);

    /**
     * Cache-specific metrics holder.
     */
    interface CacheMetrics {
        long getHitCount();

        long getMissCount();

        double getHitRate();

        long getL1HitCount();

        long getL2HitCount();

        long getPutCount();

        long getRemoveCount();

        long getEvictionCount();

        double getAverageLoadTimeMs();

        long getL1Size();
    }

}