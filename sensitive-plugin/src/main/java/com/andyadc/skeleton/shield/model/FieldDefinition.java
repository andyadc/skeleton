package com.andyadc.skeleton.shield.model;

import com.andyadc.skeleton.shield.annotation.ShieldField;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 字段定义信息
 */
public class FieldDefinition {

    /**
     * 字段定义
     */
    private Field field;

    /**
     * ShieldField注解
     */
    private ShieldField shieldField;

    /**
     * 访问Method
     */
    private Method fieldAccessMethod;

    public FieldDefinition() {
        super();
    }

    public FieldDefinition(final Field field, final ShieldField shieldField, final Method fieldAccessMethod) {
        this.field = field;
        this.shieldField = shieldField;
        this.fieldAccessMethod = fieldAccessMethod;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public ShieldField getShieldField() {
        return shieldField;
    }

    public void setShieldField(ShieldField shieldField) {
        this.shieldField = shieldField;
    }

    public Method getFieldAccessMethod() {
        return fieldAccessMethod;
    }

    public void setFieldAccessMethod(Method fieldAccessMethod) {
        this.fieldAccessMethod = fieldAccessMethod;
    }

}
