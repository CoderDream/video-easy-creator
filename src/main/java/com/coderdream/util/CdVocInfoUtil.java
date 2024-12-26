package com.coderdream.util;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.coderdream.entity.MultiLanguageContent;
import com.coderdream.entity.SentencePair;
import com.coderdream.entity.VocInfo;
import com.coderdream.entity.WordDetail;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

@Slf4j
public class CdVocInfoUtil {

  /**
   * 获取单词详情
   *
   * @param vocInfo 单词
   * @return VocInfo
   */
//  public static VocInfo fillVocInfo(VocInfo vocInfo) {
//
//    // 1. 先从本地词典获取单词详情
//    WordDetail wordDetail = CdDictionaryUtil.getWordDetail(vocInfo.getWord());
//
//
//    if (wordDetail != null) {
//      log.info("wordDetail: {}", wordDetail);
//    }
//
//
//    // 如果没有例句，
//    List<SentencePair> list = CdChatgptUtil.getSentenceFromChatgpt(vocInfo.getWord());
//
//    for (SentencePair s : list) {
//      log.info("s: {}", s);
//    }
//
//    return null;
//  }

  // 填充 VocInfo 对象

  /**
   * 填充 VocInfo 对象
   *
   * @param vocInfo 单词信息对象
   */
  public static void fillVocInfo(VocInfo vocInfo) {
    // 1. 先查本地字典
    if (!checkAndFillFromLocal(vocInfo)) {
      System.out.println("本地字典未找到，尝试网络查询...");
      // 2. 查网络词典
      if (!checkAndFillFromInternet(vocInfo)) {
        System.out.println("网络词典未找到，尝试 ChatGPT 查询...");
        // 3. 查 ChatGPT 补充
        checkAndFillFromChatGPT(vocInfo);
      }
    }

    // 4. 翻译英文单词
    if (StrUtil.isEmpty(vocInfo.getWordCn())) {
      vocInfo.setWordCn(
        CdChatgptUtil.getTranslate(vocInfo.getWord()));
    }

    // 5. 翻译英文单词释义
    vocInfo.setWordExplainCn(
      CdChatgptUtil.getTranslate(vocInfo.getWordExplainEn()));

    // 6. 填充例句（如果没有）
    if (isEmpty(vocInfo.getSampleSentenceEn()) || isEmpty(
      vocInfo.getSampleSentenceCn())) {
      fillExampleSentences(vocInfo);
    }
  }

  /**
   * 填充 VocInfo 对象
   */
  public static void genVocInfoCnFile(String folderPath, String fileName) {
    // TODO
    DictUtil.processVoc(folderPath, fileName);
  }

  // 判断 VocInfo 是否有空字段
  public static boolean isEmpty(VocInfo vocInfo) {
    return StrUtil.isEmpty(vocInfo.getWordCn())
      || StrUtil.isEmpty(vocInfo.getWordExplainCn())
      || StrUtil.isEmpty(vocInfo.getSampleSentenceEn())
      || StrUtil.isEmpty(vocInfo.getSampleSentenceCn());
  }

  // 1. 本地字典查询
  private static boolean checkAndFillFromLocal(VocInfo vocInfo) {
    // 获取单词的详细信息
    WordDetail wordDetail = CdDictionaryUtil.getWordDetail(vocInfo.getWord());

    // 如果wordDetail或者definitionList为空，直接返回
    if (wordDetail == null || wordDetail.getDefinitionList() == null) {
      return false;
    }

    // 使用Stream合并所有definitionList的contentSimple字段
    String wordCn = wordDetail.getDefinitionList().stream()
      .map(MultiLanguageContent::getContentSimple)
      .collect(Collectors.joining("; "));

    // 设置WordCn字段
    vocInfo.setWordCn(wordCn);

    // 获取sentenceList的第一个句子，并设置相应的示例句子
    Optional.ofNullable(wordDetail.getSentenceList())
      .filter(list -> !list.isEmpty())
      .map(list -> list.get(0))
      .ifPresent(sentence -> {
        vocInfo.setSampleSentenceCn(sentence.getContentSimple());
        vocInfo.setSampleSentenceEn(sentence.getContentEnglish());
      });

    return true;
  }

  // 2. 网络词典查询
  private static boolean checkAndFillFromInternet(VocInfo vocInfo) {
    // 直接从网络词典查询并更新vocInfo
    VocInfo vocInfoFromQuery = DictUtil.queryWordFromYoudao(vocInfo.getWord());

    // 如果vocInfoFromQuery不为null，则逐个字段进行拷贝
    if (vocInfoFromQuery != null) {
      // 只拷贝非空字段
      if (StrUtil.isNotEmpty(vocInfoFromQuery.getWordCn())) {
        vocInfo.setWordCn(vocInfoFromQuery.getWordCn());
      }
      if (StrUtil.isNotEmpty(vocInfoFromQuery.getWordExplainCn())) {
        vocInfo.setWordExplainCn(vocInfoFromQuery.getWordExplainCn());
      }
      if (StrUtil.isNotEmpty(vocInfoFromQuery.getSampleSentenceCn())) {
        vocInfo.setSampleSentenceCn(vocInfoFromQuery.getSampleSentenceCn());
      }
      if (StrUtil.isNotEmpty(vocInfoFromQuery.getSampleSentenceEn())) {
        vocInfo.setSampleSentenceEn(vocInfoFromQuery.getSampleSentenceEn());
      }
      return true;
    }

    // 如果没有查询到数据，返回false
    return false;
  }

  // 3. ChatGPT 查询
  private static void checkAndFillFromChatGPT(VocInfo vocInfo) {
    // 使用 ChatGPT API 补充字段
    // 4. 翻译英文单词释义
    // 4. 翻译英文单词释义
    String translate = CdChatgptUtil.getTranslate(vocInfo.getWord());
    log.info("{} translate: {}", vocInfo.getWord(), translate);
    vocInfo.setWordCn(translate);
  }

  // 4. 填充示例句子（如果没有）
  private static void fillExampleSentences(VocInfo vocInfo) {
    // 通过Chatgpt查找
    // 如果vocInfo的EnglishSentence为空，则通过ChatGPT查找示例句子，尝试3次
    if (isEmpty(vocInfo.getSampleSentenceEn()) || isEmpty(
      vocInfo.getSampleSentenceCn())) {
      for (int i = 0; i < 3; i++) {
        if (!isEmpty(vocInfo.getSampleSentenceEn()) && !isEmpty(
          vocInfo.getSampleSentenceCn())) {
          log.info("00通过ChatGPT查找示例句子成功！在尝试第{}次后找到示例句子...",
            i + 1);
          break;
        }
        // 通过ChatGPT查找示例句子，并更新到vocInfo中
        log.info("11尝试通过ChatGPT查找示例句子...尝试第{}次查找示例句子...",
          i + 1);
        List<SentencePair> list = CdChatgptUtil.getSentenceFromChatgpt(
          vocInfo.getWord());
        if (CollectionUtils.isNotEmpty(list)) {
          VocInfo finalVocInfoResult = vocInfo;
          list.stream().findFirst().ifPresent(sentence -> {
            finalVocInfoResult.setSampleSentenceEn(
              sentence.getEnglishSentence());
            finalVocInfoResult.setSampleSentenceCn(
              sentence.getChineseSentence());
          });
        }
        if (!isEmpty(vocInfo.getSampleSentenceEn()) && !isEmpty(
          vocInfo.getSampleSentenceCn())) {
          log.info("22通过ChatGPT查找示例句子成功！在尝试第{}次后找到示例句子...",
            i + 1);
          break;
        }
      }
    }
  }

  // 判断字符串是否为空
  private static boolean isEmpty(String str) {
    return str == null || str.trim().isEmpty();
  }

}
