package com.coderdream.util.pic.demo02;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class HtmlImageGenerator {

  /**
   * 生成HTML文件，将指定文件夹下的图片按指定数量平铺显示。
   *
   * @param imageFolderPath 图片文件夹路径
   * @param imagesPerRow    每行显示的图片数量
   * @param borderWidth     图片边框宽度（像素）
   * @param outputHtmlPath  输出HTML文件路径
   * @return 生成html文件所消耗的时间，格式为HH:mm:ss.SSS
   */
  public static String generateHtml(String imageFolderPath, int imagesPerRow,
    int borderWidth, String outputHtmlPath) {

    Instant start = Instant.now(); // 记录开始时间

    log.info(
      "开始生成HTML文件，图片文件夹：{}，每行图片数量：{}，边框宽度：{}，输出路径：{}",
      imageFolderPath, imagesPerRow, borderWidth, outputHtmlPath);

    File imageFolder = new File(imageFolderPath);
    if (!imageFolder.exists() || !imageFolder.isDirectory()) {
      log.error("图片文件夹不存在或不是目录：{}", imageFolderPath);
      return "00:00:00.000"; // 错误时返回默认值
    }

    File[] imageFiles = imageFolder.listFiles(file ->
      file.isFile() && (file.getName().toLowerCase().endsWith(".png") ||
        file.getName().toLowerCase().endsWith(".jpg") ||
        file.getName().toLowerCase().endsWith(".jpeg") ||
        file.getName().toLowerCase().endsWith(".gif"))
    );

    if (imageFiles == null || imageFiles.length == 0) {
      log.warn("图片文件夹为空或不包含图片文件：{}", imageFolderPath);
      try {
        Path htmlFilePath = Paths.get(outputHtmlPath);
        Files.createDirectories(htmlFilePath.getParent());
        Files.writeString(htmlFilePath,
          "<!DOCTYPE html>\n<html>\n<head>\n<title>图片展示</title>\n</head>\n<body>\n<h1>没有找到图片</h1>\n</body>\n</html>");
      } catch (IOException e) {
        log.error("创建HTML文件失败", e);
        return "00:00:00.000";
      }

      Instant end = Instant.now();  // 记录结束时间
      Duration duration = Duration.between(start, end);
      String formattedTime = DurationFormatter.formatDuration(duration);
      log.info("完成生成HTML文件，耗时：{}", formattedTime);
      return formattedTime;


    }

    StringBuilder htmlContent = new StringBuilder();
    htmlContent.append("<!DOCTYPE html>\n");
    htmlContent.append("<html>\n");
    htmlContent.append("<head>\n");
    htmlContent.append("<title>图片展示</title>\n");
    htmlContent.append("<style>\n");
    htmlContent.append("img {\n");
    htmlContent.append("  border: ").append(borderWidth)
      .append("px solid black;\n");
    htmlContent.append("  margin: 5px;\n"); // 图片之间的间距
    htmlContent.append("}\n");
    htmlContent.append("</style>\n");
    htmlContent.append("</head>\n");
    htmlContent.append("<body>\n");
    htmlContent.append("<table style='width:100%;'>\n");

    int imageCount = 0;
    for (File file : imageFiles) {
      if (imageCount % imagesPerRow == 0) {
        htmlContent.append("  <tr>\n");
      }

      File imageFile = file;
      String fileName = imageFile.getName();
      long fileSize = imageFile.length();
      String fileSizeKB = String.format("%.2f KB", (double) fileSize / 1024);

      // 获取图片分辨率
      String resolution = "未知";
      try {
        BufferedImage bimg = ImageIO.read(imageFile);
        int width = bimg.getWidth();
        int height = bimg.getHeight();
        resolution = width + "x" + height;
      } catch (IOException e) {
        log.warn("无法获取图片分辨率：{}", fileName, e);
      }

      htmlContent.append("    <td style='text-align: center;'>\n");
      htmlContent.append("      <img src=\"")
        .append(imageFile.getAbsolutePath()).append("\" alt=\"")
        .append(fileName).append("\" title=\"文件名: ").append(fileName)
        .append("\\n大小: ").append(fileSizeKB).append("\\n分辨率: ")
        .append(resolution).append("\">\n"); // 使用绝对路径
      htmlContent.append("      <br>\n");
      htmlContent.append("      <span>").append(fileName)
        .append("</span>\n"); // 显示文件名
      htmlContent.append("    </td>\n");

      imageCount++;

      if (imageCount % imagesPerRow == 0) {
        htmlContent.append("  </tr>\n");
      }
    }

    // 补全表格，处理最后一行的图片不足 imagesPerRow 的情况
    if (imageCount % imagesPerRow != 0) {
      int remainingCells = imagesPerRow - (imageCount % imagesPerRow);
      for (int i = 0; i < remainingCells; i++) {
        htmlContent.append("    <td></td>\n"); // 填充空单元格
      }
      htmlContent.append("  </tr>\n");
    }

    htmlContent.append("</table>\n");
    htmlContent.append("</body>\n");
    htmlContent.append("</html>\n");

    try (BufferedWriter writer = new BufferedWriter(
      new FileWriter(outputHtmlPath))) { // try-with-resources
      writer.write(htmlContent.toString());
      log.info("成功生成HTML文件：{}", outputHtmlPath);
    } catch (IOException e) {
      log.error("生成HTML文件失败：{}", outputHtmlPath, e);
      return "00:00:00.000";
    }

    Instant end = Instant.now();  // 记录结束时间
    Duration duration = Duration.between(start, end);
    String formattedTime = DurationFormatter.formatDuration(duration);
    log.info("完成生成HTML文件，耗时：{}", formattedTime);
    return formattedTime;


  }

  public static void main(String[] args) {
    // 示例用法
    String imageFolderPath = "C:\\Users\\CoderDream\\Pictures\\png"; // 替换为你的图片文件夹路径
    int imagesPerRow = 5;
    int borderWidth = 2;
    String outputHtmlPath = "C:\\Users\\CoderDream\\Pictures\\output.html"; // 替换为你的输出HTML文件路径

    String timeTaken = generateHtml(imageFolderPath, imagesPerRow, borderWidth,
      outputHtmlPath);
    System.out.println("耗时：" + timeTaken);
  }
}
