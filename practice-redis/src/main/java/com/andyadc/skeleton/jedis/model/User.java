package com.andyadc.skeleton.jedis.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public record User(
        String id,
        String username,
        String email,
        String displayName,
        UserStatus status,
        Instant createdAt,
        Instant lastLoginAt,
        Map<String, String> metadata
) implements Serializable {

    public User {
        Objects.requireNonNull(id, "User ID cannot be null");
        Objects.requireNonNull(username, "Username cannot be null");
        Objects.requireNonNull(email, "Email cannot be null");
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (status == null) {
            status = UserStatus.ACTIVE;
        }
        if (metadata == null) {
            metadata = Map.of();
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public enum UserStatus {
        ACTIVE, INACTIVE, SUSPENDED, PENDING_VERIFICATION
    }

    public static class Builder {
        private String id;
        private String username;
        private String email;
        private String displayName;
        private UserStatus status = UserStatus.ACTIVE;
        private Instant createdAt = Instant.now();
        private Instant lastLoginAt;
        private Map<String, String> metadata = Map.of();

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder status(UserStatus status) {
            this.status = status;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder lastLoginAt(Instant lastLoginAt) {
            this.lastLoginAt = lastLoginAt;
            return this;
        }

        public Builder metadata(Map<String, String> metadata) {
            this.metadata = metadata;
            return this;
        }

        public User build() {
            return new User(id, username, email, displayName, status,
                    createdAt, lastLoginAt, metadata);
        }
    }
}
