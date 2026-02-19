package com.andyadc.skeleton.ncache.resilience;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Circuit breaker implementation for protecting L2 cache operations.
 */
public class CircuitBreaker {

    private static final Logger logger = LoggerFactory.getLogger(CircuitBreaker.class);
    private final String name;
    private final CircuitBreakerConfig config;
    private final AtomicReference<State> state = new AtomicReference<>(State.CLOSED);
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger halfOpenCalls = new AtomicInteger(0);
    private volatile Instant lastFailureTime;
    private volatile Instant openedAt;
    public CircuitBreaker(String name, CircuitBreakerConfig config) {
        this.name = name;
        this.config = config;
    }

    public <T> T execute(Supplier<T> supplier) {
        if (!allowRequest()) {
            throw new CircuitBreakerOpenException("Circuit breaker is open: " + name);
        }

        try {
            T result = supplier.get();
            recordSuccess();
            return result;
        } catch (Exception e) {
            recordFailure();
            throw e;
        }
    }

    public <T> T executeWithFallback(Supplier<T> supplier, Supplier<T> fallback) {
        try {
            return execute(supplier);
        } catch (CircuitBreakerOpenException e) {
            logger.debug("Circuit breaker open, using fallback");
            return fallback.get();
        } catch (Exception e) {
            logger.debug("Execution failed, using fallback", e);
            return fallback.get();
        }
    }

    private boolean allowRequest() {
        State currentState = state.get();

        switch (currentState) {
            case CLOSED:
                return true;

            case OPEN:
                if (shouldAttemptReset()) {
                    if (state.compareAndSet(State.OPEN, State.HALF_OPEN)) {
                        halfOpenCalls.set(0);
                        logger.info("Circuit breaker {} transitioned to HALF_OPEN", name);
                    }
                    return true;
                }
                return false;

            case HALF_OPEN:
                return halfOpenCalls.incrementAndGet() <= config.getHalfOpenMaxCalls();

            default:
                return false;
        }
    }

    private boolean shouldAttemptReset() {
        return openedAt != null &&
                Duration.between(openedAt, Instant.now())
                        .compareTo(config.getOpenStateDuration()) > 0;
    }

    private void recordSuccess() {
        State currentState = state.get();

        if (currentState == State.HALF_OPEN) {
            int successes = successCount.incrementAndGet();
            if (successes >= config.getHalfOpenMaxCalls()) {
                reset();
            }
        } else if (currentState == State.CLOSED) {
            // Sliding window reset on success
            failureCount.updateAndGet(count -> Math.max(0, count - 1));
        }
    }

    private void recordFailure() {
        lastFailureTime = Instant.now();
        State currentState = state.get();

        if (currentState == State.HALF_OPEN) {
            trip();
        } else if (currentState == State.CLOSED) {
            int failures = failureCount.incrementAndGet();
            if (failures >= config.getFailureThreshold()) {
                trip();
            }
        }
    }

    private void trip() {
        if (state.compareAndSet(State.CLOSED, State.OPEN) ||
                state.compareAndSet(State.HALF_OPEN, State.OPEN)) {
            openedAt = Instant.now();
            logger.warn("Circuit breaker {} tripped to OPEN state", name);
        }
    }

    private void reset() {
        state.set(State.CLOSED);
        failureCount.set(0);
        successCount.set(0);
        halfOpenCalls.set(0);
        openedAt = null;
        logger.info("Circuit breaker {} reset to CLOSED state", name);
    }

    public State getState() {
        return state.get();
    }

    public int getFailureCount() {
        return failureCount.get();
    }

    public enum State {
        CLOSED,      // Normal operation
        OPEN,        // Failing, rejecting requests
        HALF_OPEN    // Testing if service recovered
    }

    public static class CircuitBreakerOpenException extends RuntimeException {
        public CircuitBreakerOpenException(String message) {
            super(message);
        }
    }
}
