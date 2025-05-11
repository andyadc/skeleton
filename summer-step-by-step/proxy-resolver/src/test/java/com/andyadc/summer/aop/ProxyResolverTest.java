package com.andyadc.summer.aop;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProxyResolverTest {

	@Test
	public void testProxyResovler() {
		OriginBean origin = new OriginBean();
		origin.name = "andy";

		assertEquals("Hello, andy.", origin.hello());

		// create proxy:
		OriginBean proxy = new ProxyResolver().createProxy(origin, new PoliteInvocationHandler());

		// Proxy类名,类似OriginBean$ByteBuddy$0opp76Eo:
		System.out.println(proxy.getClass().getName());

		// proxy class, not origin class:
		assertNotSame(OriginBean.class, proxy.getClass());
		// proxy.name is null:
		assertNull(proxy.name);

		// 带@Polite:
		assertEquals("Hello, andy!", proxy.hello());
		// 不带@Polite:
		assertEquals("Morning, andy.", proxy.morning());
	}

}
