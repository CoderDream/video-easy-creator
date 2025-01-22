package com.coderdream.util.sentence;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.coderdream.entity.ChapterInfoEntity;
import com.coderdream.entity.DialogDualSentenceEntity;
import com.coderdream.entity.DialogRawEntity;
import com.coderdream.entity.DialogSingleEntity;
import com.coderdream.entity.DialogSingleSentenceEntity;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonToBeanListConverter1 {

  public static void main(String[] args) {

    String filename = "D:\\0000\\EnBook001\\900\\900V1_ch01.txt_逐字稿V1.txt";
    File file = new File(filename);

    String jsonString = FileUtil.readString(file, StandardCharsets.UTF_8);

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

    Integer[] rangeIndexes = new Integer[]{0, 12, 16, 18, 12, 20, 8};
    String chapterInfoFileName = "D:\\0000\\EnBook001\\900\\chapter_info_v00.txt";
    String chapterInfoTag = "S101";
    assert conversationItems != null;
//    convertConversationItemListToBean(conversationItems, rangeIndexes);
    List<String> strings = convertConversationItemListToBeanWithChapterInfo(
      conversationItems,
      rangeIndexes, chapterInfoFileName, chapterInfoTag);
    //写入文件
    try {
      FileWriter writer = new FileWriter(
        "D:\\0000\\EnBook001\\900\\900V1_single_sentence.txt");
      assert strings != null;
      for (String sentence : strings) {
        writer.write(sentence + "\n");
      }
      writer.close();
    } catch (IOException e) {
//            e.printStackTrace();
      log.info("Error writing to file: {}", e.getMessage());
    }

//    //写入文件
//    try {
//      FileWriter writer = new FileWriter(
//        "D:\\0000\\EnBook001\\900\\900V1_single_sentence.txt");
//      for (String sentence : sentences) {
//        writer.write(sentence + "\n");
//      }
//      writer.close();
//    } catch (IOException e) {
////            e.printStackTrace();
//      System.out.println("Error writing to file: " + e.getMessage());
//    }
  }

  public static List<DialogSingleSentenceEntity> convertConversationItemListToBean(
    List<ConversationItem> conversationItems, Integer[] rangeIndexes) {

    int size = conversationItems.size();
    // rangeIndexes求和
    int sum = 0;
    for (Integer rangeIndex : rangeIndexes) {
      sum += rangeIndex;
    }
    if (size != sum) {
      return null;
    }
    List<DialogSingleSentenceEntity> dialogs = new ArrayList<>();

    List<List<ConversationItem>> conversationItemsList = new ArrayList<>();
    List<ConversationItem> sublist = null;
    int indexBegin = 0;
    for (int i = 1; i < rangeIndexes.length; i++) {
      sublist = conversationItems.subList(indexBegin,
        indexBegin + rangeIndexes[i]);
      conversationItemsList.add(sublist);
      indexBegin += rangeIndexes[i];
    }
    // 遍历conversationItemsList
    DialogSingleSentenceEntity dialog = null;
    ConversationItem conversationItemEn = null;
    ConversationItem conversationItemCn = null;
    for (List<ConversationItem> conversationItemsSubList : conversationItemsList) {

      int subSize = conversationItemsSubList.size();
      int halfSize = 0;
      if (subSize % 2 != 0) {
        return null;
      } else {
        halfSize = subSize / 2;
      }
      for (int i = 0; i < halfSize; i++) {
        dialog = new DialogSingleSentenceEntity();
        conversationItemEn = conversationItemsSubList.get(i);
        conversationItemCn = conversationItemsSubList.get(halfSize + i);
        dialog.setHostEn(conversationItemEn.getSpeaker());
        dialog.setContentEn(conversationItemEn.getSentences());
        dialog.setHostCn(conversationItemCn.getSpeaker());
        dialog.setContentCn(conversationItemCn.getSentences());
        dialogs.add(dialog);
      }
    }

    return dialogs;
  }


  public static List<String> convertConversationItemListToBeanWithChapterInfo(
    List<ConversationItem> conversationItems, Integer[] rangeIndexes,
    String chapterInfoFileName, String chapterInfoTag) {
    List<String> contents = new ArrayList<>();
    List<ChapterInfoEntity> chapterInfoEntities = new ArrayList<>();

    File chapterInfoFile = new File(chapterInfoFileName);
    List<String> chapterInfoLines = FileUtil.readLines(chapterInfoFile,
      "UTF-8");
    ChapterInfoEntity chapterInfoEntity = null;
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
        log.error("NumberFormatException: {}", split[4], e.getMessage());
      }
      if (split[0].startsWith(chapterInfoTag)) {
        chapterInfoEntities.add(chapterInfoEntity);
      }
    }

    int size = conversationItems.size();
    // rangeIndexes求和
    int sum = 0;
    for (Integer rangeIndex : rangeIndexes) {
      sum += rangeIndex;
    }
    if (size != sum) {
      return null;
    }
    List<DialogSingleSentenceEntity> dialogs = new ArrayList<>();

    List<List<ConversationItem>> conversationItemsList = new ArrayList<>();
    List<ConversationItem> sublist = null;
    int indexBegin = 0;
    for (int i = 1; i < rangeIndexes.length; i++) {
      sublist = conversationItems.subList(indexBegin,
        indexBegin + rangeIndexes[i]);
      conversationItemsList.add(sublist);
      indexBegin += rangeIndexes[i];
    }
    // 遍历conversationItemsList
    DialogSingleSentenceEntity dialog = null;
    ConversationItem conversationItemEn = null;
    ConversationItem conversationItemCn = null;
    int chapterInfoEntitiesSize = chapterInfoEntities.size();
    int chapterInfoIndex = 0;
    ChapterInfoEntity chapterInfoEntityTemp = null;
    DialogRawEntity dialogRawEntity = null;
    DialogRawEntity dialogRawEntityContent = null;
    List<DialogRawEntity> dialogRawEntities = new ArrayList<>();
    for (List<ConversationItem> conversationItemsSubList : conversationItemsList) {
      if (chapterInfoIndex < chapterInfoEntitiesSize) {
        chapterInfoEntityTemp = chapterInfoEntities.get(chapterInfoIndex);
        dialogRawEntity = new DialogRawEntity();
        dialogRawEntity.setContentEn(chapterInfoEntityTemp.getSectionEn());
        dialogRawEntity.setContentCn(chapterInfoEntityTemp.getSectionCn());
        dialogRawEntities.add(dialogRawEntity);
      }
      chapterInfoIndex++;

      int subSize = conversationItemsSubList.size();
      int halfSize = 0;
      if (subSize % 2 != 0) {
        return null;
      } else {
        halfSize = subSize / 2;
      }
      for (int i = 0; i < halfSize; i++) {
        dialog = new DialogSingleSentenceEntity();
        conversationItemEn = conversationItemsSubList.get(i);
        conversationItemCn = conversationItemsSubList.get(halfSize + i);
        dialog.setHostEn(conversationItemEn.getSpeaker());
        dialog.setContentEn(conversationItemEn.getSentences());
        dialog.setHostCn(conversationItemCn.getSpeaker());
        dialog.setContentCn(conversationItemCn.getSentences());
        dialogs.add(dialog);

        // 句子的列表大小
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

    return contents;
  }


  public static List<ConversationItem> convertJsonToBeanList(
    String jsonString) {
    if (jsonString == null || jsonString.trim().isEmpty()) {
      return null;
    }
    JSONArray jsonArray = JSONUtil.parseArray(jsonString);
    List<ConversationItem> conversationItems = new ArrayList<>();

    for (int i = 0; i < jsonArray.size(); i++) {
      JSONObject jsonObject = jsonArray.getJSONObject(i);
      ConversationItem conversationItem = new ConversationItem();
      conversationItem.setSpeaker(jsonObject.getStr("speaker"));
      conversationItem.setSentences(
        jsonObject.getBeanList("sentences", String.class));
      conversationItems.add(conversationItem);
    }

    return conversationItems;
  }
}
