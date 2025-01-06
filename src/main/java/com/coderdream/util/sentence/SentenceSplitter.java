package com.coderdream.util.sentence;

import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class SentenceSplitter {

  public static void main(String[] args) {
//    String text = "这是一个段落。它包含多个句子。句子的结尾通常有标点符号。";
//    String[] sentences = StringUtils.splitByCharacterType(text);
//
//    for (String sentence : sentences) {
//      System.out.println(sentence);
//    }


      String text = "这是一个段落。“它包含多个句子。”句子的结尾通常有标点符号！还有一些句子没有标点？";
      String text2 = "这是一个段落。“它包含多个句子。”。句子的结尾通常有标点符号！还有一些句子没有标点？";
      String[] sentences = StringUtils.splitByCharacterType(text);
      for (String sentence : sentences) {
        System.out.println(sentence);
      }
//      sentences.forEach(System.out::println);
      System.out.println("-------------------");
      String[] sentences2 = StringUtils.splitByCharacterType(text2);
      //sentences2.forEach(System.out::println);
      for (String sentence : sentences2) {
        System.out.println(sentence);
      }
  }
}
