package com.coderdream.util.video.demo08;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FFmpegLoopVideo {

    /**
     * 使用 FFmpeg 循环视频文件 n 次并合并成新的视频文件。
     *
     * @param inputPath  输入视频文件的路径。
     * @param outputPath 输出视频文件的路径。
     * @param loopCount  循环的次数。
     * @return true 如果成功，false 如果失败。
     */
    public static boolean loopVideo(String inputPath, String outputPath, int loopCount) {
        if (loopCount <= 0) {
            System.err.println("循环次数必须大于 0。");
            return false;
        }

        try {
            // 1. 创建一个包含循环命令的文本文件
            String concatListPath = createConcatList(inputPath, loopCount);
            if (concatListPath == null) {
                System.err.println("创建 concat list 文件失败。");
                return false;
            }

            // 2. 构建 FFmpeg 命令
            List<String> command = new ArrayList<>();
            command.add("ffmpeg");
            command.add("-f");
            command.add("concat");
            command.add("-safe");
            command.add("0"); // 允许访问任何文件，因为我们知道文件是安全的
            command.add("-i");
            command.add(concatListPath);
            command.add("-c");
            command.add("copy"); // 使用 copy codec，速度更快
            command.add(outputPath);

            // 3. 执行 FFmpeg 命令
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true); // 将错误流合并到输出流
            Process process = processBuilder.start();

            // 4. 读取 FFmpeg 的输出
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line); // 可选：打印 FFmpeg 的输出
                }
            }

            // 5. 等待 FFmpeg 进程结束
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("FFmpeg 执行失败，退出代码: " + exitCode);
                return false;
            }

            System.out.println("视频循环合并成功！");
            return true;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 创建包含循环命令的文本文件。
     *
     * @param inputPath 输入视频文件的路径。
     * @param loopCount 循环的次数。
     * @return 文本文件的路径，如果创建失败则返回 null。
     */
    private static String createConcatList(String inputPath, int loopCount) {
        String concatListPath = "concat_list.txt"; // 可以根据需要修改
        try (java.io.PrintWriter writer = new java.io.PrintWriter(concatListPath)) {
            for (int i = 0; i < loopCount; i++) {
                writer.println("file '" + inputPath + "'");
            }
            return concatListPath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        String inputPath = "input.mp4"; // 替换为你的输入视频文件
        String outputPath = "output.mp4"; // 替换为你的输出视频文件
        int loopCount = 3; // 替换为你想循环的次数

        if (loopVideo(inputPath, outputPath, loopCount)) {
            System.out.println("循环完成，输出文件: " + outputPath);
        } else {
            System.err.println("循环失败。");
        }
    }
}
