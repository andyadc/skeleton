package com.andyadc.skeleton.ncache.config;

import com.andyadc.skeleton.ncache.api.CacheSynchronizer;
import com.andyadc.skeleton.ncache.metrics.MetricsCollector;
import com.andyadc.skeleton.ncache.resilience.CircuitBreakerConfig;
import com.andyadc.skeleton.ncache.resilience.RetryConfig;
import com.andyadc.skeleton.ncache.serialization.Serializer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Configuration for TwoLevelCacheManager.
 */
public class TwoLevelCacheManagerConfig {

    private final RedisClientConfig redisConfig;
    private final CacheSynchronizer synchronizer;
    private final MetricsCollector metricsCollector;
    private final CircuitBreakerConfig circuitBreakerConfig;
    private final RetryConfig retryConfig;
    private final ExecutorService asyncExecutor;
    private final ScheduledExecutorService scheduledExecutor;
    private final Serializer<?> defaultSerializer;
    private final CacheConfig<?, ?> defaultCacheConfig;
    private final String instanceId;

    private TwoLevelCacheManagerConfig(Builder builder) {
        this.redisConfig = builder.redisConfig;
        this.synchronizer = builder.synchronizer;
        this.metricsCollector = builder.metricsCollector;
        this.circuitBreakerConfig = builder.circuitBreakerConfig;
        this.retryConfig = builder.retryConfig;
        this.asyncExecutor = builder.asyncExecutor;
        this.scheduledExecutor = builder.scheduledExecutor;
        this.defaultSerializer = builder.defaultSerializer;
        this.defaultCacheConfig = builder.defaultCacheConfig;
        this.instanceId = builder.instanceId;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public RedisClientConfig getRedisConfig() {
        return redisConfig;
    }

    public CacheSynchronizer getSynchronizer() {
        return synchronizer;
    }

    public MetricsCollector getMetricsCollector() {
        return metricsCollector;
    }

    public CircuitBreakerConfig getCircuitBreakerConfig() {
        return circuitBreakerConfig;
    }

    public RetryConfig getRetryConfig() {
        return retryConfig;
    }

    public ExecutorService getAsyncExecutor() {
        return asyncExecutor;
    }

    public ScheduledExecutorService getScheduledExecutor() {
        return scheduledExecutor;
    }

    public Serializer<?> getDefaultSerializer() {
        return defaultSerializer;
    }

    public CacheConfig<?, ?> getDefaultCacheConfig() {
        return defaultCacheConfig;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public static class Builder {
        private RedisClientConfig redisConfig;
        private CacheSynchronizer synchronizer;
        private MetricsCollector metricsCollector;
        private CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.defaultConfig();
        private RetryConfig retryConfig = RetryConfig.defaultConfig();
        private ExecutorService asyncExecutor;
        private ScheduledExecutorService scheduledExecutor;
        private Serializer<?> defaultSerializer;
        private CacheConfig<?, ?> defaultCacheConfig;
        private String instanceId;

        public Builder redisConfig(RedisClientConfig redisConfig) {
            this.redisConfig = redisConfig;
            return this;
        }

        public Builder synchronizer(CacheSynchronizer synchronizer) {
            this.synchronizer = synchronizer;
            return this;
        }

        public Builder metricsCollector(MetricsCollector metricsCollector) {
            this.metricsCollector = metricsCollector;
            return this;
        }

        public Builder circuitBreakerConfig(CircuitBreakerConfig config) {
            this.circuitBreakerConfig = config;
            return this;
        }

        public Builder retryConfig(RetryConfig config) {
            this.retryConfig = config;
            return this;
        }

        public Builder asyncExecutor(ExecutorService executor) {
            this.asyncExecutor = executor;
            return this;
        }

        public Builder scheduledExecutor(ScheduledExecutorService executor) {
            this.scheduledExecutor = executor;
            return this;
        }

        public Builder defaultSerializer(Serializer<?> serializer) {
            this.defaultSerializer = serializer;
            return this;
        }

        public Builder defaultCacheConfig(CacheConfig<?, ?> config) {
            this.defaultCacheConfig = config;
            return this;
        }

        public Builder instanceId(String instanceId) {
            this.instanceId = instanceId;
            return this;
        }

        public TwoLevelCacheManagerConfig build() {
            if (instanceId == null) {
                instanceId = java.util.UUID.randomUUID().toString();
            }
            return new TwoLevelCacheManagerConfig(this);
        }
    }

}
