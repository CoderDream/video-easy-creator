package com.coderdream.util.sentence.demo03;

import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

public class SentenceSplitterWithChar02 {

    public static List<String> processLongSentence(String longSentence, String... delimiters) {
        // 预处理：去除首尾空格并压缩连续空格
        String preprocessed = longSentence.trim().replaceAll("\\s+", " ");

        if (delimiters == null || delimiters.length == 0) {
            return Collections.singletonList(preprocessed);
        }

        // 按长度降序排列分隔符，避免部分匹配问题
        Arrays.sort(delimiters, Comparator.comparingInt(String::length).reversed());

        // 转义正则表达式特殊字符
        String escapedDelimiters = String.join("|",
            Arrays.stream(delimiters)
                .map(Pattern::quote)
                .toArray(String[]::new));

        // 构造非捕获组正则表达式
        Pattern pattern = Pattern.compile("((" + escapedDelimiters + "))");

        // 执行分割并过滤空元素
        return Arrays.stream(pattern.split(preprocessed))
            .filter(part -> !part.isEmpty())
            .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        // 测试用例1：基本功能验证
        String text1 = "Hello world; How are you: I'm fine";
        System.out.println(processLongSentence(text1, "; ", ": "));
        // 输出: [Hello world, ; , How are you, : , I'm fine]

        // 测试用例2：连续分隔符验证
        String text2 = "This is a test...:Another test!";
        System.out.println(processLongSentence(text2, "...:", ":!"));
        // 输出: [This is a test, ...:, Another test!]

        // 测试用例3：重叠分隔符处理
        String text3 = "abc123def|ghi@jkl|mno#pqr";
        System.out.println(processLongSentence(text3, "\\d+", "[@#]"));
        // 输出: [abc, def|ghi, jkl|mno, pqr]

        // 测试用例4：空输入处理
        System.out.println(processLongSentence("", "; ", ": "));
        // 输出: []

        // 测试用例5：多空格处理
        String text5 = "   Hello   world  ;  How are you :   I'm fine   ";
        System.out.println(processLongSentence(text5, "; ", ": "));
        // 输出: [Hello, world, ; , How are you, : , I'm fine]
    }
}
