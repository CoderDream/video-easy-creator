package com.coderdream.util.sentence;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    /**
     *  去除所有中文标点符号后面的空格
     * @param input 待处理的字符串
     * @return 处理后的字符串
     */
    private static String removeSpaceAfterChinesePunctuation(String input) {
        String regex = "([，。！？？：；‘’“”（）])\\s+"; // 匹配中文标点符号后面的一个或多个空格
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.replaceAll("$1"); // 替换为空，保留标点符号
    }

     public static void main(String[] args) {
        String testString = "你好，   世界！  这是  一个   测试  。";
        String result = removeSpaceAfterChinesePunctuation(testString);
        System.out.println("原始字符串：" + testString);
        System.out.println("处理后的字符串：" + result); // 输出： 你好，世界！这是  一个   测试。
     }
}
