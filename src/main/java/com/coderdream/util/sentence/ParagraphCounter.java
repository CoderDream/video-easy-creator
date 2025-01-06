package com.coderdream.util.sentence;

import cn.hutool.core.io.FileUtil;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ParagraphCounter {

  public static void main(String[] args) {

    String filename = "D:\\0000\\EnBook001\\商务职场英语口语900句\\商务职场英语口语900句V1_ch01.txt_逐字稿V_407.txt";
    filename = "D:\\0000\\EnBook001\\商务职场英语口语900句\\商务职场英语口语900句V1_ch02.txt";
    File file = new File(filename);

    String jsonString = FileUtil.readString(file, StandardCharsets.UTF_8);

    Integer[] linesPerParagraph = ParagraphCounter.countLinesPerParagraph(jsonString);

    System.out.print("每段的行数: ");
    for (Integer lines : linesPerParagraph) {
      System.out.print(lines + " ");
    }
  }

  /**
   * 统计每段的行数
   *
   * @param text 要统计的文本
   * @return 一个 Integer 数组，其中每个元素代表对应段落的行数
   */
//  public static Integer[] countLinesPerParagraph(String text) {
//    if (text == null || text.isEmpty()) {
//      return new Integer[0]; // 返回一个空数组
//    }
//
//    String[] paragraphs = text.split("\r\n\r\n");
//    List<Integer> linesPerParagraphList = new ArrayList<>();
//
//    for (String paragraph : paragraphs) {
//      String[] lines = paragraph.split("\r\n");
//      linesPerParagraphList.add(lines.length);
//    }
//
//    // 将 List<Integer> 转换为 Integer[]
//    return linesPerParagraphList.toArray(new Integer[0]);
//  }

  private static final Pattern PARAGRAPH_SPLIT_PATTERN = Pattern.compile("(\r\n){2}|\n{2}");
  private static final Pattern LINE_SPLIT_PATTERN = Pattern.compile("\r\n|\n");

  public static Integer[] countLinesPerParagraph(String text) {
    if (text == null || text.isEmpty()) {
      return new Integer[0];
    }

    String[] paragraphs = PARAGRAPH_SPLIT_PATTERN.split(text);
    List<Integer> linesPerParagraphList = new ArrayList<>();

    for (String paragraph : paragraphs) {
      String[] lines = LINE_SPLIT_PATTERN.split(paragraph);
      linesPerParagraphList.add(lines.length);
    }

    return linesPerParagraphList.toArray(new Integer[0]);
  }
}
