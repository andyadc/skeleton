package com.andyadc.skeleton.jedis.health;

import com.andyadc.skeleton.jedis.connection.RedisConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class RedisHealthCheck implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(RedisHealthCheck.class);

    private final RedisConnectionManager connectionManager;
    private final ScheduledExecutorService scheduler;
    private final AtomicReference<HealthStatus> lastStatus;

    public RedisHealthCheck(RedisConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "redis-health-check");
            t.setDaemon(true);
            return t;
        });
        this.lastStatus = new AtomicReference<>(HealthStatus.unknown());

        // Initial check
        performHealthCheck();

        // Schedule periodic checks
        scheduler.scheduleWithFixedDelay(
                this::performHealthCheck,
                30, 30, TimeUnit.SECONDS
        );
    }

    private void performHealthCheck() {
        Instant checkTime = Instant.now();
        try {
            long startTime = System.nanoTime();

            // Ping check
            boolean pingOk = connectionManager.isHealthy();

            long latencyNanos = System.nanoTime() - startTime;
            Duration latency = Duration.ofNanos(latencyNanos);

            // Get pool stats
            RedisConnectionManager.PoolStats poolStats = connectionManager.getPoolStats();

            // Get server info
            Map<String, String> serverInfo = getServerInfo();

            HealthStatus status = new HealthStatus(
                    pingOk ? Status.UP : Status.DOWN,
                    checkTime,
                    latency,
                    poolStats,
                    serverInfo,
                    null
            );

            lastStatus.set(status);

            if (pingOk) {
                logger.debug("Redis health check passed (latency: {}ms)",
                        latency.toMillis());
            } else {
                logger.warn("Redis health check failed - ping unsuccessful");
            }

        } catch (Exception e) {
            HealthStatus status = new HealthStatus(
                    Status.DOWN,
                    checkTime,
                    null,
                    null,
                    null,
                    e.getMessage()
            );
            lastStatus.set(status);
            logger.error("Redis health check failed", e);
        }
    }

    private Map<String, String> getServerInfo() {
        try {
            return connectionManager.execute(jedis -> {
                String info = jedis.info("server");
                Map<String, String> result = new java.util.HashMap<>();

                for (String line : info.split("\n")) {
                    if (line.contains(":")) {
                        String[] parts = line.split(":", 2);
                        result.put(parts[0].trim(), parts[1].trim());
                    }
                }

                return Map.of(
                        "redis_version", result.getOrDefault("redis_version", "unknown"),
                        "uptime_in_seconds", result.getOrDefault("uptime_in_seconds", "0"),
                        "connected_clients", getInfoValue("connected_clients"),
                        "used_memory_human", getInfoValue("used_memory_human")
                );
            });
        } catch (Exception e) {
            logger.warn("Failed to get server info", e);
            return Map.of();
        }
    }

    private String getInfoValue(String key) {
        try {
            return connectionManager.execute(jedis -> {
                String section = key.startsWith("used_memory") ? "memory" : "clients";
                String info = jedis.info(section);

                for (String line : info.split("\n")) {
                    if (line.startsWith(key + ":")) {
                        return line.split(":")[1].trim();
                    }
                }
                return "unknown";
            });
        } catch (Exception e) {
            return "unknown";
        }
    }

    public HealthStatus getStatus() {
        return lastStatus.get();
    }

    public boolean isHealthy() {
        HealthStatus status = lastStatus.get();
        return status != null && status.status() == Status.UP;
    }

    public void checkNow() {
        performHealthCheck();
    }

    @Override
    public void close() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public enum Status {
        UP, DOWN, UNKNOWN
    }

    public record HealthStatus(
            Status status,
            Instant checkTime,
            Duration latency,
            RedisConnectionManager.PoolStats poolStats,
            Map<String, String> serverInfo,
            String error
    ) {
        public static HealthStatus unknown() {
            return new HealthStatus(Status.UNKNOWN, null, null, null, null, null);
        }

        public boolean isUp() {
            return status == Status.UP;
        }
    }

}
