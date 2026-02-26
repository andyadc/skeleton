package com.andyadc.skeleton.ncache.impl;

import com.andyadc.skeleton.ncache.api.CacheStats;

import java.util.concurrent.atomic.LongAdder;

/**
 * Default implementation of cache statistics.
 */
public class DefaultCacheStats implements CacheStats {

    private final LongAdder hitCount = new LongAdder();
    private final LongAdder missCount = new LongAdder();
    private final LongAdder loadSuccessCount = new LongAdder();
    private final LongAdder loadFailureCount = new LongAdder();
    private final LongAdder evictionCount = new LongAdder();
    private final LongAdder putCount = new LongAdder();
    private final LongAdder removeCount = new LongAdder();
    private final LongAdder totalLoadTime = new LongAdder();

    private final LongAdder l1HitCount = new LongAdder();
    private final LongAdder l1MissCount = new LongAdder();
    private final LongAdder l2HitCount = new LongAdder();
    private final LongAdder l2MissCount = new LongAdder();

    private volatile long l1Size = 0;

    public void recordHit() {
        hitCount.increment();
    }

    public void recordMiss() {
        missCount.increment();
    }

    public void recordLoadSuccess(long loadTimeNanos) {
        loadSuccessCount.increment();
        totalLoadTime.add(loadTimeNanos);
    }

    public void recordLoadFailure() {
        loadFailureCount.increment();
    }

    public void recordEviction() {
        evictionCount.increment();
    }

    public void recordPut() {
        putCount.increment();
    }

    public void recordRemove() {
        removeCount.increment();
    }

    public void recordL1Hit() {
        l1HitCount.increment();
    }

    public void recordL1Miss() {
        l1MissCount.increment();
    }

    public void recordL2Hit() {
        l2HitCount.increment();
    }

    public void recordL2Miss() {
        l2MissCount.increment();
    }

    public void updateL1Size(long size) {
        this.l1Size = size;
    }

    @Override
    public long getHitCount() {
        return hitCount.sum();
    }

    @Override
    public long getMissCount() {
        return missCount.sum();
    }

    @Override
    public long getLoadSuccessCount() {
        return loadSuccessCount.sum();
    }

    @Override
    public long getLoadFailureCount() {
        return loadFailureCount.sum();
    }

    @Override
    public long getEvictionCount() {
        return evictionCount.sum();
    }

    @Override
    public long getPutCount() {
        return putCount.sum();
    }

    @Override
    public long getRemoveCount() {
        return removeCount.sum();
    }

    @Override
    public double getHitRate() {
        long total = getHitCount() + getMissCount();
        return total == 0 ? 0.0 : (double) getHitCount() / total;
    }

    @Override
    public double getMissRate() {
        return 1.0 - getHitRate();
    }

    @Override
    public long getAverageLoadTime() {
        long successCount = getLoadSuccessCount();
        return successCount == 0 ? 0 : totalLoadTime.sum() / successCount;
    }

    @Override
    public long getL1HitCount() {
        return l1HitCount.sum();
    }

    @Override
    public long getL1MissCount() {
        return l1MissCount.sum();
    }

    @Override
    public long getL1Size() {
        return l1Size;
    }

    @Override
    public long getL2HitCount() {
        return l2HitCount.sum();
    }

    @Override
    public long getL2MissCount() {
        return l2MissCount.sum();
    }

    @Override
    public CacheStats snapshot() {
        return new ImmutableCacheStats(this);
    }

    @Override
    public void reset() {
        hitCount.reset();
        missCount.reset();
        loadSuccessCount.reset();
        loadFailureCount.reset();
        evictionCount.reset();
        putCount.reset();
        removeCount.reset();
        totalLoadTime.reset();
        l1HitCount.reset();
        l1MissCount.reset();
        l2HitCount.reset();
        l2MissCount.reset();
    }

    /**
     * Immutable snapshot of stats.
     */
    private static class ImmutableCacheStats implements CacheStats {
        private final long hitCount;
        private final long missCount;
        private final long loadSuccessCount;
        private final long loadFailureCount;
        private final long evictionCount;
        private final long putCount;
        private final long removeCount;
        private final long averageLoadTime;
        private final long l1HitCount;
        private final long l1MissCount;
        private final long l1Size;
        private final long l2HitCount;
        private final long l2MissCount;

        ImmutableCacheStats(DefaultCacheStats source) {
            this.hitCount = source.getHitCount();
            this.missCount = source.getMissCount();
            this.loadSuccessCount = source.getLoadSuccessCount();
            this.loadFailureCount = source.getLoadFailureCount();
            this.evictionCount = source.getEvictionCount();
            this.putCount = source.getPutCount();
            this.removeCount = source.getRemoveCount();
            this.averageLoadTime = source.getAverageLoadTime();
            this.l1HitCount = source.getL1HitCount();
            this.l1MissCount = source.getL1MissCount();
            this.l1Size = source.getL1Size();
            this.l2HitCount = source.getL2HitCount();
            this.l2MissCount = source.getL2MissCount();
        }

        @Override
        public long getHitCount() {
            return hitCount;
        }

        @Override
        public long getMissCount() {
            return missCount;
        }

        @Override
        public long getLoadSuccessCount() {
            return loadSuccessCount;
        }

        @Override
        public long getLoadFailureCount() {
            return loadFailureCount;
        }

        @Override
        public long getEvictionCount() {
            return evictionCount;
        }

        @Override
        public long getPutCount() {
            return putCount;
        }

        @Override
        public long getRemoveCount() {
            return removeCount;
        }

        @Override
        public double getHitRate() {
            long total = hitCount + missCount;
            return total == 0 ? 0.0 : (double) hitCount / total;
        }

        @Override
        public double getMissRate() {
            return 1.0 - getHitRate();
        }

        @Override
        public long getAverageLoadTime() {
            return averageLoadTime;
        }

        @Override
        public long getL1HitCount() {
            return l1HitCount;
        }

        @Override
        public long getL1MissCount() {
            return l1MissCount;
        }

        @Override
        public long getL1Size() {
            return l1Size;
        }

        @Override
        public long getL2HitCount() {
            return l2HitCount;
        }

        @Override
        public long getL2MissCount() {
            return l2MissCount;
        }

        @Override
        public CacheStats snapshot() {
            return this;
        }

        @Override
        public void reset() {
            throw new UnsupportedOperationException();
        }
    }

}
