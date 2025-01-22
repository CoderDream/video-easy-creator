package com.coderdream.util.bbc;

import java.util.ArrayList;
import java.util.List;

public class StringSplitter4 {

  private static final int MAX_LENGTH = 65;

  public static List<String> splitString(String input) {
    if (input == null || input.isEmpty()) {
      return new ArrayList<>();
    }

    if (input.length() <= MAX_LENGTH) {
      return List.of(input);
    }

    List<String> commaSplittedList = StringSplitUtil2.splitStringWithComma(
      input);
    List<String> result = new ArrayList<>();

    String[] separators = {" and ", " that ", " when ", " or ", " but ",
      " for ", " than ", " because ", " with ", " where ", " which ", " how ",
      " why ", " by ", " from ", " is ",
      " to ",
      " not "," And ", " That ", " When ", " Or ", " But ",
      " For ", " Than ", " Because ", " With ", " Where ", " Which ", " How ",
      " Why ", " By ", " From ", " Is ",
      " To ",
      " Not "}; // , " a\\) ", " b\\) ", " c\\) "

    for (String s : commaSplittedList) {
      List<String> currentList = new ArrayList<>(List.of(s));

      for (String separator : separators) {
        boolean overLengthOccurred = false;
        List<String> tempResult = new ArrayList<>();
        for (String current : currentList) {
          if (current.length() <= MAX_LENGTH) {
            tempResult.add(current);
          } else {
            List<String> splittedStrings = StringSplitter6.splitAndPrependSeparator(
              current, separator);
            for (String s2 : splittedStrings) {
              if (s2.length() > MAX_LENGTH) {
                overLengthOccurred = true;
              }
              tempResult.add(s2);
            }
          }
        }
        currentList = tempResult;
        if (!overLengthOccurred) {
          break;
        }

      }
      result.addAll(currentList);

    }
    return result;
  }


  public static void main(String[] args) {
    System.out.println("列表:");
    List<String> strings = splitString(
      "If you are living happily in isolation in The Scottish Highlands, for example, I'm sure you could feel lonely if you came here to London.");
    for (String string : strings) {
      System.out.println(string);
    }
  }
}
