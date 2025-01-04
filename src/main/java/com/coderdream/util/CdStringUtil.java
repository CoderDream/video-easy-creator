package com.coderdream.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class CdStringUtil {

  /**
   * 统计字符串中左括号（的数量
   *
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
   *
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
   *
   * @param input 输入字符串
   * @return 移除开头数字和点号后的字符串
   */
  public static String removePrefix(String input) {
    // 正则表达式：匹配以数字和". "开头的部分
    return input.replaceAll("^\\d+\\.\\s*", "").trim();
  }

  /**
   * 移除images标签括号后的第一个的斜线
   *
   * @param originalString
   * @return
   */
  public static String replaceImagesFirstLinks(String originalString) {
    // 定义一个数组，包含所有需要替换的旧字符串和新字符串
    String[] patterns = {
      "(/images/im", "(images/im",
      "(/images/Im", "(images/im",
      "(/images//im", "(images/im",
      "(/images//Im", "(images/im"
    };
    int imageType = 0;
    if (originalString.contains(patterns[0])) {
      imageType = 1;
    } else if (originalString.contains(patterns[2])) {
      imageType = 2;
    } else if (originalString.contains(patterns[4])) {
      imageType = 3;
    } else if (originalString.contains(patterns[6])) {
      imageType = 4;
    }

    // 使用循环进行替换，避免重复代码
    String tempStr = originalString;
    tempStr = switch (imageType) {
      case 1 -> tempStr.replace(patterns[0], patterns[1]);
      case 2 -> tempStr.replace(patterns[2], patterns[3]);
      case 3 -> tempStr.replace(patterns[4], patterns[5]);
      case 4 -> tempStr.replace(patterns[6], patterns[7]);
      default -> tempStr;
    };

    return tempStr;
  }


  public static String replaceImagesLinks(
    String originalString) {

    // 定义一个数组，包含所有需要替换的旧字符串和新字符串
    String[] patterns = {
      "(/images/images/", "(images/",
      "(/images/Images/", "(images/",
      "(/images//images/", "(images/",
      "(/images//Images/", "(images/"
    };
    int imageType = 0;
    if (originalString.contains("(/images/images")) {
      imageType = 1;
    } else if (originalString.contains("(/images/Images")) {
      imageType = 2;
    } else if (originalString.contains("(/images//images")) {
      imageType = 3;
    } else if (originalString.contains("(/images//Images")) {
      imageType = 4;
    }

    // 使用循环进行替换，避免重复代码
    String tempStr = originalString;
//    for (int i = 0; i < patterns.length; i += 2) {
//      tempStr = tempStr.replace(patterns[i], patterns[i + 1]);
//    }
    tempStr = switch (imageType) {
      case 1 -> tempStr.replace(patterns[0], patterns[1]);
      case 2 -> tempStr.replace(patterns[2], patterns[3]);
      case 3 -> tempStr.replace(patterns[4], patterns[5]);
      case 4 -> tempStr.replace(patterns[6], patterns[7]);
      default -> tempStr;
    };

    // 图片居左
    tempStr = leftImagesLinks(tempStr);

    return tempStr;
  }

  public static String leftImagesLinks(
    String originalString) {
    String replacementString = "![";
    String newString = " ![";

    //      String tempStr = originalString.toString()
    //            .replace(newString, replacementString);
    // 第一次替换
    return originalString.replace(replacementString,
      newString);
  }

  /**
   * 正则表达式：匹配形如 ^[数字]^ 的字符串
   */
  private static final String REGEX = "\\^\\[\\d+\\]\\^";

  /**
   * 替换字符串中所有匹配的 ^[数字]^ 模式为空字符串
   *
   * @param text 要处理的字符串
   * @return 替换后的字符串，如果输入字符串为 null 或者空字符串，则返回空字符串
   */
  public static String replaceHtmlTag(String text) {
    // 如果输入字符串为 null 或者空字符串，直接返回空字符串，避免空指针异常
    if (text == null || text.isEmpty()) {
      log.warn("输入字符串为空，直接返回空字符串。");
      return "";
    }
    // 记录方法开始执行的时间
    LocalDateTime startTime = LocalDateTime.now();
    String result = "";
    try {
      // 使用 replaceAll 方法替换所有匹配的模式，并赋值给 result
      result = text.replaceAll(REGEX, "");
      log.debug("字符串替换完成, 替换前：{} , 替换后: {}", text, result);
    } catch (Exception e) {
      log.error("字符串替换发生异常，原始字符串：{}", text, e);
      // 返回空字符串
      result = "";
    } finally {
      // 记录方法结束执行的时间
      LocalDateTime endTime = LocalDateTime.now();
      // 计算方法执行耗时
      Duration duration = Duration.between(startTime, endTime);
      // 将耗时格式化为 时分秒 的字符串
      String elapsedTime = CdDateUtil.formatDuration(duration);
      log.info("方法 replaceAllPatterns 执行完成, 耗时: {}", elapsedTime);
    }

    // 返回替换后的字符串
    return result;
  }

  /**
   * 删除以 | 开头的行和以 | [数字] 开头的行
   *
   * @param text 多行文本
   * @return 删除指定行后的文本
   */
  public static String removeUseLessLines(String text) {
    if (text == null || text.isEmpty()) {
      return ""; // 处理空或 null 的情况
    }

    // 定义匹配规则，匹配以 | 开头 或 以 | [数字] 开头的行
    String regex = "^\\|(\\s*\\[\\d+\\])?.*$"; // \\| 用于匹配 |，\\s* 匹配0个或多个空格， \\[ 和 \\] 用于匹配 [ ]

    Pattern pattern = Pattern.compile(regex,
      Pattern.MULTILINE); // MULTILINE 模式允许 ^ 和 $ 匹配每行的开头和结尾

    // 使用 stream 处理每一行，过滤掉匹配的行和空行，并用换行符拼接
    return Arrays.stream(text.split("\\r?\\n")) // 将文本按行分割
      .filter(line -> !pattern.matcher(line).matches() && !line.trim()
        .isEmpty()) // 过滤掉匹配的行和空行
      .collect(Collectors.joining(System.lineSeparator()));  // 使用换行符拼接剩余的行
  }
}
