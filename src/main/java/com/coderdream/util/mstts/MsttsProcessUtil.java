//package com.coderdream.util.mstts;
//
//import cn.hutool.core.collection.ListUtil;
//import cn.hutool.core.io.FileUtil;
//import com.coderdream.util.cd.CdConstants;
//import com.coderdream.util.cd.CdFileUtil;
//import com.coderdream.util.proxy.OperatingSystem;
//import com.coderdream.util.sentence.StanfordSentenceSplitter;
//import java.io.File;
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//public class MsttsProcessUtil {
//
//  // 1. 创建单一、共享的 AudioGenerationService 实例 (保持不变)
//  // 这个实例必须是静态的，才能被下面的静态方法访问
//  private static final AudioGenerationService audioService = new AudioGenerationService();
//
//  // 用于确保 Shutdown Hook 只注册一次的标志 (可选但推荐)
//  private static volatile boolean shutdownHookRegistered = false;
////
////    public static void main(String[] args) {
////        // --- 配置 ---
////        String bookName = "EnBook010";
////        String subFolder = "Chapter001";
////        String lang = CdConstants.LANG_EN;
////        int groupSize = 50; // 或其他你希望的分组大小, 例如 50 行一组
////
////        // --- 调用单一静态方法启动整个流程 ---
////        startAudioGenerationProcess(bookName, subFolder, lang, groupSize);
////
////        // 主线程完成任务提交配置，后台线程将处理音频生成
////        log.info("Main thread finished initiating the process. Background threads will continue processing.");
////        // main 方法现在可以结束了，后台线程和 Shutdown Hook 会接管
////    }
//
//  /**
//   * 启动整个音频生成流程：处理指定书籍章节的文本，分组成多个任务提交到线程池，并注册关闭钩子。
//   *
//   * @param bookName  书籍名称，用于构建路径
//   * @param subFolder 章节或子目录名称
//   * @param lang      语言代码
//   * @param groupSize 每个音频文件包含的文本行数
//   */
//  public static void startAudioGenerationProcess(String bookName,
//    String subFolder, String lang, int groupSize) {
//    // --- 构建路径 ---
//    String folderPath = OperatingSystem.getFolderPath(bookName);
//    String inputFilePath =
//      folderPath + File.separator + subFolder + File.separator + subFolder
//        + ".txt";
//    String baseAudioFileName = CdFileUtil.changeExtension(inputFilePath, "wav");
//
//    log.info("Starting audio generation process for file: {}", inputFilePath);
//    log.info("Base output file template: {}", baseAudioFileName);
//    log.info("Language: {}, Group Size: {}", lang, groupSize);
//
//    // --- 读取和分组 ---
//    String content = FileUtil.readString(inputFilePath, StandardCharsets.UTF_8);
//    content = content.replace("(", " ");
//    content = content.replace(")", " ");
//
//    List<String> textList = StanfordSentenceSplitter.splitSentences(content);
//
//    if (textList == null || textList.isEmpty()) {
//      log.warn("Input file is empty or could not be read: {}", inputFilePath);
//      return; // 如果文件有问题，直接返回，不继续执行
//    }
//    List<List<String>> textListGroup = ListUtil.partition(textList, groupSize);
//    log.info(
//      "Total {} lines read, divided into {} groups (max {} lines per group).",
//      textList.size(), textListGroup.size(), groupSize);
//
//    // --- 循环提交任务 ---
//    int groupIndex = 1;
//    for (List<String> textGroup : textListGroup) {
//      String indexedAudioFileName = CdFileUtil.addPostfixToFileName(
//        baseAudioFileName,
//        "_" + String.format("%03d", groupIndex));
//      log.debug("Submitting task for group {}, output file: {}", groupIndex,
//        indexedAudioFileName);
//      // 直接使用静态的 audioService 实例提交任务
//      audioService.submitAudioGenerationTask(textGroup, indexedAudioFileName,
//        lang);
//      groupIndex++;
//    }
//    log.info("All {} tasks have been submitted to the thread pool.",
//      textListGroup.size());
//
//    // --- 注册关闭钩子 (只注册一次) ---
//    // 使用 Double-Checked Locking (DCL) 或简单的标志确保只注册一次
//    if (!shutdownHookRegistered) {
//      synchronized (MsttsProcessUtil.class) { // 同步确保线程安全
//        if (!shutdownHookRegistered) {
//          Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            log.info(
//              "JVM Shutdown Hook activated: Initiating shutdown of Audio Generation Service...");
//            audioService.shutdown(); // 关闭共享的 service
//            log.info("Audio Generation Service shutdown complete.");
//          }, "AudioServiceShutdownHook")); // 给钩子线程命名
//          shutdownHookRegistered = true; // 标记已注册
//          log.info("Shutdown hook registered successfully.");
//        }
//      }
//    } else {
//      log.debug("Shutdown hook already registered.");
//    }
//  }
//
//  // 原来的 processBookChapter 和 registerShutdownHook 方法现在被合并到了 startAudioGenerationProcess 中，可以移除。
//}
