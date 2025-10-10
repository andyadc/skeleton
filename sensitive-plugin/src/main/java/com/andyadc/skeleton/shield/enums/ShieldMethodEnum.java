package com.andyadc.skeleton.shield.enums;

import com.andyadc.skeleton.shield.Shield;
import com.andyadc.skeleton.shield.shields.*;

/**
 * 属性屏蔽注解 方法
 */
public enum ShieldMethodEnum {

    ALL(AllShield.class),
    CLEAN(CleanShield.class),
    BANK_CARD(BankCardShield.class),
    EMAIL(EmailShield.class),
    ID_CARD(IDCardShield.class),
    PHONE(PhoneShield.class),
    SECRET(SecretShield.class),
    BEGIN3_END4(Begin3End4Shield.class),
    CUSTOMIZE(CustomizeShield.class);

    private final Class<? extends Shield> clazz;

    ShieldMethodEnum(Class<? extends Shield> clazz) {
        this.clazz = clazz;
    }

    public Class<? extends Shield> getClazz() {
        return clazz;
    }

}
