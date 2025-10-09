package com.andyadc.skeleton.shield.shields;

import com.andyadc.skeleton.shield.utils.SensitiveDataUtils;

/**
 * 身份证脱敏：前1后1
 */
public class IDCardShield extends AbstractShield {

    @Override
    protected String doSensitive(Object fieldValue, String[] addition) {
        return SensitiveDataUtils.customizeHide(fieldValue.toString(), 1, 1, 0);
    }

}
