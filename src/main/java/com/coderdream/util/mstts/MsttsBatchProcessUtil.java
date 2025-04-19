package com.coderdream.util.mstts;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.proxy.OperatingSystem;
import com.coderdream.util.sentence.StanfordSentenceSplitter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MsttsBatchProcessUtil {


  /**
   * 启动整个音频生成流程：处理指定书籍章节的文本，分组成多个任务提交到线程池，并注册关闭钩子。
   *
   * @param bookName  书籍名称，用于构建路径
   * @param subFolder 章节或子目录名称
   * @param lang      语言代码
   * @param groupSize 每个音频文件包含的文本行数
   */
  public static void process(String bookName, String subFolder, String lang,
    int groupSize) {
    // --- 构建路径 ---
    String folderPath = OperatingSystem.getFolderPath(bookName);
    String inputFilePath =
      folderPath + File.separator + subFolder + File.separator + subFolder
        + ".txt";
    String audioFilePath =
      folderPath + File.separator + subFolder + File.separator
        + CdConstants.AUDIO_FOLDER + File.separator + lang;
    if (!FileUtil.isDirectory(audioFilePath)) {
      FileUtil.mkdir(audioFilePath);
    }

    String ssmlFilePath =
      folderPath + File.separator + subFolder + File.separator
        + CdConstants.SSML_FOLDER + File.separator + lang;
    if (!FileUtil.isDirectory(ssmlFilePath)) {
      FileUtil.mkdir(ssmlFilePath);
    }

    String pureTextFileName = new File(inputFilePath).getName();
    String baseAudioFileName =
      audioFilePath + File.separator + CdFileUtil.getPureFileNameWithoutExtensionWithPath(pureTextFileName) + ".wav";


    String baseSsmlFileName =
      ssmlFilePath + File.separator + CdFileUtil.getPureFileNameWithoutExtensionWithPath(pureTextFileName) + ".xml";

    log.info("Starting audio generation process for file: {}", inputFilePath);
    log.info("Base output file template: {}", baseAudioFileName);
    log.info("Language: {}, Group Size: {}", lang, groupSize);

    // --- 读取和分组 ---
//        List<String> textList = CdFileUtil.readFileContent(inputFilePath); // TODO
    String content = FileUtil.readString(inputFilePath, StandardCharsets.UTF_8);
    content = content.replace("(", " ");
    content = content.replace(")", " ");

    List<String> textList = StanfordSentenceSplitter.splitSentences(content);

    if (textList.isEmpty()) {
      log.warn("Input file is empty or could not be read: {}", inputFilePath);
      return; // 如果文件有问题，直接返回，不继续执行
    }
    List<List<String>> textListGroup = ListUtil.partition(textList, groupSize);
    log.info(
      "Total {} lines read, divided into {} groups (max {} lines per group).",
      textList.size(), textListGroup.size(), groupSize);

    // --- 循环提交任务 ---
    int groupIndex = 1;
    for (List<String> textGroup : textListGroup) {
      String indexedAudioFileName = CdFileUtil.addPostfixToFileName(
        baseAudioFileName,
        "_" + String.format("%03d", groupIndex));
      String indexedSsmlFileName = CdFileUtil.addPostfixToFileName(
        baseSsmlFileName,
        "_" + String.format("%03d", groupIndex));
      log.debug("Submitting task for group {}, output file: {}", groupIndex,
        indexedAudioFileName);
      // 直接使用静态的 audioService 实例提交任务
      MsttsAudioUtil.genAudioFileWithIndexSsmlFileName(textGroup,
        indexedAudioFileName, lang, indexedSsmlFileName);
      groupIndex++;
    }
    log.info("All {} tasks have been submitted to the thread pool.",
      textListGroup.size());

  }

  // 原来的 processBookChapter 和 registerShutdownHook 方法现在被合并到了 startAudioGenerationProcess 中，可以移除。
}
