package com.coderdream.util.wechat;

import cn.hutool.core.date.DateUtil;
import com.coderdream.util.cd.CdConstants;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MarkdownSplitterAdvanced2 {

  private static final Pattern IMAGE_PATTERN = Pattern.compile(
    "!\\[(.*?)]\\((.*?)\\)");
  private static final Path ROOT_PATH = Paths.get(
    "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts");
  private static final Path INPUT_FILE_PATH = ROOT_PATH.resolve(
    "bai-ci-zan.md");

  /**
   * 处理 Markdown 文件，提取指定日期范围内的内容，并生成新的 Markdown 文件。
   *
   * @param targetDate 目标日期
   * @param dayNumber  天数序号
   * @param title      文章标题
   * @param tags       文章标签
   */
  public static void processMarkdown(Date targetDate, Integer dayNumber,
    String title, List<String> tags) {
    // 1. 构造起始日期和结束日期字符串
    String startDateLine = String.format("### %s",
      DateUtil.format(targetDate, "yyyy-MM-dd"));
    Date nextDay = DateUtil.offsetDay(targetDate, 1); // 计算下一天
    String endDateLine = String.format("### %s",
      DateUtil.format(nextDay, "yyyy-MM-dd"));

    // 2. 构建输出文件路径和图片输出路径
    Path outputDir = INPUT_FILE_PATH.getParent(); // 输出目录为输入文件所在目录
    String dayNumberString = String.format("%03d", dayNumber); // 格式化天数序号为3位字符串
    Path outputFilePath = outputDir.resolve(
      "bai-ci-zan-" + dayNumberString + ".md"); // 输出文件路径
    Path imageOutputDir = outputDir.resolve(
      "bai-ci-zan-" + dayNumberString); // 图片输出目录

    List<String> extractedContent = new ArrayList<>(); // 用于存储提取的内容
    boolean inSection = false; // 标记是否在目标日期内容区域内
    String line; // 读取的每一行内容

    // 3. 读取输入文件，提取目标日期范围内的数据
    try (BufferedReader reader = Files.newBufferedReader(INPUT_FILE_PATH)) {
      while ((line = reader.readLine()) != null) {
        if (line.trim().equals(startDateLine)) {
          inSection = true;
          continue;
        }
        if (line.trim().equals(endDateLine)) {
          inSection = false;
          break; // 结束行之后的内容不需要了
        }
        if (inSection) {
          extractedContent.add(line); // 将内容行添加到提取列表
        }
      }
    } catch (IOException e) {
      log.error("Error reading input file: {}", INPUT_FILE_PATH,
        e); // 记录读取文件错误日志
      return; // 读取失败，直接返回
    }

    // 4. 检查是否提取到内容，如果为空，则输出提示信息并返回
    if (extractedContent.isEmpty()) {
      log.warn("No matching content found for date: {}", targetDate); // 记录警告日志
      System.out.println(
        "No matching content found for date: " + targetDate); // 输出提示信息
      return;
    }

    // 5. 生成新的 Markdown 文件
    try (BufferedWriter writer = Files.newBufferedWriter(outputFilePath)) {
      // 写入描述信息
      writer.write("---");
      writer.newLine();

      // 写入标题，时间，标签，分类
      writer.write("title: 考研词汇精选-" + dayNumberString);
      writer.newLine();
      // 写入标题，时间，标签，分类
      writer.write("index_img: /images/yasicihui.png");
      writer.newLine();

      Date baseDate = DateUtil.parseDate(
        CdConstants.BAI_CI_ZAN_START_TIME); // 基准时间
      Date articleDate = DateUtil.offsetSecond(baseDate,
        dayNumber * 10); // 计算文章发布时间
      writer.write(
        "date: " + DateUtil.format(articleDate, "yyyy-MM-dd HH:mm:ss"));
      writer.newLine();
      writer.write("tags: ");
      writer.newLine();
      writer.write("    " + String.join(", ", tags));
      writer.newLine();
      writer.write("categories: ");
      writer.newLine();
      writer.write("    " + "百词斩");
      writer.newLine();
      writer.write("---");
      writer.newLine();

      List<String> updatedContent = new ArrayList<>(); // 存储更新后的内容
      // 遍历提取的内容，处理图片路径，并写入新文件
      for (String contentLine : extractedContent) {
        String updatedLine = copyImagesAndChangePath(contentLine,
          INPUT_FILE_PATH, imageOutputDir, outputFilePath.getParent());
        updatedContent.add(updatedLine);
        writer.write(updatedLine); // 将更新后的内容写入新文件
        writer.newLine();
      }
    } catch (IOException e) {
      log.error("Error writing to output file: {}", outputFilePath,
        e); // 记录写入文件错误日志
    }

    log.info("Created file: {}", outputFilePath); // 记录文件创建日志
    log.info("Created image directory: {}", imageOutputDir); // 记录图片目录创建日志
    System.out.println("Created file: " + outputFilePath);  // 输出文件创建信息
    System.out.println(
      "Created image directory: " + imageOutputDir); // 输出图片目录创建信息
  }

//  public static void processMarkdown(Date targetDate, Integer dayNumber,
//    String title, List<String> tags) throws IOException {
//    String startDateLine = String.format("### %s", targetDate);
////    Date startDate = DateUtil.parseDate(targetDate);
//    Date nextDay = DateUtil.offsetDay(targetDate, 1);
//    String endDateLine = String.format("### %s",
//      DateUtil.format(nextDay, "yyyy-MM-dd"));
//
//    Path outputDir = INPUT_FILE_PATH.getParent();
//    String dayNumberString = String.format("%03d", dayNumber);
//    Path outputFilePath = outputDir.resolve(
//      "bai-ci-zan-" + dayNumberString + ".md");
//    Path imageOutputDir = outputDir.resolve("bai-ci-zan-" + dayNumberString);
//
//    List<String> extractedContent = new ArrayList<>();
//    boolean inSection = false;
//    String line;
//
//    try (BufferedReader reader = Files.newBufferedReader(INPUT_FILE_PATH)) {
//      while ((line = reader.readLine()) != null) {
//        if (line.trim().equals(startDateLine)) {
//          inSection = true;
//          continue;
//        }
//        if (line.trim().equals(endDateLine)) {
//          inSection = false;
//          break;//结束行之后的内容不需要了
//        }
//        if (inSection) {
//          extractedContent.add(line);
//        }
//      }
//    }
//
//    if (extractedContent.isEmpty()) {
//      System.out.println("No matching content found for date: " + targetDate);
//      return;
//    }
//
//    // 生成新的 Markdown 文件
//    // title: 百词斩
//    //index_img: /images/duolingo.jpg
//    //date: 2024-03-24 09:20:39
//    //comments: true
//    //tags:
//    //	- English
//    //categories:
//    //    - 百词斩
//    try (BufferedWriter writer = Files.newBufferedWriter(outputFilePath)) {
//      //写入描述信息
//      writer.write("---");
//      writer.newLine();
//      // 期数
//      writer.write("title: 考研词汇精选-" + targetDate);
//      writer.newLine();
//      // 时间：基准时间+期数
//      Date baseDate = DateUtil.parseDate(CdConstants.BAI_CI_ZAN_START_TIME);
//      Date articleDate = DateUtil.offsetSecond(baseDate, dayNumber * 10);
//      writer.write(
//        "date: " + DateUtil.format(articleDate, "yyyy-MM-dd HH:mm:ss"));
//      writer.newLine();
//      writer.write("tags: ");
//      writer.write("\t" + String.join(", ", tags));
//      writer.newLine();
//      writer.write("categories: ");
//      writer.write("\t" + "百词斩");
//      writer.newLine();
//      writer.write("---");
//      writer.newLine();
//
//      List<String> updatedContent = new ArrayList<>();
//      for (String contentLine : extractedContent) {
//        String updatedLine = copyImagesAndChangePath(contentLine,
//          INPUT_FILE_PATH, imageOutputDir, outputFilePath.getParent());
//        updatedContent.add(updatedLine);
//        writer.write(updatedLine);
//        writer.newLine();
//      }
//    }
//
//    System.out.println("Created file: " + outputFilePath);
//    System.out.println("Created image directory: " + imageOutputDir);
//  }

  private static String copyImagesAndChangePath(String line,
    Path markdownFilePath, Path imageOutputDir, Path targetDirPath) {
    StringBuffer sb = new StringBuffer();
    Matcher matcher = IMAGE_PATTERN.matcher(line);
    while (matcher.find()) {
      String imageDescription = matcher.group(1); //图片描述
      String originalImagePath = matcher.group(2); // 获取原始图片路径
      try {
        Path originalPath = markdownFilePath.getParent()
          .resolve(originalImagePath).normalize();//获取原始图片的绝对路径
        if (Files.exists(originalPath)) {
          Path targetPath = imageOutputDir.resolve(
            originalPath.getFileName());//获取目标图片路径
          Files.createDirectories(imageOutputDir);
          Files.copy(originalPath, targetPath,
            StandardCopyOption.REPLACE_EXISTING);//拷贝文件
          String relativePath = targetDirPath.relativize(targetPath).toString()
            .replace("\\", "/");//目标图片相对新文件的路径
          matcher.appendReplacement(sb,
            "![" + imageDescription + "](" + relativePath + ")");
        } else {
          System.out.println(
            "Image path does not exist:" + originalPath.toString());
          matcher.appendReplacement(sb, matcher.group(0));
        }

      } catch (IOException e) {
        System.err.println(
          "Error copying image: " + originalImagePath + " " + e.getMessage());
        matcher.appendReplacement(sb, matcher.group(0));
      }
    }
    matcher.appendTail(sb);
    return sb.toString();
  }


  public static void main(String[] args) {
    String title = "2024-12-13 的百词斩";
    List<String> tags = List.of("百词斩", "单词", "学习");
    String startDateStr = "2024-11-25";
    int totalDay = 50; // "2025-01-14"
    Date startDate = DateUtil.parseDate(startDateStr);
    for (int dayNumber = 1; dayNumber <= totalDay; dayNumber++) {
      processMarkdown(DateUtil.offsetDay(startDate, dayNumber), dayNumber,
        title, tags);
    }
  }
}
