package com.andyadc.summer.io;

import java.util.Objects;

/**
 * <code>
 * public record Resource(String path, String name) {}
 * </code>
 */
public class Resource {

    private final String path;
    private final String name;

    public Resource(String path, String name) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.path = path;
        this.name = name;
    }

    public String path() {
        return path;
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resource resource = (Resource) o;
        return Objects.equals(path, resource.path) && Objects.equals(name, resource.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, name);
    }

    @Override
    public String toString() {
        return "Resource{" +
                "path=" + path +
                ", name=" + name +
                '}';
    }

}
