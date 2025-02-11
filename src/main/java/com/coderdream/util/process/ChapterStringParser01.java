package com.coderdream.util.process;

import com.coderdream.entity.Book002ChapterStringEntity;
import com.coderdream.entity.Book002SceneStringEntity;
import java.io.BufferedReader;
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
public class ChapterStringParser01 {

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
            currentScene.setContentStringList(currentContent);
            sceneEntities.add(currentScene);
          }
          currentScene = new Book002SceneStringEntity();
          currentScene.setSceneTitle(line);
          currentContent = new ArrayList<>();
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
          //  将当前行内容添加到当前块的列表中（完整保留）
          List<String> blockContent = new ArrayList<>();
          blockContent.add(line);
          currentContent.add(blockContent);
          blockCount++;

        } else {
          // 普通内容行，追加到当前块（完整保留）
          if (!currentContent.isEmpty()) {
            List<String> lastBlock = currentContent.get(
              currentContent.size() - 1);
            lastBlock.set(0, lastBlock.get(0) + " " + line);  // 空格连接
          } else {
            log.warn("发现没有块开始标记的内容行: {}", line);
          }
        }
      }
      // 处理最后一个 Scene
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


  public static void main(String[] args) {
    String filePath = "D:\\0000\\EnBook002\\Chapter008\\Chapter008_temp.txt";
    Book002ChapterStringEntity chapterEntity = ChapterStringParser01.parseChapterFile(
      filePath);

    if (chapterEntity != null) {
      // 打印解析结果 (仅为示例，实际应用中可能不需要全部打印)
      System.out.println("Chapter: " + chapterEntity.getChapterStr());
      for (Book002SceneStringEntity scene : chapterEntity.getSceneStringEntityList()) {
        System.out.println("\tScene: " + scene.getSceneTitle());
        int i = 0;
        for (List<String> block : scene.getContentStringList()) {
          System.out.println("\t\tBlock " + (++i) + ": " + block);
        }
      }
    }
  }
}
