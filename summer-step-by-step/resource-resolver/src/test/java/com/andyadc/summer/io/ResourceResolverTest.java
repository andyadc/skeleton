package com.andyadc.summer.io;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResourceResolverTest {

    @Test
    public void scanClass() {
        String pkg = "com.andyadc";
        ResourceResolver resolver = new ResourceResolver(pkg);
        List<String> classes = resolver.scan(resource -> {
            String name = resource.name();
            if (name.endsWith(".class")) {
                return name.substring(0, name.length() - 6).replace("/", ".").replace("\\", ".");
            }
            return null;
        });

        Collections.sort(classes);
        System.out.println(classes);

        String[] listClasses = new String[]{
                // list of some scan classes:
                "com.andyadc.scan.MapTest", //
                "com.andyadc.scan.file.IOTest", //
        };
        for (String clazz : listClasses) {
            assertTrue(classes.contains(clazz));
        }
    }

    @Test
    public void scanJar() {
        String pkg = Logger.class.getPackage().getName();
        System.out.println(pkg);
        ResourceResolver resolver = new ResourceResolver(pkg);
        List<String> classes = resolver.scan(resource -> {
            String name = resource.name();
            if (name.endsWith(".class")) {
                return name.substring(0, name.length() - 6).replace("/", ".").replace("\\", ".");
            }
            return null;
        });

        System.out.println(classes);

        // classes in jar:
        assertTrue(classes.contains(Logger.class.getName()));
        assertTrue(classes.contains(LoggerFactory.class.getName()));
        assertTrue(classes.contains(MDC.class.getName()));
    }

    @Test
    public void scanTxt() {
        String pkg = "com.andyadc.summer";
        ResourceResolver resolver = new ResourceResolver(pkg);
        List<String> classes = resolver.scan(resource -> {
            String name = resource.name();
            if (name.endsWith(".txt")) {
                return name.replace("\\", "/");
            }
            return null;
        });

        System.out.println(classes);
        Collections.sort(classes);

        assertArrayEquals(new String[]{
                // txt files:
                "com/andyadc/summer/resource.txt", //
                "com/andyadc/summer/resource1/resource1.txt", //
                "com/andyadc/summer/resource1/resource2/resource2.txt", //
        }, classes.toArray());
    }

}
