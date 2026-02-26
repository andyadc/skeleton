package com.andyadc.skeleton.ncache.impl;

import java.io.Serial;
import java.io.Serializable;

/**
 * Cache entry wrapper with metadata.
 */
public class CacheEntry<V> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final V value;
    private final long createTime;
    private final long expireTime;
    private final boolean nullValue;

    private CacheEntry(V value, long createTime, long expireTime, boolean nullValue) {
        this.value = value;
        this.createTime = createTime;
        this.expireTime = expireTime;
        this.nullValue = nullValue;
    }

    public static <V> CacheEntry<V> of(V value, long ttlMillis) {
        long now = System.currentTimeMillis();
        return new CacheEntry<>(value, now, now + ttlMillis, false);
    }

    public static <V> CacheEntry<V> nullEntry(long ttlMillis) {
        long now = System.currentTimeMillis();
        return new CacheEntry<>(null, now, now + ttlMillis, true);
    }

    public V getValue() {
        return value;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public boolean isNullValue() {
        return nullValue;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expireTime;
    }

    public long getTtlMillis() {
        return Math.max(0, expireTime - System.currentTimeMillis());
    }

}
