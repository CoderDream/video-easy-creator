package com.coderdream.util.sentence;

import com.coderdream.entity.DialogSingleEntity;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于解析对话文本并生成对话实体列表的工具类
 */
@Slf4j
public class DialogParser {

  /**
   * 定义匹配中英文冒号的正则表达式
   */
  private static final Pattern COLON_PATTERN = Pattern.compile("[:：]");

  /**
   * 定义匹配第一个中文字符的正则表达式 Unicode 范围 \u4E00-\u9FA5 代表汉字
   */
  private static final Pattern CHINESE_CHAR_PATTERN = Pattern.compile(
    "[\u4E00-\u9FA5]");

  /**
   * 解析文本文件，将其转换为对话实体列表
   *
   * @param filePath 文本文件路径
   * @return 对话实体列表
   */
  public static List<DialogSingleEntity> parseDialogFile(String filePath) {
    // 记录方法开始时间
    Instant startTime = Instant.now();
    List<DialogSingleEntity> dialogList = new ArrayList<>();
    Path path = Paths.get(filePath);

    if (!Files.exists(path) || !Files.isRegularFile(path)) {
      log.error("文件不存在或不是一个有效的文件: {}", filePath);
      return dialogList;  // 返回空列表
    }
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.trim().isEmpty()) {
          continue; // 跳过空行
        }
        DialogSingleEntity dialogEntity = parseLine(line);
        if (dialogEntity != null) {
          dialogList.add(dialogEntity);
        }
      }
    } catch (IOException e) {
      log.error("解析文件时发生IO异常: {}", filePath, e);
    } finally {
      // 记录方法结束时间，并计算耗时
      Instant endTime = Instant.now();
      Duration duration = Duration.between(startTime, endTime);
      long seconds = duration.getSeconds();
      long milliseconds = duration.toMillis() - seconds * 1000;
      long minutes = seconds / 60;
      long remainingSeconds = seconds % 60;
      log.info("parseDialogFile 方法耗时：{}分{}秒{}毫秒", minutes,
        remainingSeconds, milliseconds);
    }
    return dialogList;
  }

  /**
   * 解析单行文本
   *
   * @param line 单行文本
   * @return 对话实体
   */
  private static DialogSingleEntity parseLine(String line) {
    // 查找冒号位置
    Matcher colonMatcher = COLON_PATTERN.matcher(line);
    if (!colonMatcher.find()) {
      log.warn("该行不包含冒号，跳过: {}", line);
      return null;
    }
    // 主持人（英文/中文一致）
    String host = line.substring(0, colonMatcher.start()).trim();

    // 查找第一个中文字符的位置
    String contentPart = line.substring(colonMatcher.end()).trim();
    Matcher chineseMatcher = CHINESE_CHAR_PATTERN.matcher(contentPart);
    if (!chineseMatcher.find()) {
      log.warn("该行不包含中文字符，跳过: {}", line);
      return null;
    }
    String contentEn = contentPart.substring(0, chineseMatcher.start()).trim();
    String contentCn = contentPart.substring(chineseMatcher.start()).trim();
    if (contentEn.isEmpty() || contentCn.isEmpty()) {
      log.warn("英文或中文内容为空，跳过: {}", line);
    }

    DialogSingleEntity dialogEntity = new DialogSingleEntity();
    dialogEntity.setHostEn(host);
    dialogEntity.setHostCn(host);
    dialogEntity.setContentEn(contentEn);
    dialogEntity.setContentCn(contentCn);
    return dialogEntity;
  }


  public static void main(String[] args) {
    String filePath = "D:\\0000\\EnBook001\\商务职场英语口语900句\\商务职场英语口语900句V1_ch0201.txt";
    List<DialogSingleEntity> dialogList = parseDialogFile(filePath);
    if (dialogList.size() > 0) {
      dialogList.forEach(dialog -> log.info("{}", dialog));
    } else {
      log.warn("解析后的列表为空");
    }
  }
}
