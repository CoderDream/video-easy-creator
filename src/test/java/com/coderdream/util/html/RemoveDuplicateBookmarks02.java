package com.coderdream.util.html;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReUtil;
import java.util.LinkedHashSet;
import java.util.Set;

public class RemoveDuplicateBookmarks02 {
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
        // 使用正则表达式匹配所有书签链接
        String linkPattern = "<A HREF=\"([^\"]+)\">.*?</A>";
        Set<String> seenLinks = new LinkedHashSet<>(); // 用于去重并保持顺序
        StringBuilder cleanedHtml = new StringBuilder();

        // 按行处理HTML内容
        String[] lines = htmlContent.split("\n");
        for (String line : lines) {
            if (ReUtil.isMatch(linkPattern, line)) {
                // 提取链接部分
                String link = ReUtil.get(linkPattern, line, 1);
                if (!seenLinks.contains(link)) {
                    seenLinks.add(link);
                    cleanedHtml.append(line).append("\n"); // 保留非重复链接
                }
            } else {
                // 非链接行直接保留
                cleanedHtml.append(line).append("\n");
            }
        }

        return cleanedHtml.toString();
    }
}
