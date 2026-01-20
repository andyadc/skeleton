package com.andyadc.skeleton.ncache.serialization;

/**
 * Serialization interface for cache values.
 */
public interface Serializer<T> {

    /**
     * Serialize object to byte array.
     */
    byte[] serialize(T object) throws SerializationException;

    /**
     * Deserialize byte array to object.
     */
    T deserialize(byte[] bytes) throws SerializationException;

    /**
     * Get content type identifier.
     */
    default String getContentType() {
        return "application/octet-stream";
    }

}
