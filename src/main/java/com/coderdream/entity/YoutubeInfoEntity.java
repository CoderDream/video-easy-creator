package com.coderdream.entity;

import lombok.Data;

@Data
public class YoutubeInfoEntity {
  /**  */
  private String category;
  /** 文章日期 */
  private String dateString;
  /**    */
  private String videoId;
  /** 文章标题 */
  private String title;
}
