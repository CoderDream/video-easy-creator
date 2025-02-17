package com.coderdream.util.video;

import static com.coderdream.util.cd.CdConstants.OS_MAC;
import static com.coderdream.util.cd.CdConstants.OS_WINDOWS;

import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.proxy.OperatingSystem;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PureCreateVideo {

    public static boolean createVideoCore(String imageFilePath, String audioFilePath, String videoFilePath,
                                          double duration) { // 返回 boolean 值
        long startTime = System.currentTimeMillis();
        List<String> command = new ArrayList<>();
        command.add("ffmpeg");
        command.add("-y");
        command.add("-loop");
        command.add("1");
        command.add("-framerate");
        command.add("60");
        command.add("-t");
        command.add(String.valueOf(duration));
        command.add("-i");
        command.add(imageFilePath);
        command.add("-i");
        command.add(audioFilePath);
        command.add("-s");
        command.add("3840x2160");

        String os = OperatingSystem.getOS();
        if (OS_WINDOWS.equals(os)) {
            command.add("-c:v");
            command.add("h264_nvenc");
            command.add("-preset");
            command.add("p4");
            command.add("-b:v");
            command.add("10000k");
            command.add("-profile:v");
            command.add("high");
            command.add("-bf");
            command.add("2");
            command.add("-refs");
            command.add("5");

        } else if (OS_MAC.equals(os)) {
            command.add("-c:v");
            command.add("h264_videotoolbox");
            command.add("-q:v");
            command.add("60");
            command.add("-profile:v");
            command.add("high");
            command.add("-b:v");
            command.add("10000k");
        } else {
            command.add("-c:v");
            command.add("libx264");
            command.add("-preset");
            command.add("medium");
            command.add("-crf");
            command.add("19");
        }

        command.add("-c:a");
        command.add("aac");
        command.add("-ac");
        command.add("2");
        command.add("-shortest");
        command.add(videoFilePath);

        log.debug("执行 FFmpeg 命令: {}", String.join(" ", command));

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        Process process = null;
        try {
            process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.trace("{}", line);
                }
            }

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                long endTime = System.currentTimeMillis();
                log.info("视频创建成功 (文件: {}), 耗时: {}", videoFilePath, CdTimeUtil.formatDuration(endTime - startTime));
                return true; // 创建成功，返回 true
            } else {
                log.error("FFmpeg 进程执行失败，退出代码: {}, 文件: {}", exitCode, videoFilePath); // 更详细的错误信息
                return false; // 创建失败，返回 false
            }
        } catch (IOException | InterruptedException e) {
            log.error("执行 FFmpeg 命令时发生异常: ", e); // 记录异常的堆栈信息
             // 也可以记录更具体的信息，例如当前处理的文件名
            log.error("处理文件 {} 时发生错误", videoFilePath);
            return false; // 发生异常，返回 false
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }
}
