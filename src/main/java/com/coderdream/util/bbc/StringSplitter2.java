package com.coderdream.util.bbc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringSplitter2 {

  private static final int MAX_LENGTH = 65;

  public static List<String> splitString(String input) {
    if (input == null || input.isEmpty()) {
      return new ArrayList<>(); // 处理空字符串
    }

    List<String> result = new ArrayList<>();
    if (input.length() <= MAX_LENGTH) { //如果字符的长度小于 65 则直接返回
      result.add(input);
      return result;
    }
    //1. 优先按照逗号分割
    List<String> commaSplittedList = Arrays.stream(input.split(","))
      .map(String::trim).collect(Collectors.toList());
    boolean allLessThanMaxLength = true;
    for (String s : commaSplittedList) {
      if (s.length() > MAX_LENGTH) {
        allLessThanMaxLength = false;
        break;
      }
    }

    if (allLessThanMaxLength) { // 如果逗号分割后，所有的字符串长度小于等于65
      return commaSplittedList;
    }
    // 2. 如果逗号分割后还是有字符串长度大于65，则按照 that, and 分割
    List<String> thatAndSplittedList = Arrays.stream(input.split("that, and"))
      .map(String::trim).toList();

    for (String s : thatAndSplittedList) {
      if (s.length() > MAX_LENGTH) {
        result.add(s); // 如果分割后的字符长度还大于65 则不做处理
      } else {
        result.add(s);
      }
    }

    return result;
  }

  public static void main(String[] args) {
//    String input1 = "This is a test string, which is short enough.";
//    String input2 = "This is a test string, which is longer than sixty-five characters, so it needs to be split.";
//    String input3 = "This is a test string, which is longer than sixty-five characters, so it needs to be split,that, and this is another part of the string that needs splitting.";
//    String input4 = "This is a very very very very very very very very very very very long string, that, and is more than 65 characters, it can't be split";
//    String input5 = "This is a string with no comma and that, and";
//
//    System.out.println("Original string: " + input1);
//    System.out.println("Split string: " + StringSplitter2.splitString(input1));
//
//    System.out.println("\nOriginal string: " + input2);
//    System.out.println("Split string: " + splitString(input2));
//
//    System.out.println("\nOriginal string: " + input3);
//    System.out.println("Split string: " + splitString(input3));
//
//    System.out.println("\nOriginal string: " + input4);
//    System.out.println("Split string: " + splitString(input4));
//
//    System.out.println("\nOriginal string: " + input5);
//    System.out.println("Split string: " + splitString(input5));

//    System.out.println("\n 测试空列表:");
    System.out.println(splitString("And when you walk in to the coffee shop in the morning and that smell hits you, you're getting physiological responses."));
//    System.out.println("\n 测试null 列表:");
//    System.out.println(splitString(null));


  }
}
