package com.coderdream.util.bbc;

import com.coderdream.util.subtitle.GenSubtitleUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StringSplitter4 {


  public static List<String> splitString(String input, int maxLength) {

    if (input == null || input.isEmpty()) {
      return new ArrayList<>();
    }

    if (input.length() <= maxLength) {
      return List.of(input);
    }

    List<String> commaSplittedList = StringSplitUtil2.splitStringWithComma(
      input);
    List<String> resultStep01 = new ArrayList<>();
    String[] separatorsLeft = {": ", "; ", ":",
      ";"};
    for (String s : commaSplittedList) {
      List<String> currentList = new ArrayList<>(List.of(s));

      for (String separator : separatorsLeft) {
        boolean overLengthOccurred = false;
        List<String> tempResult = new ArrayList<>();
        for (String current : currentList) {
          if (current.length() <= maxLength) {
            tempResult.add(current);
          } else {
            List<String> splittedStrings = StringSplitter6.splitAndSeparator(
              current, separator);
            for (String s2 : splittedStrings) {
              if (s2.length() > maxLength) {
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
      resultStep01.addAll(currentList);
    }

    List<String> result = new ArrayList<>();
    // " than ",
    String[] separators = {" and ", " that ", " when ", " but ",
      " for ", " because ", " with ", " where ", " which ", " how ",
      " why ", " by ", " from ", " is ",
      " to ",
      " not ", " who ", " or ", " And ", " That ", " When ", " But ",
      " For ", " Than ", " Because ", " With ", " Where ", " Which ", " How ",
      " Why ", " By ", " From ", " Is ",
      " To ",
      " Not ", " Who ", " Or "}; // , " a\\) ", " b\\) ", " c\\) "

    for (String s : resultStep01) {
      List<String> currentList = new ArrayList<>(List.of(s));
      for (String separator : separators) {
        boolean overLengthOccurred = false;
        List<String> tempResult = new ArrayList<>();
        for (String current : currentList) {
          if (current.length() <= maxLength) {
            tempResult.add(current);
          } else {
            List<String> splittedStrings = StringSplitter6.splitAndPrependSeparator(
              current, separator);
            for (String s2 : splittedStrings) {
              if (s2.length() > maxLength) {
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

  public static List<String> splitString(String input) {

    int maxLength = 65;
    return splitString(input, maxLength);
  }

  public static List<String> splitStringList(List<String> strings) {
    List<String> result = new ArrayList<>();
    for (String string : strings) {
      result.addAll(StringSplitter4.splitString(string));
    }
    return result;
  }

  public static List<String> splitStringList(List<String> strings,
    int maxLength) {
    List<String> result = new ArrayList<>();
    for (String string : strings) {
      result.addAll(StringSplitter4.splitString(string, maxLength));
    }
    return result;
  }

  public static void main(String[] args) {
    System.out.println("列表:");
    String input = "If you are living happily in isolation in The Scottish Highlands, for example, I'm sure you could feel lonely if you came here to London.";
    input = "That is my job.And I will say it’s very easy to speak truth from this podium when you have a president who is implementing policies that are wildly popular with the American people,";

    int maxLength = 65;
    List<String> sentenceList = StringSplitter4.splitString(input);
    for (String string : sentenceList) {
      System.out.println(string);
    }

    sentenceList = StringSplitter4.splitStringList(sentenceList);
//
  }
}
