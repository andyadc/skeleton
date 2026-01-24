package com.andyadc.skeleton.jedis.util;

import java.util.StringJoiner;

public final class RedisKeyUtil {

    private static final String SEPARATOR = ":";
    private static final String APP_PREFIX = "app";

    private RedisKeyUtil() {
    }

    // User keys
    public static String userKey(String userId) {
        return buildKey("user", userId);
    }

    public static String userByEmailKey(String email) {
        return buildKey("user", "email", email);
    }

    public static String userByUsernameKey(String username) {
        return buildKey("user", "username", username);
    }

    // Session keys
    public static String sessionKey(String sessionId) {
        return buildKey("session", sessionId);
    }

    public static String userSessionsKey(String userId) {
        return buildKey("user", userId, "sessions");
    }

    // Cache keys
    public static String cacheKey(String namespace, String key) {
        return buildKey("cache", namespace, key);
    }

    // Rate limiting keys
    public static String rateLimitKey(String identifier, String action) {
        return buildKey("ratelimit", action, identifier);
    }

    // Lock keys
    public static String lockKey(String resource) {
        return buildKey("lock", resource);
    }

    // Leaderboard keys
    public static String leaderboardKey(String name) {
        return buildKey("leaderboard", name);
    }

    // Stream keys
    public static String streamKey(String name) {
        return buildKey("stream", name);
    }

    // Channel keys
    public static String channelKey(String channel) {
        return buildKey("channel", channel);
    }

    // Counter keys
    public static String counterKey(String name) {
        return buildKey("counter", name);
    }

    public static String buildKey(String... parts) {
        StringJoiner joiner = new StringJoiner(SEPARATOR);
        joiner.add(APP_PREFIX);
        for (String part : parts) {
            if (part != null && !part.isEmpty()) {
                joiner.add(part);
            }
        }
        return joiner.toString();
    }

    public static String pattern(String... parts) {
        return buildKey(parts) + SEPARATOR + "*";
    }

}
