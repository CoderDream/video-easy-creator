package com.coderdream.util.gemini.demo03;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PinyinSeparator {

    /**
     * 分割拼音字符串，格式为 XXXXY，其中 XXXX 是英文字母代表拼音，Y 是数字代表声调。
     *
     * @param inputString 待分割的拼音字符串
     * @return 分割后的拼音字符串，以空格分隔
     */
    public static String separatePinyin(String inputString) {
        if (inputString == null || inputString.isEmpty()) {
            return inputString; //  处理空字符串或 null
        }

        // 使用正则表达式匹配拼音和声调
        Pattern pattern = Pattern.compile("([a-zA-Z]+[1-5])");
        Matcher matcher = pattern.matcher(inputString);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group() + " ");  // 匹配到的内容后添加一个空格
        }
        matcher.appendTail(sb); // 追加剩余未匹配的部分

        return sb.toString().trim(); // 去掉末尾多余的空格
    }

    public static void main(String[] args) {
        String input = "zhe4ge5wu4ti3de5zhong4liang4shi4wu3gong1jin1qing3chong2fu4yi2bian4";
        String separatedPinyin = separatePinyin(input);
        System.out.println("Input: " + input);
        System.out.println("Output: " + separatedPinyin);
    }
}
