package com.coderdream.util.cd;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    /**
     * 判断字符串是否除了时间中的冒号外，只包含一个中文或英文冒号
     *
     * @param str 要判断的字符串
     * @return 如果符合条件返回 true，否则返回 false
     */
    public static boolean hasSingleColonOutsideTime(String str) {
        if (str == null || str.isEmpty()) {
            return false; // 空字符串或 null 返回 false
        }

        // 1. 移除时间中的冒号（使用正则表达式匹配 HH:mm:ss 或 HH:mm 格式）
        String timePattern = "(\\d{1,2}:\\d{2}(:\\d{2})?)";
        Pattern pattern = Pattern.compile(timePattern);
        Matcher matcher = pattern.matcher(str);
        String strWithoutTimeColons = matcher.replaceAll("");

        // 2. 计算剩余字符串中冒号的数量（中文或英文冒号）
        int colonCount = 0;
        for (int i = 0; i < strWithoutTimeColons.length(); i++) {
            char c = strWithoutTimeColons.charAt(i);
            if (c == ':' || c == '：') {
                colonCount++;
            }
        }

        // 3. 判断冒号的数量是否为 1
        return colonCount == 1;
    }

    public static void main(String[] args) {
        System.out.println(hasSingleColonOutsideTime("Hello:world"));     // true
        System.out.println(hasSingleColonOutsideTime("Hello：world"));     // true
        System.out.println(hasSingleColonOutsideTime("12:34:Hello:world")); // true
        System.out.println(hasSingleColonOutsideTime("12:34:56:Hello:world")); //true
        System.out.println(hasSingleColonOutsideTime("Hello:world:test"));  // false
        System.out.println(hasSingleColonOutsideTime("Hello world"));     // false
        System.out.println(hasSingleColonOutsideTime(""));            // false
        System.out.println(hasSingleColonOutsideTime(null));         // false
        System.out.println(hasSingleColonOutsideTime("12:34"));  // false
        System.out.println(hasSingleColonOutsideTime("1:23:45")); // false
        System.out.println(hasSingleColonOutsideTime("12:34:hello")); // false
         System.out.println(hasSingleColonOutsideTime("hello12:34:56")); // false
        System.out.println(hasSingleColonOutsideTime("测试:12:34")); //true
        System.out.println(hasSingleColonOutsideTime("测试：12:34")); //true
    }
}
