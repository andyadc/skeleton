package com.andyadc.summer.utils;

import java.util.Arrays;
import java.util.stream.Stream;

public final class StringUtils {

    /**
     * 在Java 8中实现类似于Java 9的String.lines()方法
     *
     * @param input 输入字符串
     * @return 包含每一行的Stream<String>
     */
    public static Stream<String> lines(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input string cannot be null");
        }

        // 分割字符串并过滤掉空行
        return Arrays.stream(input.split("\\R"))
                .filter(line -> !line.isEmpty());
    }

}
