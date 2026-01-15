package com.andyadc.skeleton.ncache.config;

import com.andyadc.skeleton.ncache.api.CacheLoader;
import com.andyadc.skeleton.ncache.api.CacheWriter;
import com.andyadc.skeleton.ncache.serialization.Serializer;

import java.time.Duration;
import java.util.Objects;

public class CacheConfig<K, V> {

    // L1 (Local/Caffeine) settings
    private final L1Config l1Config;

    // L2 (Redis) settings
    private final L2Config l2Config;

    // Common settings
    private final Duration defaultTtl;
    private final boolean syncEnabled;
    private final CacheLoader<K, V> cacheLoader;
    private final CacheWriter<K, V> cacheWriter;
    private final Serializer<V> valueSerializer;
    private final Serializer<K> keySerializer;
    private final boolean statisticsEnabled;
    private final boolean nullValueCachingEnabled;
    private final Duration nullValueTtl;

    private CacheConfig(Builder<K, V> builder) {
        this.l1Config = builder.l1Config;
        this.l2Config = builder.l2Config;
        this.defaultTtl = builder.defaultTtl;
        this.syncEnabled = builder.syncEnabled;
        this.cacheLoader = builder.cacheLoader;
        this.cacheWriter = builder.cacheWriter;
        this.valueSerializer = builder.valueSerializer;
        this.keySerializer = builder.keySerializer;
        this.statisticsEnabled = builder.statisticsEnabled;
        this.nullValueCachingEnabled = builder.nullValueCachingEnabled;
        this.nullValueTtl = builder.nullValueTtl;
    }

    public static <K, V> Builder<K, V> builder() {
        return new Builder<>();
    }

    public static <K, V> CacheConfig<K, V> defaultConfig() {
        return CacheConfig.<K, V>builder().build();
    }

    // Getters
    public L1Config getL1Config() {
        return l1Config;
    }

    public L2Config getL2Config() {
        return l2Config;
    }

    public Duration getDefaultTtl() {
        return defaultTtl;
    }

    public boolean isSyncEnabled() {
        return syncEnabled;
    }

    public CacheLoader<K, V> getCacheLoader() {
        return cacheLoader;
    }

    public CacheWriter<K, V> getCacheWriter() {
        return cacheWriter;
    }

    public Serializer<V> getValueSerializer() {
        return valueSerializer;
    }

    public Serializer<K> getKeySerializer() {
        return keySerializer;
    }

    public boolean isStatisticsEnabled() {
        return statisticsEnabled;
    }

    public boolean isNullValueCachingEnabled() {
        return nullValueCachingEnabled;
    }

    public Duration getNullValueTtl() {
        return nullValueTtl;
    }

    /**
     * L1 (Local Cache) Configuration.
     */
    public static class L1Config {
        private final boolean enabled;
        private final long maximumSize;
        private final Duration expireAfterWrite;
        private final Duration expireAfterAccess;
        private final Duration refreshAfterWrite;
        private final int initialCapacity;
        private final boolean weakKeys;
        private final boolean weakValues;
        private final boolean softValues;
        private final boolean recordStats;

        private L1Config(L1ConfigBuilder builder) {
            this.enabled = builder.enabled;
            this.maximumSize = builder.maximumSize;
            this.expireAfterWrite = builder.expireAfterWrite;
            this.expireAfterAccess = builder.expireAfterAccess;
            this.refreshAfterWrite = builder.refreshAfterWrite;
            this.initialCapacity = builder.initialCapacity;
            this.weakKeys = builder.weakKeys;
            this.weakValues = builder.weakValues;
            this.softValues = builder.softValues;
            this.recordStats = builder.recordStats;
        }

        public static L1ConfigBuilder builder() {
            return new L1ConfigBuilder();
        }

        public static L1Config defaultConfig() {
            return builder().build();
        }

        // Getters
        public boolean isEnabled() {
            return enabled;
        }

        public long getMaximumSize() {
            return maximumSize;
        }

        public Duration getExpireAfterWrite() {
            return expireAfterWrite;
        }

        public Duration getExpireAfterAccess() {
            return expireAfterAccess;
        }

        public Duration getRefreshAfterWrite() {
            return refreshAfterWrite;
        }

        public int getInitialCapacity() {
            return initialCapacity;
        }

        public boolean isWeakKeys() {
            return weakKeys;
        }

        public boolean isWeakValues() {
            return weakValues;
        }

        public boolean isSoftValues() {
            return softValues;
        }

        public boolean isRecordStats() {
            return recordStats;
        }

        public static class L1ConfigBuilder {
            private boolean enabled = true;
            private long maximumSize = 10_000;
            private Duration expireAfterWrite = Duration.ofMinutes(10);
            private Duration expireAfterAccess = null;
            private Duration refreshAfterWrite = null;
            private int initialCapacity = 256;
            private boolean weakKeys = false;
            private boolean weakValues = false;
            private boolean softValues = false;
            private boolean recordStats = true;

            public L1ConfigBuilder enabled(boolean enabled) {
                this.enabled = enabled;
                return this;
            }

            public L1ConfigBuilder maximumSize(long maximumSize) {
                this.maximumSize = maximumSize;
                return this;
            }

            public L1ConfigBuilder expireAfterWrite(Duration duration) {
                this.expireAfterWrite = duration;
                return this;
            }

            public L1ConfigBuilder expireAfterAccess(Duration duration) {
                this.expireAfterAccess = duration;
                return this;
            }

            public L1ConfigBuilder refreshAfterWrite(Duration duration) {
                this.refreshAfterWrite = duration;
                return this;
            }

            public L1ConfigBuilder initialCapacity(int initialCapacity) {
                this.initialCapacity = initialCapacity;
                return this;
            }

            public L1ConfigBuilder weakKeys(boolean weakKeys) {
                this.weakKeys = weakKeys;
                return this;
            }

            public L1ConfigBuilder weakValues(boolean weakValues) {
                this.weakValues = weakValues;
                return this;
            }

            public L1ConfigBuilder softValues(boolean softValues) {
                this.softValues = softValues;
                return this;
            }

            public L1ConfigBuilder recordStats(boolean recordStats) {
                this.recordStats = recordStats;
                return this;
            }

            public L1Config build() {
                return new L1Config(this);
            }
        }
    }

    /**
     * L2 (Redis Cache) Configuration.
     */
    public static class L2Config {
        private final boolean enabled;
        private final String keyPrefix;
        private final Duration defaultTtl;
        private final boolean useKeyHashTag;
        private final CompressionConfig compression;
        private final BatchConfig batch;

        private L2Config(L2ConfigBuilder builder) {
            this.enabled = builder.enabled;
            this.keyPrefix = builder.keyPrefix;
            this.defaultTtl = builder.defaultTtl;
            this.useKeyHashTag = builder.useKeyHashTag;
            this.compression = builder.compression;
            this.batch = builder.batch;
        }

        public static L2ConfigBuilder builder() {
            return new L2ConfigBuilder();
        }

        public static L2Config defaultConfig() {
            return builder().build();
        }

        // Getters
        public boolean isEnabled() {
            return enabled;
        }

        public String getKeyPrefix() {
            return keyPrefix;
        }

        public Duration getDefaultTtl() {
            return defaultTtl;
        }

        public boolean isUseKeyHashTag() {
            return useKeyHashTag;
        }

        public CompressionConfig getCompression() {
            return compression;
        }

        public BatchConfig getBatch() {
            return batch;
        }

        public static class L2ConfigBuilder {
            private boolean enabled = true;
            private String keyPrefix = "cache:";
            private Duration defaultTtl = Duration.ofHours(1);
            private boolean useKeyHashTag = false;
            private CompressionConfig compression = CompressionConfig.disabled();
            private BatchConfig batch = BatchConfig.defaultConfig();

            public L2ConfigBuilder enabled(boolean enabled) {
                this.enabled = enabled;
                return this;
            }

            public L2ConfigBuilder keyPrefix(String keyPrefix) {
                this.keyPrefix = keyPrefix;
                return this;
            }

            public L2ConfigBuilder defaultTtl(Duration defaultTtl) {
                this.defaultTtl = defaultTtl;
                return this;
            }

            public L2ConfigBuilder useKeyHashTag(boolean useKeyHashTag) {
                this.useKeyHashTag = useKeyHashTag;
                return this;
            }

            public L2ConfigBuilder compression(CompressionConfig compression) {
                this.compression = compression;
                return this;
            }

            public L2ConfigBuilder batch(BatchConfig batch) {
                this.batch = batch;
                return this;
            }

            public L2Config build() {
                return new L2Config(this);
            }
        }
    }

    /**
     * Compression configuration.
     */
    public static class CompressionConfig {
        private final boolean enabled;
        private final CompressionType type;
        private final int threshold;

        public CompressionConfig(boolean enabled, CompressionType type, int threshold) {
            this.enabled = enabled;
            this.type = type;
            this.threshold = threshold;
        }

        public static CompressionConfig disabled() {
            return new CompressionConfig(false, CompressionType.GZIP, 1024);
        }

        public static CompressionConfig gzip(int threshold) {
            return new CompressionConfig(true, CompressionType.GZIP, threshold);
        }

        public static CompressionConfig lz4(int threshold) {
            return new CompressionConfig(true, CompressionType.LZ4, threshold);
        }

        public boolean isEnabled() {
            return enabled;
        }

        public CompressionType getType() {
            return type;
        }

        public int getThreshold() {
            return threshold;
        }

        public enum CompressionType {
            GZIP, LZ4, SNAPPY, ZSTD
        }
    }


    /**
     * Batch operation configuration.
     */
    public static class BatchConfig {
        private final int maxBatchSize;
        private final Duration batchTimeout;

        public BatchConfig(int maxBatchSize, Duration batchTimeout) {
            this.maxBatchSize = maxBatchSize;
            this.batchTimeout = batchTimeout;
        }

        public static BatchConfig defaultConfig() {
            return new BatchConfig(100, Duration.ofMillis(50));
        }

        public int getMaxBatchSize() {
            return maxBatchSize;
        }

        public Duration getBatchTimeout() {
            return batchTimeout;
        }
    }

    public static class Builder<K, V> {
        private L1Config l1Config = L1Config.defaultConfig();
        private L2Config l2Config = L2Config.defaultConfig();
        private Duration defaultTtl = Duration.ofHours(1);
        private boolean syncEnabled = true;
        private CacheLoader<K, V> cacheLoader;
        private CacheWriter<K, V> cacheWriter;
        private Serializer<V> valueSerializer;
        private Serializer<K> keySerializer;
        private boolean statisticsEnabled = true;
        private boolean nullValueCachingEnabled = false;
        private Duration nullValueTtl = Duration.ofMinutes(1);

        public Builder<K, V> l1Config(L1Config l1Config) {
            this.l1Config = Objects.requireNonNull(l1Config);
            return this;
        }

        public Builder<K, V> l2Config(L2Config l2Config) {
            this.l2Config = Objects.requireNonNull(l2Config);
            return this;
        }

        public Builder<K, V> defaultTtl(Duration defaultTtl) {
            this.defaultTtl = Objects.requireNonNull(defaultTtl);
            return this;
        }

        public Builder<K, V> syncEnabled(boolean syncEnabled) {
            this.syncEnabled = syncEnabled;
            return this;
        }

        public Builder<K, V> cacheLoader(CacheLoader<K, V> cacheLoader) {
            this.cacheLoader = cacheLoader;
            return this;
        }

        public Builder<K, V> cacheWriter(CacheWriter<K, V> cacheWriter) {
            this.cacheWriter = cacheWriter;
            return this;
        }

        public Builder<K, V> valueSerializer(Serializer<V> serializer) {
            this.valueSerializer = serializer;
            return this;
        }

        public Builder<K, V> keySerializer(Serializer<K> serializer) {
            this.keySerializer = serializer;
            return this;
        }

        public Builder<K, V> statisticsEnabled(boolean enabled) {
            this.statisticsEnabled = enabled;
            return this;
        }

        public Builder<K, V> nullValueCaching(boolean enabled, Duration ttl) {
            this.nullValueCachingEnabled = enabled;
            this.nullValueTtl = ttl;
            return this;
        }

        public CacheConfig<K, V> build() {
            return new CacheConfig<>(this);
        }
    }
}
