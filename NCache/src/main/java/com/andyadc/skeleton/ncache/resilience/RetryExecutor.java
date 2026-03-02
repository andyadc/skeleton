package com.andyadc.skeleton.ncache.resilience;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Retry executor with exponential backoff.
 */
public class RetryExecutor {

    private static final Logger logger = LoggerFactory.getLogger(RetryExecutor.class);

    private final RetryConfig config;

    public RetryExecutor(RetryConfig config) {
        this.config = config;
    }

    public <T> T execute(Callable<T> callable) {
        int attempt = 0;
        Duration delay = config.getInitialDelay();
        Exception lastException = null;

        while (attempt < config.getMaxAttempts()) {
            attempt++;

            try {
                return callable.call();
            } catch (Exception e) {
                lastException = e;

                if (shouldIgnore(e)) {
                    throw new RuntimeException(e);
                }

                if (!shouldRetry(e) || attempt >= config.getMaxAttempts()) {
                    throw new RuntimeException("Retry exhausted after " + attempt + " attempts", e);
                }

                logger.debug("Attempt {} failed, retrying in {}ms", attempt, delay.toMillis(), e);

                try {
                    // Add jitter to prevent thundering herd
                    long jitter = ThreadLocalRandom.current().nextLong(delay.toMillis() / 4);
                    Thread.sleep(delay.toMillis() + jitter);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                }

                // Exponential backoff
                delay = Duration.ofMillis(
                        Math.min(
                                (long) (delay.toMillis() * config.getMultiplier()),
                                config.getMaxDelay().toMillis()
                        )
                );
            }
        }

        throw new RuntimeException("Retry exhausted", lastException);
    }

    private boolean shouldRetry(Exception e) {
        if (config.getRetryableExceptions().isEmpty()) {
            // Retry all exceptions by default
            return true;
        }

        for (Class<? extends Throwable> retryable : config.getRetryableExceptions()) {
            if (retryable.isInstance(e) ||
                    (e.getCause() != null && retryable.isInstance(e.getCause()))) {
                return true;
            }
        }

        return false;
    }

    private boolean shouldIgnore(Exception e) {
        for (Class<? extends Throwable> ignored : config.getIgnoreExceptions()) {
            if (ignored.isInstance(e) ||
                    (e.getCause() != null && ignored.isInstance(e.getCause()))) {
                return true;
            }
        }

        return false;
    }

}
