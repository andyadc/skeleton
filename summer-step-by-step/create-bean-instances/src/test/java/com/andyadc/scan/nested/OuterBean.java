package com.andyadc.scan.nested;

import com.andyadc.summer.annotation.Component;

@Component
public class OuterBean {

    @Component
    public static class NestedBean {

    }
}
