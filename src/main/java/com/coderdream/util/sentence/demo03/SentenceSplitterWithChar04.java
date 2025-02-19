package com.coderdream.util.sentence.demo03;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SentenceSplitterWithChar04 {

  public static List<String> processLongSentence(String longSentence,
    String... delimiters) {
    // 动态构造分隔符正则表达式，确保 `;` 或 `:` 保留在句子末尾
    String delimiterRegex = String.join("", delimiters);  // 组合分隔符
    String regex =
      "([^" + delimiterRegex + "]+[" + delimiterRegex + "])";  // 句子+分隔符

    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(longSentence);

    List<String> result = new ArrayList<>();
    int lastIndex = 0;

    while (matcher.find()) {
      result.add(matcher.group(1).trim()); // 获取完整的"句子+分隔符"
      lastIndex = matcher.end();
    }

    // 处理最后一个句子（如果它没有以 `;` 或 `:` 结尾）
    if (lastIndex < longSentence.length()) {
      result.add(longSentence.substring(lastIndex).trim());
    }

    return result;
  }

  public static void main(String[] args) {
    String text = "Hello world; How are you: I'm fine; Thank you!";
    System.out.println(
      SentenceSplitterWithChar04.processLongSentence(text, ";", ":"));
  }
}
