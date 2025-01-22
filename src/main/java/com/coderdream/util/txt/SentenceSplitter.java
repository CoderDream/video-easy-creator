package com.coderdream.util.txt;

import com.coderdream.entity.SentencePair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 句子对提取工具类 用于将字符串分割成中英文句子对
 */
@Slf4j
public class SentenceSplitter {
  /**
   * 将字符串分割成中英文句子对，考虑到中英文标点和中文包含数字
   *
   * @param text 待分割的字符串
   * @return 解析后的句子对，如果无法分割返回 null
   */
  public static SentencePair splitToSentencePair(String text) {
    if (text == null || text.trim().isEmpty()) {
      log.warn("输入的字符串为空，无法进行分割.");
      return null;
    }
    log.debug("开始分割字符串：{}", text);
    // 定义匹配中文字符、数字和中文标点的正则表达式
    Pattern chinesePattern = Pattern.compile(
      "[\u4e00-\u9fa50-9\\p{Punct}。？！，、；：“”‘’（）《》【】…—·]+");
    //定义匹配英文字符和英文标点的正则表达式
    Pattern englishPattern = Pattern.compile("[a-zA-Z0-9\\p{Punct}\\s]+");

    List<String> parts = new ArrayList<>();
    Matcher chineseMatcher = chinesePattern.matcher(text);
    Matcher englishMatcher = englishPattern.matcher(text);
    int lastIndex = 0;

    while (englishMatcher.find() || chineseMatcher.find()) {
      if (englishMatcher.find()) {
        if (englishMatcher.start() > lastIndex) {  // 英文匹配，但是不是从上次最后索引开始的
          String nonMatch = text.substring(lastIndex, englishMatcher.start());
          log.debug("非中英文匹配， 字符串: {}, index {}, part {}", text,
            lastIndex, nonMatch);
          parts.add(nonMatch.trim());
        }

        String englishPart = englishMatcher.group().trim();
        log.debug("英文匹配， 字符串: {}, index {}, part {}", text,
          englishMatcher.start(), englishPart);
        parts.add(englishPart);
        lastIndex = englishMatcher.end();
      }
      if (chineseMatcher.find()) {
        if (chineseMatcher.start() > lastIndex) { //中文匹配，但是不是从上次最后索引开始的
          String nonMatch = text.substring(lastIndex, chineseMatcher.start());
          log.debug("非中英文匹配， 字符串: {}, index {}, part {}", text,
            lastIndex, nonMatch);
          parts.add(nonMatch.trim());
        }
        String chinesePart = chineseMatcher.group().trim();
        log.debug("中文匹配， 字符串: {}, index {}, part {}", text,
          chineseMatcher.start(), chinesePart);
        parts.add(chinesePart);
        lastIndex = chineseMatcher.end();
      }
    }

    if (lastIndex < text.length()) {
      String remain = text.substring(lastIndex).trim();
      log.debug("非中英文匹配， 字符串: {}, index {}, part {}", text, lastIndex,
        remain);
      parts.add(remain);
    }
    log.debug("分割后的字符串列表：{}", parts);

    String englishSentence = "";
    String chineseSentence = "";

    boolean startsWithChinese = startsWithChinese(text);

    for (String part : parts) {
      if (englishPattern.matcher(part).matches()) {
        if (startsWithChinese) {
          englishSentence += part + " ";
        } else {
          englishSentence += part + " ";
        }
      } else if (chinesePattern.matcher(part).matches()) {
        if (startsWithChinese) {
          chineseSentence += part + " ";
        } else {
          chineseSentence += part + " ";
        }

      } else {
        log.warn("忽略未知类型的字符串：{}", part);
      }
    }

    englishSentence = englishSentence.trim();
    chineseSentence = chineseSentence.trim();
    if (startsWithChinese) {
      log.info("分割完成，中文在前， 中文：{}， 英文：{}", chineseSentence,
        englishSentence);
      return new SentencePair(englishSentence, chineseSentence);
    } else {
      log.info("分割完成，英文在前， 英文：{}， 中文：{}", englishSentence,
        chineseSentence);
      return new SentencePair(englishSentence, chineseSentence);
    }


  }


  /**
   * 判断字符串是否以中文字符开头
   *
   * @param text 待判断的字符串
   * @return 如果字符串以中文字符开头，返回true，否则返回 false
   */
  private static boolean startsWithChinese(String text) {
    if (text == null || text.isEmpty()) {
      return false;
    }
    int firstChineseIndex = findFirstChineseCharIndex(text);
    return firstChineseIndex == 0;
  }


  /**
   * 查找字符串中第一个中文字符的索引
   *
   * @param text 待查找的字符串
   * @return 如果找到，返回索引，否则返回 -1
   */
  private static int findFirstChineseCharIndex(String text) {
    Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]");
    Matcher matcher = pattern.matcher(text);
    if (matcher.find()) {
      return matcher.start();
    }
    return -1;
  }


  public static void main(String[] args) {
    String str1 = "Hello, 你好！世界.";
    String str2 = "你好，世界！Hello.";
    String str3 = "你好，世界！";
    String str4 = "Hello world!";
    String str5 = "This is a test. 这是一个测试。";
    String str6 = "你好,世界! Hello,world!";
    String str7 = "。你好，世界！。hello,world.";
    String str8 = "hello,world.，你好，世界！";
    String str9 = "你好世界hello world!";
    String str10 = " hello world!,你好世界!";
    String str11 = "I usually go to bed at ten, but last night I went to bed at eleven. 我一般都10点睡觉，但昨晚我11点睡的。";

//        SentencePair pair1 = splitToSentencePair(str1);
//        printSentencePair(str1,pair1);
//
//        SentencePair pair2 = splitToSentencePair(str2);
//        printSentencePair(str2,pair2);
//
//        SentencePair pair3 = splitToSentencePair(str3);
//        printSentencePair(str3,pair3);
//
//        SentencePair pair4 = splitToSentencePair(str4);
//        printSentencePair(str4,pair4);
//
//        SentencePair pair5=splitToSentencePair(str5);
//        printSentencePair(str5,pair5);
//
//        SentencePair pair6=splitToSentencePair(str6);
//        printSentencePair(str6,pair6);
//        SentencePair pair7=splitToSentencePair(str7);
//        printSentencePair(str7,pair7);
//
//        SentencePair pair8=splitToSentencePair(str8);
//        printSentencePair(str8,pair8);
//
//        SentencePair pair9=splitToSentencePair(str9);
//        printSentencePair(str9,pair9);
//
//        SentencePair pair10=splitToSentencePair(str10);
//        printSentencePair(str10,pair10);

    SentencePair pair11 = splitToSentencePair(str11);
    printSentencePair(str11, pair11);

  }

  private static void printSentencePair(String input, SentencePair pair) {
    if (pair != null) {
      System.out.println(
        "输入字符串：" + input + ",  中文: " + pair.getChineseSentence() + " , 英文: "
          + pair.getEnglishSentence());
    } else {
      System.out.println("输入字符串：" + input + ",  分割失败!");
    }
  }
}
