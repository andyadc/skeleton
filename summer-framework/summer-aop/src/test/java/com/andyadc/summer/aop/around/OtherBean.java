package com.andyadc.summer.aop.around;

import com.andyadc.summer.annotation.Autowired;
import com.andyadc.summer.annotation.Component;
import com.andyadc.summer.annotation.Order;

@Order(0)
@Component
public class OtherBean {

    public OriginBean origin;

    public OtherBean(@Autowired OriginBean origin) {
        this.origin = origin;
    }
}
