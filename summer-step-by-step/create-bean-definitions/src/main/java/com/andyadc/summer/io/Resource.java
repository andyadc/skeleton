package com.andyadc.summer.io;

public record Resource(String path, String name) {

    public Resource {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
    }

    @Override
    public String toString() {
        return "Resource{" +
                "path=" + path +
                ", name=" + name +
                '}';
    }

}
