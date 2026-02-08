package com.andyadc.skeleton.redis.config;

import com.andyadc.skeleton.redis.client.RedisClient;
import com.andyadc.skeleton.redis.client.RedisClientFactory;
import com.andyadc.skeleton.redis.serializer.JsonSerializer;
import com.andyadc.skeleton.redis.serializer.RedisSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Central Redis configuration and component initialization.
 */
public class RedisConfig {

    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    private final RedisProperties properties;
    private final RedisClient redisClient;
    private final RedisSerializer<?> serializer;

    public RedisConfig() {
        this(RedisProperties.load());
    }

    public RedisConfig(RedisProperties properties) {
        this.properties = properties;
        this.serializer = new JsonSerializer<>();
        this.redisClient = createClient();

        logger.info("Redis configuration initialized with client type: {}",
                properties.getClientType());
    }

    private RedisClient createClient() {
        return RedisClientFactory.create(properties);
    }

    public RedisProperties getProperties() {
        return properties;
    }

    public RedisClient getRedisClient() {
        return redisClient;
    }

    public RedisSerializer<?> getSerializer() {
        return serializer;
    }

    public void shutdown() {
        if (redisClient != null) {
            redisClient.close();
            logger.info("Redis client shut down");
        }
    }
}
