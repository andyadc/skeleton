package com.andyadc.skeleton.shield.shields;

import com.andyadc.skeleton.shield.utils.SensitiveDataUtils;

public class PhoneShield extends AbstractShield {

    @Override
    protected String doSensitive(Object fieldValue, String[] addition) {
        return SensitiveDataUtils.customizeHide(fieldValue.toString(), 3, 4, 0);
    }

}
