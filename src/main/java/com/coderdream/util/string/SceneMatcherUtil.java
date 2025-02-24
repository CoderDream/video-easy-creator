package com.coderdream.util.string;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SceneMatcherUtil {

  /**
   * 匹配字符串开头部分 "Scene" 或 "場景" 后面跟着数字，再跟空格的位置，并在空格前添加逗号。
   *
   * @param input 字符串输入
   * @return 如果找到匹配，返回添加逗号后的字符串；没有找到则返回原字符串
   */
  public static String addCommaBeforeSpace(String input) {
    // 正则表达式，匹配 "Scene " 或 "場景 " 后跟 1 或 3 位数字和一个空格，要求匹配字符串开头
    String regex = "^(Scene|場景)\\s(\\d{1,3})\\s";

    // 创建 Pattern 和 Matcher 对象
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(input);

    // 查找第一个匹配项
    if (matcher.find()) {
      // 获取匹配的场景类型（Scene 或 場景）
      String sceneType = matcher.group(1);

      // 根据场景类型替换不同的逗号类型，这里用的是英文句号和中文句号的区别
      String replacement = sceneType.equals("Scene") ? "." : "。";

      // 将空格前加逗号，逗号后仍然保留空格
      String modifiedString =
        input.substring(0, matcher.end(2)) + replacement + " "
          + input.substring(matcher.end(2));
      return modifiedString;
    }

    return input;  // 没有匹配项，返回原字符串
  }

  public static void main(String[] args) {
    // 测试数据
    String[] testStrings = {
      "Scene 76 Satisfaction",
      "場景 76 滿足",
      "Scene 123 Some text",
      "場景 123 Some content",
      "Scene 9 This won't match",
      "場景 9 This won't match",
      "Some random text Scene 99 here"
    };

    // 测试每个字符串
    for (String testString : testStrings) {
      String modifiedString = SceneMatcherUtil.addCommaBeforeSpace(testString);
      System.out.println(
        "原字符串: '" + testString + "' -> 添加逗号后: '" + modifiedString
          + "'");
    }
  }
}
