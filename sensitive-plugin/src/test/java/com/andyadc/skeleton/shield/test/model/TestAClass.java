package com.andyadc.skeleton.shield.test.model;

import com.andyadc.skeleton.shield.annotation.ShieldClass;
import com.andyadc.skeleton.shield.annotation.ShieldField;
import com.andyadc.skeleton.shield.enums.ShieldMethodEnum;

import java.util.List;
import java.util.Map;

@ShieldClass
public class TestAClass {

    @ShieldField(method = ShieldMethodEnum.ALL)
    private String a;

    @ShieldField(method = ShieldMethodEnum.CUSTOMIZE, addition = {"3", "4", "0"})
    private String b;

    @ShieldField(method = ShieldMethodEnum.CUSTOMIZE, addition = {"3", "4", "0"})
    private Integer bint;

    @ShieldField(method = ShieldMethodEnum.CUSTOMIZE, addition = {"3", "4", "0"})
    private Long blong;

    @ShieldField(method = ShieldMethodEnum.CLEAN)
    private String c;

    @ShieldField(method = ShieldMethodEnum.CUSTOMIZE, addition = {"3", "4", "0"})
    private List<String> d;

    @ShieldField(method = ShieldMethodEnum.CUSTOMIZE, addition = {"3", "4", "0"})
    private Map<String, String> e;

    private TestABClass testABClass;

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public Integer getBint() {
        return bint;
    }

    public void setBint(Integer bint) {
        this.bint = bint;
    }

    public Long getBlong() {
        return blong;
    }

    public void setBlong(Long blong) {
        this.blong = blong;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public List<String> getD() {
        return d;
    }

    public void setD(List<String> d) {
        this.d = d;
    }

    public Map<String, String> getE() {
        return e;
    }

    public void setE(Map<String, String> e) {
        this.e = e;
    }

    public TestABClass getTestABClass() {
        return testABClass;
    }

    public void setTestABClass(TestABClass testABClass) {
        this.testABClass = testABClass;
    }

    @Override
    public String toString() {
        return "TestAClass{" +
                "a=" + a +
                ", b=" + b +
                ", bint=" + bint +
                ", blong=" + blong +
                ", c=" + c +
                ", d=" + d +
                ", e=" + e +
                ", testABClass=" + testABClass +
                '}';
    }

}

