package com.coderdream.util.bbc;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringSplitter {

  public static List<String> splitByPattern(String input) {
    if (input == null || input.isEmpty()) {
      return List.of();
    }

    String regex = "\\s+[a-zA-Z]\\)\\s+";
    String[] parts = input.split(regex);

    return Arrays.stream(parts)
      .filter(s -> !s.trim().isEmpty())
      .map(String::trim)
      .collect(Collectors.toList());
  }


  public static void main(String[] args) {
    String input = " a) This is the first part. b) This is the second part.  c) This is the third part.";
    List<String> result = splitByPattern(input);
    result.forEach(System.out::println);

    String input2 = "a) This is the first part.b) This is the second part.c) This is the third part.";
    List<String> result2 = splitByPattern(input2);
    result2.forEach(System.out::println);

    String input3 = "a) This is the first part. b) This is the second part. c) This is the third part. d) This is the fourth part.";
    List<String> result3 = splitByPattern(input3);
    result3.forEach(System.out::println);
  }
}
