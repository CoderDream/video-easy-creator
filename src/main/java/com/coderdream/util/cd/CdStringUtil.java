package com.coderdream.util.cd;

import com.coderdream.entity.SentencePair;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
      String elapsedTime = CdTimeUtil.formatDuration(duration.toMillis());
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

  // int 转 n 位字符串，前面补0
  public static String int2N(int num, int n) {
    return String.format("%0" + n + "d", num);
  }


  // int 转 n 位字符串，前面补0
  public static String int2StN(int num, int n) {
    return String.format("%0" + n + "d", num);
  }

  /**
   * 替换字符串中指定模式的文本，模式为 ^数字^
   *
   * @param originalString 原始字符串
   * @param replacement    替换后的内容
   * @return 替换后的字符串
   */
  public static String replaceNumberPattern(String originalString,
    String replacement) {
    if (originalString == null || originalString.isEmpty()) {
      log.warn("原始字符串为空，无法进行替换操作");
      return originalString; // 如果输入为空，则直接返回
    }
    String regex = "\\^\\d+\\^"; //  ^数字^的正则表达式

    String replacedString = originalString.replaceAll(regex, replacement);
    log.info("字符串替换完成，原始字符串：{}，替换后的字符串：{}", originalString,
      replacedString);
    return replacedString;
  }


  /**
   * 判断字符串是否除了时间中的冒号外，只包含一个中文或英文冒号
   *
   * @param str 要判断的字符串
   * @return 如果符合条件返回 true，否则返回 false
   */
  public static boolean hasSingleColonOutsideTime(String str) {
    if (str == null || str.isEmpty()) {
      return false; // 空字符串或 null 返回 false
    }

    // 1. 移除时间中的冒号（使用正则表达式匹配 HH:mm:ss 或 HH:mm 格式）
    String timePattern = "(\\d{1,2}:\\d{2}(:\\d{2})?)";
    Pattern pattern = Pattern.compile(timePattern);
    Matcher matcher = pattern.matcher(str);
    String strWithoutTimeColons = matcher.replaceAll("");

    // 2. 计算剩余字符串中冒号的数量（中文或英文冒号）
    int colonCount = 0;
    for (int i = 0; i < strWithoutTimeColons.length(); i++) {
      char c = strWithoutTimeColons.charAt(i);
      if (c == ':' || c == '：') {
        colonCount++;
      }
    }

    // 3. 判断冒号的数量是否为 1
    return colonCount == 1;
  }

  /**
   * 判断字符串是否只包含一个中英文冒号
   *
   * @param str 待判断的字符串
   * @return true 如果字符串只包含一个中英文冒号，否则返回 false
   */
  public static boolean hasOneColon(String str) {
    if (str == null || str.isEmpty()) {
      return false; // 空字符串或null不符合条件
    }
    int count = 0;
    for (char c : str.toCharArray()) {
      if (c == ':' || c == '：') {
        count++;
      }
    }

    // 如果只有一个冒号，返回true
    return count == 1;
  }


  /**
   * 将字符串列表按照空行分割成多个字符串列表 将字符串列表按空行分割成字符串列表的列表。 空行被定义为 null 或者 经过 trim()
   * 后为空的字符串。
   *
   * @param inputList 待分割的字符串列表
   * @return 分割后的字符串列表的列表
   */
  public static List<List<String>> splitByEmptyLine(List<String> inputList) {
    if (inputList == null || inputList.isEmpty()) {
      return new ArrayList<>(); // 返回空列表
    }

    // 用于存储结果的列表，其中每个元素都是一个字符串列表
    List<List<String>> result = new ArrayList<>();
    // 用于临时存储当前子列表的列表
    List<String> currentList = new ArrayList<>();

    // 遍历输入列表中的每一行
    for (String line : inputList) {
      // 检查当前行是否是空行 (null 或 trim() 后为空)
      if (line == null || line.trim().isEmpty()) {
        // 如果当前子列表不为空，则将其添加到结果列表中
        if (!currentList.isEmpty()) {
          result.add(currentList);
        }
        // 创建一个新的空列表，开始构建下一个子列表
        currentList = new ArrayList<>();
      } else {
        // 如果当前行不是空行，则将其添加到当前子列表中
        currentList.add(line);
      }
    }

    // 处理输入列表末尾没有空行的情况，确保最后一个子列表也被添加到结果中
    if (!currentList.isEmpty()) {
      result.add(currentList);
    }

    // 返回分割后的字符串列表的列表
    return result;

  }

  /**
   * 查找字符串中第一个英文字符（A-Z 或 a-z）的索引。
   *
   * @param text 待查找的字符串
   * @return 如果找到，返回索引；否则返回 -1
   */
  public static int findFirstEnglishLetterIndex(String text) {
    Pattern pattern = Pattern.compile(
      "[A-Za-z]"); // 或者 Pattern.compile("[a-zA-Z]");
    Matcher matcher = pattern.matcher(text);
    if (matcher.find()) {
      return matcher.start();
    }
    return -1;
  }

  /**
   * 查找字符串中第一个中文字符或中文标点的索引 (不使用 \p{Punct})
   *
   * @param text 待查找的字符串
   * @return 如果找到，返回索引，否则返回 -1
   */
  public static int findFirstChineseCharOrPunctuationIndexWithoutPunct(
    String text) {
    Pattern pattern = Pattern.compile("[\u4e00-\u9fa5。？！，、；：“”‘’（）《》【】…—·]");
    Matcher matcher = pattern.matcher(text);
    if (matcher.find()) {
      return matcher.start();
    }
    return -1;
  }

  /**
   * 查找字符串中最后一个中文字符或中文标点的索引 (不使用 \p{Punct})
   *
   * @param text 待查找的字符串
   * @return 如果找到，返回索引，否则返回 -1
   */
  public static int findLastChineseCharOrPunctuationIndexWithoutPunct(
    String text) {
    Pattern pattern = Pattern.compile("[\u4e00-\u9fa5。？！，、；：“”‘’（）《》【】…—·]");
    Matcher matcher = pattern.matcher(text);
    int lastIndex = -1;
    while (matcher.find()) {
      lastIndex = matcher.start();
    }
    return lastIndex;
  }

  /**
   * 查找字符串中第一个中文字符或中文标点的索引
   *
   * @param text 待查找的字符串
   * @return 如果找到，返回索引，否则返回 -1
   */
  public static int findFirstChineseCharOrPunctuationIndex(String text) {
    Pattern pattern = Pattern.compile(
      "[\u4e00-\u9fa5\\p{Punct}。？！，、；：“”‘’（）《》【】…—·]");
    Matcher matcher = pattern.matcher(text);
    if (matcher.find()) {
      return matcher.start();
    }
    return -1;
  }


  /**
   * 查找字符串中最后一个中文字符或中文标点的索引
   *
   * @param text 待查找的字符串
   * @return 如果找到，返回索引，否则返回 -1
   */
  public static int findLastChineseCharOrPunctuationIndex(String text) {
    Pattern pattern = Pattern.compile(
      "[\u4e00-\u9fa5\\p{Punct}。？！，、；：“”‘’（）《》【】…—·]");
    Matcher matcher = pattern.matcher(text);
    int lastIndex = -1;
    while (matcher.find()) {
      lastIndex = matcher.start();
    }
    return lastIndex;
  }


  /**
   * 查找字符串中第一个中文字符的索引
   *
   * @param text 待查找的字符串
   * @return 如果找到，返回索引，否则返回 -1
   */
  public static int findFirstChineseCharIndex(String text) {
    Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]");
    Matcher matcher = pattern.matcher(text);
    if (matcher.find()) {
      return matcher.start();
    }
    return -1;
  }

  /**
   * 查找字符串中最后一个中文字符的索引
   *
   * @param text 待查找的字符串
   * @return 如果找到，返回索引，否则返回 -1
   */
  public static int findLastChineseCharIndex(String text) {
    Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]");
    Matcher matcher = pattern.matcher(text);
    int lastIndex = -1;
    while (matcher.find()) {
      lastIndex = matcher.start();
    }
    return lastIndex;
  }

  public static void main(String[] args) {
    String lineWithoutSequence = "该起床了！It's time to get up!";

    int lastChineseCharIndex = CdStringUtil.findLastChineseCharOrPunctuationIndexWithoutPunct(
      lineWithoutSequence);
    System.out.println("最后个中文字符的索引: " + lastChineseCharIndex);

    String chineseSentence = "";
    String englishSentence = "";
    //如果 lastChineseCharIndex 为 -1,则说明没有中文
    if (lastChineseCharIndex != -1) {
      chineseSentence = lineWithoutSequence.substring(0,
        lastChineseCharIndex + 1).trim(); // 需要加1,
      englishSentence = lineWithoutSequence.substring(
        lastChineseCharIndex + 1).trim();
    } else {
      englishSentence = lineWithoutSequence.trim(); // 没有中文，全部是英文
    }

//          log.debug("处理前的字符串：{}， 处理后的字符串：{}", line,
//            lineWithoutSequence);

    SentencePair sentencePairTemp = new SentencePair(englishSentence,
      chineseSentence);
    System.out.println(sentencePairTemp);
  }

  // 中文标点符号正则 (包含句号、逗号、问号、感叹号、分号、冒号等常见符号)
  private static final Pattern CHINESE_PUNCTUATION_PATTERN = Pattern.compile(
    "[，。！？；：]$");

  /**
   * 判断字符串是否以中文标点符号结尾，如果不是，补上中文句号。
   *
   * @param input 输入字符串
   * @return 处理后的字符串
   */
  public static String ensureEndsWithChinesePunctuation(String input) {
    if (input == null || input.isEmpty()) {
      return "。";
    }
    // 如果已以中文标点符号结尾，直接返回
    if (CHINESE_PUNCTUATION_PATTERN.matcher(input).find()) {
      return input;
    }
    // 否则追加中文句号
    return input + "。";
  }

  /**
   * 将超过80个字符的字符串按逗号分割，如果仍然超过80个字符，继续递归分割。
   *
   * @param input 输入字符串
   * @return 分割后的字符串列表
   */
  public static List<String> splitLongSentence(String input) {
    List<String> result = new ArrayList<>();
    if (input == null || input.isEmpty()) {
      result.add("");
      return result;
    }

    if (input.length() <= 20) {
      result.add(input);
      return result;
    }

    int mid = input.length() / 2;
    int commaIndex = input.lastIndexOf("，", mid);

    if (commaIndex == -1) {
      // 如果没有逗号，则直接硬切
      result.add(input);
    } else {
      // 按逗号分割
      result.addAll(splitLongSentence(input.substring(0, commaIndex + 1)));
      result.addAll(splitLongSentence(input.substring(commaIndex + 1)));
    }

    return result;
  }

  /**
   * 将超过80个字符的字符串按逗号分割，如果仍然超过80个字符，继续递归分割。
   *
   * @param input 输入字符串
   * @return 分割后的字符串列表
   */
  public static List<String> splitLongSentence(String input, int maxLength) {
    List<String> result = new ArrayList<>();
    if (input == null || input.isEmpty()) {
      result.add("");
      return result;
    }

    if (input.length() <= maxLength) {
      result.add(input);
      return result;
    }

    int mid = input.length() / 2;
    int commaIndex = input.lastIndexOf("，", mid);

    if (commaIndex == -1) {
      // 如果没有逗号，则直接硬切
      result.add(input);
    } else {
      // 按逗号分割
      result.addAll(
        splitLongSentence(input.substring(0, commaIndex + 1), maxLength));
      result.addAll(
        splitLongSentence(input.substring(commaIndex + 1), maxLength));
    }

    return result;
  }

  private static final String CHINESE_COMMA = "，";
  private static final String SPLIT_MARKER = "；";


  /**
   * 将超过80个字符的字符串按逗号分割，如果仍然超过80个字符，继续递归分割。
   *
   * @param input 输入字符串
   * @return 分割后的字符串列表
   */
  public static List<String> splitLongSentenceWithComma(String input) {
    List<String> result = new ArrayList<>();
    List<String> resultTemp = new ArrayList<>();

    String[] split = input.split(CHINESE_COMMA);
    for (String s : split) {
      if (s.length() > 80) {
        resultTemp.addAll(splitLongSentenceWithComma(s));
      } else {
        resultTemp.add(s);
      }
    }

    return result;
  }

  /**
   * 处理字符串列表，将字符串中间的斜线替换为空格，然后把多个空格替换为单个空格，保留首尾斜线。
   *
   * @param inputList 输入的字符串列表
   * @return 处理后的字符串列表
   */
  public static List<String> processStringList(List<String> inputList) {
    if (inputList == null || inputList.isEmpty()) {
      return new ArrayList<>();
    }

    List<String> resultList = new ArrayList<>();
    for (String str : inputList) {
      String processedString = processString(str);
      resultList.add(processedString);
    }

    return resultList;
  }

  /**
   * 处理单个字符串，将字符串中间的斜线替换为空格，然后把多个空格替换为单个空格，保留首尾斜线。
   *
   * @param str 输入的字符串
   * @return 处理后的字符串
   */
  public static String processString(String str) {
    if (str == null) {
      return null;
    }

    // 1. 处理首尾斜线: 保存首尾斜线，并从字符串中移除
    boolean startsWithSlash = str.startsWith("/");
    boolean endsWithSlash = str.endsWith("/");
    String tempStr = str;

    if (startsWithSlash) {
      tempStr = tempStr.substring(1);
    }
    if (endsWithSlash) {
      tempStr = tempStr.substring(0, tempStr.length() - 1);
    }

    // 2. 将字符串中间的斜线替换为空格
    String replacedString = tempStr.replace("/", " ");

    // 3. 将多个空格替换为单个空格 (使用正则表达式)
    Pattern pattern = Pattern.compile("\\s+"); // \s+ 表示一个或多个空白字符
    Matcher matcher = pattern.matcher(replacedString);
    String singleSpacedString = matcher.replaceAll(" ");

    // 4.  恢复首尾斜线
    if (startsWithSlash) {
      singleSpacedString = "/" + singleSpacedString;
    }
    if (endsWithSlash) {
      singleSpacedString = singleSpacedString + "/";
    }

    return singleSpacedString;
  }

  /**
   * 将驼峰命名法转换为短横线命名法
   * @param input 驼峰命名法的字符串
   * @return 短横线命名法的字符串
   */
  public static String convertToKebabCase(String input) {
    // 使用正则表达式匹配大写字母
    Pattern pattern = Pattern.compile("(?<=[a-z])(?=[A-Z])");

    // 使用正则表达式将大写字母前插入 "-"，然后将字符串转换为小写
    return pattern.matcher(input).replaceAll("-").toLowerCase();
  }
}
