package com.coderdream.entity;

import lombok.Data;

@Data
public class DialogEntity {

  /**
   * 主持人
   */
  private String host;
  /**
   * 英文脚本
   */
  private String contentEn;
  /**
   * 中文翻译
   */
  private String contentCn;
}
