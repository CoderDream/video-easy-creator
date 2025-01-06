package com.coderdream.util.sentence;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SentenceSplitter1 {

    public static void main(String[] args) {
        String text = "这是一个段落。“它包含多个句子。”句子的结尾通常有标点符号！还有一些句子没有标点？";
        String text2 = "这是一个段落。“它包含多个句子。”。句子的结尾通常有标点符号！还有一些句子没有标点？";
        List<String> sentences = splitSentences(text);
        sentences.forEach(System.out::println);
        System.out.println("-------------------");
        List<String> sentences2 = splitSentences(text2);
        sentences2.forEach(System.out::println);
    }


    public static List<String> splitSentences(String text) {
        List<String> sentences = new ArrayList<>();
         Pattern pattern = Pattern.compile("([^。？！]+(?:[。？！]|$)|(?:“[^”]*?”(?:[。？！]|$))|[^。？！]*?(?:[。？！]|$))");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
             sentences.add(matcher.group().trim());
          }
        return sentences;
    }
}
