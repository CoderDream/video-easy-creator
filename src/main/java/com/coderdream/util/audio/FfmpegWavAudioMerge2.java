package com.coderdream.util.audio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FfmpegWavAudioMerge2 {

    public static void main(String[] args) {
        // 假设这里是音频文件列表，实际应用中可以从其他地方获取
        List<String> audioFiles = new ArrayList<>();
        audioFiles.add("audio1.wav");
        audioFiles.add("audio2.wav");
        audioFiles.add("audio3.wav");

        // 生成用于ffmpeg合并的文本文件
        String listFilePath = createFileList(audioFiles);

        try {
            // ffmpeg命令构建，用于合并.wav音频文件
            List<String> command = new ArrayList<>();
            command.add("ffmpeg");
            command.add("-f");
            command.add("concat");
            command.add("-safe");
            command.add("0");
            command.add("-i");
            command.add(listFilePath);
            command.add("-c");
            command.add("copy");
            command.add("merged_audio.wav");

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            // 将标准输出和标准错误输出合并，方便查看执行信息
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // 读取命令执行的输出信息
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine())!= null) {
                System.out.println(line);
            }

            // 等待命令执行完成并获取退出状态码
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("音频合并成功！");
            } else {
                System.out.println("音频合并失败，退出码: " + exitCode);
            }

            // 删除用于合并的临时文本文件
            File listFile = new File(listFilePath);
            if (listFile.exists()) {
                listFile.delete();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建用于ffmpeg合并音频的文本文件，格式为每行一个音频文件路径
     *
     * @param audioFiles 音频文件列表
     * @return 文本文件路径
     */
    private static String createFileList(List<String> audioFiles) {
        try {
            File tempFile = File.createTempFile("audio_list", ".txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            for (String audioFile : audioFiles) {
                writer.write("file '" + audioFile + "'\n");
            }
            writer.close();
            return tempFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
