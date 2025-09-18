package com.andyadc.skeleton.shield;

public interface Desensitizer {

    /**
     * 屏蔽符
     */
    char SHIELD_CHAR = '*';

    /**
     * 脱敏方法
     */
    String desensitize(Object fieldValue, String[] additions);

    /**
     * 是否清空字符串
     */
    boolean isClean();
}
