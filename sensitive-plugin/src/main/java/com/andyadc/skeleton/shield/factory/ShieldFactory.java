package com.andyadc.skeleton.shield.factory;

import com.andyadc.skeleton.shield.Shield;
import com.andyadc.skeleton.shield.enums.ShieldMethodEnum;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ShieldFactory {

    private static final Map<ShieldMethodEnum, Shield> SHIELD_METHOD_ENUM_SHIELD_MAP;

    static {
        ShieldMethodEnum[] values = ShieldMethodEnum.values();
        SHIELD_METHOD_ENUM_SHIELD_MAP = new HashMap<>(values.length);
        for (ShieldMethodEnum shieldMethodEnum : values) {
            Class<? extends Shield> clazz = shieldMethodEnum.getClazz();
            if (clazz == null) {
                continue;
            }
            try {
                Shield shield = clazz.getDeclaredConstructor().newInstance();
                SHIELD_METHOD_ENUM_SHIELD_MAP.put(shieldMethodEnum, shield);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private ShieldFactory() {
    }

    public static Shield getShield(ShieldMethodEnum shieldMethodEnum) {
        return SHIELD_METHOD_ENUM_SHIELD_MAP.get(shieldMethodEnum);
    }

}
