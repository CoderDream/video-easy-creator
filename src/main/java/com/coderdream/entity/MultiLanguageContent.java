package com.coderdream.entity;

import lombok.Data;

@Data
public class MultiLanguageContent {

  /**
   * 英文例句
   */
  private String contentEnglish;
  /**
   * 简体中文例句
   */
  private String contentSimple;
  /**
   * 繁体中文例句
   */
  private String contentTraditional;
}
