package com.andyadc.skeleton.shield.shields;

import com.andyadc.skeleton.shield.utils.SensitiveDataUtils;

/**
 * 密钥脱敏, 前4*********后4
 */
public class SecretShield extends AbstractShield {

    @Override
    protected String doSensitive(Object fieldValue, String[] addition) {
        return SensitiveDataUtils.customizeHide(fieldValue.toString(), 4, 4, 9);
    }

}

