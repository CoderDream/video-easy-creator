package com.coderdream.util.video.demo01;

import com.coderdream.util.ffmpeg.FfmpegUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class BatchVideoCreator {

    private final String imageDir;
    private final String audioDir;
    private final String outputDir;
    private final int corePoolSize;
    private final int maxPoolSize;
    private final long keepAliveTime;
    private final TimeUnit unit;
    private final BlockingQueue<Runnable> workQueue;

    public BatchVideoCreator(String imageDir, String audioDir, String outputDir,
                              int corePoolSize, int maxPoolSize, long keepAliveTime, TimeUnit unit,
                              BlockingQueue<Runnable> workQueue) {
        this.imageDir = imageDir;
        this.audioDir = audioDir;
        this.outputDir = outputDir;
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.unit = unit;
        this.workQueue = workQueue;
    }

    public void processVideos() {
        // 自定义线程工厂
        ThreadFactory threadFactory = new ThreadFactory() {
            private final AtomicInteger threadNumber = new AtomicInteger(1);
            @Override
            public Thread newThread(@NotNull Runnable r) {
                Thread t = new Thread(r, "video-creator-" + threadNumber.getAndIncrement());
                if (t.isDaemon()) {
                    t.setDaemon(false); // 将线程设置为非守护线程
                }
                if (t.getPriority() != Thread.NORM_PRIORITY) {
                    t.setPriority(Thread.NORM_PRIORITY); // 设置为普通优先级
                }
                return t;
            }
        };

        // 创建 ThreadPoolExecutor
        ThreadPoolExecutor executorService = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                threadFactory,
                new ThreadPoolExecutor.AbortPolicy() // 拒绝策略，你可以根据需要选择其他的
        );


        // 预热核心线程（可选）
       // executorService.prestartAllCoreThreads();

        List<Future<Boolean>> futures = new ArrayList<>();

        File[] imageFiles = new File(imageDir).listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpeg"));
        File[] audioFiles = new File(audioDir).listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3") || name.toLowerCase().endsWith(".wav") || name.toLowerCase().endsWith(".flac") || name.toLowerCase().endsWith(".aac"));

        if (imageFiles == null || audioFiles == null) {
            log.error("图片或音频文件夹为空或读取失败。");
            return;
        }

        if (imageFiles.length != audioFiles.length) {
            log.error("图片和音频文件数量不匹配。 图片数量: {}, 音频数量: {}", imageFiles.length, audioFiles.length);
            return;
        }

        for (int i = 0; i < imageFiles.length; i++) {
            File imageFile = imageFiles[i];
            File audioFile = audioFiles[i];
            String outputFileName = imageFile.getName().substring(0, imageFile.getName().lastIndexOf(".")) + ".mp4";
            File outputFile = new File(outputDir, outputFileName);
            double duration = FfmpegUtil.getAudioDuration(audioFile);

            VideoCreator videoCreator = new VideoCreator(imageFile, audioFile, outputFile, duration);
            Future<Boolean> future = executorService.submit(videoCreator);
            futures.add(future);
        }
        executorService.shutdown();

        int successCount = 0;
        int failCount = 0;
        for (Future<Boolean> future : futures) {
            try {
                if (future.get()) {
                    successCount++;
                } else {
                    failCount++;
                }
            } catch (InterruptedException | ExecutionException e) {
                log.error("获取任务结果时发生异常:", e);
                failCount++;
            }
        }

        log.info("批量视频处理完成。 成功: {}, 失败: {}", successCount, failCount);

        try {
            if (!executorService.awaitTermination(1, TimeUnit.HOURS)) { // 增加超时时间到1小时
                List<Runnable> unfinishedTasks = executorService.shutdownNow(); // 如果超时，强制关闭并返回未完成的任务
                log.warn("仍有 {} 个任务未完成。", unfinishedTasks.size());
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private String parseDurationFromFFmpegOutput(String ffmpegOutput) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("Duration: (\\d{2}:\\d{2}:\\d{2}\\.\\d+),");
        java.util.regex.Matcher matcher = pattern.matcher(ffmpegOutput);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private double convertDurationToSeconds(String durationStr) {
        String[] parts = durationStr.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        double seconds = Double.parseDouble(parts[2]);
        return hours * 3600 + minutes * 60 + seconds;
    }

    public static void main(String[] args) {
        String imageDir = "path/to/your/images";
        String audioDir = "path/to/your/audios";
        String outputDir = "path/to/your/output/videos";

        // 根据你的 M4 芯片的核心数和任务特性调整以下参数
        int corePoolSize = Runtime.getRuntime().availableProcessors(); // 通常设置为 CPU 核心数
        int maxPoolSize = corePoolSize * 2;  // 最大线程数，可以根据需要调整
        long keepAliveTime = 60L; // 空闲线程存活时间
        TimeUnit unit = TimeUnit.SECONDS;
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(100); // 任务队列，设置合适的容量


        BatchVideoCreator creator = new BatchVideoCreator(imageDir, audioDir, outputDir,
                corePoolSize, maxPoolSize, keepAliveTime, unit, workQueue);
        creator.processVideos();
    }
}