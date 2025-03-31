package com.coderdream.util.cd;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringConverter {

    public static String convertToKebabCase(String input) {
        // 使用正则表达式匹配大写字母
        Pattern pattern = Pattern.compile("(?<=[a-z])(?=[A-Z])");

        // 使用正则表达式将大写字母前插入 "-"，然后将字符串转换为小写
        String result = pattern.matcher(input).replaceAll("-").toLowerCase();

        return result;
    }

    public static void main(String[] args) {
        String input = "PressBriefings";
        String output = convertToKebabCase(input);
        System.out.println(output); // 输出：press-briefings
    }
}
