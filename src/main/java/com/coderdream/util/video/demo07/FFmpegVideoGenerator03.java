package com.coderdream.util.video.demo07;

import com.coderdream.util.cmd.CommandUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FFmpegVideoGenerator03 {

    public static void generateVideo(String path, String inputVideoPath, String outputVideoPath, String assPath,
                                     int width, int height, String maskColor, double maskOpacity,
                                     String videoCodec, String audioCodec, boolean overwrite)
            throws IOException, InterruptedException {
        // 文件存在性檢查
        File inputFile = new File(path + File.separator + inputVideoPath);
        File assFile = new File(path + File.separator + assPath);
        if (!inputFile.exists()) {
            log.error("輸入視頻文件不存在：{}", inputVideoPath);
            throw new IOException("輸入視頻文件不存在：" + inputVideoPath);
        }
        if (!assFile.exists()) {
            log.error("ASS 文件不存在：{}", assPath);
            throw new IOException("ASS 文件不存在：" + assPath);
        }

        // 使用規範化路徑
//        inputVideoPath = inputFile.getCanonicalPath();
//        outputVideoPath = new File(outputVideoPath).getCanonicalPath();
//        assPath = assFile.getCanonicalPath();

        List<String> command = new ArrayList<>();
        command.add("ffmpeg");
        if (overwrite) {
            command.add("-y");
        }
        command.add("-i");
        command.add(inputVideoPath);
        command.add("-vf");
        // 使用雙引號包裹 subtitles 路徑
//        command.add(
//            String.format("drawbox=x=0:y=0:w=%d:h=%d:c=%s@%.1f:t=fill,subtitles=%s",
//                width, height, maskColor, maskOpacity, assPath));

        command.add(
          String.format("drawbox=x=0:y=0:w=%d:h=%d:c=%s@%.1f:t=fill,subtitles='%s'",
            width, height, maskColor, maskOpacity, assPath));
        command.add("-c:v");
        command.add(videoCodec);
        command.add("-c:a");
        command.add(audioCodec);
        command.add(outputVideoPath);

        String fullCommand = String.join(" ", command);
        log.info("執行 FFmpeg 命令：{}", fullCommand);

//        String baseHexoFolder = OperatingSystem.getHexoFolder();
//        List<String> commandList = Arrays.asList(
//          "cd " + path + " && " + fullCommand);
//        for (String command : commandList) {
//            CommandUtil.executeCommand(command);
//        }

        CommandUtil.executeCommand("cd " + path + " && " + fullCommand);

//        CommandUtil.executeCommand("cd " + path + " && " + "ffmpeg -y -i Chapter001.mp4      -vf drawbox=x=0:y=0:w=1920:h=1080:c=black@0.5:t=fill,subtitles=subtitles3.ass -c:v libx264 -c:a copy output6.mp4");

//        ProcessBuilder pb = new ProcessBuilder(command);
//        Process process = pb.start();
//
//        // 捕獲 FFmpeg 輸出
//        BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
//        BufferedReader stdErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
//        String line;
//        log.info("FFmpeg 標準輸出：");
//        while ((line = stdOut.readLine()) != null) {
//            log.info(line);
//        }
//        log.error("FFmpeg 錯誤輸出：");
//        while ((line = stdErr.readLine()) != null) {
//            log.error(line);
//        }
//
//        int exitCode = process.waitFor();
//        if (exitCode == 0) {
//            log.info("視頻生成成功：{}", outputVideoPath);
//        } else {
//            log.error("FFmpeg 執行失敗，退出碼：{}", exitCode);
//            log.error("失敗命令：{}", fullCommand);
//        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String path = "D:\\0000\\EnBook005\\Chapter001";
        String chapterName = "Chapter001";

//        String inputVideoPath = path + File.separator + chapterName + ".mp4";
//        String outputVideoPath = path + File.separator + "output6.mp4";
//        String assPath = path + File.separator + "subtitles3.ass";

        String inputVideoPath = chapterName + ".mp4";
        String outputVideoPath = "output6.mp4";
        String assPath = "subtitles3.ass";
        int width = 1920;
        int height = 1080;
        String maskColor = "black";
        double maskOpacity = 0.5;
        String videoCodec = "h264_nvenc";
        String audioCodec = "copy";
        boolean overwrite = true;

        generateVideo(path,
            inputVideoPath, outputVideoPath, assPath, width, height, maskColor,
            maskOpacity, videoCodec, audioCodec, overwrite
        );
    }
}
