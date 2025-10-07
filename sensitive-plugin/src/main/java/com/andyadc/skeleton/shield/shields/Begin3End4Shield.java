package com.andyadc.skeleton.shield.shields;

import com.andyadc.skeleton.shield.utils.SensitiveDataUtils;

/**
 * 前3后4脱敏
 */
public class Begin3End4Shield extends AbstractShield {

    @Override
    protected String doSensitive(Object fieldValue, String[] addition) {
        return SensitiveDataUtils.customizeHide(fieldValue.toString(), 3, 4, 0);
    }

}
