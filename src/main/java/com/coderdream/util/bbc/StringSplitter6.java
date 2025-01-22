package com.coderdream.util.bbc;

import java.util.ArrayList;
import java.util.List;

public class StringSplitter6 {

  /**
   * 以 String2 分割 String1，并将 String2 添加到每个分割后的字符串前面。
   *
   * @param input 要分割的字符串
   * @param word  分隔符
   * @return 分割并添加分隔符后的字符串列表
   */
  public static List<String> splitAndPrependSeparator(String input,
    String word) {
    List<String> result = new ArrayList<>();
    if (input == null || input.isEmpty() || word == null
      || word.isEmpty()) {
      return result;
    }

    String[] parts = input.split(word); // 用 word 分割 input

    for (int i = 0; i < parts.length; i++) {
      String part = parts[i].trim();//去除首尾空格
      if (i > 0) {
        result.add(word.trim() + " " + part); // 将 word 添加到分割后的部分前面
      } else {
        result.add(part);
      }
    }

    return result;
  }

  public static void main(String[] args) {
    String input = "When you walk in to the coffee shop in the morning and that smell hits you,";
    String word = "and";
    List<String> result = StringSplitter6.splitAndPrependSeparator(input, word);
    System.out.println(result);
  }
}
