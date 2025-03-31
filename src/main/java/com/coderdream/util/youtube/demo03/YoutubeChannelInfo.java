package com.coderdream.util.youtube.demo03;

import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.youtube.YouTubeApiUtil;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

/**
 * 寻找YouTube频道信息，打开页面，按F12，找到meta标签，找到og:url，找到content，复制即可。
 * <meta property="og:url" content="https://www.youtube.com/channel/UCKVqsG3eQsbUiJirkeJnsyQ">
 */
@Slf4j
public class YoutubeChannelInfo {

  private static final String API_KEY = CdConstants.YOUTUBE_API_KEY;
  private static final String CHANNEL_USERNAME = null; // 设置为 null 或 ""
  private static final String CHANNEL_ID = "UCKVqsG3eQsbUiJirkeJnsyQ"; // 替换为正确的频道 ID https://www.youtube.com/channel/UCKVqsG3eQsbUiJirkeJnsyQ
  private static final String APPLICATION_NAME = "YoutubeChannelInfo";

  public static void main(String[] args) {
    try {
      NetHttpTransport transport = new NetHttpTransport();
      JacksonFactory jsonFactory = new JacksonFactory();
      YouTube youtubeService = YouTubeApiUtil.createYoutubeService(transport,
        jsonFactory);

      if (youtubeService == null) {
        log.error("创建 YouTube 服务失败。请检查您的 API 配置。");
        return;
      }

      String channelId = CHANNEL_ID;
      log.info("使用提供的频道 ID: {}", channelId);

      if (channelId != null) {
        Channel channel = getChannelDetails(youtubeService, channelId, API_KEY);

        if (channel != null) {
          log.info("频道名称: {}", channel.getSnippet().getTitle());
          log.info("频道描述: {}", channel.getSnippet().getDescription());
          log.info("订阅者人数: {}",
            channel.getStatistics().getSubscriberCount());
          log.info("视频总观看次数: {}",
            channel.getStatistics().getViewCount());
          log.info("频道视频数量: {}", channel.getStatistics().getVideoCount());
          log.info("创建时间: {}", channel.getSnippet().getPublishedAt());

          String thumbnailUrl = channel.getSnippet().getThumbnails()
            .getDefault().getUrl();
          log.info("频道缩略图: {}", thumbnailUrl);
        } else {
          log.warn("无法获取频道详情.");
        }
      } else {
        log.error("无法获取频道ID，程序终止.");
      }

    } catch (Exception e) {
      log.error("发生错误: {}", e.getMessage(), e);
    }
  }

  // 通过用户名获取频道ID (不推荐, 优先使用频道ID)
  private static String getChannelIdByUsername(YouTube youtubeService,
    String username, String apiKey) throws IOException {
    try {
      YouTube.Channels.List request = youtubeService.channels().list("id");
      request.setForUsername(username);
      request.setKey(apiKey);
      ChannelListResponse response = request.execute();

      List<Channel> channels = response.getItems();
      if (channels != null && !channels.isEmpty()) {
        return channels.get(0).getId();
      } else {
        log.warn("未找到与用户名 {} 匹配的频道.", username);
        return null;
      }
    } catch (GoogleJsonResponseException e) {
      log.error("API 请求失败 (getUsername): {}", e.getMessage());
      log.error("详细信息: {}", e.getDetails().getMessage());
      log.error("状态码: {}", e.getDetails().getCode());
      return null;
    } catch (IOException e) {
      log.error("I/O 异常 (getUsername): {}", e.getMessage(), e);
      return null;
    }
  }

  // 获取频道详情
  private static Channel getChannelDetails(YouTube youtubeService,
    String channelId, String apiKey) throws IOException {
    try {
      YouTube.Channels.List request = youtubeService.channels()
        .list("statistics,snippet");
      request.setId(channelId);
      request.setKey(apiKey);

      ChannelListResponse response = request.execute();
      List<Channel> channels = response.getItems();
      if (channels != null && !channels.isEmpty()) {
        return channels.get(0);
      } else {
        log.warn("未找到 ID 为 {} 的频道.", channelId);
        return null;
      }
    } catch (GoogleJsonResponseException e) {
      log.error("API 请求失败 (getDetails): {}", e.getMessage());
      log.error("详细信息: {}", e.getDetails().getMessage());
      log.error("状态码: {}", e.getDetails().getCode());
      return null;
    } catch (IOException e) {
      log.error("I/O 异常 (getDetails): {}", e.getMessage(), e);
      return null;
    }
  }
}
