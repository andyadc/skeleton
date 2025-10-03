package com.andyadc.skeleton.shield.utils;

import com.andyadc.skeleton.shield.Shield;

public class SensitiveDataUtils {

    private SensitiveDataUtils() {
    }

    /**
     * SensitiveDataUtils.customizeHide("13312345678", 3,4,4) = "133****5678"
     * SensitiveDataUtils.customizeHide("13312345678", 0,4,4) = "****5678"
     * SensitiveDataUtils.customizeHide("13312345678", 3,0,4) = "133****"
     * SensitiveDataUtils.customizeHide("13312345678", 3,0,8) = "133********"
     *
     * @param sensitiveData 原数据
     * @param frontCharNum  展示前几位
     * @param tailCharNum   展示后几位
     * @param hiddenCharNum 展示星号*的个数，当为0时，则返回数据将保留原始数据位数
     */
    public static String customizeHide(final String sensitiveData, final int frontCharNum, final int tailCharNum, int hiddenCharNum) {
        if (sensitiveData == null || sensitiveData.isBlank()) {
            return sensitiveData;
        }
        String tmp = sensitiveData.trim();
        int length = tmp.length();
        // 合法性检查，如果参数不合法，返回源数据内容
        if (frontCharNum < 0 || tailCharNum < 0 || hiddenCharNum < 0 || frontCharNum + tailCharNum > length) {
            return tmp;
        }
        int beginIndex = frontCharNum - 1;
        int endIndex = length - tailCharNum;
        // 原数据前半部分
        StringBuilder result = new StringBuilder();
        if (beginIndex >= 0 && beginIndex < length) {
            result.append(tmp, 0, frontCharNum);
        }

        // 中间*
        if (hiddenCharNum == 0) {
            hiddenCharNum = length - frontCharNum - tailCharNum;
        }

        for (int i = 0; i < hiddenCharNum; i++) {
            result.append(Shield.SHIELD_CHAR);
        }

        // 原数据后半部分
        if (endIndex >= 0 && endIndex < length) {
            result.append(tmp.substring(endIndex));
        }
        return result.toString();
    }

}

