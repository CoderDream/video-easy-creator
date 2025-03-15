package com.coderdream.util.html;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReUtil;

import cn.hutool.core.util.StrUtil;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class RemoveDuplicateBookmarks {
    public static void main(String[] args) {
        // 输入文件路径
        String inputFilePath = "D:\\00_Green\\ChromePlugins\\bookmarks_2025_03_12.htm"; // 替换为你的HTML文件路径
        // 输出文件路径
        String outputFilePath = "D:\\00_Green\\ChromePlugins\\bookmarks_2025_03_12_new.htm"; // 替换为输出文件路径



        // 读取HTML文件内容
        String htmlContent = FileUtil.readUtf8String(inputFilePath);

        // 调用方法去除重复书签
        String cleanedHtml = removeDuplicateBookmarks(htmlContent);

        // 将清理后的HTML写入到新文件
        FileUtil.writeUtf8String(cleanedHtml, outputFilePath);

        System.out.println("清理完成，去重后的HTML已保存到：" + outputFilePath);
    }

    /**
     * 去除HTML中的重复书签
     * @param htmlContent 原始HTML内容
     * @return 去重后的HTML内容
     */
    public static String removeDuplicateBookmarks(String htmlContent) {
        Set<String> seenLines = new LinkedHashSet<>(); // 用于去重并保持顺序
        StringBuilder cleanedHtml = new StringBuilder();

        // 按行处理HTML内容
        String[] lines = htmlContent.split("\n");
        for (String line : lines) {
            // 检查是否以 <DT><A 开头
            if (StrUtil.startWith(line.trim(), "<DT><A")) {
                // 如果该行尚未出现过，则保留
                if (!seenLines.contains(line)) {
                    seenLines.add(line);
                    cleanedHtml.append(line).append("\n");
                }
            } else {
                // 非书签行直接保留
                cleanedHtml.append(line).append("\n");
            }
        }

        return cleanedHtml.toString();
    }
}

//        // 读取HTML文件内容
//        List<String> htmlContentList = CdFileUtil.readLines(inputFilePath, StandardCharsets.UTF_8);
//
//        // 调用方法去除重复书签
//        String cleanedHtml = removeDuplicateBookmarks(htmlContentList);
//
//        // 将清理后的HTML写入到新文件
//        CdFileUtil.writeUtf8String(cleanedHtml, outputFilePath);
//
//        System.out.println("清理完成，去重后的HTML已保存到：" + outputFilePath);
//    }
//
//    /**
//     * 去除HTML中的重复书签
//     * @param htmlContent 原始HTML内容
//     * @return 去重后的HTML内容
//     */
//    public static List<String> removeDuplicateBookmarks(List<String> htmlContentList) {
//        List<String> result = new ArrayList<>();
//        Set<String> dtSet = new LinkedHashSet<>();
//        for (String line : htmlContentList) {
//            if(line.trim().toLowerCase().startsWith("<dt><a")){
//                dtSet.add(line.trim());
//            }
//            if(line.trim().toLowerCase().startsWith("<dt><a")){
//                dtSet.add(line.trim());
//            }
//        }
//
//        return null;
//    }
//}
