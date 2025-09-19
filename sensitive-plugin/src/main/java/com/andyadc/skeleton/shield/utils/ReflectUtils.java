package com.andyadc.skeleton.shield.utils;

import com.andyadc.skeleton.shield.Constants;
import com.andyadc.skeleton.shield.Shield;
import com.andyadc.skeleton.shield.annotation.ShieldClass;
import com.andyadc.skeleton.shield.annotation.ShieldField;
import com.andyadc.skeleton.shield.factory.ShieldFactory;
import com.andyadc.skeleton.shield.model.FieldDefinition;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectUtils {

    /**
     * 默认返回值
     */
    private static final Object DEFAULT_VALUE = null;
    /**
     * 字符串空值返回值
     */
    private static final String RETURN_NULL = "null";
    /**
     * 左中括号
     */
    private static final String LEFT_SQUARE_BRACKET = "[";
    /**
     * 右中括号
     */
    private static final String RIGHT_SQUARE_BRACKET = "]";
    /**
     * 属性值 分隔符
     */
    private static final String PROPERTY_SEPARATING_CHARACTER = "=";
    /**
     * 分号，用于分割field
     */
    private static final String SEMICOLON = ";";
    /**
     * 逗号，用于分割Collection或者数组
     */
    private static final String COMMA = ",";
    /**
     * 默认嵌套层数
     */
    private static final int DEFAULT_NESTED_LAYER_NUM = 1;
    /**
     * 缓存
     */
    private static final Map<String, List<FieldDefinition>> FIELD_DEFINITION_MAP = new ConcurrentHashMap<>();

    private ReflectUtils() {
    }

    /**
     * 脱敏数据
     */
    public static String reflectToLogStringByFields(Object object) {
        return reflectToLogStringByFields(object, DEFAULT_NESTED_LAYER_NUM);
    }

    /**
     * 脱敏数据
     */
    public static String reflectToLogStringByFields(Object object, int nestedLayerNum) {
        if (object == null) {
            return null;
        }
        // 原生类返回toString()
        if (object instanceof Byte
                || object instanceof Short
                || object instanceof Integer
                || object instanceof Long
                || object instanceof Float
                || object instanceof Double
                || object instanceof Character
                || object instanceof Boolean
                || object instanceof String
                || object instanceof Date) {
            return object.toString();
        }
        ShieldClass shieldClass = object.getClass().getAnnotation(ShieldClass.class);
        if (shieldClass == null) {
            return object.toString();
        }
        // 拼接各个字段toString()方法
        StringBuilder result = new StringBuilder();
        result.append(object.getClass().getSimpleName());
        result.append(LEFT_SQUARE_BRACKET);
        List<FieldDefinition> fieldDefinitions = getFieldDefinitions(object);
        for (FieldDefinition fieldDefinition : fieldDefinitions) {
            ShieldField shieldField = fieldDefinition.getShieldField();
            int modifiers = fieldDefinition.getField().getModifiers();
            if (Modifier.isStatic(modifiers)) {
                continue;
            }
            try {
                String fieldName = fieldDefinition.getField().getName();
                Object propertyVal = DEFAULT_VALUE;
                if (fieldDefinition.getFieldAccessMethod() != null) {
                    try {
                        propertyVal = fieldDefinition.getFieldAccessMethod().invoke(object);
                    } catch (IllegalAccessException e) {
                        fieldDefinition.setFieldAccessMethod(null);
                    }
                }

                if (!isClean(shieldField)) {
                    String shieldValue = shield(shieldField, propertyVal, nestedLayerNum);
                    result.append(fieldName).append(PROPERTY_SEPARATING_CHARACTER).append(shieldValue).append(SEMICOLON);
                }
            } catch (Exception e) {
                // ignore
            }
        }
        result.append(RIGHT_SQUARE_BRACKET);
        return result.toString();
    }

    private static boolean isClean(ShieldField shieldField) {
        if (shieldField == null) {
            return false;
        }
        Shield shield = ShieldFactory.getShield(shieldField.method());
        return shield.isClean();
    }

    private static String shield(ShieldField shieldField, Object value, int nestedLayerNum) {
        StringBuilder builder = new StringBuilder();
        if (value instanceof Collection<?> values) {
            for (Object item : values) {
                builder.append(shieldSingleObject(shieldField, item, nestedLayerNum));
                builder.append(COMMA);
            }
            return builder.toString();
        }
        if (value instanceof Map && shieldField == null) {
            builder.append(value);
            return builder.toString();
        }
        if (value instanceof Object[] array) {
            for (Object item : array) {
                builder.append(shieldSingleObject(shieldField, item, nestedLayerNum));
                builder.append(COMMA);
            }
            return builder.toString();
        }
        return shieldSingleObject(shieldField, value, nestedLayerNum);
    }

    private static String shieldSingleObject(ShieldField shieldField, Object value, int nestedLayerNum) {
        if (shieldField == null) {
            if (value == null) {
                return RETURN_NULL;
            }
            if (nestedLayerNum <= 0) {
                return value.toString();
            }
            nestedLayerNum--;
            return reflectToLogStringByFields(value, nestedLayerNum);
        }

        // 获取过滤器，必须在内嵌对象方法的后面，否则会导致解析内嵌对象失败
        Shield shield = ShieldFactory.getShield(shieldField.method());
        if (shield == null) {
            return RETURN_NULL;
        }
        return shield.sensitive(value, shieldField.addition());
    }

    /**
     * 获取字段定义列表
     */
    private static List<FieldDefinition> getFieldDefinitions(Object object) {
        Class<?> clazz = object.getClass();
        String className = clazz.getName();
        List<FieldDefinition> fieldDefinitions = FIELD_DEFINITION_MAP.get(className);
        if (fieldDefinitions == null) {
            fieldDefinitions = new ArrayList<>();
            appendField(fieldDefinitions, clazz);
            FIELD_DEFINITION_MAP.put(className, fieldDefinitions);
        }
        return fieldDefinitions;
    }

    /**
     * 递归获取所有字段
     */
    private static void appendField(List<FieldDefinition> fieldDefinitions, Class<?> clazz) {
        if (Object.class.getName().equals(clazz.getName())) {
            return;
        }
        for (Field field : clazz.getDeclaredFields()) {
            ShieldField shieldField = field.getAnnotation(ShieldField.class);
            Method fieldAccessMethod = getFieldAccessMethod(field, clazz);
            fieldDefinitions.add(new FieldDefinition(field, shieldField, fieldAccessMethod));
        }
        appendField(fieldDefinitions, clazz.getSuperclass());
    }

    private static Method getFieldAccessMethod(Field field, Class<?> clazz) {
        String methodName = getMethodName(field);
        Method fieldAccessMethod = getMethodByName(clazz, methodName);
        // 兼容boolean变量定义了getXxx的方法
        if (fieldAccessMethod == null && (boolean.class.equals(field.getType()) || Boolean.class.equals(field.getType()))) {
            String fieldName = field.getName();
            methodName = Constants.INVOKE_GET_PREFIX + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            fieldAccessMethod = getMethodByName(clazz, methodName);
        }
        return fieldAccessMethod;
    }

    /**
     * 根据字段找到对应的getXxx或者isXxx方法
     */
    private static String getMethodName(Field field) {
        String fieldName = field.getName();
        Class<?> fieldClazz = field.getType();
        String methodName;
        if (boolean.class.equals(fieldClazz) || Boolean.class.equals(fieldClazz)) {
            if (fieldName.startsWith(Constants.INVOKE_IS_PREFIX)) {
                methodName = fieldName;
            } else {
                methodName = Constants.INVOKE_IS_PREFIX + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            }
        } else {
            methodName = Constants.INVOKE_GET_PREFIX + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        }
        return methodName;
    }

    /**
     * 根据名称获取对应的method
     */
    private static Method getMethodByName(Class<?> clazz, String methodName) {
        try {
            return clazz.getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
            // 遍历父类
            Class<?> supperClass = clazz.getSuperclass();
            if (supperClass != null && !Object.class.getName().equals(supperClass.getName())) {
                return getMethodByName(supperClass, methodName);
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

}
