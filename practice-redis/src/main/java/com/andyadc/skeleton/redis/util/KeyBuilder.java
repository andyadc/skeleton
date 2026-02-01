package com.andyadc.skeleton.redis.util;

import java.util.StringJoiner;

/**
 * Utility class for building Redis keys with consistent naming conventions.
 */
public class KeyBuilder {

    private static final String DEFAULT_SEPARATOR = ":";
    private final StringJoiner joiner;

    private KeyBuilder(String separator) {
        this.joiner = new StringJoiner(separator);
    }

    public static KeyBuilder create() {
        return new KeyBuilder(DEFAULT_SEPARATOR);
    }

    public static KeyBuilder create(String separator) {
        return new KeyBuilder(separator);
    }

    public static String userKey(Object userId) {
        return create().add("user").add(userId).build();
    }

    public static String sessionKey(String sessionId) {
        return create().add("session").add(sessionId).build();
    }

    public static String cacheKey(String namespace, Object... parts) {
        KeyBuilder builder = create().add("cache").add(namespace);
        for (Object part : parts) {
            builder.add(part);
        }
        return builder.build();
    }

    public static String lockKey(String resource) {
        return create().add("lock").add(resource).build();
    }

    public static String rateLimitKey(String identifier) {
        return create().add("ratelimit").add(identifier).build();
    }

    // Convenience static methods

    public static String queueKey(String queueName) {
        return create().add("queue").add(queueName).build();
    }

    public static String counterKey(String name) {
        return create().add("counter").add(name).build();
    }

    public KeyBuilder add(String part) {
        if (part != null && !part.isEmpty()) {
            joiner.add(part);
        }
        return this;
    }

    public KeyBuilder add(Object part) {
        if (part != null) {
            joiner.add(String.valueOf(part));
        }
        return this;
    }

    public KeyBuilder addAll(String... parts) {
        for (String part : parts) {
            add(part);
        }
        return this;
    }

    public String build() {
        return joiner.toString();
    }

    @Override
    public String toString() {
        return build();
    }

}
