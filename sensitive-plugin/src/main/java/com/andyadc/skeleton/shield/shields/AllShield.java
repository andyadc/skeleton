package com.andyadc.skeleton.shield.shields;

/**
 * 全脱敏，对于非String非空对象，返回空字符串""，对于null或者空字符串，返回对象本身
 */
public class AllShield extends AbstractShield {

    private static final String ASTERISK = "******";

    @Override
    protected String handleNull() {
        return ASTERISK;
    }

    @Override
    protected String handleEmpty() {
        return ASTERISK;
    }

    @Override
    protected String doSensitive(Object fieldValue, String[] addition) {
        return ASTERISK;
    }

}
