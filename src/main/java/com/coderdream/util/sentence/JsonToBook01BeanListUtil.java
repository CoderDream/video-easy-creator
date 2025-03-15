package com.coderdream.util.sentence;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.coderdream.entity.ChapterInfoEntity;
import com.coderdream.entity.DialogRawEntity;
import com.coderdream.entity.DialogSingleSentenceEntity;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * JSON 数据转换为 Bean 列表的工具类
 */
@Slf4j
public class JsonToBook01BeanListUtil {

  /**
   * 将 JSON 字符串转换为 ConversationItem 列表
   *
   * @param jsonString JSON 字符串
   * @return ConversationItem 列表，如果转换失败或 JSON 字符串为空，则返回 null
   */
  public static List<ConversationItem> convertJsonToBeanList(
    String jsonString) {
    LocalDateTime startTime = LocalDateTime.now();
    log.info("开始执行 convertJsonToBeanList 方法, 参数: jsonString = {}",
      jsonString);
    if (jsonString == null || jsonString.trim().isEmpty()) {
      log.warn("JSON 字符串为空，无法进行转换。");
      return null;
    }
    List<ConversationItem> conversationItems = new ArrayList<>();
    try {
      JSONArray jsonArray = JSONUtil.parseArray(jsonString);
      for (int i = 0; i < jsonArray.size(); i++) {
        JSONObject jsonObject = jsonArray.getJSONObject(i);
        ConversationItem conversationItem = new ConversationItem();
        conversationItem.setSpeaker(jsonObject.getStr("speaker"));
        conversationItem.setSentences(
          jsonObject.getBeanList("sentences", String.class));
        conversationItems.add(conversationItem);
      }
      log.info("JSON 转换 ConversationItem 列表成功。");
    } catch (Exception e) {
      log.error("JSON 转换 ConversationItem 列表失败：{}", e.getMessage(), e);
      return null;
    } finally {
      logMethodExecutionTime(startTime, "convertJsonToBeanList");
    }

    return conversationItems;
  }

  /**
   * 将 ConversationItem 列表转换为 DialogSingleSentenceEntity 列表
   *
   * @param conversationItems ConversationItem 列表
   * @param rangeIndexes      范围索引数组
   * @return DialogSingleSentenceEntity 列表，如果转换失败或参数不合法，则返回 null
   */
  public static List<DialogSingleSentenceEntity> convertConversationItemListToBean(
    List<ConversationItem> conversationItems, Integer[] rangeIndexes) {
    LocalDateTime startTime = LocalDateTime.now();
    log.info(
      "开始执行 convertConversationItemListToBean 方法, 参数: conversationItems.size = {}, rangeIndexes = {}",
      conversationItems == null ? "null" : conversationItems.size(),
      rangeIndexes);

    if (conversationItems == null || rangeIndexes == null
      || conversationItems.isEmpty() || rangeIndexes.length == 0) {
      log.warn("ConversationItem 列表为空或 rangeIndexes 为空，无法进行转换。");
      return null;
    }
    int size = conversationItems.size();
    int sum = 0; // rangeIndexes 求和
    for (Integer rangeIndex : rangeIndexes) {
      sum += rangeIndex;
    }
    if (size != sum) {
      log.warn(
        "ConversationItem 列表大小与 rangeIndexes 总和不匹配，无法进行转换。conversationItems.size = {}, sum = {}",
        size, sum);
      return null;
    }

    List<DialogSingleSentenceEntity> dialogs = new ArrayList<>();
    List<List<ConversationItem>> conversationItemsList = new ArrayList<>();
    int indexBegin = 0;
    try {
      for (int i = 1; i < rangeIndexes.length; i++) {
        List<ConversationItem> sublist = conversationItems.subList(indexBegin,
          indexBegin + rangeIndexes[i]);
        conversationItemsList.add(sublist);
        indexBegin += rangeIndexes[i];
      }

      for (List<ConversationItem> conversationItemsSubList : conversationItemsList) {
        int subSize = conversationItemsSubList.size();
        int halfSize;
        if (subSize % 2 != 0) {
          log.warn("子列表大小为奇数，无法进行转换。subSize = {}", subSize);
          return null;
        } else {
          halfSize = subSize / 2;
        }
        for (int i = 0; i < halfSize; i++) {
          DialogSingleSentenceEntity dialog = new DialogSingleSentenceEntity();
          ConversationItem conversationItemEn = conversationItemsSubList.get(i);
          ConversationItem conversationItemCn = conversationItemsSubList.get(
            halfSize + i);
          dialog.setHostEn(conversationItemEn.getSpeaker());
          dialog.setContentEn(conversationItemEn.getSentences());
          dialog.setHostCn(conversationItemCn.getSpeaker());
          dialog.setContentCn(conversationItemCn.getSentences());
          dialogs.add(dialog);
        }
      }
      log.info(
        "ConversationItem 列表转换为 DialogSingleSentenceEntity 列表成功");
    } catch (Exception e) {
      log.error(
        "ConversationItem 列表转换为 DialogSingleSentenceEntity 列表失败: {}",
        e.getMessage(), e);
      return null;
    } finally {
      logMethodExecutionTime(startTime, "convertConversationItemListToBean");
    }
    return dialogs;
  }

  /**
   * 将 ConversationItem 列表转换为包含章节信息的字符串列表
   *
   * @param conversationItems   ConversationItem 列表
   * @param rangeIndexes        范围索引数组
   * @param chapterInfoFileName 章节信息文件名
   * @param chapterInfoTag      章节信息标签
   * @return 包含章节信息的字符串列表，如果转换失败或参数不合法，则返回 null
   */
  public static List<String> convertConversationItemListToBeanWithChapterInfo(
    List<ConversationItem> conversationItems, Integer[] rangeIndexes,
    String chapterInfoFileName, String chapterInfoTag) {
    LocalDateTime startTime = LocalDateTime.now();
    log.info(
      "开始执行 convertConversationItemListToBeanWithChapterInfo 方法，参数：conversationItems.size = {}, rangeIndexes = {}, chapterInfoFileName = {}, chapterInfoTag = {}",
      conversationItems == null ? "null" : conversationItems.size(),
      rangeIndexes, chapterInfoFileName, chapterInfoTag);

    if (conversationItems == null || rangeIndexes == null
      || chapterInfoFileName == null || chapterInfoTag == null
      || conversationItems.isEmpty() || rangeIndexes.length == 0
      || chapterInfoFileName.trim().isEmpty() || chapterInfoTag.trim()
      .isEmpty()) {
      log.warn("参数不合法，无法进行转换。");
      return null;
    }

    List<String> contents = new ArrayList<>();
    List<ChapterInfoEntity> chapterInfoEntities = new ArrayList<>();
    File chapterInfoFile = new File(chapterInfoFileName);
    try {
      List<String> chapterInfoLines = FileUtil.readLines(chapterInfoFile,
        "UTF-8");
      ChapterInfoEntity chapterInfoEntity;
      for (String chapterInfoLine : chapterInfoLines) {
        chapterInfoEntity = new ChapterInfoEntity();
        String[] split = chapterInfoLine.split("\t");
        chapterInfoEntity.setIndexStr(split[0]);
        chapterInfoEntity.setSectionStr(split[1]);
        chapterInfoEntity.setSectionCn(split[2]);
        chapterInfoEntity.setSectionEn(split[3]);
        try {
          int sectionCount = Integer.parseInt(split[4]);
          chapterInfoEntity.setSectionCount(sectionCount);
        } catch (NumberFormatException e) {
          log.error("章节信息中的 sectionCount 格式错误：{}，信息：{}", split[4],
            e.getMessage(), e);
        }
        if (split[0].startsWith(chapterInfoTag)) {
          chapterInfoEntities.add(chapterInfoEntity);
        }
      }
      log.info("读取章节信息成功。");
    } catch (Exception e) {
      log.error("读取章节信息失败: {}", e.getMessage(), e);
      return null;
    }

    int size = conversationItems.size();
    int sum = 0;// rangeIndexes求和
    for (Integer rangeIndex : rangeIndexes) {
      sum += rangeIndex;
    }
    if (size != sum) {
      log.warn(
        "ConversationItem 列表大小与 rangeIndexes 总和不匹配，无法进行转换。conversationItems.size = {}, sum = {}",
        size, sum);
      return null;
    }

    List<DialogSingleSentenceEntity> dialogs = new ArrayList<>();
    List<List<ConversationItem>> conversationItemsList = new ArrayList<>();
    int indexBegin = 0;

    try {
      for (int i = 0; i < rangeIndexes.length - 1; i++) {
        List<ConversationItem> sublist = conversationItems.subList(indexBegin,
          indexBegin + rangeIndexes[i]);
        conversationItemsList.add(sublist);
        indexBegin += rangeIndexes[i];
      }

      int chapterInfoEntitiesSize = chapterInfoEntities.size();
      int chapterInfoIndex = 0;
      DialogRawEntity dialogRawEntityContent;
      List<DialogRawEntity> dialogRawEntities = new ArrayList<>();
      for (List<ConversationItem> conversationItemsSubList : conversationItemsList) {
        if (chapterInfoIndex < chapterInfoEntitiesSize) {
          ChapterInfoEntity chapterInfoEntityTemp = chapterInfoEntities.get(
            chapterInfoIndex);
          DialogRawEntity dialogRawEntity = new DialogRawEntity();
          dialogRawEntity.setContentEn(chapterInfoEntityTemp.getSectionEn());
          dialogRawEntity.setContentCn(chapterInfoEntityTemp.getSectionCn());
          dialogRawEntities.add(dialogRawEntity);
        }
        chapterInfoIndex++;

        int subSize = conversationItemsSubList.size();
        int halfSize;
        if (subSize % 2 != 0) {
          log.warn("子列表大小为奇数，无法进行转换。subSize = {}", subSize);
          return null;
        } else {
          halfSize = subSize / 2;
        }

        for (int i = 0; i < halfSize; i++) {
          DialogSingleSentenceEntity dialog = new DialogSingleSentenceEntity();
          ConversationItem conversationItemEn = conversationItemsSubList.get(i);
          ConversationItem conversationItemCn = conversationItemsSubList.get(
            halfSize + i);
          dialog.setHostEn(conversationItemEn.getSpeaker());
          dialog.setContentEn(conversationItemEn.getSentences());
          dialog.setHostCn(conversationItemCn.getSpeaker());
          dialog.setContentCn(conversationItemCn.getSentences());
          dialogs.add(dialog);

          int sentencesEnSize = conversationItemEn.getSentences().size();
          int sentencesCnSize = conversationItemCn.getSentences().size();
          int sentencesSize = 0;
          if (sentencesEnSize == sentencesCnSize) {
            sentencesSize = sentencesEnSize;
          }

          for (int j = 0; j < sentencesSize; j++) {
            dialogRawEntityContent = new DialogRawEntity();
            dialogRawEntityContent.setContentEn(
              conversationItemEn.getSentences().get(j));
            dialogRawEntityContent.setContentCn(
              conversationItemCn.getSentences().get(j));
            dialogRawEntities.add(dialogRawEntityContent);
          }
        }
      }

      for (DialogRawEntity dialogRawEntityTemp : dialogRawEntities) {
        contents.add(dialogRawEntityTemp.getContentEn());
        contents.add(dialogRawEntityTemp.getContentCn());
      }
      log.info("ConversationItem 列表转换为包含章节信息的字符串列表成功。");
    } catch (Exception e) {
      log.error("ConversationItem 列表转换为包含章节信息的字符串列表失败: {}",
        e.getMessage(), e);
      return null;
    } finally {
      logMethodExecutionTime(startTime,
        "convertConversationItemListToBeanWithChapterInfo");
    }

    return contents;
  }

  /**
   * 将 ConversationItem 列表转换为包含章节信息的字符串列表
   *
   * @param stringList          String 列表
   * @param rangeIndexes        范围索引数组
   * @param chapterInfoFileName 章节信息文件名
   * @param chapterInfoTag      章节信息标签
   * @return 包含章节信息的字符串列表，如果转换失败或参数不合法，则返回 null
   */
  public static List<String> convertStringListToBeanWithChapterInfo(
    List<String> stringList, Integer[] rangeIndexes,
    String chapterInfoFileName, String chapterInfoTag) {
    LocalDateTime startTime = LocalDateTime.now();
    log.info(
      "开始执行 convertConversationItemListToBeanWithChapterInfo 方法，参数：conversationItems.size = {}, rangeIndexes = {}, chapterInfoFileName = {}, chapterInfoTag = {}",
      stringList == null ? "null" : stringList.size(),
      rangeIndexes, chapterInfoFileName, chapterInfoTag);

    if (stringList == null || rangeIndexes == null
      || chapterInfoFileName == null || chapterInfoTag == null
      || stringList.isEmpty() || rangeIndexes.length == 0
      || chapterInfoFileName.trim().isEmpty() || chapterInfoTag.trim()
      .isEmpty()) {
      log.warn("参数不合法，无法进行转换。");
      return null;
    }

    List<String> contents = new ArrayList<>();
    List<ChapterInfoEntity> chapterInfoEntities = new ArrayList<>();
    File chapterInfoFile = new File(chapterInfoFileName);
    try {
      List<String> chapterInfoLines = FileUtil.readLines(chapterInfoFile,
        "UTF-8");
      ChapterInfoEntity chapterInfoEntity;
      for (String chapterInfoLine : chapterInfoLines) {
        chapterInfoEntity = new ChapterInfoEntity();
        String[] split = chapterInfoLine.split("\t");
        chapterInfoEntity.setIndexStr(split[0]);
        chapterInfoEntity.setSectionStr(split[1]);
        chapterInfoEntity.setSectionCn(split[2]);
        chapterInfoEntity.setSectionEn(split[3]);
        try {
          int sectionCount = Integer.parseInt(split[4]);
          chapterInfoEntity.setSectionCount(sectionCount);
        } catch (NumberFormatException e) {
          log.error("章节信息中的 sectionCount 格式错误：{}，信息：{}", split[4],
            e.getMessage(), e);
        }
        if (split[0].startsWith(chapterInfoTag)) {
          chapterInfoEntities.add(chapterInfoEntity);
        }
      }
      log.info("读取章节信息成功。");
    } catch (Exception e) {
      log.error("读取章节信息失败: {}", e.getMessage(), e);
      return null;
    }

    int size = stringList.size();
    int sum = 0;// rangeIndexes求和
    for (Integer rangeIndex : rangeIndexes) {
      sum += rangeIndex;
    }
    if (size != sum) {
      log.warn(
        "ConversationItem 列表大小与 rangeIndexes 总和不匹配，无法进行转换。conversationItems.size = {}, sum = {}",
        size, sum);
      return null;
    }

    List<DialogSingleSentenceEntity> dialogs = new ArrayList<>();
    List<List<String>> conversationItemsList = new ArrayList<>();
    int indexBegin = 0;

    try {
      for (int i = 1; i < rangeIndexes.length; i++) {
        List<String> sublist = stringList.subList(indexBegin,
          indexBegin + rangeIndexes[i]);
        conversationItemsList.add(sublist);
        indexBegin += rangeIndexes[i];
      }

      int chapterInfoEntitiesSize = chapterInfoEntities.size();
      int chapterInfoIndex = 0;
      DialogRawEntity dialogRawEntityContent;
      List<DialogRawEntity> dialogRawEntities = new ArrayList<>();
      for (List<String> conversationItemsSubList : conversationItemsList) {
        if (chapterInfoIndex < chapterInfoEntitiesSize) {
          ChapterInfoEntity chapterInfoEntityTemp = chapterInfoEntities.get(
            chapterInfoIndex);
          DialogRawEntity dialogRawEntity = new DialogRawEntity();
          dialogRawEntity.setContentEn(chapterInfoEntityTemp.getSectionEn());
          dialogRawEntity.setContentCn(chapterInfoEntityTemp.getSectionCn());
          dialogRawEntities.add(dialogRawEntity);
        }
        chapterInfoIndex++;

        int subSize = conversationItemsSubList.size();
        int halfSize;
        if (subSize % 2 != 0) {
          log.warn("子列表大小为奇数，无法进行转换。subSize = {}", subSize);
          return null;
        } else {
          halfSize = subSize / 2;
        }

        for (int i = 0; i < halfSize; i++) {
//          DialogSingleSentenceEntity dialog = new DialogSingleSentenceEntity();
//          String conversationItemEn = conversationItemsSubList.get(i);
//          String conversationItemCn = conversationItemsSubList.get(
//            halfSize + i);
//          dialog.setHostEn(conversationItemEn);
//          dialog.setContentEn(conversationItemEn);
//          dialog.setHostCn(conversationItemCn.getSpeaker());
//          dialog.setContentCn(conversationItemCn.getSentences());
//          dialogs.add(dialog);
//
//          int sentencesEnSize = conversationItemEn.getSentences().size();
//          int sentencesCnSize = conversationItemCn.getSentences().size();
//          int sentencesSize = 0;
//          if (sentencesEnSize == sentencesCnSize) {
//            sentencesSize = sentencesEnSize;
//          }
//
//          for (int j = 0; j < sentencesSize; j++) {
//            dialogRawEntityContent = new DialogRawEntity();
//            dialogRawEntityContent.setContentEn(
//              conversationItemEn.getSentences().get(j));
//            dialogRawEntityContent.setContentCn(
//              conversationItemCn.getSentences().get(j));
//            dialogRawEntities.add(dialogRawEntityContent);
//          }
        }
      }

      for (DialogRawEntity dialogRawEntityTemp : dialogRawEntities) {
        contents.add(dialogRawEntityTemp.getContentEn());
        contents.add(dialogRawEntityTemp.getContentCn());
      }
      log.info("ConversationItem 列表转换为包含章节信息的字符串列表成功。");
    } catch (Exception e) {
      log.error("ConversationItem 列表转换为包含章节信息的字符串列表失败: {}",
        e.getMessage(), e);
      return null;
    } finally {
      logMethodExecutionTime(startTime,
        "convertConversationItemListToBeanWithChapterInfo");
    }

    return contents;
  }


  /**
   * 记录方法执行耗时
   *
   * @param startTime  方法开始执行的时间
   * @param methodName 方法名
   */
  private static void logMethodExecutionTime(LocalDateTime startTime,
    String methodName) {
    LocalDateTime endTime = LocalDateTime.now();
    Duration duration = Duration.between(startTime, endTime);
    long seconds = duration.getSeconds();
    long milliseconds = duration.toMillisPart();
    long minutes = seconds / 60;
    seconds = seconds % 60;
    log.info("{} 方法执行耗时: {}分{}秒{}毫秒", methodName, minutes, seconds,
      milliseconds);
  }


  public static void main(String[] args) {
    String rawFilename = "D:\\0000\\EnBook001\\900\\900V1_ch02.txt";
    File file1 = new File(rawFilename);
    String strings = FileUtil.readString(file1, StandardCharsets.UTF_8);
    Integer[] rangeIndexes = ParagraphCounter.countLinesPerParagraph(strings);

    String chapterInfoTag = "S101";
    String filename = "D:\\0000\\EnBook001\\900\\900V1_ch01.txt_逐字稿V_407.txt";
    filename = "D:\\0000\\EnBook001\\900\\900V1_ch02.txt_逐字稿V_409.txt";
    File file = new File(filename);

    String jsonString = FileUtil.readString(file, StandardCharsets.UTF_8);

    processBackup(rawFilename, jsonString, rangeIndexes, chapterInfoTag);

//    String filename = "D:\\0000\\EnBook001\\900\\900V1_ch01_v3.txt_逐字稿V2.txt";
//    File file = new File(filename);
//
//    List<String> stringList = CdFileUtil.readLines(file, StandardCharsets.UTF_8);
//    process(stringList);
  }

  public static void process(List<String> stringList) {
//    String filename = "D:\\0000\\EnBook001\\900\\900V1_ch01.txt_逐字稿V1.txt";
//    File file = new File(filename);
//
//    String jsonString = CdFileUtil.readString(file, StandardCharsets.UTF_8);

    Integer[] rangeIndexes = new Integer[]{0, 12, 16, 18, 12, 20, 8};
    String chapterInfoFileName = "D:\\0000\\EnBook001\\900\\chapter_info_v00.txt";
    String chapterInfoTag = "S101";

    List<String> strings = convertStringListToBeanWithChapterInfo(
      stringList,
      rangeIndexes, chapterInfoFileName, chapterInfoTag);
    //写入文件
    String outputFilename = "D:\\0000\\EnBook001\\900\\900V1_single_sentence.txt";
    try (FileWriter writer = new FileWriter(outputFilename)) {
      assert strings != null;
      for (String sentence : strings) {
        writer.write(sentence + "\n");
      }
    } catch (IOException e) {
      log.error("写入文件失败：{}，文件名：{}", e.getMessage(), outputFilename, e);
    }
  }

  public static void processBackup(String rawFilename, String jsonString,
    Integer[] rangeIndexes,
    String chapterInfoTag) {
//    String filename = "D:\\0000\\EnBook001\\900\\900V1_ch01.txt_逐字稿V1.txt";
//    File file = new File(filename);
//
//    String jsonString = CdFileUtil.readString(file, StandardCharsets.UTF_8);

    List<ConversationItem> conversationItems = convertJsonToBeanList(
      jsonString);

    List<String> sentences = new ArrayList<>();
    if (conversationItems != null) {
      conversationItems.forEach(item -> {
        System.out.println("Speaker: " + item.getSpeaker());
        System.out.println("Sentences: " + item.getSentences());
        sentences.addAll(item.getSentences());
      });
    } else {
      System.out.println("Failed to convert JSON to List<ConversationItem>.");
    }

    String chapterInfoFileName = "D:\\0000\\EnBook001\\900\\chapter_info_v00.txt";

//    Integer[] rangeIndexes = new Integer[]{0, 12, 16, 18, 12, 20, 8};
//    String chapterInfoTag = "S101";
    assert conversationItems != null;

    List<String> strings = convertConversationItemListToBeanWithChapterInfo(
      conversationItems,
      rangeIndexes, chapterInfoFileName, chapterInfoTag);
    //写入文件
    String outputFilename =
      rawFilename.replace(".txt", ".") + "_single_sentence.txt";
    try (FileWriter writer = new FileWriter(outputFilename)) {
      assert strings != null;
      for (String sentence : strings) {
        writer.write(sentence + "\n");
      }
    } catch (IOException e) {
      log.error("写入文件失败：{}，文件名：{}", e.getMessage(), outputFilename, e);
    }
  }

}
