package com.coderdream.util.video.demo04;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

/**
 * 文件拷贝工具类，提供将文件拷贝到指定目录的功能.
 */
public class FileCopyUtils {

  /**
   * 将文件拷贝到指定的目录。如果目标目录不存在，则会创建它。
   *
   * @param sourceFile           源文件的路径。 不能为空。
   * @param destinationFileName   目标文件的名称。 不能为空。
   * @param destinationDirectory 目标目录的路径。 不能为空。
   * @param replaceExisting      如果为true，则替换目标目录中已存在的文件。 如果为false，则抛出异常。
   * @return 拷贝后的文件的路径。
   * @throws IOException          如果在拷贝过程中发生I/O错误，或者无法创建目标目录， 或者源文件不存在，或者
   *                              replaceExisting 为false 且目标文件已存在。
   * @throws NullPointerException 如果 sourceFile 或 destinationDirectory 为 null。
   */
  public static Path copyFileToDirectory(String sourceFile,
    String destinationFileName, String destinationDirectory,
    boolean replaceExisting) throws IOException {
    Objects.requireNonNull(sourceFile, "源文件路径不能为空。");
    Objects.requireNonNull(destinationDirectory, "目标目录路径不能为空。");

    Path sourcePath = Paths.get(sourceFile);
    Path destinationPath = Paths.get(destinationDirectory);

    if (!Files.exists(sourcePath)) {
      throw new IOException("源文件不存在: " + sourceFile);
    }

    // 创建目标目录，如果它不存在
    if (!Files.exists(destinationPath)) {
      Files.createDirectories(destinationPath);  // 创建所有必要的父目录
    } else {
      if (!Files.isDirectory(destinationPath)) {
        throw new IOException("目标路径不是一个目录: " + destinationDirectory);
      }
    }

    // 构造完整的目标文件路径
    Path destinationFile = destinationPath.resolve(destinationFileName);
    StandardCopyOption copyOption =
      replaceExisting ? StandardCopyOption.REPLACE_EXISTING
        : StandardCopyOption.COPY_ATTRIBUTES;

    if (Files.exists(destinationFile) && !replaceExisting) {
      throw new IOException("目标文件已存在: " + destinationFile);
    }

    // 执行文件拷贝
    return Files.copy(sourcePath, destinationFile, copyOption);
  }

  /**
   * 将文件拷贝到指定的目录。如果目标目录不存在，则会创建它。
   *
   * @param sourceFile           源文件的路径。 不能为空。
   * @param destinationDirectory 目标目录的路径。 不能为空。
   * @param replaceExisting      如果为true，则替换目标目录中已存在的文件。 如果为false，则抛出异常。
   * @return 拷贝后的文件的路径。
   * @throws IOException          如果在拷贝过程中发生I/O错误，或者无法创建目标目录， 或者源文件不存在，或者
   *                              replaceExisting 为false 且目标文件已存在。
   * @throws NullPointerException 如果 sourceFile 或 destinationDirectory 为 null。
   */
  public static Path copyFileToDirectory(String sourceFile,
    String destinationDirectory, boolean replaceExisting) throws IOException {

    Objects.requireNonNull(sourceFile, "源文件路径不能为空。");
    Objects.requireNonNull(destinationDirectory, "目标目录路径不能为空。");

    Path sourcePath = Paths.get(sourceFile);
    Path destinationPath = Paths.get(destinationDirectory);

    if (!Files.exists(sourcePath)) {
      throw new IOException("源文件不存在: " + sourceFile);
    }

    // 创建目标目录，如果它不存在
    if (!Files.exists(destinationPath)) {
      Files.createDirectories(destinationPath);  // 创建所有必要的父目录
    } else {
      if (!Files.isDirectory(destinationPath)) {
        throw new IOException("目标路径不是一个目录: " + destinationDirectory);
      }
    }
    String destinationFileName = new File(sourceFile).getName();

    return copyFileToDirectory(sourceFile, destinationFileName,
      destinationDirectory, replaceExisting);
  }


  /**
   * 将文件拷贝到指定的目录，替换已存在的文件. 这是一个便捷方法，调用
   * {@link #copyFileToDirectory(String, String, boolean)} 并将 `replaceExisting`
   * 设置为 true。
   *
   * @param sourceFile           源文件的路径。 不能为空。
   * @param destinationDirectory 目标目录的路径。 不能为空。
   * @return 拷贝后的文件的路径。
   * @throws IOException          如果在拷贝过程中发生I/O错误。
   * @throws NullPointerException 如果 sourceFile 或 destinationDirectory 为 null。
   */
  public static Path copyFileToDirectory(String sourceFile,
    String destinationDirectory) throws IOException {
    return copyFileToDirectory(sourceFile, destinationDirectory, true);
  }

  public static void main(String[] args) {
    String sourceFile = "path/to/your/source/file.txt"; // 替换成你的源文件路径
    String destinationDirectory = "path/to/your/destination/directory"; // 替换成你的目标目录路径

    try {
      Path copiedFile = FileCopyUtils.copyFileToDirectory(sourceFile,
        destinationDirectory, true); // 需要替换现有文件就设置为 true
      System.out.println("文件拷贝成功到: " + copiedFile);
    } catch (IOException e) {
      System.err.println("拷贝文件出错: " + e.getMessage());
      e.printStackTrace(); // 打印完整的堆栈信息
    }
  }
}
