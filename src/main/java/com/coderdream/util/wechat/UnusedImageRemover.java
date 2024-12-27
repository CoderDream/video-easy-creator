package com.coderdream.util.wechat;

import lombok.extern.slf4j.Slf4j;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class UnusedImageRemover {

  // 定义图片文件目录
  private static final Path IMAGE_DIR_PATH = Paths.get(
    "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\bai-ci-zan");
  // 定义 Markdown 文件路径
  private static final Path MARKDOWN_FILE_PATH = Paths.get(
    "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\bai-ci-zan.md");
  // 定义图片匹配的正则表达式
  private static final Pattern IMAGE_PATTERN = Pattern.compile(
    "!\\[(.*?)]\\((.*?)\\)");

  /**
   * 查找 Markdown 文件中使用的图片文件名，并检查文件是否存在。
   *
   * @return 包含所有使用的图片文件名的集合
   */
  private static Set<String> findUsedImageNames() {
    Set<String> usedImageNames = new HashSet<>();
    try (BufferedReader reader = Files.newBufferedReader(MARKDOWN_FILE_PATH)) {
      String line;
      while ((line = reader.readLine()) != null) {
        Matcher matcher = IMAGE_PATTERN.matcher(line);
        while (matcher.find()) {
          String imagePath = matcher.group(2); // 获取图片路径
          Path path = Paths.get(imagePath);
          Path absolutePath = MARKDOWN_FILE_PATH.getParent().resolve(path)
            .normalize(); // 获取绝对路径
          if (Files.exists(absolutePath)) {
            usedImageNames.add(path.getFileName().toString()); // 只存储文件名
          } else {
            log.error("Markdown 文件中引用的图片不存在：{}",
              absolutePath); // 记录不存在的图片
          }
        }
      }
    } catch (IOException e) {
      log.error("Error reading markdown file: {}", MARKDOWN_FILE_PATH, e);
    }
    return usedImageNames;
  }

  /**
   * 获取图片目录下的所有文件路径。
   *
   * @return 包含所有图片文件路径的列表
   * @throws IOException 如果读取图片目录失败，则抛出 IOException
   */
  private static List<Path> listImageFiles() throws IOException {
    if (!Files.exists(IMAGE_DIR_PATH) || !Files.isDirectory(IMAGE_DIR_PATH)) {
      log.warn("Image directory not found or not a directory: {}",
        IMAGE_DIR_PATH);
      return new ArrayList<>(); // 返回空列表
    }

    try (Stream<Path> pathStream = Files.list(
      IMAGE_DIR_PATH)) { // 使用 try-with-resources 自动关闭 Stream
      return pathStream
        .filter(Files::isRegularFile) // 过滤出常规文件
        .collect(Collectors.toList());
    }
  }

  /**
   * 删除未使用的图片文件，并记录日志。
   */
  public static void removeUnusedImages() {
    Set<String> usedImageNames = findUsedImageNames(); // 获取所有使用的图片文件名
    log.info("Found {} used image names.", usedImageNames.size());

    try {
      List<Path> allImageFiles = listImageFiles();  // 获取图片目录下的所有图片文件
      log.info("Found {} image files in directory.", allImageFiles.size());

      // 获取未使用的图片文件
      List<Path> unusedImageFiles = allImageFiles.stream()
        .filter(filePath -> {
          String fileName = filePath.getFileName().toString();
          boolean isUnused = !usedImageNames.contains(fileName); // 检查是否未使用
          if (isUnused) {
            log.info("发现未使用的图片：{}", fileName); // 使用中文日志记录未使用的图片
          }
          return isUnused; // 返回是否未使用
        })
        .collect(Collectors.toList());

      if (unusedImageFiles.isEmpty()) {
        log.info("No unused image files found.");
        System.out.println("No unused image files found.");
        return;
      }

      log.info("Found {} unused image files.", unusedImageFiles.size());

      unusedImageFiles.forEach(file -> {
        try {
          Files.delete(file); // 删除文件
          log.info("Deleted unused image file: {}", file); // 记录删除的图片路径
        } catch (IOException e) {
          log.error("Error deleting file: {}", file, e);
          System.err.println(
            "Error deleting file: " + file + ", Error:" + e.getMessage());
        }
      });

      System.out.println(
        "Removed " + unusedImageFiles.size() + " unused image files.");

    } catch (IOException e) {
      log.error("Error processing image files: {}", IMAGE_DIR_PATH, e);
    }
  }

  /**
   * 主方法，用于测试删除未使用的图片功能。
   *
   * @param args 命令行参数
   */
  public static void main(String[] args) {
    removeUnusedImages();
  }
}
