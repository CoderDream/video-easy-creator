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
import java.util.stream.Collectors;

/**
 * 字幕翻译工具类 (顺序执行版本)
 */
@Slf4j
public class SubtitleTranslatorSequential {

    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 1000; // 1 second

//    private static final String TRANSLATE_PROMPT_TEMPLATE = "请将以下英文句子翻译成简洁准确的中文: '%s'";
    private static final String TRANSLATE_PROMPT_TEMPLATE = "请将以下英文句子翻译成简洁准确的中文，直接翻译，不要添加任何解释或上下文，只给我文本的翻译，别添加其他的内容，因为我要做字幕，谢谢: '%s'";
    /**
     * 翻译字幕列表 (顺序执行)
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

        List<DualSubtitleEntity> translatedSubtitles = new ArrayList<>();
        for (DualSubtitleEntity subtitle : subtitleList) {
            translatedSubtitles.add(translateSubtitle(subtitle));
        }

        // 确保返回的字幕列表按照subIndex排序
        List<DualSubtitleEntity> sortedSubtitles = translatedSubtitles.stream()
                .sorted(Comparator.comparingInt(DualSubtitleEntity::getSubIndex))
                .collect(Collectors.toList());

        stopWatch.stop();
        log.info("字幕翻译完成，总耗时：{}",
                DateUtil.formatBetween(stopWatch.getTotalTimeMillis()));

        return sortedSubtitles;
    }

    /**
     * 翻译单个字幕 (顺序执行)
     *
     * @param subtitle 待翻译的字幕实体
     * @return 翻译后的字幕实体
     */
    private static DualSubtitleEntity translateSubtitle(DualSubtitleEntity subtitle) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                String englishSubtitle = subtitle.getEnglishSubtitle();
                // 使用新的提示词模板
                String prompt = String.format(TRANSLATE_PROMPT_TEMPLATE, englishSubtitle);
                String chineseSubtitle = OllamaApiHuTool.generate(prompt);
                if (chineseSubtitle != null) {
                    subtitle.setChineseSubtitle(chineseSubtitle);
                    log.debug("字幕 {} 翻译成功：{}", subtitle.getSubIndex(), chineseSubtitle);
                    return subtitle; // 翻译成功，退出重试
                } else {
                    log.warn("字幕 {} 翻译失败，尝试重试 {}/{}", subtitle.getSubIndex(), attempt, MAX_RETRIES);
                }
            } catch (Exception e) {
                log.error("字幕 {} 翻译异常：{}", subtitle.getSubIndex(), e.getMessage(), e);
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

        List<DualSubtitleEntity> translatedSubtitles = SubtitleTranslatorSequential
                .translateSubtitles(subtitles);

        for (DualSubtitleEntity subtitle : translatedSubtitles) {
            System.out.println(subtitle);
        }
    }
}
