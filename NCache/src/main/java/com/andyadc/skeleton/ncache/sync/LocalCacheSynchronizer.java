package com.andyadc.skeleton.ncache.sync;

import com.andyadc.skeleton.ncache.api.CacheSynchronizer;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Local-only synchronizer for single-node deployments or testing.
 */
public class LocalCacheSynchronizer implements CacheSynchronizer {

    private final Set<InvalidationListener> listeners = new CopyOnWriteArraySet<>();

    @Override
    public void publishInvalidation(String cacheName, Object key) {
        // No-op for local synchronizer
    }

    @Override
    public void publishBulkInvalidation(String cacheName, Collection<?> keys) {
        // No-op for local synchronizer
    }

    @Override
    public void publishClear(String cacheName) {
        // No-op for local synchronizer
    }

    @Override
    public void subscribe(InvalidationListener listener) {
        listeners.add(listener);
    }

    @Override
    public void unsubscribe(InvalidationListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void start() {
        // No-op
    }

    @Override
    public void stop() {
        listeners.clear();
    }

}
