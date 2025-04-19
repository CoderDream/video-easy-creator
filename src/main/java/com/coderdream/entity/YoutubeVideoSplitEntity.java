package com.coderdream.entity;

import lombok.Data;

@Data
public class YoutubeVideoSplitEntity {
  /** 视频分类 */
  private String category;
  /** 视频发布日期 */
  private String dateString;
  /**  时间戳  (00:00:34,000 --> 00:36:47,000) */
  private String timeStr;
}
