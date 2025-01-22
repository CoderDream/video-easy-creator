package com.coderdream.util.markdown;

import com.coderdream.util.markdown.my.MarkdownBean;
import com.coderdream.util.mdict.dict.parser.WordUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
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
 * 用于解析 Markdown 文件并存储到 MarkdownBean 中的工具类
 */
@Slf4j
public class MarkdownWordUtil {

  /**
   * 解析 Markdown 文件，将数据存储到 List<String> 中
   * @param markdownFilePath Markdown文件路径
   * @return Markdown 文件行内容
   */
  public static List<String> parseMarkdownFile(String markdownFilePath) {
    Instant start = Instant.now(); // 记录方法开始时间
    List<String> contentLines = new ArrayList<>(); // 初始化存储markdown文件行内容的列表
    MarkdownBean markdownBean = new MarkdownBean(); // 创建 MarkdownBean 对象

    try {
      String markdownContent = readFileContent(markdownFilePath); // 读取 Markdown 文件内容
      // 解析 front matter
      String frontMatter = extractFrontMatter(markdownContent);
      if (frontMatter != null && !frontMatter.isEmpty()) {
        markdownBean.setDescription(frontMatter); // 将 front matter 设置到 MarkdownBean
      }

      // 解析单词信息
      String[] lines = markdownContent.split("\\r?\\n"); // 使用换行符分割markdown文件内容
      Pattern h4Pattern = Pattern.compile("####\\s+(.+)"); // 匹配 #### 标题行

      for (String line : lines) {
        Matcher h4Matcher = h4Pattern.matcher(line); // 创建 matcher 对象用于匹配标题行

        if (h4Matcher.find()) { // 如果匹配到了标题行
          log.info("Found h4: {}", line); // 记录日志
          String[] split = line.split(" "); // 分割标题行
          if (split.length == 3) { // 如果标题行分割后的长度等于3
            String word = split[2]; // 获取单词
            String wordPhonetics = WordUtil.getWordPhonetics(word); // 获取单词音标
            log.info("word: {}, phonetic: {}", word, wordPhonetics); // 记录单词和音标日志
            contentLines.add(line + " [" + wordPhonetics + "] "); // 将带有音标的标题行添加到列表
          } else {
            contentLines.add(line); // 将非标题行添加到列表
            log.info("这一行单词格式有问题 h4: {}", line); // 记录日志
          }
        } else {
//          log.info("No h4: {}", line); // 记录非标题行日志
          contentLines.add(line); // 将非标题行添加到列表
        }
      }

    } catch (IOException e) {
      log.error("Error reading or parsing file: {}", markdownFilePath, e);
    }

    Instant end = Instant.now(); // 记录方法结束时间
    log.info("Method parseMarkdownFile execute time: {}", formatDuration(Duration.between(start, end)));
    return contentLines; // 返回 MarkdownBean 对象
  }

  /**
   * 读取文件内容
   * @param filePath 文件路径
   * @return 文件内容字符串
   * @throws IOException 读取文件失败时抛出异常
   */
  private static String readFileContent(String filePath) throws IOException {
    Path path = Paths.get(filePath);
    try {
      return Files.readString(path); // 使用 try-with-resources 读取文件内容
    } catch (IOException e) {
      log.error("Error reading file: {}", filePath, e);
      throw e; // 抛出异常
    }
  }

  /**
   * 提取 front matter 部分
   * @param markdownContent 文件内容
   * @return front matter 部分字符串
   */
  private static String extractFrontMatter(String markdownContent) {
    Pattern pattern = Pattern.compile("---(.*?)---", Pattern.DOTALL); // 匹配 --- 开头的 front matter
    Matcher matcher = pattern.matcher(markdownContent);
    if (matcher.find()) {
      return matcher.group(1).trim(); // 返回提取的 front matter
    }
    return null; // 如果没有匹配到，返回 null
  }


  /**
   * 解析 front matter 部分，并将数据存入 MarkdownBean
   * @param frontMatter  front matter 部分内容
   * @param markdownBean MarkdownBean 对象
   */
  private static void parseFrontMatter(String frontMatter,
    MarkdownBean markdownBean) {
    markdownBean.setDescription(frontMatter); // 将 front matter 设置到 MarkdownBean
  }

  /**
   * 格式化时间
   * @param duration 时间长度
   * @return 时分秒字符串
   */
  private static String formatDuration(Duration duration) {
    long seconds = duration.getSeconds(); // 获取时间长度的秒数
    long absSeconds = Math.abs(seconds);  // 获取绝对秒数
    String positive = String.format("%02d:%02d:%02d", absSeconds / 3600, (absSeconds % 3600) / 60, absSeconds % 60);
    return seconds < 0 ? "-" + positive : positive; // 如果时间是负数，则返回负号加时间字符串，否则返回时间字符串
  }


  /**
   * 将 Markdown 文件解析，并添加音标到相应单词后
   * @param markdownFilePath Markdown 文件路径
   */
  public static void fillWordPhonetics(String markdownFilePath) {
    Instant start = Instant.now(); // 记录方法开始时间
    List<String> contentLines = MarkdownWordUtil.parseMarkdownFile(markdownFilePath); // 解析 Markdown 文件
    File markdownFile = new File(markdownFilePath); // 创建文件对象
    String fileName = markdownFile.getAbsolutePath(); // 获取完整文件名（含文件夹）
    // 新名字为 _output
    String outputFile = fileName.replace(".md", "_output.md"); // 生成输出文件名

    try {
      writeOutputToFile(outputFile, contentLines); // 将解析后的内容写入输出文件
    } catch (IOException e) {
      log.error("Error writing to output file: {}", outputFile, e);
    }
    Instant end = Instant.now(); // 记录方法结束时间
    log.info("Method fillWordPhonetics execute time: {}", formatDuration(Duration.between(start, end)));
  }


  /**
   * 将解析后的内容写入输出文件
   * @param outputFile 输出文件路径
   * @param contentLines 内容行列表
   * @throws IOException 写入文件失败时抛出异常
   */
  private static void writeOutputToFile(String outputFile, List<String> contentLines) throws IOException {
    Path outputPath = Paths.get(outputFile);
    try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) { // 使用 try-with-resources 创建 BufferedWriter
      for (String line : contentLines) {
        writer.write(line); // 写入文件行
        writer.newLine(); // 换行
      }
      log.info("Successfully wrote to output file: {}", outputFile);
    } catch (IOException e) {
      log.error("Error writing to file: {}", outputFile, e);
      throw e;
    }
  }

  public static void main(String[] args) {
    String markdownFilePath = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\bai-ci-zan-001.md"; // Markdown 文件路径
    MarkdownWordUtil.fillWordPhonetics(markdownFilePath); // 执行添加音标
  }
}
