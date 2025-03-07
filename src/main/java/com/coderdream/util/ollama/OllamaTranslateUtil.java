package com.coderdream.util.ollama;

import com.coderdream.util.chatgpt.SubtitleConverter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * OllamaTranslateUtil 类： 使用 Ollama API 翻译文本，包含多线程处理。
 */
@Slf4j
public class OllamaTranslateUtil {

  private static final String TRANSLATE_PROMPT = "你是一个精通中文和英文的翻译大师。如果我给你英文就翻译成中文，给你中文就翻译成英文。";

  /**
   * 辅助类，用于记录字幕的序号、原文和翻译状态
   */
  @Data
  public static class FutureSubtitle {

    private int index;
    private String original;
    private String translated;
    private boolean isTranslated = false;

    public FutureSubtitle(int index, String original) {
      this.index = index;
      this.original = original;
    }
  }

  /**
   * 翻译输入的英文文本
   *
   * @param text 待翻译的英文字幕
   * @return 翻译后的文本
   */
  public static String translateText(String text) {
    log.info("待翻译的英文字幕: \n{}", text);
    List<String> subtitleList = SubtitleConverter.convertToSubtitleList(text);
    return translateSubtitles(subtitleList);
  }

  /**
   * 翻译字幕列表
   *
   * @param subtitleList 待翻译的字幕列表
   * @return 翻译后的字幕列表
   */
  public static String translateSubtitles(List<String> subtitleList) {
    int numThreads = Math.min(subtitleList.size(), 10); // 限制最大线程数，避免资源耗尽
    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    List<Future<String>> futures = new ArrayList<>();
    List<String> translatedSubtitleList = new ArrayList<>(
      subtitleList.size()); // 预分配大小
    List<FutureSubtitle> futureSubtitles = new ArrayList<>();

    for (int i = 0; i < subtitleList.size(); i++) {
      translatedSubtitleList.add(null); // 初始化占位符
      futureSubtitles.add(new FutureSubtitle(i, subtitleList.get(i)));
    }

    for (FutureSubtitle futureSubtitle : futureSubtitles) {
      Callable<String> task = () -> {
        try {
          String translatedText = translateTextRaw(futureSubtitle.original);
          futureSubtitle.setTranslated(translatedText);
//          futureSubtitle.setTranslated(true);
          return translatedText;
        } catch (Exception e) {
          log.error("翻译字幕 '{}' 时发生错误：", futureSubtitle.original, e);
          return "翻译失败: " + futureSubtitle.original;
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

  /**
   * 翻译单个文本
   *
   * @param text 待翻译的文本
   * @return 翻译后的文本
   */
  public static String translateTextRaw(String text) {
    OllamaApiUtil.OllamaRequest ollamaRequest = new OllamaApiUtil.OllamaRequest();
    ollamaRequest.setPrompt(TRANSLATE_PROMPT);
    ollamaRequest.setMessage(text);
    OllamaApiUtil.OllamaResponse ollamaResponse = OllamaApiUtil.generate(
      ollamaRequest);
    log.debug("翻译后的文本: {}", ollamaResponse.getResponse());
    return ollamaResponse.getResponse();
  }
}
