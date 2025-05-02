package com.andyadc.summer.context;

import com.andyadc.scan.ScanApplication;
import com.andyadc.scan.convert.ValueConverterBean;
import com.andyadc.scan.custom.CustomAnnotationBean;
import com.andyadc.scan.init.AnnotationInitBean;
import com.andyadc.scan.init.SpecifyInitBean;
import com.andyadc.scan.multi.PersonBean;
import com.andyadc.scan.multi.TeacherBean;
import com.andyadc.scan.nested.OuterBean;
import com.andyadc.scan.primary.DogBean;
import com.andyadc.summer.io.PropertyResolver;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class AnnotationConfigApplicationContextTest {

    @Test
    public void testInitMethod() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ScanApplication.class, createPropertyResolver());
        // test @PostConstruct:
        AnnotationInitBean bean1 = ctx.getBean(AnnotationInitBean.class);
        SpecifyInitBean bean2 = ctx.getBean(SpecifyInitBean.class);
        assertEquals("Scan App / v1.0", bean1.appName);
        assertEquals("Scan App / v1.0", bean2.appName);
    }

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

    @Test
    public void testConverter() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ScanApplication.class, createPropertyResolver());
        ValueConverterBean bean = ctx.getBean(ValueConverterBean.class);

        assertNotNull(bean.injectedBoolean);
        assertTrue(bean.injectedBooleanPrimitive);
        assertTrue(bean.injectedBoolean);

        assertNotNull(bean.injectedByte);
        assertEquals((byte) 123, bean.injectedByte);
        assertEquals((byte) 123, bean.injectedBytePrimitive);

        assertNotNull(bean.injectedShort);
        assertEquals((short) 12345, bean.injectedShort);
        assertEquals((short) 12345, bean.injectedShortPrimitive);

        assertNotNull(bean.injectedInteger);
        assertEquals(1234567, bean.injectedInteger);
        assertEquals(1234567, bean.injectedIntPrimitive);

        assertNotNull(bean.injectedLong);
        assertEquals(123456789_000L, bean.injectedLong);
        assertEquals(123456789_000L, bean.injectedLongPrimitive);

        assertNotNull(bean.injectedFloat);
        assertEquals(12345.6789F, bean.injectedFloat, 0.0001F);
        assertEquals(12345.6789F, bean.injectedFloatPrimitive, 0.0001F);

        assertNotNull(bean.injectedDouble);
        assertEquals(123456789.87654321, bean.injectedDouble, 0.0000001);
        assertEquals(123456789.87654321, bean.injectedDoublePrimitive, 0.0000001);

        assertEquals(LocalDate.parse("2025-01-03"), bean.injectedLocalDate);
        assertEquals(LocalTime.parse("20:45:01"), bean.injectedLocalTime);
        assertEquals(LocalDateTime.parse("2025-01-03T20:45:01"), bean.injectedLocalDateTime);
        assertEquals(ZonedDateTime.parse("2025-01-03T20:45:01+08:00[Asia/Shanghai]"), bean.injectedZonedDateTime);
        assertEquals(Duration.parse("P2DT3H4M"), bean.injectedDuration);
        assertEquals(ZoneId.of("Asia/Shanghai"), bean.injectedZoneId);
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
        ps.put("convert.localdate", "2025-01-03");
        ps.put("convert.localtime", "20:45:01");
        ps.put("convert.localdatetime", "2025-01-03T20:45:01");
        ps.put("convert.zoneddatetime", "2025-01-03T20:45:01+08:00[Asia/Shanghai]");
        ps.put("convert.duration", "P2DT3H4M");
        ps.put("convert.zoneid", "Asia/Shanghai");
        return new PropertyResolver(ps);
    }

}
