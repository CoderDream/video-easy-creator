package com.coderdream.entity;

import java.util.List;
import lombok.Data;

@Data
public class Book002ContentEntity {
  /** 内容序号，如：❶ 、❷等等 */
  private String sceneIndex;
  /** 内容 */
  private SentencePair sentencePair;
  /** 同类表达 */
  private List<SentencePair> sameSentencePairList;
}
