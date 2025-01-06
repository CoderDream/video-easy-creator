package com.coderdream.util.txt;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SplitSentence2 {

  public static void main(String[] args) {
    String sentence = "The book title is \"The Lord of the Rings\" and it's great. He said \"Hello, World!\".";
    List<List<String>> sentences = splitSentences(sentence);
    System.out.println(sentences);
  }

  public static List<List<String>> splitSentences(String sentence) {
    List<List<String>> sentences = new ArrayList<>();
    List<String> currentSentence = new ArrayList<>();

    Pattern pattern = Pattern.compile(
      "(\"[^\"]*\")|([^\"\\s.]+)"); // 匹配双引号内的内容或非引号空格或句点字符
    Matcher matcher = pattern.matcher(sentence);

    while (matcher.find()) {
      String part = matcher.group();
      currentSentence.add(part);

      if (sentence.indexOf(part) + part.length() < sentence.length()
        && sentence.charAt(sentence.indexOf(part) + part.length()) == '.') {
        sentences.add(currentSentence);
        currentSentence = new ArrayList<>();
      }
    }
    if (!currentSentence.isEmpty()) {
      sentences.add(currentSentence);
    }
    return sentences;
  }
}
