package com.coderdream.util.txt;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SplitSentence1 {

    public static void main(String[] args) {
        String sentence = "The book title is \"The Lord of the Rings\" and it's great. He said \"Hello, World!\".";
        List<String> parts = splitSentenceWithQuotes(sentence);
        System.out.println(parts);
    }

    public static List<String> splitSentenceWithQuotes(String sentence) {
        List<String> parts = new ArrayList<>();
        Pattern pattern = Pattern.compile("(\"[^\"]*\")|([^\"\\s]+)"); // 匹配双引号内的内容或非引号空格字符
        Matcher matcher = pattern.matcher(sentence);

        while (matcher.find()) {
            parts.add(matcher.group()); // 添加匹配到的内容
        }
        return parts;
    }
}
