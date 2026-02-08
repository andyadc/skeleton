package com.andyadc.skeleton.redis.config;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Redis configuration properties loader and holder.
 * Supports loading from properties file or programmatic configuration.
 */
public class RedisProperties {

    private static final String DEFAULT_PROPERTIES_FILE = "redis.properties";

    // Client type
    private String clientType = "jedis";

    // Connection settings
    private String host = "localhost";
    private int port = 6379;
    private String password = "";
    private int database = 0;
    private boolean ssl = false;

    // Timeout settings
    private Duration connectionTimeout = Duration.ofMillis(2000);
    private Duration socketTimeout = Duration.ofMillis(2000);
    private Duration commandTimeout = Duration.ofMillis(5000);

    // Pool settings
    private int poolMaxTotal = 50;
    private int poolMaxIdle = 20;
    private int poolMinIdle = 5;
    private Duration poolMaxWait = Duration.ofMillis(3000);
    private boolean poolTestOnBorrow = true;
    private boolean poolTestOnReturn = false;
    private boolean poolTestWhileIdle = true;
    private Duration timeBetweenEvictionRuns = Duration.ofMillis(30000);
    private Duration minEvictableIdleTime = Duration.ofMillis(60000);

    // Cluster settings
    private boolean clusterEnabled = false;
    private List<String> clusterNodes = List.of();

    // Sentinel settings
    private boolean sentinelEnabled = false;
    private String sentinelMaster = "mymaster";
    private List<String> sentinelNodes = List.of();

    // Retry settings
    private int retryMaxAttempts = 3;
    private Duration retryDelay = Duration.ofMillis(100);

    // Key prefix
    private String keyPrefix = "";

    /**
     * Creates properties with default values.
     */
    public RedisProperties() {
    }

    /**
     * Loads properties from the default properties file.
     */
    public static RedisProperties load() {
        return load(DEFAULT_PROPERTIES_FILE);
    }

    /**
     * Loads properties from a specified properties file.
     */
    public static RedisProperties load(String propertiesFile) {
        RedisProperties redisProperties = new RedisProperties();
        Properties props = new Properties();

        try (InputStream is = RedisProperties.class.getClassLoader()
                .getResourceAsStream(propertiesFile)) {
            if (is != null) {
                props.load(is);
                redisProperties.loadFromProperties(props);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Redis properties", e);
        }

        // Override with environment variables if present
        redisProperties.loadFromEnvironment();

        return redisProperties;
    }

    // Builder pattern for programmatic configuration
    public static Builder builder() {
        return new Builder();
    }

    private void loadFromProperties(Properties props) {
        this.clientType = props.getProperty("redis.client.type", clientType);
        this.host = props.getProperty("redis.host", host);
        this.port = Integer.parseInt(props.getProperty("redis.port", String.valueOf(port)));
        this.password = props.getProperty("redis.password", password);
        this.database = Integer.parseInt(props.getProperty("redis.database", String.valueOf(database)));
        this.ssl = Boolean.parseBoolean(props.getProperty("redis.ssl", String.valueOf(ssl)));

        this.connectionTimeout = Duration.ofMillis(
                Long.parseLong(props.getProperty("redis.connection.timeout",
                        String.valueOf(connectionTimeout.toMillis()))));
        this.socketTimeout = Duration.ofMillis(
                Long.parseLong(props.getProperty("redis.socket.timeout",
                        String.valueOf(socketTimeout.toMillis()))));
        this.commandTimeout = Duration.ofMillis(
                Long.parseLong(props.getProperty("redis.command.timeout",
                        String.valueOf(commandTimeout.toMillis()))));

        this.poolMaxTotal = Integer.parseInt(
                props.getProperty("redis.pool.max-total", String.valueOf(poolMaxTotal)));
        this.poolMaxIdle = Integer.parseInt(
                props.getProperty("redis.pool.max-idle", String.valueOf(poolMaxIdle)));
        this.poolMinIdle = Integer.parseInt(
                props.getProperty("redis.pool.min-idle", String.valueOf(poolMinIdle)));
        this.poolMaxWait = Duration.ofMillis(
                Long.parseLong(props.getProperty("redis.pool.max-wait",
                        String.valueOf(poolMaxWait.toMillis()))));
        this.poolTestOnBorrow = Boolean.parseBoolean(
                props.getProperty("redis.pool.test-on-borrow", String.valueOf(poolTestOnBorrow)));
        this.poolTestOnReturn = Boolean.parseBoolean(
                props.getProperty("redis.pool.test-on-return", String.valueOf(poolTestOnReturn)));
        this.poolTestWhileIdle = Boolean.parseBoolean(
                props.getProperty("redis.pool.test-while-idle", String.valueOf(poolTestWhileIdle)));
        this.timeBetweenEvictionRuns = Duration.ofMillis(
                Long.parseLong(props.getProperty("redis.pool.time-between-eviction-runs",
                        String.valueOf(timeBetweenEvictionRuns.toMillis()))));
        this.minEvictableIdleTime = Duration.ofMillis(
                Long.parseLong(props.getProperty("redis.pool.min-evictable-idle-time",
                        String.valueOf(minEvictableIdleTime.toMillis()))));

        this.clusterEnabled = Boolean.parseBoolean(
                props.getProperty("redis.cluster.enabled", String.valueOf(clusterEnabled)));
        String clusterNodesStr = props.getProperty("redis.cluster.nodes", "");
        if (!clusterNodesStr.isEmpty()) {
            this.clusterNodes = Arrays.asList(clusterNodesStr.split(","));
        }

        this.sentinelEnabled = Boolean.parseBoolean(
                props.getProperty("redis.sentinel.enabled", String.valueOf(sentinelEnabled)));
        this.sentinelMaster = props.getProperty("redis.sentinel.master", sentinelMaster);
        String sentinelNodesStr = props.getProperty("redis.sentinel.nodes", "");
        if (!sentinelNodesStr.isEmpty()) {
            this.sentinelNodes = Arrays.asList(sentinelNodesStr.split(","));
        }

        this.retryMaxAttempts = Integer.parseInt(
                props.getProperty("redis.retry.max-attempts", String.valueOf(retryMaxAttempts)));
        this.retryDelay = Duration.ofMillis(
                Long.parseLong(props.getProperty("redis.retry.delay",
                        String.valueOf(retryDelay.toMillis()))));

        this.keyPrefix = props.getProperty("redis.key.prefix", keyPrefix);
    }

    private void loadFromEnvironment() {
        String envClientType = System.getenv("REDIS_CLIENT_TYPE");
        if (envClientType != null) this.clientType = envClientType;

        String envHost = System.getenv("REDIS_HOST");
        if (envHost != null) this.host = envHost;

        String envPort = System.getenv("REDIS_PORT");
        if (envPort != null) this.port = Integer.parseInt(envPort);

        String envPassword = System.getenv("REDIS_PASSWORD");
        if (envPassword != null) this.password = envPassword;

        String envDatabase = System.getenv("REDIS_DATABASE");
        if (envDatabase != null) this.database = Integer.parseInt(envDatabase);
    }

    // Getters
    public String getClientType() {
        return clientType;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getPassword() {
        return password;
    }

    public int getDatabase() {
        return database;
    }

    public boolean isSsl() {
        return ssl;
    }

    public Duration getConnectionTimeout() {
        return connectionTimeout;
    }

    public Duration getSocketTimeout() {
        return socketTimeout;
    }

    public Duration getCommandTimeout() {
        return commandTimeout;
    }

    public int getPoolMaxTotal() {
        return poolMaxTotal;
    }

    public int getPoolMaxIdle() {
        return poolMaxIdle;
    }

    public int getPoolMinIdle() {
        return poolMinIdle;
    }

    public Duration getPoolMaxWait() {
        return poolMaxWait;
    }

    public boolean isPoolTestOnBorrow() {
        return poolTestOnBorrow;
    }

    public boolean isPoolTestOnReturn() {
        return poolTestOnReturn;
    }

    public boolean isPoolTestWhileIdle() {
        return poolTestWhileIdle;
    }

    public Duration getTimeBetweenEvictionRuns() {
        return timeBetweenEvictionRuns;
    }

    public Duration getMinEvictableIdleTime() {
        return minEvictableIdleTime;
    }

    public boolean isClusterEnabled() {
        return clusterEnabled;
    }

    public List<String> getClusterNodes() {
        return clusterNodes;
    }

    public boolean isSentinelEnabled() {
        return sentinelEnabled;
    }

    public String getSentinelMaster() {
        return sentinelMaster;
    }

    public List<String> getSentinelNodes() {
        return sentinelNodes;
    }

    public int getRetryMaxAttempts() {
        return retryMaxAttempts;
    }

    public Duration getRetryDelay() {
        return retryDelay;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public boolean hasPassword() {
        return password != null && !password.isEmpty();
    }

    public static class Builder {
        private final RedisProperties properties = new RedisProperties();

        public Builder clientType(String clientType) {
            properties.clientType = clientType;
            return this;
        }

        public Builder host(String host) {
            properties.host = host;
            return this;
        }

        public Builder port(int port) {
            properties.port = port;
            return this;
        }

        public Builder password(String password) {
            properties.password = password;
            return this;
        }

        public Builder database(int database) {
            properties.database = database;
            return this;
        }

        public Builder ssl(boolean ssl) {
            properties.ssl = ssl;
            return this;
        }

        public Builder connectionTimeout(Duration timeout) {
            properties.connectionTimeout = timeout;
            return this;
        }

        public Builder socketTimeout(Duration timeout) {
            properties.socketTimeout = timeout;
            return this;
        }

        public Builder commandTimeout(Duration timeout) {
            properties.commandTimeout = timeout;
            return this;
        }

        public Builder poolMaxTotal(int maxTotal) {
            properties.poolMaxTotal = maxTotal;
            return this;
        }

        public Builder poolMaxIdle(int maxIdle) {
            properties.poolMaxIdle = maxIdle;
            return this;
        }

        public Builder poolMinIdle(int minIdle) {
            properties.poolMinIdle = minIdle;
            return this;
        }

        public Builder keyPrefix(String prefix) {
            properties.keyPrefix = prefix;
            return this;
        }

        public Builder clusterEnabled(boolean enabled) {
            properties.clusterEnabled = enabled;
            return this;
        }

        public Builder clusterNodes(List<String> nodes) {
            properties.clusterNodes = nodes;
            return this;
        }

        public Builder sentinelEnabled(boolean enabled) {
            properties.sentinelEnabled = enabled;
            return this;
        }

        public Builder sentinelMaster(String master) {
            properties.sentinelMaster = master;
            return this;
        }

        public Builder sentinelNodes(List<String> nodes) {
            properties.sentinelNodes = nodes;
            return this;
        }

        public RedisProperties build() {
            return properties;
        }
    }

}
