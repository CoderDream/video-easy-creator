package com.coderdream.util.txt.doubao;

import lombok.extern.slf4j.Slf4j;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@Slf4j
public class DoubaoTextFileProcessor3 {

    /**
     * 该方法用于处理文本文件，将 ch003_temp.txt 的内容处理后写入 ch003.txt 中
     * 处理规则为：若行末尾不是句子结束标点符号（中英文句号、问号、感叹号等），则将下一行或下下一行合并到当前行，但如果后面一行包含中英文冒号则不合并，而是保留该行
     * 同时记录方法调用的耗时，返回时分秒毫秒
     *
     * @param inputFilePath  输入文件的路径，如 D:\0000\EnBook001\900\ch003\ch003_t.txt
     * @param outputFilePath 输出文件的路径，如 D:\0000\EnBook001\900\ch003\ch003.txt
     * @return 方法调用的耗时，格式为时分秒毫秒
     */
    public static String processTextFile(String inputFilePath, String outputFilePath) {
        Instant start = Instant.now();
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            String line = reader.readLine();
            while (line!= null) {
                result.append(removeAllExtraSpaces(line));
                if (!isSentenceEnd(line)) {
                    String nextLine = reader.readLine();
                    if (nextLine!= null) {
                        if (!nextLine.contains(":") &&!nextLine.contains("：")) { // 检查下一行是否包含冒号
                            result.append(removeAllExtraSpaces(nextLine));
                        } else {
                            result.append("\n").append(removeAllExtraSpaces(nextLine)); // 若包含冒号则另起一行添加
                        }
                        if (!isSentenceEnd(nextLine)) {
                            String nextNextLine = reader.readLine();
                            if (nextNextLine!= null) {
                                if (!nextNextLine.contains(":") &&!nextNextLine.contains("：")) { // 检查下下一行是否包含冒号
                                    result.append(removeAllExtraSpaces(nextNextLine));
                                } else {
                                    result.append("\n").append(removeAllExtraSpaces(nextNextLine)); // 若包含冒号则另起一行添加
                                }
                            }
                        }
                    }
                }
                result.append("\n");
                writer.write(result.toString());
                result = new StringBuilder();
                line = reader.readLine();
            }
        } catch (IOException e) {
            log.error("处理文件时发生异常", e);
        }
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        long millis = duration.toMillis() % 1000;
        return String.format("%02d:%02d:%02d:%03d", hours, minutes, seconds, millis);
    }

    /**
     * 判断一行是否以句子结束标点符号结尾
     *
     * @param line 输入的一行文本
     * @return 如果以句子结束标点结尾返回 true，否则返回 false
     */
    private static boolean isSentenceEnd(String line) {
        if (line.endsWith(".") || line.endsWith("!") || line.endsWith("?") || line.endsWith("。") || line.endsWith("！") || line.endsWith("？")) {
            return true;
        }
        return false;
    }

    /**
     * 去除字符串中所有多余的空格，包括行内的多个连续空格和行尾的空格
     *
     * @param line 输入的字符串
     * @return 去除多余空格后的字符串
     */
    private static String removeAllExtraSpaces(String line) {
        return line.replaceAll("\\s+", " ").trim();
    }

    public static void main(String[] args) {
        String inputFilePath = "D:\\0000\\EnBook001\\900\\ch003\\ch003_t.txt";
        String outputFilePath = "D:\\0000\\EnBook001\\900\\ch003\\ch003.txt";
        String timeTaken = processTextFile(inputFilePath, outputFilePath);
        System.out.println("方法调用耗时: " + timeTaken);
    }
}
