package com.coderdream.util.ollama;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.StopWatch;
import com.coderdream.entity.DualSubtitleEntity;
import com.coderdream.util.ollama.OllamaApiHuTool;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 字幕翻译工具类
 */
@Slf4j
public class SubtitleTranslator {

    private static final int CORE_POOL_SIZE = Runtime.getRuntime()
            .availableProcessors();
    private static final int MAX_POOL_SIZE = CORE_POOL_SIZE * 2;
    private static final long KEEP_ALIVE_TIME = 60L;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private static final int QUEUE_CAPACITY = 1000;
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 1000; // 1 second
    /**
     * 翻译线程池，使用阿里推荐的线程池创建方式
     */
    private static final ThreadPoolExecutor TRANSLATE_EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TIME_UNIT,
            new LinkedBlockingQueue<>(QUEUE_CAPACITY),
            new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * 翻译字幕列表
     *
     * @param subtitleList 排序好的字幕列表
     * @return 翻译后的字幕列表
     */
    public static List<DualSubtitleEntity> translateSubtitles(
            List<DualSubtitleEntity> subtitleList) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        if (CollUtil.isEmpty(subtitleList)) {
            log.warn("字幕列表为空，无需翻译。");
            return new ArrayList<>();
        }

        List<Future<DualSubtitleEntity>> futures = new ArrayList<>();
        for (DualSubtitleEntity subtitle : subtitleList) {
            futures.add(TRANSLATE_EXECUTOR.submit(() -> translateSubtitle(subtitle)));
        }

        List<DualSubtitleEntity> translatedSubtitles = new ArrayList<>();
        for (Future<DualSubtitleEntity> future : futures) {
            try {
                translatedSubtitles.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                log.error("翻译任务执行异常：{}", e.getMessage(), e);
            }
        }

        // 确保返回的字幕列表按照subIndex排序
        List<DualSubtitleEntity> sortedSubtitles = translatedSubtitles.stream()
                .sorted(Comparator.comparingInt(DualSubtitleEntity::getSubIndex))
                .collect(Collectors.toList());

        stopWatch.stop();
        log.info("字幕翻译完成，总耗时：{}",
                DateUtil.formatBetween(stopWatch.getTotalTimeMillis()));

        // 关闭线程池
        shutdownThreadPool();
        return sortedSubtitles;
    }

    /**
     * 翻译单个字幕
     *
     * @param subtitle 待翻译的字幕实体
     * @return 翻译后的字幕实体
     */
    private static DualSubtitleEntity translateSubtitle(
            DualSubtitleEntity subtitle) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                String englishSubtitle = subtitle.getEnglishSubtitle();
                String chineseSubtitle = OllamaApiHuTool.generate(englishSubtitle);
                if (chineseSubtitle != null) {
                    subtitle.setChineseSubtitle(chineseSubtitle);
                    log.debug("字幕 {} 翻译成功：{}", subtitle.getSubIndex(),
                            chineseSubtitle);
                    return subtitle; // 翻译成功，退出重试
                } else {
                    log.warn("字幕 {} 翻译失败，尝试重试 {}/{}", subtitle.getSubIndex(),
                            attempt, MAX_RETRIES);
                }
            } catch (Exception e) {
                log.error("字幕 {} 翻译异常：{}", subtitle.getSubIndex(),
                        e.getMessage(), e);
            }

            if (attempt < MAX_RETRIES) {
                try {
                    Thread.sleep(RETRY_DELAY_MS); // 休眠后重试
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupted state.
                    log.error("Retry interrupted.", e);
                    return subtitle;
                }
            }
        }

        log.error("字幕 {} 翻译失败，已达到最大重试次数。", subtitle.getSubIndex());
        return subtitle; // 达到最大重试次数，返回未翻译的字幕
    }

    /**
     * 关闭线程池
     */
    private static void shutdownThreadPool() {
        TRANSLATE_EXECUTOR.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!TRANSLATE_EXECUTOR.awaitTermination(60, TimeUnit.SECONDS)) {
                TRANSLATE_EXECUTOR.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!TRANSLATE_EXECUTOR.awaitTermination(60, TimeUnit.SECONDS))
                    log.error("线程池未能完全关闭！");
            }
            log.info("线程池已成功关闭。");
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            TRANSLATE_EXECUTOR.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
            log.error("关闭线程池过程中发生中断！", ie);
        }
    }

    // 示例使用
    public static void main(String[] args) {
        List<DualSubtitleEntity> subtitles = new ArrayList<>();
        DualSubtitleEntity subtitle1 = new DualSubtitleEntity();
        subtitle1.setSubIndex(1);
        subtitle1.setTimeStr("00:00:01,000 --> 00:00:02,000");
        subtitle1.setEnglishSubtitle("Hello, world!");

        DualSubtitleEntity subtitle2 = new DualSubtitleEntity();
        subtitle2.setSubIndex(2);
        subtitle2.setTimeStr("00:00:03,000 --> 00:00:04,000");
        subtitle2.setEnglishSubtitle("This is a test.");

        DualSubtitleEntity subtitle3 = new DualSubtitleEntity();
        subtitle3.setSubIndex(3);
        subtitle3.setTimeStr("00:00:05,000 --> 00:00:06,000");
        subtitle3.setEnglishSubtitle("How are you?");

        subtitles.add(subtitle1);
        subtitles.add(subtitle2);
        subtitles.add(subtitle3);

        List<DualSubtitleEntity> translatedSubtitles = SubtitleTranslator
                .translateSubtitles(subtitles);

        for (DualSubtitleEntity subtitle : translatedSubtitles) {
            System.out.println(subtitle);
        }
    }
}
