package com.coderdream.util.video.demo02;

import com.coderdream.util.video.PureCreateVideo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FFmpegParallel {

    public static void process(String[] args) throws InterruptedException {
        // 创建一个固定大小的线程池，例如 4 个线程
        ExecutorService executor = Executors.newFixedThreadPool(4);

        // 假设你有多个输入文件和输出文件
        List<String> inputFiles = List.of("input1.mp4", "input2.mp4", "input3.mp4", "input4.mp4");
        List<String> outputFiles = List.of("output1.mp4", "output2.mp4", "output3.mp4", "output4.mp4");

        // 为每个 FFmpeg 命令创建一个任务
        for (int i = 0; i < inputFiles.size(); i++) {
            final String inputFile = inputFiles.get(i);
            final String outputFile = outputFiles.get(i);

            Runnable task = () -> {
//                try {
//                    PureCreateVideo.createVideoCore(imageFile, audioFile, videoFile, duration);
//                } catch (IOException | InterruptedException e) {
//                    e.printStackTrace();
//                }
            };

            // 提交任务到线程池
            executor.submit(task);
        }

        // 关闭线程池，等待所有任务完成
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS); //或者设置一个超时时间

        System.out.println("All FFmpeg tasks completed.");
    }
}
