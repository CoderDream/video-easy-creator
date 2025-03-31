package com.coderdream.util.cd;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TextProcessor {

//    public static void main(String[] args) {
//        String fileName = "D:\\14_LearnEnglish\\6MinuteEnglish\\2018\\180503\\eng_raw.srt";
//        try {
//            String result = TextProcessor.processFile(fileName);
//            if (result != null) {
//                System.out.println(result);
//            } else {
//                System.out.println("未找到符合条件的字符串。");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

  /**
   * 解析字幕文件，提取第一部分和第二部分的时间戳。
   *
   * @param fileName 字幕文件的路径。
   * @return 第一部分和第二部分的时间戳，以制表符分隔。
   */
  public static String processFile(String fileName) {
    List<String> lines;
    try {
      lines = readLinesFromFile(fileName);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    String firstPart = findFirstPart(lines);
    String secondPart = findSecondPart(lines);

    if (firstPart != null && secondPart != null) {
      return firstPart + "\t" + secondPart;
    } else {
      return null;
    }
  }

  private static List<String> readLinesFromFile(String fileName)
    throws IOException {
    List<String> lines = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(
      new FileReader(fileName))) {
      String line;
      while ((line = reader.readLine()) != null) {
        lines.add(line);
      }
    }
    return lines;
  }

  private static String findFirstPart(List<String> lines) {
    String previousLine = null;
    for (String line : lines) {
      if (line.contains("I'm") || line.contains("I’m") || line.contains("I ")
        || line.contains("Er ")) {
        if (previousLine != null && previousLine.length() >= 12) {
          return previousLine.substring(0, 12);
        }
      }
      previousLine = line;
    }
    log.error("未找到符合条件的字符串。");
    return null;
  }

  private static String findSecondPart(List<String> lines) {
    int secondMarkerCount = 0;
    int secondMarkerIndex = -1;

    for (int i = lines.size() - 1; i >= 0; i--) {
      if (lines.get(i).contains("-->")) {
        secondMarkerCount++;
        if (secondMarkerCount == 2) {
          secondMarkerIndex = i;
          break;
        }
      }
    }

    if (secondMarkerIndex != -1
      && lines.get(secondMarkerIndex).length() >= 12) {
      String originalTime = lines.get(secondMarkerIndex).substring(0, 12);
      String endTime = lines.get(secondMarkerIndex).substring(17);

      String updatedTime = "";
      //nextLine
      String nextLine = lines.get(secondMarkerIndex + 1);
      int timePeriod = 300;
      if (nextLine.length() > 4) {
        timePeriod = 800;
      }
      updatedTime = addTimePeriod(originalTime, timePeriod);
      if (!nextLine.toLowerCase().startsWith("bye")) { // Bye
        updatedTime = endTime;
      }

      return updatedTime; //lines.get(secondMarkerIndex)                .substring(lines.get(secondMarkerIndex).length() - 12);
    }

    return null;
  }

  /**
   * 给定时间字符串增加时间间隔并返回新的时间字符串
   *
   * @param timeTag    原始时间字符串，格式为"HH:mm:ss,SSS"
   * @param timePeriod 增加的时间间隔（毫秒）
   * @return 更新后的时间字符串
   */
  public static String addTimePeriod(String timeTag, Integer timePeriod) {
    // 解析时间字符串为DateTime对象
    DateTime dateTime = DateUtil.parse(timeTag, "HH:mm:ss,SSS");

    // 增加时间间隔（毫秒）
    dateTime = dateTime.offset(DateField.MILLISECOND, timePeriod);

    // 格式化输出为所需的时间字符串格式
    return dateTime.toString("HH:mm:ss,SSS");
  }

  public static void main(String[] args) {
    String originalTime = "00:05:55,640";
    int timePeriod = 123;
    String updatedTime = addTimePeriod(originalTime, timePeriod);
    System.out.println("更新后的时间: " + updatedTime);
  }
}
