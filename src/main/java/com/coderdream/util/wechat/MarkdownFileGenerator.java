package com.coderdream.util.wechat;

import cn.hutool.core.date.DateUtil;
import com.coderdream.util.CommonUtil;
import java.util.Date;
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
public class MarkdownFileGenerator {

  private static final String POSTS_FOLDER = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts";
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
  public static void generateMarkdownFile(String folderNameStr, String date,
    String introduction,
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
      writeIntroduction(writer, folderNameStr, introduction);

      // 5. 处理图片
      List<Path> imageFiles = processImages(imageFolder, targetFolder,
        folderName);

      // 6. 插入第一张图片作为封面
      insertFirstImageAsCover(writer, imageFiles, folderName);

      // 7. 插入英文文本
      insertTextFromFile(writer, "英文脚本", englishTextFile);

      // 8. 插入中英文双语文本
      insertTextFromFile(writer, "中英文双语脚本", bilingualTextFile);

      // 9. 插入所有的图片
      insertAllImages(writer, imageFiles, folderName);

      // 10. 插入词汇文本
      insertVocabularyWithH4(writer, vocabularyFile);

      // 11. 插入结尾字符串
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
    String folderNameStr,
    String introduction) throws IOException {
//    writer.write(introduction);
//    writer.newLine();
//    writer.newLine(); // 添加两个空行

    //写入描述信息
    writer.write("---");
    writer.newLine();
    writer.write("title: " + introduction);
    writer.newLine();
    String dateStr = "20" + folderNameStr;
    Date startDate = DateUtil.parse(dateStr, "yyyyMMdd");
    writer.write(
      "date: " + DateUtil.format(startDate, "yyyy-MM-dd HH:mm:ss"));
    writer.newLine();
    writer.write("tags: ");
    writer.write("\t" + String.join(", ", "六分钟英语"));
    writer.newLine();
    writer.write("categories: ");
    writer.write("\t" + "学习笔记");
    writer.newLine();
    writer.write("---");
    writer.newLine();
    log.info("成功写入简介");
  }


  /**
   * 处理图片，返回排序后的图片列表
   *
   * @param imageFolder  图片文件夹路径
   * @param targetFolder 目标文件夹路径
   * @param folderName   文件夹名称
   * @return 排序后的图片列表
   * @throws IOException 当处理图片时抛出异常
   */
  private static List<Path> processImages(String imageFolder, Path targetFolder,
    String folderName)
    throws IOException {
    if (Objects.nonNull(imageFolder) && Files.exists(Paths.get(imageFolder))) {
      List<Path> imageFiles =
        Files.list(Paths.get(imageFolder))
          .filter(Files::isRegularFile)
          .filter(path -> {
            String fileName = path.getFileName().toString().toLowerCase();
            return fileName.endsWith(".png")
              || fileName.endsWith(".jpg")
              || fileName.endsWith(".jpeg")
              || fileName.endsWith(".gif");
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
        log.info("图片 {} 已复制到 {} ", imageFile.toString(),
          targetImage.toString());
      }
      return imageFiles;
    } else {
      log.warn("图片文件夹不存在或为空：{}", imageFolder);
      return null;
    }
  }


  /**
   * 插入第一张图片作为封面
   *
   * @param writer     BufferedWriter
   * @param imageFiles 图片文件列表
   * @param folderName 文件夹名称
   * @throws IOException 当写入封面图片时抛出异常
   */
  private static void insertFirstImageAsCover(BufferedWriter writer,
    List<Path> imageFiles, String folderName) throws IOException {
    if (Objects.nonNull(imageFiles) && !imageFiles.isEmpty()) {
      String imageName = imageFiles.get(0).getFileName().toString();
      String markdownImage = String.format("![%s](%s/%s)\n", imageName,
        folderName, imageName);
      writer.write(markdownImage);
      writer.newLine();
      log.info("成功插入第一张图片作为封面：{}", imageName);
    } else {
      log.warn("没有图片可作为封面");
    }
  }


  /**
   * 插入所有图片到 Markdown 文件
   *
   * @param writer     BufferedWriter
   * @param imageFiles 图片文件列表
   * @param folderName 文件夹名称
   * @throws IOException 当写入图片时抛出异常
   */
  private static void insertAllImages(BufferedWriter writer,
    List<Path> imageFiles, String folderName) throws IOException {
    if (Objects.nonNull(imageFiles) && !imageFiles.isEmpty()) {
      for (Path imageFile : imageFiles) {
        String imageName = imageFile.getFileName().toString();
        String markdownImage = String.format("![%s](%s/%s)\n", imageName,
          folderName, imageName);
        writer.write(markdownImage);
        log.info("图片 {}  已添加到 Markdown 文件中", imageName);
      }
      writer.newLine();
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
  private static void insertTextFromFile(BufferedWriter writer, String tag,
    String textFile)
    throws IOException {
    if (Objects.nonNull(textFile) && Files.exists(Paths.get(textFile))) {
      List<String> lines = Files.readAllLines(Paths.get(textFile));
      writer.write("### 【" + tag + "】");
      writer.newLine();
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
   * 插入词汇文本，并为每组的第一个单词添加 H4 标题
   *
   * @param writer         BufferedWriter
   * @param vocabularyFile 词汇文本文件名
   * @throws IOException 当读取词汇文本或写入失败时抛出
   */
  private static void insertVocabularyWithH4(BufferedWriter writer,
    String vocabularyFile)
    throws IOException {
    if (Objects.nonNull(vocabularyFile) && Files.exists(
      Paths.get(vocabularyFile))) {
      List<String> lines = Files.readAllLines(Paths.get(vocabularyFile));
      // 每六行一组，第一行添加 H4 标题
      writer.write("### 【核心词汇】");
      writer.newLine();
      for (int i = 0; i < lines.size(); i++) {
        String line = lines.get(i);
        if (i % 6 == 0) { // 每六行一组，第一行添加 H4 标题
          writer.write("#### " + line);
          writer.newLine();
        } else {
          writer.write(line);
          writer.newLine();
        }
      }
      writer.newLine();
      log.info("成功插入词汇文本，并添加H4标题");
    } else {
      log.warn("词汇文件不存在或为空：{}", vocabularyFile);
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
    String date = "2018-08-23";
    String introduction = "【BBC六分钟英语】哪些人会购买高端相机？";
    String folderName = "180823";
    String folderPath = CommonUtil.getFullPath(folderName);
    String imageFolder = folderPath + "\\" + folderName;
    String englishTextFile = folderPath + "\\" + "script_dialog.txt";
    String bilingualTextFile =
      folderPath + "\\" + folderName + "_中英双语对话脚本.txt";
    String vocabularyFile = folderPath + "\\" + "voc_cn.txt";
    String endString = "在公众号里输入6位数字，获取【对话音频、英文文本、中文翻译、核心词汇和高级词汇表】电子档，6位数字【暗号】在文章的最后一张图片，如【220728】，表示22年7月28日这一期。公众号没有的文章说明还没有制作相关资料。年度合集在B站【六分钟英语】工房获取，每年共计300+文档，感谢支持！";

    try {
      generateMarkdownFile(folderName, date, introduction, imageFolder,
        englishTextFile,
        bilingualTextFile, vocabularyFile, endString);
      log.info("Markdown 文件生成完毕！");
    } catch (IOException e) {
      log.error("生成 Markdown 文件时出现异常：", e);
    }
  }
}
