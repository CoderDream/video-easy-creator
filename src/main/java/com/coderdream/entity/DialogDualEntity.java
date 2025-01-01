package com.coderdream.entity;

import lombok.Data;

@Data
public class DialogDualEntity {

  /**
   * 主持人A英文名
   */
  private String hostAEn;
  /**
   * 主持人A中文名
   */
  private String hostACn;
  /**
   * 脚本A英文
   */
  private String contentAEn;
  /**
   * 脚本A中文
   */
  private String contentACn;

  /**
   * 主持人B英文名
   */
  private String hostBEn;
  /**
   * 主持人B中文名
   */
  private String hostBCn;
  /**
   * 脚本B英文
   */
  private String contentBEn;
  /**
   * 脚本B英文
   */
  private String contentBCn;
}
