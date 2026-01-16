package com.andyadc.skeleton.ncache.api;

import java.util.Collection;
import java.util.Map;

/**
 * Cache writer for write-through/write-behind caching.
 */
public interface CacheWriter<K, V> {

    /**
     * Write single entry.
     */
    void write(K key, V value) throws Exception;

    /**
     * Write multiple entries.
     */
    default void writeAll(Map<K, V> entries) throws Exception {
        for (Map.Entry<K, V> entry : entries.entrySet()) {
            write(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Delete single entry.
     */
    void delete(K key) throws Exception;

    /**
     * Delete multiple entries.
     */
    default void deleteAll(Collection<K> keys) throws Exception {
        for (K key : keys) {
            delete(key);
        }
    }

}
