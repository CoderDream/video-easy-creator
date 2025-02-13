package com.coderdream.util.process;

import cn.hutool.core.util.StrUtil;
import com.coderdream.entity.Book002ChapterInfoEntity;
import com.coderdream.entity.Book002ChapterStringEntity;
import com.coderdream.entity.Book002ContentEntity;
import com.coderdream.entity.Book002DialogPairEntity;
import com.coderdream.entity.Book002SceneEntity;
import com.coderdream.entity.Book002SceneStringEntity;
import com.coderdream.entity.SentencePair;
import com.coderdream.util.cd.CdStringUtil;
import com.coderdream.util.proxy.OperatingSystem;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

/**
 * 用于解析文本文件并将其转换为 {@link Book002ChapterStringEntity} 对象的工具类。
 */
@Slf4j
public class ChapterStringParser {

  /**
   * 将指定路径的文本文件解析为 {@link Book002ChapterStringEntity} 对象。
   *
   * @param filePath 文件路径
   * @return 解析后的 {@link Book002ChapterStringEntity} 对象，如果发生错误则返回 null
   */
  public static Book002ChapterStringEntity parseChapterFile(String filePath) {
    Instant start = Instant.now();
    Book002ChapterStringEntity chapterEntity = new Book002ChapterStringEntity();
    List<Book002SceneStringEntity> sceneEntities = new ArrayList<>();

    try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath),
      StandardCharsets.UTF_8)) {
      String chapterLine = reader.readLine(); // 读取第一行，即章节标题
      if (chapterLine != null) {
        chapterEntity.setChapterStr(chapterLine.trim());
      } else {
        log.error("文件为空或缺少章节标题: {}", filePath);
        return null;
      }

      String line;
      Book002SceneStringEntity currentScene = null;
      List<List<String>> currentContent = null;
      List<String> currentBlock = null; // 当前块
      int blockCount = 0;
      Pattern sceneTitlePattern = Pattern.compile("Scene \\d+　.+");
      Pattern blockStartPattern = Pattern.compile("[❶-❿]");

      while ((line = reader.readLine()) != null) {
        line = line.trim();
        if (line.isEmpty()) {
          continue; // 跳过空行
        }

        Matcher sceneTitleMatcher = sceneTitlePattern.matcher(line);
        if (sceneTitleMatcher.matches()) {
          // 新的 Scene 开始
          if (currentScene != null) {
            // 将上一个 Scene 添加到列表
            if (currentBlock != null) {
              currentContent.add(currentBlock);
            }
            currentScene.setContentStringList(currentContent);
            sceneEntities.add(currentScene);
          }
          currentScene = new Book002SceneStringEntity();
          currentScene.setSceneTitle(line);
          currentContent = new ArrayList<>();
          currentBlock = null;
          blockCount = 0;
          continue;
        }

        Matcher blockStartMatcher = blockStartPattern.matcher(line);

        if (blockStartMatcher.find()) {
          // 新的块开始
          if (blockCount >= 10) {
            log.warn("Scene中的块数量超过10个，可能有问题。Scene: {}",
              currentScene.getSceneTitle());
          }

          // 如果前一个块不为空，将其添加到 currentContent
          if (currentBlock != null) {
            currentContent.add(currentBlock);
          }
          currentBlock = new ArrayList<>();
          currentBlock.add(line); // 将块起始行添加到新的块
          blockCount++;

        } else {
          // 普通内容行，添加到当前块
          if (currentBlock != null) {
            currentBlock.add(line);
          } else {
            log.warn("发现没有块开始标记的内容行: {}", line);
          }
        }
      }
      // 处理最后一个 Scene 和 块
      if (currentBlock != null) {
        currentContent.add(currentBlock);
      }
      if (currentScene != null) {
        currentScene.setContentStringList(currentContent);
        sceneEntities.add(currentScene);
      }

      chapterEntity.setSceneStringEntityList(sceneEntities);

    } catch (IOException e) {
      log.error("读取文件时发生错误: {}", filePath, e);
      return null;
    }

    Instant end = Instant.now();
    Duration duration = Duration.between(start, end);
    log.info("解析文件 {} 耗时: {} 时 {} 分 {} 秒 {} 毫秒", filePath,
      duration.toHoursPart(), duration.toMinutesPart(),
      duration.toSecondsPart(), duration.toMillisPart());
    return chapterEntity;
  }

  public static Book002ChapterInfoEntity genChapterInfoEntity(String filePath) {
    Book002ChapterInfoEntity chapterInfoEntity = new Book002ChapterInfoEntity();
//        String filePath = "D:\\0000\\EnBook002\\Chapter008\\Chapter008_temp.txt";
    Book002ChapterStringEntity chapterEntity = ChapterStringParser.parseChapterFile(
      filePath);
    int errorCount = 0;
    if (chapterEntity != null) {
      chapterInfoEntity.setChapterStr(chapterEntity.getChapterStr());
      List<Book002SceneEntity> sceneEntityList = new ArrayList<>();
      List<Book002ContentEntity> contentEntityList;
      Book002SceneEntity sceneEntity;
      for (Book002SceneStringEntity scene : chapterEntity.getSceneStringEntityList()) {
        sceneEntity = new Book002SceneEntity();
        sceneEntity.setSceneTitle(scene.getSceneTitle());
//        System.out.println("\tScene: " + scene.getSceneTitle());
        contentEntityList = new ArrayList<>();
        Book002ContentEntity contentEntity;
        // 同类表达列表
        List<SentencePair> sameSentencePairList;
        for (List<String> block : scene.getContentStringList()) {
          contentEntity = new Book002ContentEntity();
          SentencePair sentencePair = new SentencePair(); // TODO
          // 查找字符串中最后一个中文字符或中文标点的索引 (不使用 \p{Punct})
          int lastChineseCharOrPunctuationIndexWithoutPunct = CdStringUtil.findLastChineseCharOrPunctuationIndexWithoutPunct(
            block.get(0));
          String sceneIndex = block.get(0).substring(0, 1);
          contentEntity.setSceneIndex(sceneIndex); // 设置场景序号，如 "1"
          sentencePair.setChineseSentence(block.get(0)
            .substring(1, lastChineseCharOrPunctuationIndexWithoutPunct + 1)
            .trim());
          sentencePair.setEnglishSentence(block.get(0)
            .substring(lastChineseCharOrPunctuationIndexWithoutPunct + 1)
            .trim());

          if (StrUtil.isBlank(sentencePair.getEnglishSentence())
            || StrUtil.isBlank(sentencePair.getChineseSentence())) {
            log.warn("sentencePair 对话文本为空，忽略：{}", sentencePair);
            errorCount++;
          }
          contentEntity.setSentencePair(sentencePair);

          int blockSize = block.size();
          sameSentencePairList = new ArrayList<>();
          SentencePair sameSentencePair;
          for (int j = 1; j < blockSize; j++) {
            String temp = block.get(j);
            temp = temp.replace("同类表达 ", "");
            temp = temp.replace("对话 A:", "");
            temp = temp.replace("B:", "");
            // 查询是否有中文
            int firstIndex = CdStringUtil.findFirstChineseCharIndex(temp);
            sameSentencePair = new SentencePair();
            String chineseSentence = ""; // 没有找到，补充序号句子的中文为中文
            String englishSentence = temp;
            if (firstIndex == -1) {
              chineseSentence = sentencePair.getChineseSentence();
            } else {
              chineseSentence = temp.substring(firstIndex); // 中文在后面
              englishSentence = temp.substring(0, firstIndex); // 英文在前
            }
            sameSentencePair.setChineseSentence(chineseSentence.trim());
            sameSentencePair.setEnglishSentence(englishSentence.trim());

            if (StrUtil.isBlank(sameSentencePair.getEnglishSentence())
              || StrUtil.isBlank(sameSentencePair.getChineseSentence())) {
              log.warn("sameSentencePair 对话文本为空，忽略：{}",
                sameSentencePair);
              errorCount++;
            }

            sameSentencePairList.add(sameSentencePair);
          }
          // 设置同类表达列表
          contentEntity.setSameSentencePairList(sameSentencePairList);
          contentEntityList.add(contentEntity);
        }

        sceneEntity.setContentEntityList(contentEntityList);
        sceneEntityList.add(sceneEntity);
      }

      chapterInfoEntity.setSceneEntityList(sceneEntityList);
    }
    if (errorCount > 0) {
      log.warn("解析文件 {} 时，发现 {} 处对话文本为空。", filePath, errorCount);
      return null;
    }
    return chapterInfoEntity;
  }

  public static void getTextFile() {
    String filePath = "D:\\0000\\EnBook002\\Chapter008\\Chapter008_temp.txt";
    Book002ChapterInfoEntity chapterInfoEntity = ChapterStringParser.genChapterInfoEntity(
      filePath);
//    log.info("chapterInfoEntity: {}", chapterInfoEntity);

    // 打印解析结果 (示例)
    log.info(
      new GsonBuilder().setPrettyPrinting().create().toJson(chapterInfoEntity));
  }

  public static void main(String[] args) {
//    String filePath = "D:\\0000\\EnBook002\\Chapter008\\Chapter008_temp.txt";

    String bookName = "EnBook002";
    String folderPath = OperatingSystem.getFolderPath(bookName);
//    String subFolder = "Chapter004";
//    BeforeGenerateUtil.processBook02(folderPath, subFolder);
//    assertTrue(true);
//    System.out.println("done");
    List<String> subFolders = new ArrayList<>();
    int end = 51;
    for (int i = 9; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("Chapter" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      String sourcePath =
        folderPath + subFolder + File.separator + subFolder + "_temp.txt";

      Book002ChapterStringEntity chapterEntity = ChapterStringParser.parseChapterFile(
        sourcePath);

//      if (chapterEntity != null) {
//        // 打印解析结果 (仅为示例，实际应用中可能不需要全部打印)
//        System.out.println("Chapter: " + chapterEntity.getChapterStr());
//        for (Book002SceneStringEntity scene : chapterEntity.getSceneStringEntityList()) {
//          System.out.println("\tScene: " + scene.getSceneTitle());
//          int i = 0;
//          for (List<String> block : scene.getContentStringList()) {
//            System.out.println("\t\tBlock " + (++i) + ": ");
//            for (String str : block) {
//              System.out.println("\t\t\t" + str);
//            }
//          }
//        }
//      }
    }


//    Book002ChapterStringEntity chapterEntity = ChapterStringParser.parseChapterFile(
//      filePath);
//
//    if (chapterEntity != null) {
//      // 打印解析结果 (仅为示例，实际应用中可能不需要全部打印)
//      System.out.println("Chapter: " + chapterEntity.getChapterStr());
//      for (Book002SceneStringEntity scene : chapterEntity.getSceneStringEntityList()) {
//        System.out.println("\tScene: " + scene.getSceneTitle());
//        int i = 0;
//        for (List<String> block : scene.getContentStringList()) {
//          System.out.println("\t\tBlock " + (++i) + ": ");
//          for (String str : block) {
//            System.out.println("\t\t\t" + str);
//          }
//        }
//      }
//    }
  }
}
