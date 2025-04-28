package com.andyadc.summer.context;

import com.andyadc.imported.LocalDateConfiguration;
import com.andyadc.imported.ZonedDateConfiguration;
import com.andyadc.scan.ScanApplication;
import com.andyadc.scan.custom.CustomAnnotationBean;
import com.andyadc.scan.multi.PersonBean;
import com.andyadc.scan.multi.StudentBean;
import com.andyadc.scan.multi.TeacherBean;
import com.andyadc.scan.nested.OuterBean;
import com.andyadc.scan.primary.DogBean;
import com.andyadc.summer.io.PropertyResolver;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class AnnotationConfigApplicationContextTest {

    @Test
    public void testAnnotationConfigApplicationContext() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ScanApplication.class, createPropertyResolver());

        // @CustomAnnotation:
        assertNotNull(context.findBeanDefinition("customAnnotation"));
        assertNotNull(context.findBeanDefinition(CustomAnnotationBean.class));

        // nested:
        assertNotNull(context.findBeanDefinition(OuterBean.class));
        assertNotNull(context.findBeanDefinition(OuterBean.NestedBean.class));

        assertEquals(2, context.findBeanDefinitions(DogBean.class).size());

        List<BeanDefinition> definitionList = context.findBeanDefinitions(PersonBean.class);
        BeanDefinition studentDef = context.findBeanDefinition(StudentBean.class);
        BeanDefinition teacherDef = context.findBeanDefinition(TeacherBean.class);

        assertSame(definitionList.get(0), studentDef);
        assertSame(definitionList.get(1), teacherDef);

        assertNotNull(context.findBeanDefinition(ZonedDateConfiguration.class));
        assertNotNull(context.findBeanDefinition("startZonedDateTime"));

        assertNotNull(context.findBeanDefinition(LocalDateConfiguration.class));
        assertNotNull(context.findBeanDefinition("startLocalDate"));
        assertNotNull(context.findBeanDefinition("startLocalDateTime"));
    }

    PropertyResolver createPropertyResolver() {
        Properties ps = new Properties();
        ps.put("app.title", "Scan App");
        ps.put("app.version", "v1.0");
        ps.put("jdbc.url", "jdbc:hsqldb:file:testdb.tmp");
        ps.put("jdbc.username", "user");
        ps.put("jdbc.password", "pwd");
        ps.put("convert.boolean", "true");
        ps.put("convert.byte", "123");
        ps.put("convert.short", "12345");
        ps.put("convert.integer", "1234567");
        ps.put("convert.long", "123456789000");
        ps.put("convert.float", "12345.6789");
        ps.put("convert.double", "123456789.87654321");
        ps.put("convert.localdate", "2025-01-01");
        ps.put("convert.localtime", "20:45:01");
        ps.put("convert.localdatetime", "2023-01-01T20:45:01");
        ps.put("convert.zoneddatetime", "2025-01-01T20:45:01+08:00[Asia/Shanghai]");
        ps.put("convert.duration", "P2DT3H4M");
        ps.put("convert.zoneid", "Asia/Shanghai");
        return new PropertyResolver(ps);
    }

}
