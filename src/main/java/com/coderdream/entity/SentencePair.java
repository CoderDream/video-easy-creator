package com.coderdream.entity;

import java.util.Objects;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SentencePair that = (SentencePair) o;
    return Objects.equals(englishSentence, that.englishSentence)
      && Objects.equals(chineseSentence, that.chineseSentence);
  }

  @Override
  public int hashCode() {
    return Objects.hash(englishSentence, chineseSentence);
  }
}
