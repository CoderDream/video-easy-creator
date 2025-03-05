package com.coderdream.util.cd;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
public class DateFolderGenerator {

    /**
     * 生成指定年份的每周文件夹
     *
     * @param startDateStr 开始日期字符串，格式为"yyMMdd"，例如 "250102" 代表 2025年1月2日
     * @param rootFolderPath 根文件夹路径
     */
    public static void generateWeeklyFolders(String startDateStr, String rootFolderPath) {
        // 1. 校验参数
        if (startDateStr == null || startDateStr.length() != 6) {
            log.error("开始日期格式不正确，应为 yyMMdd 格式，例如 250102");
            return;
        }
        if (rootFolderPath == null || rootFolderPath.isEmpty()) {
            log.error("根文件夹路径不能为空");
            return;
        }

        // 2. 解析开始日期
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyMMdd");
        LocalDate startDate;
        try {
            startDate = LocalDate.parse(startDateStr, inputFormatter);
            // 兼容2000年后的日期
            int yearPrefix = 2000;
            if (Integer.parseInt(startDateStr.substring(0, 2)) < 100) {
                startDate = startDate.withYear(yearPrefix + Integer.parseInt(startDateStr.substring(0, 2)));

            }

        } catch (Exception e) {
            log.error("解析开始日期失败: {}", e.getMessage(), e);
            return;
        }

        // 3. 获取年份
        int year = startDate.getYear();

        // 4. 循环生成每周文件夹直到下一年
        LocalDate currentDate = startDate;
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyMMdd");
        while (currentDate.getYear() == year) {
            // 5. 生成文件夹名称
            String folderName = currentDate.format(outputFormatter);

            // 6. 构建完整的文件夹路径
            String folderPath = rootFolderPath + File.separator + folderName;

            // 7. 创建文件夹
            File folder = new File(folderPath);
            if (!folder.exists()) {
                boolean created = folder.mkdirs();
                if (created) {
                    log.info("成功创建文件夹: {}", folderPath);
                } else {
                    log.error("创建文件夹失败: {}", folderPath);
                }
            } else {
                log.warn("文件夹已存在: {}", folderPath);
            }

            // 8. 更新日期到下周的同一天
            currentDate = currentDate.plusWeeks(1);
        }

        log.info("年度文件夹生成完成!");
    }

    /**
     * 找出指定月份的文件夹名称列表
     *
     * @param month       月份，两位数字，例如 "03" 代表 3 月
     * @param rootFolderPath 根文件夹路径
     * @return 文件夹名称列表，如果根文件夹不存在或月份格式错误，则返回空列表
     */
    public static List<String> findFoldersByMonth(String month, String rootFolderPath) {
        // 1. 校验参数
        if (month == null || month.length() != 2) {
            log.error("月份格式不正确，应为两位数字，例如 03");
            return new ArrayList<>();
        }

        if (rootFolderPath == null || rootFolderPath.isEmpty()) {
            log.error("根文件夹路径不能为空");
            return new ArrayList<>();
        }

        File rootFolder = new File(rootFolderPath);
        if (!rootFolder.exists() || !rootFolder.isDirectory()) {
            log.error("根文件夹不存在或不是一个目录: {}", rootFolderPath);
            return new ArrayList<>();
        }

        // 2. 遍历根文件夹下的所有文件夹
        File[] subFolders = rootFolder.listFiles(File::isDirectory);
        if (subFolders == null || subFolders.length == 0) {
            log.warn("根文件夹下没有子文件夹");
            return new ArrayList<>();
        }

        List<String> folderNames = new ArrayList<>();
        for (File folder : subFolders) {
            String folderName = folder.getName();
            // 3. 提取月份并进行比较
            if (folderName.length() == 6 && folderName.substring(2, 4).equals(month)) {
                folderNames.add(folderName);
            }
        }

        log.info("找到 {} 月份的文件夹 {} 个", month, folderNames.size());
        return folderNames;
    }

    public static void main(String[] args) {
//        // 示例用法
        String startDate = "230102"; // 2025年1月2日
        String rootFolder = "D:\\14_LearnEnglish\\6MinuteEnglish\\2017"; // 根文件夹
//
//        generateWeeklyFolders(startDate, rootFolder);

        // 查找3月份的文件夹
        String month = "03";
        List<String> folders = findFoldersByMonth(month, rootFolder);

        if (!folders.isEmpty()) {
            for (String folder : folders) {
                System.out.println(folder);
            }

        }

    }
}
