package com.coderdream.entity;

import lombok.Data;

@Data
public class DialogSingleEntity {

  /**
   * 主持人英文名
   */
  private String hostEn;
  /**
   * 主持人中文名
   */
  private String hostCn;
  /**
   * 脚本英文
   */
  private String contentEn;
  /**
   * 脚本中文
   */
  private String contentCn;

}
