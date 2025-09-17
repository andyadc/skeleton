package com.andyadc.skeleton.shield;

public interface Shield {

    /**
     * 屏蔽符
     */
    char SHIELD_CHAR = '*';

    /**
     * 脱敏方法
     */
    String sensitive(Object fieldValue, String[] addition);

    /**
     * 是否清空字符串
     */
    boolean isClean();

}
