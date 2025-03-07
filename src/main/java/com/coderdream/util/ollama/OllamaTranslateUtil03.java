package com.coderdream.util.ollama;

import com.coderdream.util.chatgpt.SubtitleConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OllamaTranslateUtil03 {

    private static final String TRANSLATE_PROMPT = "你是一个精通中文和英文的翻译大师。如果我给你英文就翻译成中文，给你中文就翻译成英文。";

    public static String translateText(String text) {
        log.info("待翻译的英文字幕: \n{}", text);
        List<String> subtitleList = SubtitleConverter.convertToSubtitleList(text);
        return translateSubtitles(subtitleList);
    }

    public static String translateSubtitles(List<String> subtitleList) {
        int numThreads = Math.min(subtitleList.size(), 10); // 限制最大线程数，避免资源耗尽
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<String>> futures = new ArrayList<>();
        List<String> translatedSubtitleList = new ArrayList<>(subtitleList.size()); // 预分配大小
        for (int i = 0; i < subtitleList.size(); i++) {
            translatedSubtitleList.add(null); // 初始化占位符
        }
        for (int i = 0; i < subtitleList.size(); i++) {
            final int index = i;
            String subtitle = subtitleList.get(i);
            Callable<String> task = () -> {
                try {
                    return translateTextRaw(subtitle);
                } catch (Exception e) {
                    log.error("翻译字幕 '{}' 时发生错误：", subtitle, e);
                    return "翻译失败: " + subtitle;
                }
            };
            futures.add(executor.submit(task));
        }

        // 等待所有任务完成并获取结果
        for (int i = 0; i < futures.size(); i++) {
            try {
                translatedSubtitleList.set(i, futures.get(i).get());
            } catch (InterruptedException | ExecutionException e) {
                log.error("获取翻译结果时发生错误：", e);
                translatedSubtitleList.set(i, "翻译失败: " + subtitleList.get(i));
            }
        }
        executor.shutdown();
        try {
            executor.awaitTermination(60, TimeUnit.SECONDS); // 最长等待时间
        } catch (InterruptedException e) {
            log.error("等待线程池结束时发生中断", e);
        }

        return String.join("\n", translatedSubtitleList);
    }

    public static String translateTextRaw(String text) {
        OllamaApiUtil.OllamaRequest ollamaRequest = new OllamaApiUtil.OllamaRequest();
        ollamaRequest.setPrompt(TRANSLATE_PROMPT);
        ollamaRequest.setMessage(text);
        OllamaApiUtil.OllamaResponse ollamaResponse = OllamaApiUtil.generate(ollamaRequest);
        log.debug("翻译后的文本: {}", ollamaResponse.getResponse());
        return ollamaResponse.getResponse();
    }
}
