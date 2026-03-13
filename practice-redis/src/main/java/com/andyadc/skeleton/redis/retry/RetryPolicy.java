package com.andyadc.skeleton.redis.retry;

import com.andyadc.skeleton.redis.exception.RedisException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

/**
 * Configurable retry policy for Redis operations.
 */
public class RetryPolicy {

    private static final Logger logger = LoggerFactory.getLogger(RetryPolicy.class);

    private final int maxAttempts;
    private final Duration initialDelay;
    private final Duration maxDelay;
    private final double backoffMultiplier;
    private final Set<Class<? extends Exception>> retryableExceptions;
    private final Predicate<Exception> retryPredicate;

    private RetryPolicy(Builder builder) {
        this.maxAttempts = builder.maxAttempts;
        this.initialDelay = builder.initialDelay;
        this.maxDelay = builder.maxDelay;
        this.backoffMultiplier = builder.backoffMultiplier;
        this.retryableExceptions = builder.retryableExceptions;
        this.retryPredicate = builder.retryPredicate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static RetryPolicy defaultPolicy() {
        return builder().build();
    }

    /**
     * Executes an operation with retry logic.
     */
    public <T> T execute(Callable<T> operation, String operationName) {
        Exception lastException = null;
        Duration currentDelay = initialDelay;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return operation.call();
            } catch (Exception e) {
                lastException = e;

                if (!shouldRetry(e) || attempt >= maxAttempts) {
                    break;
                }

                logger.warn("Operation '{}' failed on attempt {}/{}, retrying in {}ms",
                        operationName, attempt, maxAttempts, currentDelay.toMillis());

                sleep(currentDelay);
                currentDelay = calculateNextDelay(currentDelay);
            }
        }

        throw new RedisException("Operation '" + operationName +
                "' failed after " + maxAttempts + " attempts", lastException);
    }

    /**
     * Executes a void operation with retry logic.
     */
    public void execute(Runnable operation, String operationName) {
        execute(() -> {
            operation.run();
            return null;
        }, operationName);
    }

    private boolean shouldRetry(Exception e) {
        if (retryPredicate != null && retryPredicate.test(e)) {
            return true;
        }

        if (retryableExceptions.isEmpty()) {
            return true; // Retry all exceptions by default
        }

        return retryableExceptions.stream()
                .anyMatch(clazz -> clazz.isInstance(e));
    }

    private Duration calculateNextDelay(Duration currentDelay) {
        long nextDelayMs = (long) (currentDelay.toMillis() * backoffMultiplier);
        return Duration.ofMillis(Math.min(nextDelayMs, maxDelay.toMillis()));
    }

    private void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RedisException("Retry interrupted", e);
        }
    }

    public static class Builder {
        private int maxAttempts = 3;
        private Duration initialDelay = Duration.ofMillis(100);
        private Duration maxDelay = Duration.ofSeconds(5);
        private double backoffMultiplier = 2.0;
        private Set<Class<? extends Exception>> retryableExceptions = Set.of();
        private Predicate<Exception> retryPredicate;

        public Builder maxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
            return this;
        }

        public Builder initialDelay(Duration initialDelay) {
            this.initialDelay = initialDelay;
            return this;
        }

        public Builder maxDelay(Duration maxDelay) {
            this.maxDelay = maxDelay;
            return this;
        }

        public Builder backoffMultiplier(double multiplier) {
            this.backoffMultiplier = multiplier;
            return this;
        }

        @SafeVarargs
        public final Builder retryOn(Class<? extends Exception>... exceptions) {
            this.retryableExceptions = Set.of(exceptions);
            return this;
        }

        public Builder retryIf(Predicate<Exception> predicate) {
            this.retryPredicate = predicate;
            return this;
        }

        public RetryPolicy build() {
            return new RetryPolicy(this);
        }
    }

}
