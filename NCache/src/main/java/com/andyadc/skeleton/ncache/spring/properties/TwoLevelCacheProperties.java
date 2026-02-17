package com.andyadc.skeleton.ncache.spring.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration properties for two-level cache.
 */
@ConfigurationProperties(prefix = "cache.two-level")
public class TwoLevelCacheProperties {

    /**
     * Enable two-level cache.
     */
    private boolean enabled = true;

    /**
     * Instance ID for cache synchronization.
     */
    private String instanceId;

    /**
     * Default TTL for cache entries.
     */
    private Duration defaultTtl = Duration.ofHours(1);

    /**
     * Enable cache synchronization across nodes.
     */
    private boolean sync = true;

    /**
     * Enable statistics collection.
     */
    private boolean statisticsEnabled = true;

    /**
     * Enable null value caching.
     */
    private boolean nullValueCachingEnabled = false;

    /**
     * TTL for null values.
     */
    private Duration nullValueTtl = Duration.ofMinutes(1);

    /**
     * L1 (local) cache configuration.
     */
    @NestedConfigurationProperty
    private L1Properties l1 = new L1Properties();

    /**
     * L2 (Redis) cache configuration.
     */
    @NestedConfigurationProperty
    private L2Properties l2 = new L2Properties();

    /**
     * Redis configuration.
     */
    @NestedConfigurationProperty
    private RedisProperties redis = new RedisProperties();

    /**
     * Resilience configuration.
     */
    @NestedConfigurationProperty
    private ResilienceProperties resilience = new ResilienceProperties();

    /**
     * Per-cache configuration overrides.
     */
    private Map<String, CacheInstanceProperties> caches = new HashMap<>();

    // Getters and setters
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public Duration getDefaultTtl() {
        return defaultTtl;
    }

    public void setDefaultTtl(Duration defaultTtl) {
        this.defaultTtl = defaultTtl;
    }

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public boolean isStatisticsEnabled() {
        return statisticsEnabled;
    }

    public void setStatisticsEnabled(boolean statisticsEnabled) {
        this.statisticsEnabled = statisticsEnabled;
    }

    public boolean isNullValueCachingEnabled() {
        return nullValueCachingEnabled;
    }

    public void setNullValueCachingEnabled(boolean nullValueCachingEnabled) {
        this.nullValueCachingEnabled = nullValueCachingEnabled;
    }

    public Duration getNullValueTtl() {
        return nullValueTtl;
    }

    public void setNullValueTtl(Duration nullValueTtl) {
        this.nullValueTtl = nullValueTtl;
    }

    public L1Properties getL1() {
        return l1;
    }

    public void setL1(L1Properties l1) {
        this.l1 = l1;
    }

    public L2Properties getL2() {
        return l2;
    }

    public void setL2(L2Properties l2) {
        this.l2 = l2;
    }

    public RedisProperties getRedis() {
        return redis;
    }

    public void setRedis(RedisProperties redis) {
        this.redis = redis;
    }

    public ResilienceProperties getResilience() {
        return resilience;
    }

    public void setResilience(ResilienceProperties resilience) {
        this.resilience = resilience;
    }

    public Map<String, CacheInstanceProperties> getCaches() {
        return caches;
    }

    public void setCaches(Map<String, CacheInstanceProperties> caches) {
        this.caches = caches;
    }

    /**
     * L1 cache properties.
     */
    public static class L1Properties {
        private boolean enabled = true;
        private long maximumSize = 10000;
        private Duration expireAfterWrite = Duration.ofMinutes(10);
        private Duration expireAfterAccess;
        private int initialCapacity = 256;
        private boolean recordStats = true;
        private boolean weakKeys = false;
        private boolean softValues = false;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public long getMaximumSize() {
            return maximumSize;
        }

        public void setMaximumSize(long maximumSize) {
            this.maximumSize = maximumSize;
        }

        public Duration getExpireAfterWrite() {
            return expireAfterWrite;
        }

        public void setExpireAfterWrite(Duration expireAfterWrite) {
            this.expireAfterWrite = expireAfterWrite;
        }

        public Duration getExpireAfterAccess() {
            return expireAfterAccess;
        }

        public void setExpireAfterAccess(Duration expireAfterAccess) {
            this.expireAfterAccess = expireAfterAccess;
        }

        public int getInitialCapacity() {
            return initialCapacity;
        }

        public void setInitialCapacity(int initialCapacity) {
            this.initialCapacity = initialCapacity;
        }

        public boolean isRecordStats() {
            return recordStats;
        }

        public void setRecordStats(boolean recordStats) {
            this.recordStats = recordStats;
        }

        public boolean isWeakKeys() {
            return weakKeys;
        }

        public void setWeakKeys(boolean weakKeys) {
            this.weakKeys = weakKeys;
        }

        public boolean isSoftValues() {
            return softValues;
        }

        public void setSoftValues(boolean softValues) {
            this.softValues = softValues;
        }
    }

    /**
     * L2 cache properties.
     */
    public static class L2Properties {
        private boolean enabled = true;
        private String keyPrefix = "cache:";
        private Duration defaultTtl = Duration.ofHours(1);
        private boolean useKeyHashTag = false;
        private CompressionProperties compression = new CompressionProperties();

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getKeyPrefix() {
            return keyPrefix;
        }

        public void setKeyPrefix(String keyPrefix) {
            this.keyPrefix = keyPrefix;
        }

        public Duration getDefaultTtl() {
            return defaultTtl;
        }

        public void setDefaultTtl(Duration defaultTtl) {
            this.defaultTtl = defaultTtl;
        }

        public boolean isUseKeyHashTag() {
            return useKeyHashTag;
        }

        public void setUseKeyHashTag(boolean useKeyHashTag) {
            this.useKeyHashTag = useKeyHashTag;
        }

        public CompressionProperties getCompression() {
            return compression;
        }

        public void setCompression(CompressionProperties compression) {
            this.compression = compression;
        }

        public static class CompressionProperties {
            private boolean enabled = false;
            private String type = "GZIP";
            private int threshold = 1024;

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public int getThreshold() {
                return threshold;
            }

            public void setThreshold(int threshold) {
                this.threshold = threshold;
            }
        }
    }

    /**
     * Redis properties.
     */
    public static class RedisProperties {
        private String host = "localhost";
        private int port = 6379;
        private String password;
        private int database = 0;
        private Duration connectionTimeout = Duration.ofSeconds(2);
        private Duration socketTimeout = Duration.ofSeconds(2);

        @NestedConfigurationProperty
        private PoolProperties pool = new PoolProperties();

        @NestedConfigurationProperty
        private SentinelProperties sentinel = new SentinelProperties();

        @NestedConfigurationProperty
        private ClusterProperties cluster = new ClusterProperties();

        @NestedConfigurationProperty
        private SslProperties ssl = new SslProperties();

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getDatabase() {
            return database;
        }

        public void setDatabase(int database) {
            this.database = database;
        }

        public Duration getConnectionTimeout() {
            return connectionTimeout;
        }

        public void setConnectionTimeout(Duration connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
        }

        public Duration getSocketTimeout() {
            return socketTimeout;
        }

        public void setSocketTimeout(Duration socketTimeout) {
            this.socketTimeout = socketTimeout;
        }

        public PoolProperties getPool() {
            return pool;
        }

        public void setPool(PoolProperties pool) {
            this.pool = pool;
        }

        public SentinelProperties getSentinel() {
            return sentinel;
        }

        public void setSentinel(SentinelProperties sentinel) {
            this.sentinel = sentinel;
        }

        public ClusterProperties getCluster() {
            return cluster;
        }

        public void setCluster(ClusterProperties cluster) {
            this.cluster = cluster;
        }

        public SslProperties getSsl() {
            return ssl;
        }

        public void setSsl(SslProperties ssl) {
            this.ssl = ssl;
        }

        public static class PoolProperties {
            private int maxTotal = 128;
            private int maxIdle = 32;
            private int minIdle = 8;
            private Duration maxWait = Duration.ofSeconds(1);

            public int getMaxTotal() {
                return maxTotal;
            }

            public void setMaxTotal(int maxTotal) {
                this.maxTotal = maxTotal;
            }

            public int getMaxIdle() {
                return maxIdle;
            }

            public void setMaxIdle(int maxIdle) {
                this.maxIdle = maxIdle;
            }

            public int getMinIdle() {
                return minIdle;
            }

            public void setMinIdle(int minIdle) {
                this.minIdle = minIdle;
            }

            public Duration getMaxWait() {
                return maxWait;
            }

            public void setMaxWait(Duration maxWait) {
                this.maxWait = maxWait;
            }
        }

        public static class SentinelProperties {
            private boolean enabled = false;
            private String master;
            private List<String> nodes = new ArrayList<>();

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            public String getMaster() {
                return master;
            }

            public void setMaster(String master) {
                this.master = master;
            }

            public List<String> getNodes() {
                return nodes;
            }

            public void setNodes(List<String> nodes) {
                this.nodes = nodes;
            }
        }

        public static class ClusterProperties {
            private boolean enabled = false;
            private List<String> nodes = new ArrayList<>();
            private int maxRedirects = 5;

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            public List<String> getNodes() {
                return nodes;
            }

            public void setNodes(List<String> nodes) {
                this.nodes = nodes;
            }

            public int getMaxRedirects() {
                return maxRedirects;
            }

            public void setMaxRedirects(int maxRedirects) {
                this.maxRedirects = maxRedirects;
            }
        }

        public static class SslProperties {
            private boolean enabled = false;
            private String trustStorePath;
            private String trustStorePassword;

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            public String getTrustStorePath() {
                return trustStorePath;
            }

            public void setTrustStorePath(String trustStorePath) {
                this.trustStorePath = trustStorePath;
            }

            public String getTrustStorePassword() {
                return trustStorePassword;
            }

            public void setTrustStorePassword(String trustStorePassword) {
                this.trustStorePassword = trustStorePassword;
            }
        }
    }

    /**
     * Resilience properties.
     */
    public static class ResilienceProperties {
        @NestedConfigurationProperty
        private CircuitBreakerProperties circuitBreaker = new CircuitBreakerProperties();

        @NestedConfigurationProperty
        private RetryProperties retry = new RetryProperties();

        public CircuitBreakerProperties getCircuitBreaker() {
            return circuitBreaker;
        }

        public void setCircuitBreaker(CircuitBreakerProperties circuitBreaker) {
            this.circuitBreaker = circuitBreaker;
        }

        public RetryProperties getRetry() {
            return retry;
        }

        public void setRetry(RetryProperties retry) {
            this.retry = retry;
        }

        public static class CircuitBreakerProperties {
            private int failureThreshold = 5;
            private Duration openStateDuration = Duration.ofSeconds(30);
            private int halfOpenMaxCalls = 3;
            private double failureRateThreshold = 50.0;
            private int minimumNumberOfCalls = 10;

            public int getFailureThreshold() {
                return failureThreshold;
            }

            public void setFailureThreshold(int failureThreshold) {
                this.failureThreshold = failureThreshold;
            }

            public Duration getOpenStateDuration() {
                return openStateDuration;
            }

            public void setOpenStateDuration(Duration openStateDuration) {
                this.openStateDuration = openStateDuration;
            }

            public int getHalfOpenMaxCalls() {
                return halfOpenMaxCalls;
            }

            public void setHalfOpenMaxCalls(int halfOpenMaxCalls) {
                this.halfOpenMaxCalls = halfOpenMaxCalls;
            }

            public double getFailureRateThreshold() {
                return failureRateThreshold;
            }

            public void setFailureRateThreshold(double failureRateThreshold) {
                this.failureRateThreshold = failureRateThreshold;
            }

            public int getMinimumNumberOfCalls() {
                return minimumNumberOfCalls;
            }

            public void setMinimumNumberOfCalls(int minimumNumberOfCalls) {
                this.minimumNumberOfCalls = minimumNumberOfCalls;
            }
        }

        public static class RetryProperties {
            private int maxAttempts = 3;
            private Duration initialDelay = Duration.ofMillis(100);
            private Duration maxDelay = Duration.ofSeconds(1);
            private double multiplier = 2.0;

            public int getMaxAttempts() {
                return maxAttempts;
            }

            public void setMaxAttempts(int maxAttempts) {
                this.maxAttempts = maxAttempts;
            }

            public Duration getInitialDelay() {
                return initialDelay;
            }

            public void setInitialDelay(Duration initialDelay) {
                this.initialDelay = initialDelay;
            }

            public Duration getMaxDelay() {
                return maxDelay;
            }

            public void setMaxDelay(Duration maxDelay) {
                this.maxDelay = maxDelay;
            }

            public double getMultiplier() {
                return multiplier;
            }

            public void setMultiplier(double multiplier) {
                this.multiplier = multiplier;
            }
        }
    }

    /**
     * Per-cache instance properties.
     */
    public static class CacheInstanceProperties {
        private Duration ttl;
        private Long l1MaxSize;
        private Duration l1ExpireAfterWrite;
        private Duration l2Ttl;
        private boolean sync = true;

        public Duration getTtl() {
            return ttl;
        }

        public void setTtl(Duration ttl) {
            this.ttl = ttl;
        }

        public Long getL1MaxSize() {
            return l1MaxSize;
        }

        public void setL1MaxSize(Long l1MaxSize) {
            this.l1MaxSize = l1MaxSize;
        }

        public Duration getL1ExpireAfterWrite() {
            return l1ExpireAfterWrite;
        }

        public void setL1ExpireAfterWrite(Duration l1ExpireAfterWrite) {
            this.l1ExpireAfterWrite = l1ExpireAfterWrite;
        }

        public Duration getL2Ttl() {
            return l2Ttl;
        }

        public void setL2Ttl(Duration l2Ttl) {
            this.l2Ttl = l2Ttl;
        }

        public boolean isSync() {
            return sync;
        }

        public void setSync(boolean sync) {
            this.sync = sync;
        }
    }
}
