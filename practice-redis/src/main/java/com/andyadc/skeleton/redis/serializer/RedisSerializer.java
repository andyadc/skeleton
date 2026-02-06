package com.andyadc.skeleton.redis.serializer;

/**
 * Interface for Redis value serialization/deserialization.
 */
public interface RedisSerializer<T> {

    /**
     * Serializes an object to byte array.
     */
    byte[] serialize(T object);

    /**
     * Deserializes a byte array to an object.
     */
    T deserialize(byte[] bytes, Class<T> clazz);

    /**
     * Serializes an object to String.
     */
    String serializeToString(T object);

    /**
     * Deserializes a String to an object.
     */
    T deserializeFromString(String str, Class<T> clazz);
}
