package com.coderdream.util.wechat;

import com.coderdream.util.CommonUtil;
import com.coderdream.util.proxy.OperatingSystem;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class MarkdownFileGenerator2 {

  private static final String POSTS_FOLDER =
    OperatingSystem.getHexoFolder() + "source" + File.separator + "_posts";
  private static final Pattern IMAGE_NAME_PATTERN = Pattern.compile(
    "幻灯片(\\d+)\\.(png|jpg|jpeg|gif)", Pattern.CASE_INSENSITIVE);

  /**
   * 生成 Markdown 文件
   *
   * @param date              日期，用于生成文件名和文件夹名（例如：2024-12-25）
   * @param introduction      简介
   * @param imageFolder       图片文件夹路径
   * @param englishTextFile   英文文本文件名
   * @param bilingualTextFile 中英文双语文本文件名
   * @param vocabularyFile    词汇文本文件名
   * @param endString         结尾字符串
   * @throws IOException 当发生 I/O 异常时抛出
   */
  public static void generateMarkdownFile(String date, String introduction,
    String imageFolder,
    String englishTextFile, String bilingualTextFile, String vocabularyFile,
    String endString) throws IOException {

    // 1. 构建文件名和文件夹路径
    String folderName = "wechat-" + date;
    Path targetFolder = Paths.get(POSTS_FOLDER, folderName);
    Path markdownFile = Paths.get(POSTS_FOLDER, folderName + ".md");
    log.info("目标文件夹：{}", targetFolder);
    log.info("目标markdown文件：{}", markdownFile);
    // 2. 创建目标文件夹
    createDirectory(targetFolder);
    // 3. 创建markdown文件
    try (BufferedWriter writer = new BufferedWriter(
      new FileWriter(markdownFile.toFile()))) {

      // 4. 写入简介
      writeIntroduction(writer, introduction);

      // 5. 处理图片
      processImages(writer, imageFolder, targetFolder, folderName);

      // 6. 插入英文文本
      insertTextFromFile(writer, englishTextFile);

      // 7. 插入中英文双语文本
      insertTextFromFile(writer, bilingualTextFile);

      // 8. 插入词汇文本
      insertTextFromFile(writer, vocabularyFile);

      // 9. 插入结尾字符串
      writeEndString(writer, endString);

    } catch (IOException e) {
      log.error("生成markdown文件失败", e);
      throw e; // 将异常抛出
    }
    log.info("markdown文件生成成功");
  }


  /**
   * 创建目录
   *
   * @param directoryPath 目录路径
   * @throws IOException 当创建目录失败时抛出
   */
  private static void createDirectory(Path directoryPath) throws IOException {
    if (Files.notExists(directoryPath)) {
      Files.createDirectories(directoryPath);
      log.info("成功创建目录：{}", directoryPath);
    } else {
      log.warn("目录已存在，无需创建：{}", directoryPath);
    }
  }


  /**
   * 写入简介
   *
   * @param writer       BufferedWriter
   * @param introduction 简介字符串
   * @throws IOException 当写入简介失败时抛出
   */
  private static void writeIntroduction(BufferedWriter writer,
    String introduction) throws IOException {
    writer.write(introduction);
    writer.newLine();
    writer.newLine(); // 添加两个空行
    log.info("成功写入简介");
  }

  /**
   * 处理图片
   *
   * @param writer       BufferedWriter
   * @param imageFolder  图片文件夹路径
   * @param targetFolder 目标文件夹路径
   * @param folderName   文件夹名称
   * @throws IOException 当处理图片时抛出异常
   */
  private static void processImages(BufferedWriter writer, String imageFolder,
    Path targetFolder, String folderName) throws IOException {
    if (Objects.nonNull(imageFolder) && Files.exists(Paths.get(imageFolder))) {
      List<Path> imageFiles = Files.list(Paths.get(imageFolder))
        .filter(Files::isRegularFile)
        .filter(path -> {
          String fileName = path.getFileName().toString().toLowerCase();
          return fileName.endsWith(".png") || fileName.endsWith(".jpg")
            || fileName.endsWith(".jpeg") || fileName.endsWith(".gif");
        })
        .sorted((path1, path2) -> { // 添加图片排序逻辑
          String fileName1 = path1.getFileName().toString();
          String fileName2 = path2.getFileName().toString();
          int number1 = extractNumberFromImageName(fileName1);
          int number2 = extractNumberFromImageName(fileName2);
          return Integer.compare(number1, number2);
        })
        .collect(Collectors.toList());

      log.info("图片文件数量：{}", imageFiles.size());
      for (Path imageFile : imageFiles) {
        String imageName = imageFile.getFileName().toString();
        Path targetImage = targetFolder.resolve(imageName);
        Files.copy(imageFile, targetImage, StandardCopyOption.REPLACE_EXISTING);
        String markdownImage = String.format("![%s](%s/%s)\n", imageName,
          folderName, imageName);
        writer.write(markdownImage);
        log.info("图片 {} 已复制到 {}，并添加到 Markdown 文件中",
          imageFile.toString(), targetImage.toString());
      }
      writer.newLine(); // 添加空行
    } else {
      log.warn("图片文件夹不存在或为空：{}", imageFolder);
    }
  }

  /**
   * 从图片名称中提取数字
   *
   * @param imageName 图片名称
   * @return 图片名称中的数字，如果提取失败返回 -1
   */
  private static int extractNumberFromImageName(String imageName) {
    Matcher matcher = IMAGE_NAME_PATTERN.matcher(imageName);
    if (matcher.find()) {
      try {
        return Integer.parseInt(matcher.group(1));
      } catch (NumberFormatException e) {
        log.warn("无法解析图片名称中的数字: {}", imageName, e);
      }
    }
    return -1;
  }

  /**
   * 插入文本文件内容
   *
   * @param writer   BufferedWriter
   * @param textFile 文本文件名
   * @throws IOException 当读取文本文件或写入失败时抛出
   */
  private static void insertTextFromFile(BufferedWriter writer, String textFile)
    throws IOException {
    if (Objects.nonNull(textFile) && Files.exists(Paths.get(textFile))) {
      List<String> lines = Files.readAllLines(Paths.get(textFile));
      for (String line : lines) {
        writer.write(line);
        writer.newLine();
      }
      writer.newLine(); // 添加一个空行
      log.info("成功插入文本内容：{}", textFile);
    } else {
      log.warn("文本文件不存在或为空：{}", textFile);
    }
  }


  /**
   * 写入结尾字符串
   *
   * @param writer    BufferedWriter
   * @param endString 结尾字符串
   * @throws IOException 当写入结尾字符串失败时抛出
   */
  private static void writeEndString(BufferedWriter writer, String endString)
    throws IOException {
    writer.write(endString);
    log.info("成功写入结尾字符串");
  }

  public static void main(String[] args) {
    String date = "2024-12-26";
    String introduction = "这是一个示例简介。";
    String folderName = "180830";
    String folderPath = CommonUtil.getFullPath(folderName);
    String imageFolder = folderPath + File.separator + "\\" + folderName;
    String englishTextFile = folderPath + File.separator + "\\" + "script_dialog.txt";
    String bilingualTextFile =
      folderPath + File.separator + "\\" + folderName + "_中英双语对话脚本.txt";
    String vocabularyFile = folderPath + File.separator + "\\" + "voc_cn.txt";
    String endString = "结尾字符串。";

    try {
      generateMarkdownFile(date, introduction, imageFolder, englishTextFile,
        bilingualTextFile, vocabularyFile, endString);
      log.info("Markdown 文件生成完毕！");
    } catch (IOException e) {
      log.error("生成 Markdown 文件时出现异常：", e);
    }
  }
}
