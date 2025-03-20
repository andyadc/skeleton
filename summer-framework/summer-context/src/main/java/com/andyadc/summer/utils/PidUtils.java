package com.andyadc.summer.utils;

import java.lang.management.ManagementFactory;

public class PidUtils {

    /**
     * 在Java 8中实现类似于Java 9的ManagementFactory.getRuntimeMXBean().getPid()方法
     *
     * @return 当前JVM进程的PID，如果无法解析则返回-1
     */
    public static long getPid() {
        // 获取RuntimeMXBean的Name属性
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (name == null || name.isEmpty()) {
            throw new IllegalStateException("Unable to retrieve PID from RuntimeMXBean");
        }

        // 解析PID部分
        int atIndex = name.indexOf('@');
        if (atIndex > 0) {
            try {
                return Long.parseLong(name.substring(0, atIndex));
            } catch (NumberFormatException e) {
                // 如果无法解析为数字，返回-1
            }
        }

        // 如果无法解析PID，返回-1
        return -1;
    }

    public static void main(String[] args) {
        // 示例：获取并打印当前JVM的PID
        long pid = getPid();
        System.out.println("Current JVM PID: " + pid);
    }
}
