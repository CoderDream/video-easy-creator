package com.coderdream.entity;

import lombok.Data;

@Data
public class YoutubeInfoEntity {
  /** 视频分类 */
  private String category;
  /** 视频发布日期 */
  private String dateString;
  /**  视频ID  */
  private String videoId;
  /** 视频标题 */
  private String title;
}
