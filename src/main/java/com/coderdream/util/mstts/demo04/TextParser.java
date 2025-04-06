package com.coderdream.util.mstts.demo04;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextParser {
    // 匹配 [数字] 组合的正则表达式
    private static final Pattern PRONUNCIATION_PATTERN = Pattern.compile("(?<char>[\\u4E00-\\u9FA5])\\[(?<tone>[1-5])\\]");

    public static List<TextWithPronunciation> parseTextWithPronunciation(String text) {
        List<TextWithPronunciation> result = new ArrayList<>();
        Matcher matcher = PRONUNCIATION_PATTERN.matcher(text);
        int lastEnd = 0;

        while (matcher.find()) {
            // 添加 [数字] 之前的文本
            if (matcher.start() > lastEnd) {
                TextWithPronunciation plainText = new TextWithPronunciation();
                plainText.setText(text.substring(lastEnd, matcher.start()));
                result.add(plainText);
            }

            // 添加带有注音的文本
            TextWithPronunciation annotatedText = new TextWithPronunciation();
            annotatedText.setText(matcher.group("char"));
            annotatedText.setPronunciation(matcher.group("tone"));
            result.add(annotatedText);

            lastEnd = matcher.end();
        }

        // 添加最后一个 [数字] 之后的文本
        if (lastEnd < text.length()) {
            TextWithPronunciation plainText = new TextWithPronunciation();
            plainText.setText(text.substring(lastEnd));
            result.add(plainText);
        }

        return result;
    }

    public static void main(String[] args) {
        String text = "这个物体的重[4]量[4]是五公斤，请重[2]复[4]一遍。";
        List<TextWithPronunciation> parts = parseTextWithPronunciation(text);
        for (TextWithPronunciation part : parts) {
            System.out.println("Text: " + part.getText() + ", Pronunciation: " + part.getPronunciation());
        }
    }
}
