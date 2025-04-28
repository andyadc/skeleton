package com.andyadc.summer.utils;

import com.andyadc.summer.annotation.Component;
import com.andyadc.summer.annotation.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AnnoUtilsTest {

    @Test
    public void noComponent() {
        assertNull(ClassUtils.findAnnotation(Simple.class, Component.class));
    }

    @Test
    public void simpleComponent() {
        assertNotNull(ClassUtils.findAnnotation(SimpleComponent.class, Component.class));
        assertEquals("simpleComponent", ClassUtils.getBeanName(SimpleComponent.class));
    }

    @Test
    public void simpleComponentWithName() {
        assertNotNull(ClassUtils.findAnnotation(SimpleComponentWithName.class, Component.class));
        assertEquals("simpleName", ClassUtils.getBeanName(SimpleComponentWithName.class));
    }
}

@Order(1)
class Simple {
}

@Component
class SimpleComponent {
}

@Component("simpleName")
class SimpleComponentWithName {
}
