package com.andyadc.skeleton.shield.annotation;

import com.andyadc.skeleton.shield.enums.ShieldMethodEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 屏蔽字段
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ShieldField {

    /**
     * 屏蔽方式
     */
    ShieldMethodEnum method() default ShieldMethodEnum.ALL;

    /**
     * 附加条件值
     */
    String[] addition() default {};

}
