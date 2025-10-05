package com.andyadc.skeleton.shield.shields;

import com.andyadc.skeleton.shield.utils.SensitiveDataUtils;

/**
 * 卡号脱敏，保留后四位
 */
public class BackCardShield extends AbstractShield {

    @Override
    protected String doSensitive(Object fieldValue, String[] addition) {
        return SensitiveDataUtils.customizeHide(fieldValue.toString(), 0, 4, 0);
    }

}
