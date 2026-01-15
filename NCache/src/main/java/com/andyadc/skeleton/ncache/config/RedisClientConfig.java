package com.andyadc.skeleton.ncache.config;

import java.time.Duration;
import java.util.List;

/**
 * Redis client configuration.
 */
public class RedisClientConfig {

    private final Mode mode;
    private final String host;
    private final int port;
    private final String password;
    private final int database;
    private final Duration connectionTimeout;
    private final Duration socketTimeout;
    // Pool settings
    private final int poolMaxTotal;
    private final int poolMaxIdle;
    private final int poolMinIdle;
    private final Duration poolMaxWait;
    // Sentinel settings
    private final String masterName;
    private final List<String> sentinelNodes;
    // Cluster settings
    private final List<String> clusterNodes;
    private final int maxRedirects;
    // SSL settings
    private final boolean sslEnabled;
    private final String sslTrustStorePath;
    private final String sslTrustStorePassword;

    private RedisClientConfig(Builder builder) {
        this.mode = builder.mode;
        this.host = builder.host;
        this.port = builder.port;
        this.password = builder.password;
        this.database = builder.database;
        this.connectionTimeout = builder.connectionTimeout;
        this.socketTimeout = builder.socketTimeout;
        this.poolMaxTotal = builder.poolMaxTotal;
        this.poolMaxIdle = builder.poolMaxIdle;
        this.poolMinIdle = builder.poolMinIdle;
        this.poolMaxWait = builder.poolMaxWait;
        this.masterName = builder.masterName;
        this.sentinelNodes = builder.sentinelNodes;
        this.clusterNodes = builder.clusterNodes;
        this.maxRedirects = builder.maxRedirects;
        this.sslEnabled = builder.sslEnabled;
        this.sslTrustStorePath = builder.sslTrustStorePath;
        this.sslTrustStorePassword = builder.sslTrustStorePassword;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static RedisClientConfig standalone(String host, int port) {
        return builder().mode(Mode.STANDALONE).host(host).port(port).build();
    }

    // Getters
    public Mode getMode() {
        return mode;
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

    public Duration getConnectionTimeout() {
        return connectionTimeout;
    }

    public Duration getSocketTimeout() {
        return socketTimeout;
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

    public String getMasterName() {
        return masterName;
    }

    public List<String> getSentinelNodes() {
        return sentinelNodes;
    }

    public List<String> getClusterNodes() {
        return clusterNodes;
    }

    public int getMaxRedirects() {
        return maxRedirects;
    }

    public boolean isSslEnabled() {
        return sslEnabled;
    }

    public String getSslTrustStorePath() {
        return sslTrustStorePath;
    }

    public String getSslTrustStorePassword() {
        return sslTrustStorePassword;
    }

    public enum Mode {
        STANDALONE, SENTINEL, CLUSTER
    }

    public static class Builder {
        private Mode mode = Mode.STANDALONE;
        private String host = "localhost";
        private int port = 6379;
        private String password;
        private int database = 0;
        private Duration connectionTimeout = Duration.ofSeconds(2);
        private Duration socketTimeout = Duration.ofSeconds(2);
        private int poolMaxTotal = 128;
        private int poolMaxIdle = 32;
        private int poolMinIdle = 8;
        private Duration poolMaxWait = Duration.ofSeconds(1);
        private String masterName;
        private List<String> sentinelNodes;
        private List<String> clusterNodes;
        private int maxRedirects = 5;
        private boolean sslEnabled = false;
        private String sslTrustStorePath;
        private String sslTrustStorePassword;

        public Builder mode(Mode mode) {
            this.mode = mode;
            return this;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder database(int database) {
            this.database = database;
            return this;
        }

        public Builder connectionTimeout(Duration timeout) {
            this.connectionTimeout = timeout;
            return this;
        }

        public Builder socketTimeout(Duration timeout) {
            this.socketTimeout = timeout;
            return this;
        }

        public Builder poolMaxTotal(int max) {
            this.poolMaxTotal = max;
            return this;
        }

        public Builder poolMaxIdle(int max) {
            this.poolMaxIdle = max;
            return this;
        }

        public Builder poolMinIdle(int min) {
            this.poolMinIdle = min;
            return this;
        }

        public Builder poolMaxWait(Duration duration) {
            this.poolMaxWait = duration;
            return this;
        }

        public Builder masterName(String masterName) {
            this.masterName = masterName;
            return this;
        }

        public Builder sentinelNodes(List<String> nodes) {
            this.sentinelNodes = nodes;
            return this;
        }

        public Builder clusterNodes(List<String> nodes) {
            this.clusterNodes = nodes;
            return this;
        }

        public Builder maxRedirects(int maxRedirects) {
            this.maxRedirects = maxRedirects;
            return this;
        }

        public Builder ssl(boolean enabled, String trustStorePath, String password) {
            this.sslEnabled = enabled;
            this.sslTrustStorePath = trustStorePath;
            this.sslTrustStorePassword = password;
            return this;
        }

        public RedisClientConfig build() {
            return new RedisClientConfig(this);
        }
    }

}
