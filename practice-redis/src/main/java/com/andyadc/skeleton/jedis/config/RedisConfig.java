package com.andyadc.skeleton.jedis.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public record RedisConfig(
        String host,
        int port,
        String password,
        int database,
        boolean ssl,
        PoolConfig poolConfig,
        TimeoutConfig timeoutConfig,
        AppConfig appConfig
) {

    public static RedisConfig load() {
        Properties props = new Properties();
        try (InputStream is = RedisConfig.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration", e);
        }

        return new RedisConfig(
                getProperty(props, "redis.host", "localhost"),
                getIntProperty(props, "redis.port", 6379),
                getProperty(props, "redis.password", ""),
                getIntProperty(props, "redis.database", 0),
                getBooleanProperty(props, "redis.ssl", false),
                new PoolConfig(
                        getIntProperty(props, "redis.pool.max-total", 50),
                        getIntProperty(props, "redis.pool.max-idle", 20),
                        getIntProperty(props, "redis.pool.min-idle", 5),
                        getLongProperty(props, "redis.pool.max-wait-millis", 3000L),
                        getBooleanProperty(props, "redis.pool.test-on-borrow", true),
                        getBooleanProperty(props, "redis.pool.test-on-return", false),
                        getBooleanProperty(props, "redis.pool.test-while-idle", true),
                        getLongProperty(props, "redis.pool.time-between-eviction-runs-millis", 30000L),
                        getIntProperty(props, "redis.pool.num-tests-per-eviction-run", 3)
                ),
                new TimeoutConfig(
                        getIntProperty(props, "redis.timeout.connection", 2000),
                        getIntProperty(props, "redis.timeout.socket", 2000)
                ),
                new AppConfig(
                        getIntProperty(props, "app.cache.default-ttl-seconds", 3600),
                        getIntProperty(props, "app.rate-limit.requests-per-minute", 100)
                )
        );
    }

    private static String getProperty(Properties props, String key, String defaultValue) {
        String envKey = key.replace(".", "_").replace("-", "_").toUpperCase();
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isEmpty()) {
            return envValue;
        }
        return props.getProperty(key, defaultValue);
    }

    private static int getIntProperty(Properties props, String key, int defaultValue) {
        return Integer.parseInt(getProperty(props, key, String.valueOf(defaultValue)));
    }

    private static long getLongProperty(Properties props, String key, long defaultValue) {
        return Long.parseLong(getProperty(props, key, String.valueOf(defaultValue)));
    }

    private static boolean getBooleanProperty(Properties props, String key, boolean defaultValue) {
        return Boolean.parseBoolean(getProperty(props, key, String.valueOf(defaultValue)));
    }

    public record PoolConfig(
            int maxTotal,
            int maxIdle,
            int minIdle,
            long maxWaitMillis,
            boolean testOnBorrow,
            boolean testOnReturn,
            boolean testWhileIdle,
            long timeBetweenEvictionRunsMillis,
            int numTestsPerEvictionRun
    ) {
    }

    public record TimeoutConfig(
            int connectionTimeoutMillis,
            int socketTimeoutMillis
    ) {
    }

    public record AppConfig(
            int defaultTtlSeconds,
            int rateLimitRequestsPerMinute
    ) {
    }
}
