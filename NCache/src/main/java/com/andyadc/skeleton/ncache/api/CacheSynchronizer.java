package com.andyadc.skeleton.ncache.api;

import java.util.Collection;

/**
 * Interface for cache synchronization across nodes.
 */
public interface CacheSynchronizer {

    /**
     * Publish invalidation message.
     */
    void publishInvalidation(String cacheName, Object key);

    /**
     * Publish bulk invalidation.
     */
    void publishBulkInvalidation(String cacheName, Collection<?> keys);

    /**
     * Publish clear message.
     */
    void publishClear(String cacheName);

    /**
     * Subscribe to invalidation messages.
     */
    void subscribe(InvalidationListener listener);

    /**
     * Unsubscribe listener.
     */
    void unsubscribe(InvalidationListener listener);

    /**
     * Start synchronizer.
     */
    void start();

    /**
     * Stop synchronizer.
     */
    void stop();

    /**
     * Invalidation listener interface.
     */
    interface InvalidationListener {
        void onInvalidate(String cacheName, Object key);

        void onBulkInvalidate(String cacheName, Collection<?> keys);

        void onClear(String cacheName);
    }

}
