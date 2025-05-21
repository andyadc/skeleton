package com.andyadc.summer.aop.after;

import com.andyadc.summer.annotation.Around;
import com.andyadc.summer.annotation.Component;

@Component
@Around("politeInvocationHandler")
public class GreetingBean {

    public String hello(String name) {
        return "Hello, " + name + ".";
    }

    public String morning(String name) {
        return "Morning, " + name + ".";
    }

}
