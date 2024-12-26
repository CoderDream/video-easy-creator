package com.coderdream.util.ffmpeg;

public class DurationParser {
    public static void main(String[] args) {
        String input = "Duration: 00:00:06.62, start: 0.000000, bitrate: 160 kb/s";
        String duration = extractDuration(input);
        if (duration != null) {
            System.out.println("Duration: " + duration);
        } else {
            System.out.println("Duration 未找到");
        }
    }

    /**
     * 从字符串中提取出 "Duration" 后面的时间部分
     *
     * @param input 输入字符串
     * @return 提取的时间字符串
     */
    public static String extractDuration(String input) {
        // 正则表达式匹配"Duration"后面的时间部分，直到第一个逗号为止
        String regex = "Duration: ([\\d:]+\\.\\d+),";

        // 使用正则表达式进行匹配
        if (input.matches(".*" + regex + ".*")) {
            // 使用replaceAll提取匹配的部分，并去掉多余的空白字符
            return input.replaceAll(".*" + regex + ".*", "$1").trim();
        }

        // 如果没有找到匹配的字符串，返回null
        return null;
    }
}
