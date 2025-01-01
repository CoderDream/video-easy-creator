package com.coderdream.util.markdown;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.time.Duration;
import java.time.Instant;

/**
 * MarkdownWordUtil 单元测试类
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // 使用 @Order 注解排序测试方法
class MarkdownWordUtilTest {

  /**
   * 测试 fillWordPhonetics 方法，使用 bai-ci-zan-002.md 文件
   */
  @Test
  @Order(1)
  void fillWordPhonetics_02() {
    String markdownFilePath = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\bai-ci-zan-002.md"; // Markdown 文件路径
    runAndLogTest("fillWordPhonetics_02", markdownFilePath);
  }

  /**
   * 测试 fillWordPhonetics 方法，使用 bai-ci-zan-003.md 文件
   */
  @Test
  @Order(2)
  void fillWordPhonetics_03() {
    String markdownFilePath = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\bai-ci-zan-003.md"; // Markdown 文件路径
    runAndLogTest("fillWordPhonetics_03", markdownFilePath);
  }


  /**
   * 运行测试并记录日志，包含耗时信息
   *
   * @param testName         测试名称
   * @param markdownFilePath Markdown 文件路径
   */
  private void runAndLogTest(String testName, String markdownFilePath) {
    Instant start = Instant.now(); // 记录方法开始时间
    log.info("Start testing {} with file: {}", testName, markdownFilePath);
    MarkdownWordUtil.fillWordPhonetics(markdownFilePath); // 执行添加音标
    Instant end = Instant.now(); // 记录方法结束时间
    log.info("Finished testing {} with file: {}, execute time: {}", testName,
      markdownFilePath, formatDuration(Duration.between(start, end)));
  }


  /**
   * 格式化时间
   *
   * @param duration 时间长度
   * @return 时分秒字符串
   */
  private String formatDuration(Duration duration) {
    long seconds = duration.getSeconds(); // 获取时间长度的秒数
    long absSeconds = Math.abs(seconds);  // 获取绝对秒数
    return String.format("%02d:%02d:%02d", absSeconds / 3600,
      (absSeconds % 3600) / 60,
      absSeconds % 60); // 如果时间是负数，则返回负号加时间字符串，否则返回时间字符串
  }
}
