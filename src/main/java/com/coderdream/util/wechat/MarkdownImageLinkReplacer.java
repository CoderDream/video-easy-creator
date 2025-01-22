package com.coderdream.util.wechat;

import com.coderdream.util.cd.CdFileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;


/**
 * 文本替换工具类，用于替换 Markdown 文件中的图片链接
 */
@Slf4j
public class MarkdownImageLinkReplacer {

  private static final int LINES_TO_SKIP = 10; // 需要跳过的行数
    /**
   * 替换 Markdown 文件中的图片链接
   *
   * @param sourceFilePath 源文件路径
   * @param targetFilePath 目标文件路径
   * @return 返回方法执行耗时，格式为 时:分:秒.毫秒
   */
  public String replaceImageLinks(String sourceFilePath,
      String targetFilePath) {
    String fileName = CdFileUtil.getPureFileNameWithoutExtensionWithPath(
        sourceFilePath);
    String TARGET_STRING = "](" + fileName + "/image";
    String REPLACEMENT_STRING =
        "](https://coderdream.github.io/2025/01/01/" + fileName + "/image";

    Instant start = Instant.now();
    log.info("开始替换图片链接，源文件：{}，目标文件：{}", sourceFilePath,
        targetFilePath);

    Path sourcePath = Paths.get(sourceFilePath);
    Path targetPath = Paths.get(targetFilePath);

    // 检查源文件是否存在
    if (!Files.exists(sourcePath)) {
      log.error("源文件不存在：{}", sourceFilePath);
      return formatDuration(Duration.between(start, Instant.now()));
    }

    try (BufferedReader reader = new BufferedReader(
        new FileReader(sourcePath.toFile()));
         BufferedWriter writer = new BufferedWriter(
            new FileWriter(targetPath.toFile()))) {

      // 跳过目标文件的前几行
        skipLines(reader,LINES_TO_SKIP);

      String line;
      while ((line = reader.readLine()) != null) {
        String replacedLine = line.replace(TARGET_STRING, REPLACEMENT_STRING);
        writer.write(replacedLine);
        writer.newLine();
      }
      log.info("图片链接替换完成");

    } catch (IOException e) {
      log.error("文件操作失败：", e);
    }
    return formatDuration(Duration.between(start, Instant.now()));
  }


  /**
   * 跳过指定行数
   * @param reader  BufferedReader
   * @param linesToSkip  需要跳过的行数
   * @throws IOException
   */
    private void skipLines(BufferedReader reader, int linesToSkip) throws IOException {
        for (int i = 0; i < linesToSkip; i++) {
            if (reader.readLine() == null) {
                // 如果已经读取到文件末尾，则退出循环
                break;
            }
        }
    }


  /**
   * 格式化时间间隔
   *
   * @param duration 时间间隔
   * @return 格式化的时间间隔
   */
  private String formatDuration(Duration duration) {
    long hours = duration.toHours();
    long minutes = duration.toMinutes() % 60;
    long seconds = duration.getSeconds() % 60;
    long milliseconds = duration.toMillis() % 1000;
    return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds,
        milliseconds);
  }


  public static void main(String[] args) {

    String fileName = "bai-ci-zan-002";

    String sourceFilePath =
        "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\"
            + fileName + ".md";
    String targetFilePath =
        "D:\\0000\\0003_wechat\\02_word\\" + fileName + "_for_wechat.md";

    MarkdownImageLinkReplacer replacer = new MarkdownImageLinkReplacer();
    String timeCost = replacer.replaceImageLinks(sourceFilePath,
        targetFilePath);
    log.info("总耗时：{}", timeCost);
  }
}
