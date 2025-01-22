package com.coderdream.util.bbc;

import java.util.ArrayList;
import java.util.List;

public class StringSplitter5 {

  private static final int MAX_LENGTH = 65;

  public static List<String> splitString(String input) {
    if (input == null || input.isEmpty()) {
      return new ArrayList<>();
    }

    List<String> result1 = new ArrayList<>();
    if (input.length() <= MAX_LENGTH) {
      result1.add(input);
      return result1;
    }

    // 1. 优先按照逗号分割
    List<String> commaSplittedList = StringSplitUtil2.splitStringWithComma(
      input);
    int overCount = 0;
    for (String s : commaSplittedList) {
      if (s.length() <= MAX_LENGTH) {
        result1.add(s);
      } else {
        // 2. 按照逗号分割后，如果长度依然大于65， 则不进行分割
        List<String> strings1 = StringSplitter6.splitAndPrependSeparator(s,
          "and");
        for (String s2 : strings1) {
          if (s2.length() > MAX_LENGTH) {
            overCount++;
          }

          result1.add(s2);
        }
      }
    }

    List<String> result2 = new ArrayList<>();
    if (overCount > 0) {
      overCount = 0; // 重置
      for (String s : result1) {
        if (s.length() <= MAX_LENGTH) {
          result2.add(s);
        } else {
          // 2. 按照逗号分割后，如果长度依然大于65， 则不进行分割
          List<String> strings2 = StringSplitter6.splitAndPrependSeparator(s,
            "that");
          for (String s2 : strings2) {
            if (s2.length() > MAX_LENGTH) {
              overCount++;
            }

            result2.add(s2);
          }
        }
      }
      result1 = result2;
    } else {
      return result1;
    }

    List<String> result3 = new ArrayList<>();
    if (overCount > 0) {
      overCount = 0; // 重置
      for (String s : result2) {
        if (s.length() <= MAX_LENGTH) {
          result3.add(s);
        } else {
          // 2. 按照逗号分割后，如果长度依然大于65， 则不进行分割
          List<String> strings3 = StringSplitter6.splitAndPrependSeparator(s,
            "or");
          for (String s2 : strings3) {
            if (s2.length() > MAX_LENGTH) {
              overCount++;
            }

            result3.add(s2);
          }
        }
      }
      result1 =  result3;
    } else {
      return result1;
    }

    return result1;
  }

  public static void main(String[] args) {
    System.out.println("列表:");
    List<String> strings = splitString(
      "I think you can be lonely anywhere if you feel that you are disconnected from the world around you,");
    for (String string : strings) {
      System.out.println(string);
    }
  }
}
