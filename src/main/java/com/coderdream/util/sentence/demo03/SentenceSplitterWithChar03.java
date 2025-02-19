package com.coderdream.util.sentence.demo03;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SentenceSplitterWithChar03 {
    // 默认的分隔符
    private static final String[] DEFAULT_DELIMITERS = {";", ":"};

    // 允许自定义分隔符
    public static List<String> processLongSentence(String longSentence, String... delimiters) {
        // 如果没有提供分隔符，使用默认值
        if (delimiters.length == 0) {
            delimiters = DEFAULT_DELIMITERS;
        }

        // 构造分隔符正则表达式
        String delimiterRegex = String.join("", delimiters); // 组合分隔符
        String regex = "([^" + delimiterRegex + "]+[" + delimiterRegex + "])"; // 句子 + 分隔符

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(longSentence);

        List<String> result = new ArrayList<>();
        int lastIndex = 0;

        while (matcher.find()) {
            result.add(matcher.group(1).trim()); // 提取完整句子（包含分隔符）
            lastIndex = matcher.end();
        }

        // 处理最后一个句子（如果它没有 `;` 或 `:` 结尾）
        if (lastIndex < longSentence.length()) {
            result.add(longSentence.substring(lastIndex).trim());
        }

        return result;
    }

    // 默认使用分号 (;) 和冒号 (:) 作为分隔符
    public static List<String> processLongSentence(String longSentence) {
        return processLongSentence(longSentence, DEFAULT_DELIMITERS);
    }

    public static void main(String[] args) {
        List<String> sentenceList = List.of(
            "Hello world; How are you: I'm fine; Thank you!",
            "Java is great; Python is cool: But I love C++;"
        );

        // 默认使用 `;` 和 `:` 作为分隔符
        List<String> result1 = sentenceList.stream()
            .map(SentenceSplitterWithChar03::processLongSentence)
            .flatMap(List::stream)
            .toList();

        System.out.println("默认分隔符: " + result1);

        // 使用自定义分隔符 `!` 和 `.`
        List<String> result2 = sentenceList.stream()
            .map(sentence -> SentenceSplitterWithChar03.processLongSentence(sentence, "!", "."))
            .flatMap(List::stream)
            .toList();

        System.out.println("自定义分隔符 (!, .): " + result2);
    }
}
