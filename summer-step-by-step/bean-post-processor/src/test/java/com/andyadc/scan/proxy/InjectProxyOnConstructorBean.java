package com.andyadc.scan.proxy;

import com.andyadc.summer.annotation.Autowired;
import com.andyadc.summer.annotation.Component;

@Component
public class InjectProxyOnConstructorBean {

    public final OriginBean injected;

    public InjectProxyOnConstructorBean(@Autowired OriginBean injected) {
        this.injected = injected;
    }
}
