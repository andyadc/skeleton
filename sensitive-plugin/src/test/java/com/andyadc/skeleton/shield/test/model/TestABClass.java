package com.andyadc.skeleton.shield.test.model;

import com.andyadc.skeleton.shield.annotation.ShieldClass;
import com.andyadc.skeleton.shield.annotation.ShieldField;
import com.andyadc.skeleton.shield.enums.ShieldMethodEnum;

@ShieldClass
public class TestABClass {

    @ShieldField(method = ShieldMethodEnum.CUSTOMIZE, addition = {"3", "4", "0"})
    private String a;

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

}
