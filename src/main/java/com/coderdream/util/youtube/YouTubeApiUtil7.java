package com.coderdream.util.youtube;

import com.coderdream.util.CdConstants;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class YouTubeApiUtil7 {

  private static final String APPLICATION_NAME = "YouTube API Example";
  private static final String API_KEY = CdConstants.GOOGLE_API_KEY;
  private NetHttpTransport NET_HTTP_TRANSPORT = new NetHttpTransport();
  private JacksonFactory JACKSON_FACTORY = new JacksonFactory();
  private YouTube youtubeService;
  private BufferedWriter writer;

  public YouTubeApiUtil7() throws GeneralSecurityException, IOException {
    youtubeService = new YouTube.Builder(NET_HTTP_TRANSPORT, JACKSON_FACTORY,
      new HttpRequestInitializer() {
        public void initialize(HttpRequest request) throws IOException {
        }
      }).setApplicationName(APPLICATION_NAME).build();

    // 初始化输出文件
    String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    writer = new BufferedWriter(new FileWriter("youtube_" + timestamp + ".txt"));
  }

  public void enableProxy() {
    String proxyHost = "127.0.0.1";
    String proxyPort = "7890";
    System.setProperty("http.proxyHost", proxyHost);
    System.setProperty("http.proxyPort", proxyPort);
    System.setProperty("https.proxyHost", proxyHost);
    System.setProperty("https.proxyPort", proxyPort);
  }

  public String getChannelIdByUsername(String username) throws IOException {
    enableProxy();
    YouTube.Search.List request = youtubeService.search().list("snippet");
    request.setQ(username);
    request.setType("channel");
    request.setKey(API_KEY);
    SearchListResponse response = request.execute();
    List<SearchResult> results = response.getItems();
    if (results != null && !results.isEmpty()) {
      return results.get(0).getId().getChannelId();
    }
    return null;
  }

  public Channel getChannelDetails(String channelId) throws IOException {
    YouTube.Channels.List request = youtubeService.channels()
      .list("snippet,statistics,contentDetails")
      .setId(channelId)
      .setKey(API_KEY);
    ChannelListResponse response = request.execute();
    List<Channel> channels = response.getItems();
    if (channels != null && !channels.isEmpty()) {
      return channels.get(0);
    }
    return null;
  }

  public void printChannelDetails(Channel channel) throws IOException {
    if (channel != null) {
      String details = "频道名称: " + channel.getSnippet().getTitle() + "\n" +
        "频道描述: " + channel.getSnippet().getDescription().replace("\n", " ") + "\n" +
        "频道创建时间: " + channel.getSnippet().getPublishedAt() + "\n" +
        "订阅者数: " + channel.getStatistics().getSubscriberCount() + "\n" +
        "观看次数: " + channel.getStatistics().getViewCount() + "\n" +
        "视频个数: " + channel.getStatistics().getVideoCount() + "\n" +
        "频道所属国家: " + channel.getSnippet().getCountry() + "\n";

      System.out.println(details);
      writer.write(details);
      writer.write("--------------------------------------------\n");
    } else {
      System.out.println("没有找到该频道！");
      writer.write("没有找到该频道！\n");
    }
  }

  public void closeWriter() throws IOException {
    if (writer != null) {
      writer.close();
    }
  }

  public static void main(String[] args) {
    try {
      YouTubeApiUtil7 youTubeApiUtil = new YouTubeApiUtil7();
      List<String> usernames = List.of("GoogleDevelopers", "wangzhian",
        "lingdujieshuo", "Tankman2020", "SeattleLaohu", "cnliziqi", "mkbhd",
        "onehour_English", "EnglishEasyPractice");

      for (String username : usernames) {
        String channelId = youTubeApiUtil.getChannelIdByUsername(username);
        if (channelId != null) {
          System.out.println("频道 ID: " + channelId);
          youTubeApiUtil.writer.write("频道 ID: " + channelId + "\n");
          Channel channel = youTubeApiUtil.getChannelDetails(channelId);
          youTubeApiUtil.printChannelDetails(channel);
        } else {
          System.out.println("没有找到该频道！");
          youTubeApiUtil.writer.write("没有找到该频道！\n");
        }
      }
      youTubeApiUtil.closeWriter();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}