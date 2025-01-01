package com.coderdream.util.subtitle;

import com.coderdream.util.CdConstants;
import java.io.*;
import java.util.*;
import java.text.*;

public class TextToSrtConverter1 {

    public static void main(String[] args) {
        // 输入文本文件路径
        String inputFilePath = CdConstants.RESOURCES_BASE_PATH   + "input.txt"; // 请替换为你实际的文件路径
        // 输出字幕文件路径
        String outputFilePath = CdConstants.RESOURCES_BASE_PATH +"output.srt";

        // 读取文本并转换成字幕文件
        try {
            convertTextToSrt(inputFilePath, outputFilePath);
            System.out.println("字幕文件已生成: " + outputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将文本文件转换为SRT字幕格式
     *
     * @param inputFilePath 输入文本文件路径
     * @param outputFilePath 输出字幕文件路径
     * @throws IOException 如果读取或写入文件时发生错误
     */
    public static void convertTextToSrt(String inputFilePath, String outputFilePath) throws IOException {
        // 读取输入文件
        List<String[]> lines = readLines(inputFilePath);

        // 准备输出文件
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));

        double currentTime = 0.0;  // 当前时间从0秒开始
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss,SSS");

        // 按照SRT格式写入每一条字幕
        for (int i = 0; i < lines.size(); i++) {
            String[] line = lines.get(i);
            String text = line[0];
            double duration = Double.parseDouble(line[1]);

            // 计算字幕的开始和结束时间
            String startTime = formatTime(currentTime);
            String endTime = formatTime(currentTime + duration);

            // 写入SRT格式
            writer.write((i + 1) + "\n");  // 字幕编号
            writer.write(startTime + " --> " + endTime + "\n");  // 时间戳
            writer.write(text + "\n\n");  // 字幕内容

            // 更新当前时间
            currentTime += duration;
        }

        // 关闭文件写入流
        writer.close();
    }

    /**
     * 读取输入文件，将文本和持续时间存入一个列表
     *
     * @param filePath 文件路径
     * @return 每行文本和持续时间的列表
     * @throws IOException 如果读取文件时出错
     */
    public static List<String[]> readLines(String filePath) throws IOException {
        List<String[]> lines = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        String line;
        while ((line = reader.readLine()) != null) {
            // 按照tab分隔文本和持续时间
            String[] parts = line.split("\t");
            if (parts.length == 2) {
                lines.add(parts);
            }
        }

        reader.close();
        return lines;
    }

    /**
     * 将秒数转换为SRT时间格式 (HH:mm:ss,SSS)
     *
     * @param time 秒数
     * @return 格式化后的时间字符串
     */
    public static String formatTime(double time) {
        long hours = (long) time / 3600;
        long minutes = (long) (time % 3600) / 60;
        long seconds = (long) (time % 60);
        long milliseconds = (long) ((time - (long) time) * 1000);

        // 使用SimpleDateFormat格式化时间为 SRT 标准格式
        return String.format("%02d:%02d:%02d,%03d", hours, minutes, seconds, milliseconds);
    }
}
