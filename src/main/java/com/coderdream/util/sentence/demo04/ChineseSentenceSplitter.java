package com.coderdream.util.sentence.demo04;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 中文句子分割工具类，保留分隔符并跟随在句子后面
 */
public class ChineseSentenceSplitter {

    private static final String DELIMITERS = "[，；：]";  // 使用中文逗号、分号和冒号作为分隔符
    private static final Pattern PATTERN = Pattern.compile(DELIMITERS);

    /**
     * 使用中文逗号、中文分号和中文冒号分割中文句子，并使分隔符跟随在句子后面。
     *
     * @param sentence 要分割的中文句子
     * @return 分割后的句子列表，包含分隔符。如果输入句子为空或null，则返回空列表。
     */
    public static List<String> split(String sentence) {
        List<String> parts = new ArrayList<>();

        if (sentence == null || sentence.isEmpty()) {
            return parts;
        }

        Matcher matcher = PATTERN.matcher(sentence);
        int startIndex = 0;

        while (matcher.find()) {
            String part = sentence.substring(startIndex, matcher.end()).trim(); // 提取分隔符之前的句子部分 + 分隔符
            if (!part.isEmpty()) {
                parts.add(part);
            }

            startIndex = matcher.end(); // 更新起始索引为分隔符之后
        }

        // 添加最后一个句子部分 (如果句子结尾没有分隔符)
        if (startIndex < sentence.length()) {
            String lastPart = sentence.substring(startIndex).trim();
            if (!lastPart.isEmpty()) {
                parts.add(lastPart);
            }
        }

        return parts;
    }

    /**
     * 主要用于测试
     *
     * @param args
     */
    public static void main(String[] args) {
        String sentence1 = "这是一个测试句子，使用中文逗号分割；这是另一个测试句子，使用中文分号分割：这是最后一个测试句子，使用中文冒号分割。";
        String sentence2 = "除了上述三大要素，书中还介绍了十一个可以融入日常生活的小习惯，帮助你更好地实践“浓缩睡眠法”：1、 休息日的起床时间要和平时保持一致： 不要睡懒觉，保持规律的作息。 ";


        ChineseSentenceSplitter.split(sentence1).forEach(System.out::println);
        ChineseSentenceSplitter.split(sentence2).forEach(System.out::println);
    }
}
