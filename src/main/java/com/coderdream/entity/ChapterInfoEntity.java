package com.coderdream.entity;

import lombok.Data;

@Data
public class ChapterInfoEntity {

  /**
   * 序号字符串；例如：S10100
   */
  private String indexStr;
  /**
   * Section字符串
   */
  private String sectionStr;
  /**
   * 中文
   */
  private String sectionCn;
  /**
   * 英文
   */
  private String sectionEn;
  /**
   * 对话行数（中文英文算一行）
   */
  private Integer sectionCount;
}

