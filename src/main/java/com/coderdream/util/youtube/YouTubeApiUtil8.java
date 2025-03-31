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
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j // 增加日志注解
public class YouTubeApiUtil8 {

  private static final String APPLICATION_NAME = "YouTube API Example";
  private static final String API_KEY = CdConstants.YOUTUBE_API_KEY; // 替换为你的 API 密钥

  private NetHttpTransport NET_HTTP_TRANSPORT = new NetHttpTransport();
  private JacksonFactory JACKSON_FACTORY = new JacksonFactory();
  private YouTube youtubeService;
  private BufferedWriter writer;

  /**
   * 初始化 YouTube 服务和日志文件。
   *
   * @throws GeneralSecurityException 如果安全设置失败
   * @throws IOException 如果文件操作失败
   */
  public YouTubeApiUtil8() throws GeneralSecurityException, IOException {
    youtubeService = new YouTube.Builder(NET_HTTP_TRANSPORT, JACKSON_FACTORY,
      new HttpRequestInitializer() {
        public void initialize(HttpRequest request) throws IOException {
        }
      }).setApplicationName(APPLICATION_NAME).build();

    writer = new BufferedWriter(new FileWriter("youtube_log.txt", true)); // 追加方式写入日志
    log.info("日志文件已打开: youtube_log.txt");
  }

  /**
   * 启用 HTTP 和 HTTPS 代理。
   */
  public void enableProxy() {
    String proxyHost = "127.0.0.1";
    String proxyPort = "7890";

    System.setProperty("http.proxyHost", proxyHost);
    System.setProperty("http.proxyPort", proxyPort);
    System.setProperty("https.proxyHost", proxyHost);
    System.setProperty("https.proxyPort", proxyPort);
    log.info("代理已启用: {}:{}", proxyHost, proxyPort);
  }

  /**
   * 根据用户名获取频道 ID。
   *
   * @param username YouTube 频道的自定义用户名
   * @return 频道的 ID，如果未找到则返回 null
   * @throws IOException 如果请求执行失败
   */
  public String getChannelIdByUsername(String username) throws IOException {
    enableProxy();
    YouTube.Search.List request = youtubeService.search().list("snippet");
    request.setQ(username);
    request.setType("channel");
    request.setKey(API_KEY);

    SearchListResponse response = request.execute();
    List<SearchResult> results = response.getItems();
    if (results != null && !results.isEmpty()) {
      String channelId = results.get(0).getId().getChannelId();
      log.info("获取到频道ID: {}，用户名: {}", channelId, username);
      return channelId;
    }
    log.warn("未找到用户名为 {} 的频道", username);
    return null;
  }

  /**
   * 根据频道 ID 获取频道详细信息。
   *
   * @param channelId 频道的唯一标识符
   * @return Channel 频道的详细信息对象，如果未找到则返回 null
   * @throws IOException 如果请求执行失败
   */
  public Channel getChannelDetails(String channelId) throws IOException {
    YouTube.Channels.List request = youtubeService.channels()
      .list("snippet,statistics,contentDetails")
      .setId(channelId)
      .setKey(API_KEY);

    ChannelListResponse response = request.execute();
    List<Channel> channels = response.getItems();
    if (channels != null && !channels.isEmpty()) {
      log.info("成功获取频道详细信息，频道ID: {}", channelId);
      return channels.get(0);
    }
    log.warn("未找到频道ID: {} 的详细信息", channelId);
    return null;
  }

  /**
   * 打印并记录频道的详细信息。
   *
   * @param channel 包含频道详细信息的对象
   * @throws IOException 如果写入日志文件失败
   */
  public void printChannelDetails(Channel channel) throws IOException {
    if (channel != null) {
      String channelName = channel.getSnippet().getTitle();
      String subscriberCount = channel.getStatistics().getSubscriberCount().toString();
      String videoCount = channel.getStatistics().getVideoCount().toString();
      String viewCount = channel.getStatistics().getViewCount().toString();
      String currentTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

      String logEntry = String.format("频道名称: %s, 订阅者数: %s, 视频个数: %s, 观看次数: %s, 当前时间: %s\n",
        channelName, subscriberCount, videoCount, viewCount, currentTime);
      writer.write(logEntry);
      writer.flush();
      log.info(logEntry);
    } else {
      log.warn("频道详细信息为空！");
    }
  }

  /**
   * 关闭日志文件。
   *
   * @throws IOException 如果文件关闭失败
   */
  public void closeWriter() throws IOException {
    if (writer != null) {
      writer.close();
      log.info("日志文件已关闭。");
    }
  }

  public static void main(String[] args) {
    try {
      YouTubeApiUtil8 youTubeApiUtil = new YouTubeApiUtil8();
      List<String> usernames = List.of("GoogleDevelopers", "wangzhian",
        "lingdujieshuo", "Tankman2020", "SeattleLaohu", "cnliziqi", "mkbhd",
        "onehour_English", "EnglishEasyPractice");

      for (String username : usernames) {
        String channelId = youTubeApiUtil.getChannelIdByUsername(username);
        if (channelId != null) {
          Channel channel = youTubeApiUtil.getChannelDetails(channelId);
          youTubeApiUtil.printChannelDetails(channel);
        } else {
          log.warn("未找到频道: {}", username);
        }
      }
      youTubeApiUtil.closeWriter();
    } catch (Exception e) {
      log.error("发生异常: ", e);
    }
  }
}
