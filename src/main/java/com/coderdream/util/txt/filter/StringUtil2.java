package com.coderdream.util.txt.filter;

import lombok.extern.slf4j.Slf4j;

/**
 * 字符串处理工具类
 */
@Slf4j
public class StringUtil2 {

    /**
     * 替换字符串中指定模式的文本，模式为 ^数字^
     *
     * @param originalString 原始字符串
     * @param replacement    替换后的内容
     * @return 替换后的字符串
     */
    public static String replaceNumberPattern(String originalString, String replacement) {
        if (originalString == null || originalString.isEmpty()) {
             log.warn("原始字符串为空，无法进行替换操作");
            return originalString; // 如果输入为空，则直接返回
        }
         String regex = "\\^\\d+\\^"; //  ^数字^的正则表达式

        String replacedString = originalString.replaceAll(regex, replacement);
        log.info("字符串替换完成，原始字符串：{}，替换后的字符串：{}", originalString, replacedString);
        return replacedString;
    }

    public static void main(String[] args) {
        String originalString1 = "这是一个测试字符串，包含 ^4^ 这样的字符。";
        String originalString2 = "这是一个测试字符串，包含 ^123^ 这样的字符。";
         String originalString3 = "这是一个测试字符串，包含 ^99999^ 这样的字符。";
        String originalString4 = "这是一个测试字符串，不包含 ^数字^ 这样的字符。";


        String replacement = "替换后的内容";

        String replacedString1 = StringUtil2.replaceNumberPattern(originalString1, replacement);
        System.out.println("替换后的字符串1: " + replacedString1);

        String replacedString2 = StringUtil2.replaceNumberPattern(originalString2, replacement);
        System.out.println("替换后的字符串2: " + replacedString2);

        String replacedString3 = StringUtil2.replaceNumberPattern(originalString3, replacement);
        System.out.println("替换后的字符串3: " + replacedString3);

        String replacedString4 = StringUtil2.replaceNumberPattern(originalString4, replacement);
        System.out.println("替换后的字符串4: " + replacedString4);

    }
}
