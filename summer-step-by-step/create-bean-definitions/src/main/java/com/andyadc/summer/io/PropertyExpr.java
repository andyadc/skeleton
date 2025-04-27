package com.andyadc.summer.io;

import java.util.Objects;

/**
 * <code>
 * public record PropertyExpr(String key, String defaultValue) {}
 * </code>
 */
public class PropertyExpr {

    private final String key;
    private final String defaultValue;

    public PropertyExpr(String key, String defaultValue) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String key() {
        return key;
    }

    public String defaultValue() {
        return defaultValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyExpr resource = (PropertyExpr) o;
        return Objects.equals(key, resource.key) && Objects.equals(defaultValue, resource.defaultValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, defaultValue);
    }

    @Override
    public String toString() {
        return "PropertyExpr{" +
                "key=" + key +
                ", defaultValue=" + defaultValue +
                '}';
    }

}
