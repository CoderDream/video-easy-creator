package com.coderdream.util.audio;

public class StringExtractor {

  public static String extractNumber(String str) {
    int lastUnderScore = str.lastIndexOf("_");
    if (lastUnderScore == -1) {
      return ""; // 或者抛出异常，取决于你的需求
    }
    int secondLastUnderScore = str.lastIndexOf("_", lastUnderScore - 1);
    if (secondLastUnderScore == -1) {
      return "";
    }
    return str.substring(secondLastUnderScore + 1, lastUnderScore);
  }

  public static String extractLastNumber(String str) {
    int lastUnderScore = str.lastIndexOf("_");
    if (lastUnderScore == -1) {
      return ""; // 或者抛出异常，取决于你的需求
    }
    int secondLastUnderScore = str.lastIndexOf(".");
    if (secondLastUnderScore == -1) {
      return "";
    }
    return str.substring(lastUnderScore + 1, secondLastUnderScore);
  }

  public static void main(String[] args) {
    String filename = "dialog_single_with_phonetics_001_cn.wav";
    String number = StringExtractor.extractNumber(filename);
    System.out.println(number); // 输出：001
    String filename2 = "dialog_single_with_phonetics_02_cn.wav";
    String number2 = extractNumber(filename2);
    System.out.println(number2);
    String filename3 = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics\\audio\\list_061.txt";
    String number3 = extractLastNumber(filename3);
    System.out.println(number3);
  }
}
