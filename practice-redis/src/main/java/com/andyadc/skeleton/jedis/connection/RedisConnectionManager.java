package com.andyadc.skeleton.jedis.connection;

import com.andyadc.skeleton.jedis.config.RedisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.time.Duration;
import java.util.function.Function;

public class RedisConnectionManager implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(RedisConnectionManager.class);

    private final JedisPool jedisPool;
    private final JedisPooled jedisPooled;
    private final RedisConfig config;

    public RedisConnectionManager(RedisConfig config) {
        this.config = config;
        this.jedisPool = createJedisPool(config);
        this.jedisPooled = createJedisPooled(config);
        logger.info("Redis connection manager initialized for {}:{}",
                config.host(), config.port());
    }

    private JedisPool createJedisPool(RedisConfig config) {
        JedisPoolConfig poolConfig = createPoolConfig(config);

        String password = config.password().isEmpty() ? null : config.password();

        if (config.ssl()) {
            return new JedisPool(
                    poolConfig,
                    config.host(),
                    config.port(),
                    config.timeoutConfig().connectionTimeoutMillis(),
                    config.timeoutConfig().socketTimeoutMillis(),
                    password,
                    config.database(),
                    "redis-client",
                    true,
                    null,
                    null,
                    null
            );
        }

        return new JedisPool(
                poolConfig,
                config.host(),
                config.port(),
                config.timeoutConfig().connectionTimeoutMillis(),
                config.timeoutConfig().socketTimeoutMillis(),
                password,
                config.database(),
                "redis-client"
        );
    }

    private JedisPooled createJedisPooled(RedisConfig config) {
        String password = config.password().isEmpty() ? null : config.password();

        // Option 1: Simple constructor (most common use case)
        if (password == null && config.database() == 0 && !config.ssl()) {
            return new JedisPooled(config.host(), config.port());
        }

        // Option 2: Using HostAndPort with ConnectionPoolConfig
        HostAndPort hostAndPort = new HostAndPort(config.host(), config.port());

        // Build client config
        JedisClientConfig clientConfig = DefaultJedisClientConfig.builder()
                .connectionTimeoutMillis(config.timeoutConfig().connectionTimeoutMillis())
                .socketTimeoutMillis(config.timeoutConfig().socketTimeoutMillis())
                .password(password)
                .database(config.database())
                .ssl(config.ssl())
                .build();

        // Create connection pool config
        ConnectionPoolConfig poolConfig = new ConnectionPoolConfig();
        poolConfig.setMaxTotal(config.poolConfig().maxTotal());
        poolConfig.setMaxIdle(config.poolConfig().maxIdle());
        poolConfig.setMinIdle(config.poolConfig().minIdle());
        poolConfig.setMaxWait(Duration.ofMillis(config.poolConfig().maxWaitMillis()));
        poolConfig.setBlockWhenExhausted(true);
        poolConfig.setTestOnBorrow(config.poolConfig().testOnBorrow());
        poolConfig.setTestOnReturn(config.poolConfig().testOnReturn());
        poolConfig.setTestWhileIdle(config.poolConfig().testWhileIdle());
        poolConfig.setTimeBetweenEvictionRuns(
                Duration.ofMillis(config.poolConfig().timeBetweenEvictionRunsMillis()));
        poolConfig.setNumTestsPerEvictionRun(config.poolConfig().numTestsPerEvictionRun());
        poolConfig.setJmxEnabled(true);
        poolConfig.setJmxNamePrefix("jedis-pooled");

        return new JedisPooled(poolConfig, hostAndPort, clientConfig);
    }

    private JedisPoolConfig createPoolConfig(RedisConfig config) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();

        // Pool sizing
        poolConfig.setMaxTotal(config.poolConfig().maxTotal());
        poolConfig.setMaxIdle(config.poolConfig().maxIdle());
        poolConfig.setMinIdle(config.poolConfig().minIdle());

        // Blocking behavior
        poolConfig.setMaxWait(Duration.ofMillis(config.poolConfig().maxWaitMillis()));
        poolConfig.setBlockWhenExhausted(true);

        // Connection validation
        poolConfig.setTestOnBorrow(config.poolConfig().testOnBorrow());
        poolConfig.setTestOnReturn(config.poolConfig().testOnReturn());
        poolConfig.setTestWhileIdle(config.poolConfig().testWhileIdle());

        // Eviction
        poolConfig.setTimeBetweenEvictionRuns(
                Duration.ofMillis(config.poolConfig().timeBetweenEvictionRunsMillis()));
        poolConfig.setNumTestsPerEvictionRun(config.poolConfig().numTestsPerEvictionRun());

        // JMX
        poolConfig.setJmxEnabled(true);
        poolConfig.setJmxNamePrefix("redis-pool");

        return poolConfig;
    }

    /**
     * Execute operation with automatic resource management (try-with-resources pattern)
     */
    public <T> T execute(Function<Jedis, T> operation) {
        try (Jedis jedis = jedisPool.getResource()) {
            return operation.apply(jedis);
        } catch (Exception e) {
            logger.error("Redis operation failed", e);
            throw new RedisOperationException("Failed to execute Redis operation", e);
        }
    }

    /**
     * Execute operation with retry logic
     */
    public <T> T executeWithRetry(Function<Jedis, T> operation, int maxRetries) {
        int attempt = 0;
        Exception lastException = null;

        while (attempt < maxRetries) {
            try (Jedis jedis = jedisPool.getResource()) {
                return operation.apply(jedis);
            } catch (Exception e) {
                lastException = e;
                attempt++;
                logger.warn("Redis operation failed, attempt {}/{}", attempt, maxRetries, e);

                if (attempt < maxRetries) {
                    try {
                        Thread.sleep((long) Math.pow(2, attempt) * 100);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RedisOperationException("Retry interrupted", ie);
                    }
                }
            }
        }

        throw new RedisOperationException(
                "Failed after " + maxRetries + " attempts", lastException);
    }

    /**
     * Get pooled connection for simple operations (auto resource management)
     */
    public JedisPooled getPooled() {
        return jedisPooled;
    }

    /**
     * Get raw pool for advanced operations
     */
    public JedisPool getPool() {
        return jedisPool;
    }

    public RedisConfig getConfig() {
        return config;
    }

    public boolean isHealthy() {
        try {
            String pong = jedisPooled.ping();
            return "PONG".equals(pong);
        } catch (Exception e) {
            logger.error("Health check failed", e);
            return false;
        }
    }

    public PoolStats getPoolStats() {
        return new PoolStats(
                jedisPool.getNumActive(),
                jedisPool.getNumIdle(),
                jedisPool.getNumWaiters()
        );
    }

    @Override
    public void close() {
        logger.info("Closing Redis connection manager");
        try {
            jedisPooled.close();
        } catch (Exception e) {
            logger.warn("Error closing JedisPooled", e);
        }
        try {
            jedisPool.close();
        } catch (Exception e) {
            logger.warn("Error closing JedisPool", e);
        }
    }

    public record PoolStats(int active, int idle, int waiters) {
    }

    public static class RedisOperationException extends RuntimeException {
        public RedisOperationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
