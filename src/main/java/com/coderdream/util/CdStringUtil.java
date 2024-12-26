package com.coderdream.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CdStringUtil {

  /**
   * 统计字符串中左括号（的数量
   * @param input 字符串
   * @return 左括号的数量
   */
  public static int countLeftParentheses(String input) {
    // 定义正则表达式来匹配中文左括号
    String regex = "（";
    // 编译正则表达式
    Pattern pattern = Pattern.compile(regex);
    // 创建匹配器
    Matcher matcher = pattern.matcher(input);
    // 计数匹配的次数
    int count = 0;
    while (matcher.find()) {
      count++;
    }
    return count;
  }

  /**
   * 统计给定字符串中左括号（'（'）的数量。
   *
   * @param str 要分析的输入字符串。
   * @return 左括号的数量。
   */
  public static int countRightParentheses(String str) {
    if (str == null || str.isEmpty()) {
      return 0;
    }

    int count = 0;
    for (char c : str.toCharArray()) {
      if (c == '）') {
        count++;
      }
    }
    return count;
  }

  /**
   * 移除字符串中的换行符和空行，并保持原有格式。
   * @param input 输入字符串
   * @return 处理后的字符串
   */
  public static String removeNewLinesAndBlankLines(String input) {
    StringBuilder result = new StringBuilder();
    boolean isNewLine = false; // 标记当前是否处于新行状态

    for (int i = 0; i < input.length(); i++) {
      char currentChar = input.charAt(i);

      if (currentChar == '\r' || currentChar == '\n') {
        // 如果是回车或换行，则设置 isNewLine 为 true
        isNewLine = true;
      } else {
        // 如果不是回车或换行，则检查是否处于新行状态
        if (isNewLine && !result.isEmpty()
          && result.charAt(result.length() - 1) != '\n') {
          // 如果之前是新行且 result 末尾不是换行符，则添加一个换行符以保持格式
          result.append('\n');
        }
        // 添加当前字符到 result 中
        result.append(currentChar);
        // 重置 isNewLine 为 false
        isNewLine = false;
      }
    }

    // 移除末尾可能多余的换行符
    while (!result.isEmpty() && result.charAt(result.length() - 1) == '\n') {
      result.deleteCharAt(result.length() - 1);
    }

    return result.toString();
  }

  /**
   * 移除字符串开头的数字和点号，例如 "1. "。
   * @param input 输入字符串
   * @return 移除开头数字和点号后的字符串
   */
  public static String removePrefix(String input) {
    // 正则表达式：匹配以数字和". "开头的部分
    return input.replaceAll("^\\d+\\.\\s*", "").trim();
  }
}
