package com.andyadc.skeleton.redis.lock;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * Interface for distributed lock operations.
 */
public interface DistributedLock {

    /**
     * Acquires a lock with the given name.
     */
    boolean acquire(String lockName, Duration timeout);

    /**
     * Releases a lock with the given name.
     */
    boolean release(String lockName);

    /**
     * Tries to acquire a lock immediately.
     */
    boolean tryAcquire(String lockName);

    /**
     * Executes an action with a lock.
     */
    <T> T executeWithLock(String lockName, Duration timeout, Supplier<T> action);

    /**
     * Executes an action with a lock (void return).
     */
    void executeWithLock(String lockName, Duration timeout, Runnable action);

    /**
     * Checks if a lock is currently held.
     */
    boolean isLocked(String lockName);

    /**
     * Extends the lock TTL.
     */
    boolean extend(String lockName, Duration extension);
}
