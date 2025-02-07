package com.coderdream.util.txt;

import java.io.*;
import java.util.regex.Pattern;

public class FileFilterUtil2 {

    /**
     * 按行读取文件，并根据规则过滤后保存到新文件。
     *
     * @param inputFilePath  输入文件路径
     * @param outputFilePath 输出文件路径
     * @throws IOException 如果发生 IO 异常
     */
    public static void filterAndSaveFile(String inputFilePath, String outputFilePath) throws IOException {
        File inputFile = new File(inputFilePath);
        File outputFile = new File(outputFilePath);

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (shouldKeepLine(line)) {
                    writer.write(line);
                    writer.newLine(); // 写入换行符
                }
            }

        } catch (IOException e) {
            System.err.println("发生 IO 异常: " + e.getMessage()); // 打印到标准错误流
            throw e; // 重新抛出异常，让调用者处理
        }
    }

    /**
     * 判断是否应该保留该行。
     *
     * @param line 要判断的行
     * @return 如果应该保留，则返回 true，否则返回 false
     */
    private static boolean shouldKeepLine(String line) {
        // 情况 1：保留以 "Scene " 开头的行
        if (line.startsWith("Scene ")) {
            return true;
        }

        // 情况 2：保留以 ❶、❷、...、❿ 开头的行
        Pattern pattern = Pattern.compile("^[❶❷❸❹❺❻❼❽❾❿].*");  // 使用正则表达式
        if (pattern.matcher(line).matches()) {
            return true;
        }

        // 其他情况：移除该行 (不保留)
        return false;
    }

    public static void main(String[] args) {
        String inputFilePath = "D:\\0000\\EnBook002\\Chapter007\\Chapter007_temp.txt"; // 替换为你的输入文件路径
        String outputFilePath = "D:\\0000\\EnBook002\\Chapter007\\Chapter007.txt"; // 替换为你的输出文件路径

        try {
            FileFilterUtil2.filterAndSaveFile(inputFilePath, outputFilePath);
            System.out.println("文件过滤和保存成功！");
        } catch (IOException e) {
            System.err.println("文件过滤和保存失败: " + e.getMessage());
            // 在这里可以进行更详细的错误处理，例如记录日志
        }
    }
}
