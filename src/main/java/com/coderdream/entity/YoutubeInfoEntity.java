package com.coderdream.entity;

import lombok.Data;

@Data
public class YoutubeInfoEntity {
  /** 视频分类 */
  private String category;
  /** 视频发布日期 */
  private String dateString;
  /**  原始视频ID  */
  private String rawVideoId;
  /**  我的视频ID  */
  private String myVideoId;
  /** 视频标题 */
  private String title;
  /** 顶部标题 (白宮新聞簡報(13)) */
  private String headTitle;
  /** 主标题 (重拳出擊 貿易逆差即將終結)*/
  private String subTitle;
  /**  底部标题  (MS-13恐怖份子被驅逐出境) */
  private String mainTitle;
}
