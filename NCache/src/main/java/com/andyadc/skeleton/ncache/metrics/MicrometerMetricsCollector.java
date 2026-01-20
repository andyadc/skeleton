package com.andyadc.skeleton.ncache.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Micrometer-based metrics collector for integration with various monitoring systems.
 */
public class MicrometerMetricsCollector implements MetricsCollector {

    private static final String METRIC_PREFIX = "cache.two_level";

    private final MeterRegistry registry;
    private final Map<String, CacheMeters> cacheMeters = new ConcurrentHashMap<>();

    public MicrometerMetricsCollector(MeterRegistry registry) {
        this.registry = registry;
    }

    private CacheMeters getOrCreate(String cacheName) {
        return cacheMeters.computeIfAbsent(cacheName, this::createCacheMeters);
    }

    private CacheMeters createCacheMeters(String cacheName) {
        Tags tags = Tags.of("cache", cacheName);

        return new CacheMeters(
                registry.counter(METRIC_PREFIX + ".hits", tags.and("level", "l1")),
                registry.counter(METRIC_PREFIX + ".hits", tags.and("level", "l2")),
                registry.counter(METRIC_PREFIX + ".misses", tags),
                registry.counter(METRIC_PREFIX + ".puts", tags),
                registry.counter(METRIC_PREFIX + ".removes", tags),
                registry.counter(METRIC_PREFIX + ".evictions", tags.and("level", "l1")),
                registry.timer(METRIC_PREFIX + ".loads", tags),
                registry.gauge(METRIC_PREFIX + ".size", tags.and("level", "l1"),
                        new java.util.concurrent.atomic.AtomicLong())
        );
    }

    @Override
    public void recordHit(String cacheName, String level) {
        CacheMeters meters = getOrCreate(cacheName);
        if ("L1".equals(level)) {
            meters.l1Hits.increment();
        } else {
            meters.l2Hits.increment();
        }
    }

    @Override
    public void recordMiss(String cacheName, String level) {
        getOrCreate(cacheName).misses.increment();
    }

    @Override
    public void recordPut(String cacheName) {
        getOrCreate(cacheName).puts.increment();
    }

    @Override
    public void recordRemove(String cacheName) {
        getOrCreate(cacheName).removes.increment();
    }

    @Override
    public void recordEviction(String cacheName, String level) {
        getOrCreate(cacheName).evictions.increment();
    }

    @Override
    public void recordLoadTime(String cacheName, long timeNanos, boolean success) {
        getOrCreate(cacheName).loadTimer.record(timeNanos, TimeUnit.NANOSECONDS);
    }

    @Override
    public void recordOperationLatency(String cacheName, String operation, long timeNanos) {
        Timer timer = registry.timer(METRIC_PREFIX + ".operation",
                Tags.of("cache", cacheName, "operation", operation));
        timer.record(timeNanos, TimeUnit.NANOSECONDS);
    }

    @Override
    public void updateCacheSize(String cacheName, String level, long size) {
        CacheMeters meters = getOrCreate(cacheName);
        if ("L1".equals(level) && meters.l1Size instanceof java.util.concurrent.atomic.AtomicLong) {
            ((java.util.concurrent.atomic.AtomicLong) meters.l1Size).set(size);
        }
    }

    @Override
    public Map<String, Object> getMetrics() {
        Map<String, Object> result = new ConcurrentHashMap<>();

        for (Map.Entry<String, CacheMeters> entry : cacheMeters.entrySet()) {
            String cacheName = entry.getKey();
            CacheMeters meters = entry.getValue();

            Map<String, Object> cacheMap = new ConcurrentHashMap<>();
            cacheMap.put("l1Hits", meters.l1Hits.count());
            cacheMap.put("l2Hits", meters.l2Hits.count());
            cacheMap.put("misses", meters.misses.count());
            cacheMap.put("puts", meters.puts.count());
            cacheMap.put("removes", meters.removes.count());
            cacheMap.put("evictions", meters.evictions.count());
            cacheMap.put("loadCount", meters.loadTimer.count());
            cacheMap.put("loadTotalTimeMs", meters.loadTimer.totalTime(TimeUnit.MILLISECONDS));

            result.put(cacheName, cacheMap);
        }

        return result;
    }

    @Override
    public CacheMetrics getCacheMetrics(String cacheName) {
        CacheMeters meters = cacheMeters.get(cacheName);
        if (meters == null) {
            return null;
        }

        return new CacheMetrics() {
            @Override
            public long getHitCount() {
                return (long) (meters.l1Hits.count() + meters.l2Hits.count());
            }

            @Override
            public long getMissCount() {
                return (long) meters.misses.count();
            }

            @Override
            public double getHitRate() {
                long hits = getHitCount();
                long total = hits + getMissCount();
                return total > 0 ? (double) hits / total : 0.0;
            }

            @Override
            public long getL1HitCount() {
                return (long) meters.l1Hits.count();
            }

            @Override
            public long getL2HitCount() {
                return (long) meters.l2Hits.count();
            }

            @Override
            public long getPutCount() {
                return (long) meters.puts.count();
            }

            @Override
            public long getRemoveCount() {
                return (long) meters.removes.count();
            }

            @Override
            public long getEvictionCount() {
                return (long) meters.evictions.count();
            }

            @Override
            public double getAverageLoadTimeMs() {
                return meters.loadTimer.mean(TimeUnit.MILLISECONDS);
            }

            @Override
            public long getL1Size() {
                if (meters.l1Size instanceof java.util.concurrent.atomic.AtomicLong) {
                    return ((java.util.concurrent.atomic.AtomicLong) meters.l1Size).get();
                }
                return 0;
            }
        };
    }

    private static class CacheMeters {
        final Counter l1Hits;
        final Counter l2Hits;
        final Counter misses;
        final Counter puts;
        final Counter removes;
        final Counter evictions;
        final Timer loadTimer;
        final Number l1Size;

        CacheMeters(Counter l1Hits, Counter l2Hits, Counter misses,
                    Counter puts, Counter removes, Counter evictions,
                    Timer loadTimer, Number l1Size) {
            this.l1Hits = l1Hits;
            this.l2Hits = l2Hits;
            this.misses = misses;
            this.puts = puts;
            this.removes = removes;
            this.evictions = evictions;
            this.loadTimer = loadTimer;
            this.l1Size = l1Size;
        }
    }
}
