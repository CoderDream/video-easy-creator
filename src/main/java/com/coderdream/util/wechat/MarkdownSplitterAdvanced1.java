package com.coderdream.util.wechat;

import cn.hutool.core.date.DateUtil;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownSplitterAdvanced1 {

  private static final Pattern START_DATE_PATTERN = Pattern.compile(
    "###\\s+(\\d{4}-\\d{2}-\\d{2})开始");
  private static final Pattern END_DATE_PATTERN = Pattern.compile(
    "###\\s+(\\d{4}-\\d{2}-\\d{2})结束");
  private static final Pattern IMAGE_PATTERN = Pattern.compile(
    "!\\[.*?]\\((.*?)\\)");
  private static final Path ROOT_PATH = Paths.get(
    "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts");
  private static final Path INPUT_FILE_PATH = ROOT_PATH.resolve(
    "bai-ci-zan.md");

  public static void processMarkdown(String targetDate) throws IOException {
    String startDateLine = String.format("### %s", targetDate);
    Date startDate = DateUtil.parseDate(targetDate);
    Date nextDay = DateUtil.offsetDay(startDate, 1);
    String endDateLine = String.format("### %s", DateUtil.format(nextDay, "yyyy-MM-dd"));

    Path outputDir = INPUT_FILE_PATH.getParent();
    Path outputFilePath = outputDir.resolve("bai-ci-zan-" + targetDate + ".md");
    Path imageOutputDir = outputDir.resolve("bai-ci-zan-" + targetDate);

    List<String> extractedContent = new ArrayList<>();
    boolean inSection = false;
    String line;

    try (BufferedReader reader = Files.newBufferedReader(INPUT_FILE_PATH)) {
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
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
      List<String> updatedContent = new ArrayList<>();
      for (String contentLine : extractedContent) {
        String updatedLine = copyImagesAndChangePath(contentLine,
          INPUT_FILE_PATH, imageOutputDir, outputFilePath.getParent());
        updatedContent.add(updatedLine);
        writer.write(updatedLine);
        writer.newLine();
      }
      //  writer.write(String.join(System.lineSeparator(), updatedContent));
    }

    System.out.println("Created file: " + outputFilePath);
    System.out.println("Created image directory: " + imageOutputDir);
  }

  private static String copyImagesAndChangePath(String line,
    Path markdownFilePath, Path imageOutputDir, Path targetDirPath) {
    StringBuffer sb = new StringBuffer();
    Matcher matcher = IMAGE_PATTERN.matcher(line);
    while (matcher.find()) {
      String originalImagePath = matcher.group(1);

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
            "![$0](" + relativePath + ")".replace("$0", matcher.group(0)
              .substring(2, matcher.group(0).length() - 1)));//更新新文件的图片地址
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
    String targetDate = "2024-12-13";

    try {
      processMarkdown(targetDate);
    } catch (IOException e) {
      System.err.println("Error: " + e.getMessage());
    }
  }
}
