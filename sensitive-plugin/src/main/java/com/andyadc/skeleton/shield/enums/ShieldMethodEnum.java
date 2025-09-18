package com.andyadc.skeleton.shield.enums;

import com.andyadc.skeleton.shield.Shield;
import com.andyadc.skeleton.shield.shields.AllShield;
import com.andyadc.skeleton.shield.shields.CleanShield;

/**
 * 属性屏蔽注解 方法
 */
public enum ShieldMethodEnum {

    ALL(AllShield.class),
    CLEAN(CleanShield.class),
    ;

    private final Class<? extends Shield> clazz;

    ShieldMethodEnum(Class<? extends Shield> clazz) {
        this.clazz = clazz;
    }

    public Class<? extends Shield> getClazz() {
        return clazz;
    }

}
