package com.andyadc.scan.proxy;

import com.andyadc.summer.annotation.Autowired;
import com.andyadc.summer.annotation.Component;

@Component
public class InjectProxyOnPropertyBean {

    @Autowired
    public OriginBean injected;
}
