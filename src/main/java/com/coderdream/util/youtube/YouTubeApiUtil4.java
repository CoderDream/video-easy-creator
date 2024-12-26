package com.coderdream.util.youtube;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class YouTubeApiUtil4 {

    private static final String APPLICATION_NAME = "YouTube API Example";
    private static final String API_KEY = "AIzaSyBD8pIk4XFNaXgAZadEwRk_rtgM0eZVoTw";  // 替换为你的 API 密钥
//    private static final JsonFactory JSON_FACTORY = com.google.api.client.json.JsonFactory.getDefaultInstance();

    private NetHttpTransport NET_HTTP_TRANSPORT = new NetHttpTransport();

    private JacksonFactory JACKSON_FACTORY = new JacksonFactory();

    private YouTube youtubeService;

    public YouTubeApiUtil4() throws GeneralSecurityException, IOException {
        youtubeService = new YouTube.Builder(NET_HTTP_TRANSPORT, JACKSON_FACTORY,
          new HttpRequestInitializer() {
              public void initialize(HttpRequest request) throws IOException {}
          }).setApplicationName("video-easy-creator").build();
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
     * 通过自定义的用户名获取频道的 ID
     *
     * @param username YouTube 频道的自定义用户名
     * @return 频道的 ID
     */
    public String getChannelIdByUsername(String username) throws IOException {
        enableProxy();
        YouTube.Channels.List request = youtubeService.channels()
                .list("id")
                .setForUsername(username)  // 设置为频道的用户名
                .setKey(API_KEY);  // 设置 API 密钥

        ChannelListResponse response = request.execute();
        List<Channel> channels = response.getItems();
        if (channels != null && !channels.isEmpty()) {
            // 返回频道 ID
            return channels.get(0).getId();
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
            System.out.println("频道描述: " + channel.getSnippet().getDescription());
            System.out.println("频道创建时间: " + channel.getSnippet().getPublishedAt());
            System.out.println("订阅者数: " + channel.getStatistics().getSubscriberCount());
            System.out.println("观看次数: " + channel.getStatistics().getViewCount());
        } else {
            System.out.println("没有找到该频道！");
        }
    }

    public static void main(String[] args) {
        try {
            YouTubeApiUtil4 youTubeApiUtil = new YouTubeApiUtil4();
//            String username = "GoogleDevelopers";  // YouTube 频道用户名（自定义 URL 中的 @ 后部分）

            List<String> usernames = List.of("GoogleDevelopers", "wangzhian", "lingdujieshuo", "Tankman2020", "SeattleLaohu", "cnliziqi", "mkbhd", "zero.one_English", "EnglishEasyPractice");
            for (String username : usernames) {

                // 通过用户名获取频道 ID
                String channelId = youTubeApiUtil.getChannelIdByUsername(username);
                if (channelId != null) {
                    System.out.println("频道 ID: " + channelId);

                    // 通过频道 ID 获取频道详细信息
                    Channel channel = youTubeApiUtil.getChannelDetails(channelId);

                    // 打印频道详细信息
                    youTubeApiUtil.printChannelDetails(channel);
                } else {
                    System.out.println("没有找到该频道！");
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
