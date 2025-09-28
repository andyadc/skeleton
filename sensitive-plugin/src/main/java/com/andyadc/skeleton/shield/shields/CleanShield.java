package com.andyadc.skeleton.shield.shields;

/**
 * 什么都不输出，isClean为true，不显示字段
 */
public class CleanShield extends AbstractShield {

    @Override
    protected String doSensitive(Object fieldValue, String[] addition) {
        return handleEmpty();
    }

    @Override
    public boolean isClean() {
        return true;
    }

}
