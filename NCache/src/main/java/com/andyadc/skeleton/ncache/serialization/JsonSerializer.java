package com.andyadc.skeleton.ncache.serialization;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

/**
 * JSON serialization using Jackson.
 */
public class JsonSerializer<T> implements Serializer<T> {

    private final ObjectMapper objectMapper;
    private final Class<T> type;

    public JsonSerializer(Class<T> type) {
        this(type, createDefaultObjectMapper());
    }

    public JsonSerializer(Class<T> type, ObjectMapper objectMapper) {
        this.type = type;
        this.objectMapper = objectMapper;
    }

    private static ObjectMapper createDefaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    /**
     * Create a type-aware serializer that includes type information.
     */
    public static <T> JsonSerializer<T> typeAware(Class<T> type) {
        ObjectMapper mapper = createDefaultObjectMapper();
        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        return new JsonSerializer<>(type, mapper);
    }

    @Override
    public byte[] serialize(T object) throws SerializationException {
        if (object == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Failed to serialize to JSON", e);
        }
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        try {
            return objectMapper.readValue(bytes, type);
        } catch (IOException e) {
            throw new SerializationException("Failed to deserialize from JSON", e);
        }
    }

    @Override
    public String getContentType() {
        return "application/json";
    }

}
