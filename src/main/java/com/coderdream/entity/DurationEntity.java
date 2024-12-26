package com.coderdream.entity;

import lombok.Data;

@Data
public class DurationEntity {

  /**
   * 索引字符串，用于检索
   */
  private String indexStr;
  /**
   * 语言
   */
  private String lang;
  /**
   * 时长
   */
  private double duration;
}
