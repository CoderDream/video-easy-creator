package com.coderdream.util.youtube;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.JsonContent;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;

import java.util.List;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class YouTubeApiUtil3 {

    // 使用 JacksonFactory 作为 JSON 工厂
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String APPLICATION_NAME = "YouTube API Example";
    private static final String API_KEY = "YOUR_API_KEY";  // 替换为你的 API 密钥

    private NetHttpTransport NET_HTTP_TRANSPORT = new NetHttpTransport();

    private JacksonFactory JACKSON_FACTORY = new JacksonFactory();

    private YouTube youtubeService;

    public YouTubeApiUtil3() throws GeneralSecurityException, IOException {
        youtubeService = new YouTube.Builder(NET_HTTP_TRANSPORT, JACKSON_FACTORY,
          new HttpRequestInitializer() {
              public void initialize(HttpRequest request) throws IOException {}
          }).setApplicationName("video-easy-creator").build();
    }

    // 获取频道的详细信息
    public Channel getChannelDetails(String channelId) throws IOException {
        YouTube.Channels.List request = youtubeService.channels()
                .list("snippet,contentDetails,statistics")  // 请求频道的基本信息、内容详情和统计信息
                .setId(channelId)  // 设置频道ID
                .setKey(API_KEY);  // 设置API密钥

        ChannelListResponse response = request.execute();
        List<Channel> channels = response.getItems();
        if (channels != null && !channels.isEmpty()) {
            return channels.get(0);  // 返回找到的第一个频道
        }
        return null;  // 如果没有找到频道，返回null
    }

    // 获取频道的创建时间等信息
    public void printChannelDetails(Channel channel) {
        if (channel != null) {
            System.out.println("频道名称: " + channel.getSnippet().getTitle());
            System.out.println("频道描述: " + channel.getSnippet().getDescription());
            System.out.println("频道创建时间: " + channel.getSnippet().getPublishedAt());
            System.out.println("订阅者数: " + channel.getStatistics().getSubscriberCount());
            System.out.println("观看次数: " + channel.getStatistics().getViewCount());
        } else {
            System.out.println("没有找到频道信息！");
        }
    }

    public static void main(String[] args) {
        try {
            YouTubeApiUtil3 youTubeApiUtil = new YouTubeApiUtil3();
            String channelId = "UC_x5XG1OV2P6uZZ5bYfP17A";  // 你想查询的 YouTube 频道 ID
            Channel channel = youTubeApiUtil.getChannelDetails(channelId);
            youTubeApiUtil.printChannelDetails(channel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
