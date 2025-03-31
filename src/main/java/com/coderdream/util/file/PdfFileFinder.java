package com.coderdream.util.file;

import com.coderdream.util.CommonUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PdfFileFinder {

  /**
   * 获取文件夹下以指定前缀开头的PDF文件
   *
   * @param directoryPath 文件夹路径
   * @param prefix        文件名前缀
   * @return 匹配的PDF文件列表
   */
  public static List<Path> getPdfFilesWithPrefix(String directoryPath,
    String prefix) {
    List<Path> pdfFiles = new ArrayList<>();
    Path dir = Paths.get(directoryPath);

    // 使用Files.walkFileTree方法递归遍历文件夹
    try {
      Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
          throws IOException {
          // 只处理PDF文件，并且文件名以prefix开头
          if (file.toString().endsWith(".pdf") && file.getFileName().toString()
            .startsWith(prefix)) {
            pdfFiles.add(file);
          }
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc)
          throws IOException {
          return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException e) {
      log.error("Error occurred while searching for PDF files: {}",
        e.getMessage(), e);
    }
    return pdfFiles;
  }

  public static String findPdfFileName(String folderName) {
    // 文件夹路径
    String directory = CommonUtil.getFullPath(folderName);

    List<Path> pdfFiles = getPdfFilesWithPrefix(directory, folderName);

    // 输出匹配的文件，以 folderName 开头的PDF文件
    if (pdfFiles.isEmpty()) {
      return null;
    } else {
      // 取第一个匹配的文件名，即以 folderName 开头的PDF文件
      for (Path pdfFile : pdfFiles) {
        String fileNameWithExtension = pdfFile.getFileName().toString();
        if (fileNameWithExtension.startsWith(folderName)) {
          return fileNameWithExtension;
        }
      }

      return null;
    }
  }

  public static void main(String[] args) {
//    // 文件夹路径
//    String directory = "D:\\14_LearnEnglish\\6MinuteEnglish\\2025\\250220\\";
//    // 文件名前缀
//    String prefix = "250220";
//
//    List<Path> pdfFiles = getPdfFilesWithPrefix(directory, prefix);
//
//    // 输出匹配的文件
//    if (pdfFiles.isEmpty()) {
//      System.out.println("没有找到匹配的PDF文件。");
//    } else {
//      pdfFiles.forEach(file -> System.out.println("找到PDF文件: " + file));
//    }

    String folderName = "170202";
    String pdfFileName = PdfFileFinder.findPdfFileName(folderName);
    String fileNameWithPath = CommonUtil.getFullPath(folderName)  + File.separator + pdfFileName;
    System.out.println(pdfFileName);
  }
}
