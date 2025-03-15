package com.coderdream.util.sqlite;

import lombok.Data;

/**
 * 内部类：用于封装单词信息 (英文属性)
 */
@Data
public class WordInfo {

  private String headword;
  private String pos;
  private String cefr;
  private String chineseDefinition;
  private String example;
  private String phonetic;
  private String exampleTranslation;
}
