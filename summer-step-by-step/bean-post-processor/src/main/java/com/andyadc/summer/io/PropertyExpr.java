package com.andyadc.summer.io;

public record PropertyExpr(String key, String defaultValue) {

    public PropertyExpr {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
    }


    @Override
    public String toString() {
        return "PropertyExpr{" +
                "key=" + key +
                ", defaultValue=" + defaultValue +
                '}';
    }

}
