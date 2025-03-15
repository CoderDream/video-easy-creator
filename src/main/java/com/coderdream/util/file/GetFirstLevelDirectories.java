package com.coderdream.util.file;

import cn.hutool.core.io.FileUtil;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GetFirstLevelDirectories {

    public static void main(String[] args) {
        String directoryPath = "D:\\14_LearnEnglish\\6MinuteEnglish\\2017"; // 替换成你的文件夹路径

        List<File> firstLevelDirectories = getFirstLevelDirectories(directoryPath);

        if (firstLevelDirectories != null && !firstLevelDirectories.isEmpty()) {
            System.out.println("第一层文件夹列表：");
            for (File directory : firstLevelDirectories) {
                System.out.println(directory.getName());
            }
        } else {
            System.out.println("该目录下没有第一层文件夹或路径不存在。");
        }
    }


    /**
     * 获取指定目录下第一层文件夹列表
     * @param directoryPath 目录路径
     * @return 第一层文件夹列表，如果目录不存在或为空，则返回null
     */
    public static List<File> getFirstLevelDirectories(String directoryPath) {
        File directory = new File(directoryPath);

        // 检查目录是否存在并且是目录
        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("指定的路径不存在或不是一个目录。");
            return null;
        }

        // 使用 CdFileUtil.ls(String) 获取所有文件和文件夹，然后过滤出文件夹
        File[] filesArray = FileUtil.ls(directoryPath);  // 使用 String 路径，返回 File[]

        // 修复空目录的校验逻辑
        if (filesArray == null || filesArray.length == 0) {
            System.out.println("该目录为空。");
            return null;
        }

        // 将 File[] 转换为 List<File>
        List<File> files = Arrays.asList(filesArray);

        // 过滤出文件夹
        return files.stream()
                .filter(File::isDirectory)
                .collect(Collectors.toList());
    }
}
