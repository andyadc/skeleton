package com.andyadc.skeleton.redis.serializer;

import java.nio.charset.StandardCharsets;

/**
 * Simple string serializer.
 */
public class StringSerializer implements RedisSerializer<String> {

    @Override
    public byte[] serialize(String object) {
        if (object == null) {
            return null;
        }
        return object.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String deserialize(byte[] bytes, Class<String> clazz) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public String serializeToString(String object) {
        return object;
    }

    @Override
    public String deserializeFromString(String str, Class<String> clazz) {
        return str;
    }

}
