package com.coderdream.util.subtitle;

import java.nio.file.Path;
import java.nio.file.Paths;

public class GetPureFileNameNIO {
     public static void main(String[] args) {
          String filePath1 = "/path/to/my/document.pdf";
        String filePath2 = "my_image.jpg";
          String filePath3 = "/folder/report"; //无扩展名

        System.out.println("文件1的纯文件名："+ getPureFileNameWithPath(filePath1));
        System.out.println("文件2的纯文件名："+ getPureFileNameWithPath(filePath2));
        System.out.println("文件3的纯文件名："+ getPureFileNameWithPath(filePath3));

    }

    public static String getPureFileNameWithPath(String filePath) {
        Path path = Paths.get(filePath);
        return path.getFileName().toString();
    }
}
