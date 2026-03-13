package com.andyadc.skeleton.redis.serializer;


import com.andyadc.skeleton.redis.exception.RedisException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * JSON serializer using Jackson.
 */
public class JsonSerializer<T> implements RedisSerializer<T> {

    private final ObjectMapper objectMapper;

    public JsonSerializer() {
        this.objectMapper = createObjectMapper();
    }

    public JsonSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

    @Override
    public byte[] serialize(T object) {
        if (object == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw new RedisException("Failed to serialize object", e);
        }
    }

    @Override
    public T deserialize(byte[] bytes, Class<T> clazz) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return objectMapper.readValue(bytes, clazz);
        } catch (Exception e) {
            throw new RedisException("Failed to deserialize object", e);
        }
    }

    @Override
    public String serializeToString(T object) {
        if (object == null) {
            return null;
        }
        if (object instanceof String) {
            return (String) object;
        }
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RedisException("Failed to serialize object to string", e);
        }
    }

    @Override
    public T deserializeFromString(String str, Class<T> clazz) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        if (clazz == String.class) {
            @SuppressWarnings("unchecked")
            T result = (T) str;
            return result;
        }
        try {
            return objectMapper.readValue(str, clazz);
        } catch (Exception e) {
            throw new RedisException("Failed to deserialize string to object", e);
        }
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
