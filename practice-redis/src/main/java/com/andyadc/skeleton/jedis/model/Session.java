package com.andyadc.skeleton.jedis.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

public record Session(
        String sessionId,
        String userId,
        String ipAddress,
        String userAgent,
        Instant createdAt,
        Instant lastAccessedAt,
        Instant expiresAt,
        Map<String, Object> attributes
) implements Serializable {

    public Session {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (lastAccessedAt == null) {
            lastAccessedAt = Instant.now();
        }
        if (attributes == null) {
            attributes = Map.of();
        }
    }

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    public Session touch() {
        return new Session(
                sessionId, userId, ipAddress, userAgent,
                createdAt, Instant.now(), expiresAt, attributes
        );
    }
}
