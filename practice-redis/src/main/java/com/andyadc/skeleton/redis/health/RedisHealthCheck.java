package com.andyadc.skeleton.redis.health;

import com.andyadc.skeleton.redis.client.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis health check utility.
 */
public class RedisHealthCheck {

    private static final Logger logger = LoggerFactory.getLogger(RedisHealthCheck.class);

    private final RedisClient redisClient;

    public RedisHealthCheck(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    /**
     * Performs a comprehensive health check.
     */
    public HealthStatus check() {
        Instant start = Instant.now();
        Map<String, Object> details = new HashMap<>();

        try {
            // Ping check
            boolean pingSuccess = redisClient.ping();
            details.put("ping", pingSuccess);

            if (!pingSuccess) {
                return new HealthStatus(Status.DOWN, "Ping failed", details,
                        Duration.between(start, Instant.now()));
            }

            // Connection check
            details.put("connected", redisClient.isConnected());
            details.put("clientType", redisClient.getClientType());

            // Get server info
            String serverInfo = redisClient.info("server");
            parseServerInfo(serverInfo, details);

            // Get memory info
            String memoryInfo = redisClient.info("memory");
            parseMemoryInfo(memoryInfo, details);

            // Get stats
            String statsInfo = redisClient.info("stats");
            parseStatsInfo(statsInfo, details);

            // Get clients info
            String clientsInfo = redisClient.info("clients");
            parseClientsInfo(clientsInfo, details);

            // DB size
            details.put("dbSize", redisClient.dbSize());

            Duration responseTime = Duration.between(start, Instant.now());
            details.put("responseTimeMs", responseTime.toMillis());

            logger.debug("Health check completed in {}ms", responseTime.toMillis());

            return new HealthStatus(Status.UP, "Redis is healthy", details, responseTime);

        } catch (Exception e) {
            logger.error("Health check failed", e);
            details.put("error", e.getMessage());
            return new HealthStatus(Status.DOWN, "Health check failed: " + e.getMessage(),
                    details, Duration.between(start, Instant.now()));
        }
    }

    /**
     * Performs a simple connectivity check.
     */
    public boolean isHealthy() {
        try {
            return redisClient.ping();
        } catch (Exception e) {
            return false;
        }
    }

    private void parseServerInfo(String info, Map<String, Object> details) {
        if (info == null) return;

        for (String line : info.split("\n")) {
            if (line.startsWith("redis_version:")) {
                details.put("redisVersion", line.split(":")[1].trim());
            } else if (line.startsWith("uptime_in_seconds:")) {
                details.put("uptimeSeconds", Long.parseLong(line.split(":")[1].trim()));
            } else if (line.startsWith("tcp_port:")) {
                details.put("port", Integer.parseInt(line.split(":")[1].trim()));
            }
        }
    }

    private void parseMemoryInfo(String info, Map<String, Object> details) {
        if (info == null) return;

        for (String line : info.split("\n")) {
            if (line.startsWith("used_memory_human:")) {
                details.put("usedMemory", line.split(":")[1].trim());
            } else if (line.startsWith("used_memory_peak_human:")) {
                details.put("peakMemory", line.split(":")[1].trim());
            } else if (line.startsWith("maxmemory_human:")) {
                details.put("maxMemory", line.split(":")[1].trim());
            }
        }
    }

    private void parseStatsInfo(String info, Map<String, Object> details) {
        if (info == null) return;

        for (String line : info.split("\n")) {
            if (line.startsWith("total_connections_received:")) {
                details.put("totalConnections", Long.parseLong(line.split(":")[1].trim()));
            } else if (line.startsWith("total_commands_processed:")) {
                details.put("totalCommands", Long.parseLong(line.split(":")[1].trim()));
            } else if (line.startsWith("instantaneous_ops_per_sec:")) {
                details.put("opsPerSecond", Long.parseLong(line.split(":")[1].trim()));
            }
        }
    }

    private void parseClientsInfo(String info, Map<String, Object> details) {
        if (info == null) return;

        for (String line : info.split("\n")) {
            if (line.startsWith("connected_clients:")) {
                details.put("connectedClients", Integer.parseInt(line.split(":")[1].trim()));
            } else if (line.startsWith("blocked_clients:")) {
                details.put("blockedClients", Integer.parseInt(line.split(":")[1].trim()));
            }
        }
    }

    /**
     * Health status enum.
     */
    public enum Status {
        UP, DOWN, DEGRADED
    }

    /**
     * Health status record.
     */
    public record HealthStatus(
            Status status,
            String message,
            Map<String, Object> details,
            Duration responseTime) {

        public boolean isHealthy() {
            return status == Status.UP;
        }
    }

}
