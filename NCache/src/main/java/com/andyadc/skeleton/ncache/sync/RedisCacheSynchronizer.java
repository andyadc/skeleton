package com.andyadc.skeleton.ncache.sync;

import com.andyadc.skeleton.ncache.api.CacheSynchronizer;
import com.andyadc.skeleton.ncache.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Redis-based cache synchronizer using Pub/Sub.
 */
public class RedisCacheSynchronizer implements CacheSynchronizer {

    private static final Logger logger = LoggerFactory.getLogger(RedisCacheSynchronizer.class);

    private static final String CHANNEL_PREFIX = "cache:sync:";
    private static final String INVALIDATE_CMD = "INV";
    private static final String BULK_INVALIDATE_CMD = "BINV";
    private static final String CLEAR_CMD = "CLR";
    private static final String DELIMITER = "|";

    private final JedisPool jedisPool;
    private final Serializer<Object> serializer;
    private final String instanceId;
    private final Set<InvalidationListener> listeners = new CopyOnWriteArraySet<>();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final ExecutorService subscriberExecutor;

    private volatile JedisPubSub pubSub;
    private volatile Thread subscriberThread;

    public RedisCacheSynchronizer(JedisPool jedisPool,
                                  Serializer<Object> serializer,
                                  String instanceId) {
        this.jedisPool = jedisPool;
        this.serializer = serializer;
        this.instanceId = instanceId;
        this.subscriberExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "cache-sync-subscriber");
            t.setDaemon(true);
            return t;
        });
    }

    @Override
    public void publishInvalidation(String cacheName, Object key) {
        publish(cacheName, INVALIDATE_CMD + DELIMITER + instanceId + DELIMITER +
                encodeKey(key));
    }

    @Override
    public void publishBulkInvalidation(String cacheName, Collection<?> keys) {
        StringBuilder sb = new StringBuilder();
        sb.append(BULK_INVALIDATE_CMD).append(DELIMITER).append(instanceId);
        for (Object key : keys) {
            sb.append(DELIMITER).append(encodeKey(key));
        }
        publish(cacheName, sb.toString());
    }

    @Override
    public void publishClear(String cacheName) {
        publish(cacheName, CLEAR_CMD + DELIMITER + instanceId);
    }

    private void publish(String cacheName, String message) {
        try (Jedis jedis = jedisPool.getResource()) {
            String channel = CHANNEL_PREFIX + cacheName;
            jedis.publish(channel, message);
            logger.debug("Published to channel {}: {}", channel, message);
        } catch (Exception e) {
            logger.error("Failed to publish cache synchronization message", e);
        }
    }

    @Override
    public void subscribe(InvalidationListener listener) {
        listeners.add(listener);
    }

    @Override
    public void unsubscribe(InvalidationListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void start() {
        if (running.compareAndSet(false, true)) {
            subscriberExecutor.submit(this::subscribeLoop);
            logger.info("Cache synchronizer started with instance ID: {}", instanceId);
        }
    }

    @Override
    public void stop() {
        if (running.compareAndSet(true, false)) {
            if (pubSub != null) {
                pubSub.unsubscribe();
            }
            subscriberExecutor.shutdown();
            logger.info("Cache synchronizer stopped");
        }
    }

    private void subscribeLoop() {
        while (running.get()) {
            try (Jedis jedis = jedisPool.getResource()) {
                pubSub = new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        handleMessage(channel, message);
                    }

                    @Override
                    public void onPMessage(String pattern, String channel, String message) {
                        handleMessage(channel, message);
                    }
                };

                // Subscribe to pattern for all cache channels
                jedis.psubscribe(pubSub, CHANNEL_PREFIX + "*");
            } catch (Exception e) {
                if (running.get()) {
                    logger.error("Subscriber connection lost, reconnecting...", e);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
    }

    private void handleMessage(String channel, String message) {
        try {
            String cacheName = channel.substring(CHANNEL_PREFIX.length());
            String[] parts = message.split("\\" + DELIMITER);

            if (parts.length < 2) {
                logger.warn("Invalid sync message format: {}", message);
                return;
            }

            String command = parts[0];
            String sourceInstanceId = parts[1];

            // Ignore messages from self
            if (instanceId.equals(sourceInstanceId)) {
                return;
            }

            switch (command) {
                case INVALIDATE_CMD -> {
                    if (parts.length >= 3) {
                        Object key = decodeKey(parts[2]);
                        notifyInvalidation(cacheName, key);
                    }
                }
                case BULK_INVALIDATE_CMD -> {
                    if (parts.length >= 3) {
                        Set<Object> keys = new java.util.HashSet<>();
                        for (int i = 2; i < parts.length; i++) {
                            keys.add(decodeKey(parts[i]));
                        }
                        notifyBulkInvalidation(cacheName, keys);
                    }
                }
                case CLEAR_CMD -> notifyClear(cacheName);
                default -> logger.warn("Unknown sync command: {}", command);
            }
        } catch (Exception e) {
            logger.error("Failed to process sync message", e);
        }
    }

    private void notifyInvalidation(String cacheName, Object key) {
        for (InvalidationListener listener : listeners) {
            try {
                listener.onInvalidate(cacheName, key);
            } catch (Exception e) {
                logger.error("Listener failed on invalidation", e);
            }
        }
    }

    private void notifyBulkInvalidation(String cacheName, Collection<?> keys) {
        for (InvalidationListener listener : listeners) {
            try {
                listener.onBulkInvalidate(cacheName, keys);
            } catch (Exception e) {
                logger.error("Listener failed on bulk invalidation", e);
            }
        }
    }

    private void notifyClear(String cacheName) {
        for (InvalidationListener listener : listeners) {
            try {
                listener.onClear(cacheName);
            } catch (Exception e) {
                logger.error("Listener failed on clear", e);
            }
        }
    }

    private String encodeKey(Object key) {
        byte[] bytes = serializer.serialize(key);
        return java.util.Base64.getEncoder().encodeToString(bytes);
    }

    private Object decodeKey(String encoded) {
        byte[] bytes = java.util.Base64.getDecoder().decode(encoded);
        return serializer.deserialize(bytes);
    }

}
