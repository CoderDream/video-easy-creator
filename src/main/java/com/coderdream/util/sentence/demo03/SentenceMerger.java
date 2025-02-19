package com.coderdream.util.sentence.demo03;

import java.util.ArrayList;
import java.util.List;

public class SentenceMerger {

  public static List<String> mergeSentences(List<String> input, int maxLength) {
    List<String> result = new ArrayList<>();
    StringBuilder currentSentence = new StringBuilder();

    for (String part : input) {
      // 计算当前句子 + 新字符串的长度
      if (currentSentence.length() + part.length() > maxLength) {
        result.add(currentSentence.toString().trim());
        currentSentence.setLength(0);
      }

      // 添加新字符串
      if (currentSentence.length() > 0) {
        currentSentence.append(" "); // 保持空格间隔
      }
      currentSentence.append(part);

      // 如果句子以标点符号结尾，立即存储
      if (part.matches(".*[.!?]$")) {
        result.add(currentSentence.toString().trim());
        currentSentence.setLength(0);
      }
    }

    // 处理剩余部分
    if (!currentSentence.isEmpty()) {
      result.add(currentSentence.toString().trim());
    }

    return result;
  }

  public static List<String> mergeSentences(List<String> input) {
    int maxLength = 65;
    return mergeSentences(input, maxLength);
  }

  public static void main(String[] args) {
    List<String> input = List.of(
      "I would like",
      "to point out",
      "to all of you once again have access",
      "to the most transparent",
      "and accessible president in American history."
    );

    int maxLength = 65;
    List<String> mergedSentences = SentenceMerger.mergeSentences(input,
      maxLength);

    for (String sentence : mergedSentences) {
      System.out.println(sentence);
    }
  }
}
