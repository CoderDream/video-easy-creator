package com.coderdream.util.subtitle;

import java.util.regex.Pattern;

public class SentenceSplitter {
    public static void main(String[] args) {
        String text = "这是第一句话。\n这是第二句话！—— 这是第三句？\n这是第四句；这是第五句：\n“这是第六句”。——这是第七句。";
        String[] sentences = Pattern.compile("(?<=[。！？；：])|(?=“)|(?=\n)|(?=——)").split(text);

        for (String sentence : sentences) {
            System.out.println(sentence.trim()); // 使用 trim() 去掉首尾空格
        }
    }
}
