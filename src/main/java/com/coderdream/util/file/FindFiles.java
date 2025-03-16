package com.coderdream.util.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FindFiles {

    public static List<String> findBaiCiZanFiles(String directoryPath) {
        List<String> fileNames = new ArrayList<>();
        File directory = new File(directoryPath);

        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("指定的目录不存在或不是一个目录: " + directoryPath);
            return fileNames; // 返回空列表
        }

        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().startsWith("bai-ci-zan-") && file.getName().endsWith(".md")) {
                    fileNames.add(file.getName());
                }
            }
        }

        return fileNames;
    }

    public static void main(String[] args) {
        String directoryPath = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts"; // 你的目录

        List<String> files = findBaiCiZanFiles(directoryPath);

        if (files.isEmpty()) {
            System.out.println("没有找到符合条件的文件.");
        } else {
            System.out.println("找到的文件名:");
            for (String fileName : files) {
                System.out.println(fileName);
            }
        }
    }
}
