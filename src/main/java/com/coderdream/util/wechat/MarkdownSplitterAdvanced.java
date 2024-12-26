package com.coderdream.util.wechat;

import cn.hutool.core.date.DateUtil;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownSplitterAdvanced {

  private static final Pattern IMAGE_PATTERN = Pattern.compile(
    "!\\[(.*?)\\]\\((.*?)\\)");
  private static final Path ROOT_PATH = Paths.get(
    "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts");
  private static final Path INPUT_FILE_PATH = ROOT_PATH.resolve(
    "bai-ci-zan.md");

  public static void processMarkdown(String targetDate, String title,
    List<String> tags) throws IOException {
    String startDateLine = String.format("### %s", targetDate);
    Date startDate = DateUtil.parseDate(targetDate);
    Date nextDay = DateUtil.offsetDay(startDate, 1);
    String endDateLine = String.format("### %s",
      DateUtil.format(nextDay, "yyyy-MM-dd"));

    Path outputDir = INPUT_FILE_PATH.getParent();
    Path outputFilePath = outputDir.resolve("bai-ci-zan-" + targetDate + ".md");
    Path imageOutputDir = outputDir.resolve("bai-ci-zan-" + targetDate);

    List<String> extractedContent = new ArrayList<>();
    boolean inSection = false;
    String line;

    try (BufferedReader reader = Files.newBufferedReader(INPUT_FILE_PATH)) {
      while ((line = reader.readLine()) != null) {
        if (line.trim().equals(startDateLine)) {
          inSection = true;
          continue;
        }
        if (line.trim().equals(endDateLine)) {
          inSection = false;
          break;//结束行之后的内容不需要了
        }
        if (inSection) {
          extractedContent.add(line);
        }
      }
    }

    if (extractedContent.isEmpty()) {
      System.out.println("No matching content found for date: " + targetDate);
      return;
    }

    // 生成新的 Markdown 文件
    try (BufferedWriter writer = Files.newBufferedWriter(outputFilePath)) {
      //写入描述信息
      writer.write("---");
      writer.newLine();
      writer.write("title: 考研词汇精选-" + targetDate);
      writer.newLine();
      writer.write(
        "date: " + DateUtil.format(startDate, "yyyy-MM-dd HH:mm:ss"));
      writer.newLine();
      writer.write("tags: ");
      writer.write("\t" + String.join(", ", tags));
      writer.newLine();
      writer.write("categories: ");
      writer.write("\t" + "学习笔记");
      writer.newLine();
      writer.write("---");
      writer.newLine();

      List<String> updatedContent = new ArrayList<>();
      for (String contentLine : extractedContent) {
        String updatedLine = copyImagesAndChangePath(contentLine,
          INPUT_FILE_PATH, imageOutputDir, outputFilePath.getParent());
        updatedContent.add(updatedLine);
        writer.write(updatedLine);
        writer.newLine();
      }
    }

    System.out.println("Created file: " + outputFilePath);
    System.out.println("Created image directory: " + imageOutputDir);
  }

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
//        String targetDate = "2024-12-13";
    List<String> targetDateList = List.of("2024-12-14", "2024-12-15",
      "2024-12-16", "2024-12-17", "2024-12-18");
    String title = "2024-12-13 的百词斩";
    List<String> tags = List.of("百词斩", "单词", "学习");
    for (String targetDate : targetDateList) {
      try {
        processMarkdown(targetDate, title, tags);
      } catch (IOException e) {
        System.err.println("Error: " + e.getMessage());
      }
    }
  }
}
