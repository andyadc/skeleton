package com.andyadc.skeleton.shield.shields;

import com.andyadc.skeleton.shield.utils.NumberUtils;
import com.andyadc.skeleton.shield.utils.SensitiveDataUtils;

/**
 * 自定义脱敏
 */
public class CustomizeShield extends AbstractShield {

    @Override
    protected String doSensitive(Object fieldValue, String[] addition) {
        if (addition == null || addition.length < 3) {
            return fieldValue.toString();
        }
        try {
            int frontCharNum = NumberUtils.toInt(addition[0]);
            int tailCharNum = NumberUtils.toInt(addition[1]);
            int hiddenCharNum = NumberUtils.toInt(addition[2]);
            return SensitiveDataUtils.customizeHide(fieldValue.toString(), frontCharNum, tailCharNum, hiddenCharNum);
        } catch (NumberFormatException e) {
            return fieldValue.toString();
        }
    }

}
