package com.andyadc.skeleton.ncache.api;

/**
 * Cache statistics holder.
 */
public interface CacheStats {

    long getHitCount();

    long getMissCount();

    long getLoadSuccessCount();

    long getLoadFailureCount();

    long getEvictionCount();

    long getPutCount();

    long getRemoveCount();

    double getHitRate();

    double getMissRate();

    long getAverageLoadTime();

    // L1 specific stats
    long getL1HitCount();

    long getL1MissCount();

    long getL1Size();

    // L2 specific stats
    long getL2HitCount();

    long getL2MissCount();

    /**
     * Create a snapshot of current stats.
     */
    CacheStats snapshot();

    /**
     * Reset all counters.
     */
    void reset();
}
