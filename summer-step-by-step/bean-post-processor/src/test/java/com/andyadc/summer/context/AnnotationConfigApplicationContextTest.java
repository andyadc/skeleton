package com.andyadc.summer.context;

import com.andyadc.scan.ScanApplication;
import com.andyadc.scan.proxy.InjectProxyOnConstructorBean;
import com.andyadc.scan.proxy.InjectProxyOnPropertyBean;
import com.andyadc.scan.proxy.OriginBean;
import com.andyadc.scan.proxy.SecondProxyBean;
import com.andyadc.summer.io.PropertyResolver;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class AnnotationConfigApplicationContextTest {

    @Test
    public void testProxy() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ScanApplication.class, createPropertyResolver());
        // test proxy:
        OriginBean proxy = ctx.getBean(OriginBean.class);

        assertSame(SecondProxyBean.class, proxy.getClass());
        assertEquals("Scan App", proxy.getName());
        assertEquals("v1.0", proxy.getVersion());
        // make sure proxy.field is not injected:
        assertNull(proxy.name);
        assertNull(proxy.version);

        // other beans are injected proxy instance:
        InjectProxyOnConstructorBean inject1 = ctx.getBean(InjectProxyOnConstructorBean.class);
        InjectProxyOnPropertyBean inject2 = ctx.getBean(InjectProxyOnPropertyBean.class);
        assertSame(proxy, inject1.injected);
        assertSame(proxy, inject2.injected);

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
