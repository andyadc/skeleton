package com.andyadc.summer.context;

import com.andyadc.scan.ScanApplication;
import com.andyadc.scan.custom.CustomAnnotationBean;
import com.andyadc.scan.multi.PersonBean;
import com.andyadc.scan.multi.TeacherBean;
import com.andyadc.scan.nested.OuterBean;
import com.andyadc.scan.primary.DogBean;
import com.andyadc.summer.io.PropertyResolver;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AnnotationConfigApplicationContextTest {

    @Test
    public void testCustomAnnotation() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ScanApplication.class, createPropertyResolver());
        assertNotNull(ctx.getBean(CustomAnnotationBean.class));
        assertNotNull(ctx.getBean("customAnnotation"));
    }

    @Test
    public void testNested() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ScanApplication.class, createPropertyResolver());
        assertNotNull(ctx.getBean(OuterBean.class));
        assertNotNull(ctx.getBean(OuterBean.NestedBean.class));
    }

    @Test
    public void testPrimary() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ScanApplication.class, createPropertyResolver());
        PersonBean person = ctx.getBean(PersonBean.class);
        assertEquals(TeacherBean.class, person.getClass());
        DogBean dog = ctx.getBean(DogBean.class);
        assertEquals("Samoyed", dog.type);
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
        ps.put("convert.localdate", "2025-01-02");
        ps.put("convert.localtime", "20:45:01");
        ps.put("convert.localdatetime", "2023-01-02T20:45:01");
        ps.put("convert.zoneddatetime", "2025-01-02T20:45:01+08:00[Asia/Shanghai]");
        ps.put("convert.duration", "P2DT3H4M");
        ps.put("convert.zoneid", "Asia/Shanghai");
        return new PropertyResolver(ps);
    }

}
