package com.andyadc.summer.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.time.LocalTime;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class PropertyResolverTest {

    @Test
    public void propertyValue() {
        Properties props = new Properties();
        props.setProperty("app.title", "Autumn Framework");
        props.setProperty("app.version", "v1.0");
        props.setProperty("jdbc.url", "jdbc:mysql://localhost:3306/autumn");
        props.setProperty("jdbc.username", "andy");
        props.setProperty("jdbc.password", "123456");
        props.setProperty("jdbc.pool-size", "10");
        props.setProperty("jdbc.auto-commit", "true");
        props.setProperty("scheduler.started-at", "2024-12-25T21:45:01");
        props.setProperty("scheduler.backup-at", "13:05:10");
        props.setProperty("scheduler.cleanup", "P3DT9H21M");

        PropertyResolver resolver = new PropertyResolver(props);
        assertEquals("Autumn Framework", resolver.getProperty("app.title"));
        assertEquals(Boolean.TRUE, resolver.getProperty("jdbc.auto-commit", Boolean.class));
        assertTrue(resolver.getProperty("jdbc.auto-commit", boolean.class));
        assertEquals(LocalTime.parse("13:05:10"), resolver.getProperty("scheduler.backup-at", LocalTime.class));
    }

    @Test
    public void requiredProperty() {
        Properties props = new Properties();
        props.setProperty("app.title", "Summer Framework");
        props.setProperty("app.version", "v1.0");

        PropertyResolver resolver = new PropertyResolver(props);
        assertThrows(NullPointerException.class, () -> resolver.getRequiredProperty("not.exist"));
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    public void propertyHolder() {
        String home = System.getenv("HOME");
        System.out.println("env HOME=" + home);

        Properties props = new Properties();
        props.setProperty("app.title", "Summer Framework");

        PropertyResolver resolver = new PropertyResolver(props);
        assertEquals("Summer Framework", resolver.getProperty("${app.title}"));
        assertThrows(NullPointerException.class, () -> resolver.getProperty("${app.version}"));
        assertEquals("v1.0", resolver.getProperty("${app.version:v1.0}"));
        assertEquals(1, resolver.getProperty("${app.version:1}", int.class));
        assertThrows(NumberFormatException.class, () -> resolver.getProperty("${app.version:x}", int.class));
        assertEquals(home, resolver.getProperty("${app.path:${HOME}}"));
        assertEquals(home, resolver.getProperty("${app.path:${app.home:${HOME}}}"));
        assertEquals("/not-exist", resolver.getProperty("${app.path:${app.home:${ENV_NOT_EXIST:/not-exist}}}"));
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    public void propertyHolderOnWin() {
        String os = System.getenv("OS");
        System.out.println("env OS=" + os);

        Properties props = new Properties();
        PropertyResolver resolver = new PropertyResolver(props);

        assertEquals(os, resolver.getProperty("${app.os:${OS}}"));
    }

}
