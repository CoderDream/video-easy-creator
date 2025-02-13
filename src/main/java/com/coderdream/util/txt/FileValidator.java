package com.coderdream.util.txt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class FileValidator {

  /**
   * 校验文件内容是否符合以下规则：
   * 1. 不存在长度小于等于 1 的行。
   * 2. 总行数为偶数。
   *
   * @param filePath 要校验的文件路径
   * @return 如果文件符合规则返回 true，否则返回 false
   * @throws IOException 如果读取文件时发生错误
   */
  public static boolean isValidFile(String filePath) throws IOException {
    // 使用 try-with-resources 确保资源关闭
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      int lineCount = 0;
      while ((line = reader.readLine()) != null) {
        lineCount++;
        // 检查是否存在长度小于等于 1 的行
        if (line.trim().length() <= 1) {
          System.err.println(
            "Invalid line found (length <= 1): " + line); // 打印错误信息
          return false;
        }
      }
      // 检查行数是否为偶数
      if (lineCount % 2 != 0) {
        System.err.println(
          "Invalid file: Odd number of lines (" + lineCount + ")");  // 打印错误信息
        return false;
      }
    }
    return true;
  }


  /**
   * 更高效的校验
   *
   * @param filePath
   * @return
   * @throws IOException
   */
  public static boolean isValidFile2(String filePath) throws IOException {
    Path path = Path.of(filePath);
    try (Stream<String> lines = Files.lines(path)) {
      long count = lines.filter(line -> line.trim().length() > 1).count();
      if (Files.lines(path).count() != count) {
        //存在长度小于等于1的行
        return false;
      }
      //行数不为偶数
      return count % 2 == 0;
    }
  }

  /**
   * 校验List<String>
   *
   * @param lines
   * @return
   */
  public static boolean isValidLines(List<String> lines) {
    if (lines == null || lines.isEmpty()) {
      return false; // 或者根据需求返回 true
    }

    long validLineCount = lines.stream()
      .filter(line -> line.trim().length() > 1)
      .count();

    if (lines.size() != validLineCount) {
      System.err.println("Invalid line found (length <= 1)");
      return false; // 存在长度小于等于 1 的行
    }

    return validLineCount % 2 == 0; // 行数为偶数则返回 true
  }


  public static void main(String[] args) {
    String validFilePath = "D:\\0000\\EnBook002\\Chapter016\\Chapter016_basic.txt"; // 替换为有效文件的路径
    String invalidFilePath1 = "D:\\0000\\EnBook002\\Chapter016\\Chapter016_total.txt"; // 替换为包含长度小于等于 1 的行的文件路径
    String invalidFilePath2 = "D:\\0000\\EnBook002\\Chapter016\\Chapter016_dialog.txt"; // 替换为行数为奇数的文件路径

//    // 创建测试文件（可选）
//    try {
//      createTestFiles(validFilePath, invalidFilePath1, invalidFilePath2);
//    } catch (IOException e) {
//      System.err.println("Error creating test files: " + e.getMessage());
//      return;
//    }

    try {
      System.out.println(
        "Valid file (isValidFile): " + isValidFile(validFilePath)); // 预期：true
      System.out.println("Invalid file 1 (isValidFile): " + isValidFile(
        invalidFilePath1)); // 预期：false
      System.out.println("Invalid file 2 (isValidFile): " + isValidFile(
        invalidFilePath2)); // 预期：false

      System.out.println(
        "Valid file (isValidFile2): " + isValidFile2(validFilePath)); // 预期：true
      System.out.println("Invalid file 1 (isValidFile2): " + isValidFile2(
        invalidFilePath1)); // 预期：false
      System.out.println("Invalid file 2 (isValidFile2): " + isValidFile2(
        invalidFilePath2)); // 预期：false

      // 测试 isValidLines 方法
      List<String> validLines = List.of("Line 1", "Line 2", "Line 3", "Line 4");
      List<String> invalidLines1 = List.of("Line 1", "Line 2", "Line 3");
      List<String> invalidLines2 = List.of("Line 1", "", "Line 3", "Line 4");
      System.out.println(
        "Valid List<String>: " + isValidLines(validLines));       // true
      System.out.println("Invalid List<String> (odd lines): " + isValidLines(
        invalidLines1));  // false
      System.out.println("Invalid List<String> (empty line): " + isValidLines(
        invalidLines2)); // false


    } catch (IOException e) {
      System.err.println("Error validating files: " + e.getMessage());
    }
  }

  /**
   * 创建测试文件 (仅用于 main 方法演示).
   */
  private static void createTestFiles(String validFilePath,
    String invalidFilePath1, String invalidFilePath2) throws IOException {
    // 创建一个有效文件（偶数行，每行长度大于 1）
    Files.write(Path.of(validFilePath),
      List.of("Line 1", "Line 2", "Line 3", "Line 4"));

    // 创建一个无效文件（包含长度小于等于 1 的行）
    Files.write(Path.of(invalidFilePath1),
      List.of("Line 1", "a", "Line 3", "Line 4"));

    // 创建一个无效文件（行数为奇数）
    Files.write(Path.of(invalidFilePath2),
      List.of("Line 1", "Line 2", "Line 3"));
  }
}
