package com.andyadc.summer.aop.around;


import com.andyadc.summer.annotation.Around;
import com.andyadc.summer.annotation.Component;
import com.andyadc.summer.annotation.Value;

@Component
@Around("aroundInvocationHandler")
public class OriginBean {

    @Value("${customer.name}")
    public String name;

    @Polite
    public String hello() {
        return "Hello, " + name + ".";
    }

    public String morning() {
        return "Morning, " + name + ".";
    }
}
