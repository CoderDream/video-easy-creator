package com.coderdream.util.youtube;

import com.coderdream.util.cd.CdConstants;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class YouTubeApiUtil6 {

  private static final String APPLICATION_NAME = "YouTube API Example";
  private static final String API_KEY = CdConstants.YOUTUBE_API_KEY;  // 替换为你的 API 密钥

  private NetHttpTransport NET_HTTP_TRANSPORT = new NetHttpTransport();
  private JacksonFactory JACKSON_FACTORY = new JacksonFactory();
  private YouTube youtubeService;

  public YouTubeApiUtil6() throws GeneralSecurityException, IOException {
    youtubeService = new YouTube.Builder(NET_HTTP_TRANSPORT, JACKSON_FACTORY,
      new HttpRequestInitializer() {
        public void initialize(HttpRequest request) throws IOException {
        }
      }).setApplicationName(APPLICATION_NAME).build();
  }

  public void enableProxy() {
    String proxyHost = "127.0.0.1";
    String proxyPort = "7890";

    System.setProperty("http.proxyHost", proxyHost);
    System.setProperty("http.proxyPort", proxyPort);
    // 对https也开启代理
    System.setProperty("https.proxyHost", proxyHost);
    System.setProperty("https.proxyPort", proxyPort);
  }

  /**
   * 通过用户名获取频道的 ID
   *
   * @param username YouTube 频道的自定义用户名
   * @return 频道的 ID
   */
  public String getChannelIdByUsername(String username) throws IOException {
    enableProxy();
    YouTube.Search.List request = youtubeService.search().list("snippet");
    request.setQ(username);  // 查询用户名（作为关键词）
    request.setType("channel");  // 设置查询类型为频道
    request.setKey(API_KEY);  // 设置 API 密钥

    // 执行请求
    SearchListResponse response = request.execute();
    List<SearchResult> results = response.getItems();
    if (results != null && !results.isEmpty()) {
      // 获取第一个搜索结果并返回其频道ID
      return results.get(0).getId().getChannelId();
    }
    return null;  // 如果没有找到该频道
  }

  /**
   * 通过频道 ID 获取频道的详细信息
   *
   * @param channelId 频道的 ID
   * @return 频道的详细信息
   */
  public Channel getChannelDetails(String channelId) throws IOException {
    YouTube.Channels.List request = youtubeService.channels()
      .list("snippet,statistics,contentDetails")  // 请求基本信息、统计信息等
      .setId(channelId)  // 设置频道 ID
      .setKey(API_KEY);  // 设置 API 密钥

    ChannelListResponse response = request.execute();
    List<Channel> channels = response.getItems();
    if (channels != null && !channels.isEmpty()) {
      // 返回找到的频道的详细信息
      return channels.get(0);
    }
    return null;  // 如果没有找到该频道
  }

  /**
   * 打印频道的详细信息
   *
   * @param channel 频道的详细信息对象
   */
  public void printChannelDetails(Channel channel) {
    if (channel != null) {
      System.out.println("频道名称: " + channel.getSnippet().getTitle());
      System.out.println("频道描述: " + channel.getSnippet().getDescription()
        .replace("\n", " "));
      System.out.println(
        "频道创建时间: " + channel.getSnippet().getPublishedAt());
      System.out.println(
        "订阅者数: " + channel.getStatistics().getSubscriberCount());
      System.out.println("观看次数: " + channel.getStatistics().getViewCount());
      System.out.println(
        "视频个数: " + channel.getStatistics().getVideoCount());
      System.out.println("频道所属国家: " + channel.getSnippet().getCountry());
    } else {
      System.out.println("没有找到该频道！");
    }
  }

  public static void main(String[] args) {
    try {
      YouTubeApiUtil6 youTubeApiUtil = new YouTubeApiUtil6();
      List<String> usernames = List.of("GoogleDevelopers", "wangzhian",
        "lingdujieshuo", "Tankman2020", "SeattleLaohu", "cnliziqi", "mkbhd",
        "onehour_English", "EnglishEasyPractice");

      for (String username : usernames) {
        // 通过用户名获取频道 ID
        String channelId = youTubeApiUtil.getChannelIdByUsername(username);
        if (channelId != null) {
          System.out.println("频道 ID: " + channelId);

          // 通过频道 ID 获取频道详细信息
          Channel channel = youTubeApiUtil.getChannelDetails(channelId);

          // 打印频道详细信息
          youTubeApiUtil.printChannelDetails(channel);
          System.out.println("--------------------------------------------");
        } else {
          System.out.println("没有找到该频道！");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
