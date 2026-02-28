package com.andyadc.skeleton.ncache.resilience;

import java.time.Duration;

/**
 * Circuit breaker configuration.
 */
public class CircuitBreakerConfig {

    private final int failureThreshold;
    private final Duration openStateDuration;
    private final int halfOpenMaxCalls;
    private final double failureRateThreshold;
    private final int minimumNumberOfCalls;
    private final Duration slowCallDurationThreshold;
    private final double slowCallRateThreshold;

    private CircuitBreakerConfig(Builder builder) {
        this.failureThreshold = builder.failureThreshold;
        this.openStateDuration = builder.openStateDuration;
        this.halfOpenMaxCalls = builder.halfOpenMaxCalls;
        this.failureRateThreshold = builder.failureRateThreshold;
        this.minimumNumberOfCalls = builder.minimumNumberOfCalls;
        this.slowCallDurationThreshold = builder.slowCallDurationThreshold;
        this.slowCallRateThreshold = builder.slowCallRateThreshold;
    }

    public static CircuitBreakerConfig defaultConfig() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public int getFailureThreshold() {
        return failureThreshold;
    }

    public Duration getOpenStateDuration() {
        return openStateDuration;
    }

    public int getHalfOpenMaxCalls() {
        return halfOpenMaxCalls;
    }

    public double getFailureRateThreshold() {
        return failureRateThreshold;
    }

    public int getMinimumNumberOfCalls() {
        return minimumNumberOfCalls;
    }

    public Duration getSlowCallDurationThreshold() {
        return slowCallDurationThreshold;
    }

    public double getSlowCallRateThreshold() {
        return slowCallRateThreshold;
    }

    public static class Builder {
        private int failureThreshold = 5;
        private Duration openStateDuration = Duration.ofSeconds(30);
        private int halfOpenMaxCalls = 3;
        private double failureRateThreshold = 50.0;
        private int minimumNumberOfCalls = 10;
        private Duration slowCallDurationThreshold = Duration.ofSeconds(2);
        private double slowCallRateThreshold = 80.0;

        public Builder failureThreshold(int threshold) {
            this.failureThreshold = threshold;
            return this;
        }

        public Builder openStateDuration(Duration duration) {
            this.openStateDuration = duration;
            return this;
        }

        public Builder halfOpenMaxCalls(int maxCalls) {
            this.halfOpenMaxCalls = maxCalls;
            return this;
        }

        public Builder failureRateThreshold(double threshold) {
            this.failureRateThreshold = threshold;
            return this;
        }

        public Builder minimumNumberOfCalls(int minCalls) {
            this.minimumNumberOfCalls = minCalls;
            return this;
        }

        public Builder slowCallDurationThreshold(Duration threshold) {
            this.slowCallDurationThreshold = threshold;
            return this;
        }

        public Builder slowCallRateThreshold(double threshold) {
            this.slowCallRateThreshold = threshold;
            return this;
        }

        public CircuitBreakerConfig build() {
            return new CircuitBreakerConfig(this);
        }
    }

}
