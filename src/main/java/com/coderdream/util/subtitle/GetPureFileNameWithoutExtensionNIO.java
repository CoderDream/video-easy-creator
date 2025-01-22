package com.coderdream.util.subtitle;

import java.nio.file.Path;
import java.nio.file.Paths;

public class GetPureFileNameWithoutExtensionNIO {

  public static void main(String[] args) {
    String filePath1 = "/path/to/my/document.pdf";
    String filePath2 = "my_image.jpg";
    String filePath3 = "/folder/report"; //无扩展名

    System.out.println(
      "文件1的纯文件名（无扩展名）：" + getPureFileNameWithoutExtensionWithPath(
        filePath1));
    System.out.println(
      "文件2的纯文件名（无扩展名）：" + getPureFileNameWithoutExtensionWithPath(
        filePath2));
    System.out.println(
      "文件3的纯文件名（无扩展名）：" + getPureFileNameWithoutExtensionWithPath(
        filePath3));
  }

  public static String getPureFileNameWithoutExtensionWithPath(
    String filePath) {
    Path path = Paths.get(filePath);
    String fileName = path.getFileName().toString();
    int lastDotIndex = fileName.lastIndexOf('.');
    if (lastDotIndex == -1) {
      return fileName; // 如果没有点，直接返回
    }
    return fileName.substring(0, lastDotIndex);
  }
}
