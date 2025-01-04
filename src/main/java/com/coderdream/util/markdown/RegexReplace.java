package com.coderdream.util.markdown;

import java.util.regex.Pattern;

public class RegexReplace {

  public static void main(String[] args) {
    String text1 = "^[123]^这是一段文字^[4567]^";
    String text2 = "^[0]^这是开头^[987654321]^中间^[12]^结尾";
    String text3 = "没有匹配项";

    String regex = "\\^\\[\\d+\\]\\^"; // 修改后的正则表达式，移除开头的 ^

    String result1 = text1.replaceAll(regex, "");
    String result2 = text2.replaceAll(regex, "");
    String result3 = text3.replaceAll(regex, "");

    System.out.println("原字符串1: " + text1);
    System.out.println("替换后字符串1: " + result1);
    System.out.println("------------------");
    System.out.println("原字符串2: " + text2);
    System.out.println("替换后字符串2: " + result2);
    System.out.println("------------------");
    System.out.println("原字符串3: " + text3);
    System.out.println("替换后字符串3: " + result3);
  }
}
