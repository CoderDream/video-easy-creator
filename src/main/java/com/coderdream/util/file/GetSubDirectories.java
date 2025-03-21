//package com.coderdream.util.file;
//
//import cn.hutool.core.io.FileUtil;
//import java.io.File;
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class GetSubDirectories {
//
////    public static List<File> getDirectSubdirectories(String directoryPath) {
////        File directory = new File(directoryPath);
////        return getDirectSubdirectories(directory);
////    }
//
//
//    public static List<File> getDirectSubdirectories(String directoryPath) {
//        File directory = new File(directoryPath);
//        if (!directory.exists() || !directory.isDirectory()) {
//            throw new IllegalArgumentException("Invalid directory: " + directory);
//        }
//
//        File[] files = FileUtil.ls(directory.getAbsolutePath());
//        return Arrays.stream(files)
//                .filter(File::isDirectory)
//                .collect(Collectors.toList());
//    }
//
//
//    public static void main(String[] args) {
//        // 替换为你的文件夹路径
//        String folderPath = "C:\\Users\\CoderDream\\Videos\\History_BBC\\";
//
//        try {
//            List<File> subdirectories = getDirectSubdirectories(folderPath);
//
//            if (subdirectories.isEmpty()) {
//                System.out.println("No subdirectories found.");
//            } else {
//                System.out.println("Subdirectories:");
//                for (File subdirectory : subdirectories) {
//                    System.out.println(subdirectory.getName()); // 打印子文件夹名称
//                    // 或者使用 subdirectory.getAbsolutePath() 获取完整路径
//                }
//        }
//        } catch (IllegalArgumentException e) {
//            System.err.println("Error: " + e.getMessage());
//        }
//
//    }
//}
