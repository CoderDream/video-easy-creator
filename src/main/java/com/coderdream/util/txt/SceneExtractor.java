package com.coderdream.util.txt;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 场景提取工具类 用于从文本文件中提取以 "Scene X" 开头的场景序号
 */
@Slf4j
public class SceneExtractor {

  /**
   * 从字符串中提取场景序号
   *
   * @param line 包含场景序号的字符串，例如: "Scene 1"
   * @return 如果找到场景序号，返回对应的整数，否则返回 null
   */
  public static Integer extractSceneNumberFromString(String line) {
    Pattern pattern = Pattern.compile("^Scene\\s+(\\d+)"); // 定义场景行匹配的正则表达式
    Matcher matcher = pattern.matcher(line);

    if (matcher.find()) {
      String sceneNumberStr = matcher.group(1);
      try {
        return Integer.parseInt(sceneNumberStr);
      } catch (NumberFormatException e) {
        log.error("无法将提取到的场景序号转换为整数: {}", sceneNumberStr, e);
        return null; // 转换失败，返回null
      }

    }
    return null;  //没有匹配到，返回null
  }


  /**
   * 从指定文件中提取场景序号
   *
   * @param filePath 文件路径
   * @return 场景序号列表
   */
  public static List<Integer> extractSceneNumbers(String filePath) {
    Instant start = Instant.now(); // 记录方法开始时间
    log.info("开始提取文件中的场景序号，文件路径: {}", filePath);

    List<Integer> sceneNumbers = new ArrayList<>();
    Path path = Paths.get(filePath);

    try (BufferedReader reader = new BufferedReader(
      new FileReader(path.toFile()))) {
      String line;

      while ((line = reader.readLine()) != null) {
        Integer sceneNumber = SceneExtractor.extractSceneNumberFromString(line);
        if (sceneNumber != null) {
          sceneNumbers.add(sceneNumber);
          log.debug("发现场景：{}", sceneNumber); //使用debug级别日志，在生产中不显示
        }
      }
    } catch (IOException e) {
      log.error("读取文件时发生异常: {}", e.getMessage(), e);
      return new ArrayList<>();// 如果发生异常，返回一个空的列表，避免NullPointerException
    }

    Instant end = Instant.now();  // 记录方法结束时间
    Duration duration = Duration.between(start, end); // 计算方法执行耗时
    long milliseconds = duration.toMillis();
    long seconds = milliseconds / 1000;
    long minutes = seconds / 60;
    long hours = minutes / 60;

    log.info("场景提取完成，耗时：{}时{}分{}秒{}毫秒，提取到的场景序号: {}",
      hours,
      minutes % 60,
      seconds % 60,
      milliseconds % 1000,
      sceneNumbers);
    return sceneNumbers;
  }


  public static void main(String[] args) {
    // 测试用例
    String filePath = "D:\\0000\\EnBook002\\Chapter001\\Chapter001.txt";  // 替换为您的实际文件路径
    // String filePath="D:\\0000\\EnBook002\\Chapter001\\test.txt"; //测试用例，请自行修改

    List<Integer> sceneNumbers = SceneExtractor.extractSceneNumbers(filePath);

    if (sceneNumbers != null) {
      System.out.println("提取到的场景序号列表：");
      sceneNumbers.forEach(System.out::println);
    }
  }
}
