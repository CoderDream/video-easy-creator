package com.coderdream.util;

import java.net.URL;

public class ResourcePathExample {
    public static void main(String[] args) {
        String fileName = "application.yml";

        getFolderName(fileName);
    }

    private static void getFolderName(String fileName) {
        // 获取资源的URL
        URL resourceUrl = ResourcePathExample.class.getClassLoader().getResource(
          fileName);

        if (resourceUrl != null) {
            // 将URL转化为文件路径
            String filePath = resourceUrl.getPath();
            System.out.println("文件完整路径: " + filePath);
        } else {
            System.out.println("资源文件未找到！");
        }
    }
}
