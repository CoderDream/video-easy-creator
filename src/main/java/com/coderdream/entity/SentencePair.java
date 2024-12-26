package com.coderdream.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表示英文句子和中文句子的对象。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SentencePair {

  /**
   * 英文句子
   */
  private String englishSentence;

  /**
   * 中文句子
   */
  private String chineseSentence;

}
