package com.andyadc.skeleton.jedis.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

public record Message(
        String id,
        String channel,
        String type,
        String payload,
        String senderId,
        Instant timestamp,
        Map<String, String> headers
) implements Serializable {

    public Message {
        if (timestamp == null) {
            timestamp = Instant.now();
        }
        if (headers == null) {
            headers = Map.of();
        }
    }
}