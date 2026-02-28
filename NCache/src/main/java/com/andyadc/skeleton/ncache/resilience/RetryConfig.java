package com.andyadc.skeleton.ncache.resilience;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

/**
 * Retry configuration.
 */
public class RetryConfig {

    private final int maxAttempts;
    private final Duration initialDelay;
    private final Duration maxDelay;
    private final double multiplier;
    private final Set<Class<? extends Throwable>> retryableExceptions;
    private final Set<Class<? extends Throwable>> ignoreExceptions;

    private RetryConfig(Builder builder) {
        this.maxAttempts = builder.maxAttempts;
        this.initialDelay = builder.initialDelay;
        this.maxDelay = builder.maxDelay;
        this.multiplier = builder.multiplier;
        this.retryableExceptions = builder.retryableExceptions;
        this.ignoreExceptions = builder.ignoreExceptions;
    }

    public static RetryConfig defaultConfig() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public int getMaxAttempts() {
        return maxAttempts;
    }

    public Duration getInitialDelay() {
        return initialDelay;
    }

    public Duration getMaxDelay() {
        return maxDelay;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public Set<Class<? extends Throwable>> getRetryableExceptions() {
        return retryableExceptions;
    }

    public Set<Class<? extends Throwable>> getIgnoreExceptions() {
        return ignoreExceptions;
    }

    public static class Builder {
        private int maxAttempts = 3;
        private Duration initialDelay = Duration.ofMillis(100);
        private Duration maxDelay = Duration.ofSeconds(1);
        private double multiplier = 2.0;
        private final Set<Class<? extends Throwable>> retryableExceptions = new HashSet<>();
        private final Set<Class<? extends Throwable>> ignoreExceptions = new HashSet<>();

        public Builder maxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
            return this;
        }

        public Builder initialDelay(Duration delay) {
            this.initialDelay = delay;
            return this;
        }

        public Builder maxDelay(Duration delay) {
            this.maxDelay = delay;
            return this;
        }

        public Builder multiplier(double multiplier) {
            this.multiplier = multiplier;
            return this;
        }

        public Builder retryOn(Class<? extends Throwable> exception) {
            this.retryableExceptions.add(exception);
            return this;
        }

        public Builder ignoreException(Class<? extends Throwable> exception) {
            this.ignoreExceptions.add(exception);
            return this;
        }

        public RetryConfig build() {
            return new RetryConfig(this);
        }
    }

}
