package com.coderdream.entity;

import java.util.List;
import lombok.Data;

@Data
public class WordDetail {

  /**
   * 单词
   */
  private String word;

  /**
   * 词性
   */
  private List<String> partOfSpeechList;

  /**
   * 音标
   */
  private WordPronunciation wordPronunciation;

  /**
   * 解释
   */
  private List<MultiLanguageContent> definitionList;
  /**
   * 句子
   */
  private List<MultiLanguageContent> sentenceList;
}
