//package com.coderdream.util.youtube;
//
//import com.google.api.services.youtube.YouTube;
//import com.google.api.services.youtube.model.ChannelListResponse;
//import com.google.api.services.youtube.model.Channel;
//import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
//import com.google.api.client.json.JsonFactory;
//import org.apache.http.impl.client.HttpClients;
//
//import java.io.IOException;
//import java.security.GeneralSecurityException;
//import java.util.List;
//
//public class YouTubeApiUtil3 {
//
//    private static final String APPLICATION_NAME = "YouTube API Example";
//    private static final JsonFactory JSON_FACTORY = com.google.api.client.json.JsonFactory.getDefaultInstance();
//    private static final String API_KEY = "YOUR_API_KEY";  // 替换为你的 API 密钥
//
//    private YouTube youtubeService;
//
//    public YouTubeApiUtil3() throws GeneralSecurityException, IOException {
//        youtubeService = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, HttpClients::createDefault)
//                .setApplicationName(APPLICATION_NAME)
//                .build();
//    }
//
//    // 获取频道的详细信息
//    public Channel getChannelDetails(String channelId) throws IOException {
//        YouTube.Channels.List request = youtubeService.channels()
//                .list("snippet,contentDetails,statistics")  // 请求频道的基本信息、内容详情和统计信息
//                .setId(channelId)  // 设置频道ID
//                .setKey(API_KEY);  // 设置API密钥
//
//        ChannelListResponse response = request.execute();
//        List<Channel> channels = response.getItems();
//        if (channels != null && !channels.isEmpty()) {
//            return channels.get(0);  // 返回找到的第一个频道
//        }
//        return null;  // 如果没有找到频道，返回null
//    }
//
//    // 获取频道的创建时间等信息
//    public void printChannelDetails(Channel channel) {
//        if (channel != null) {
//            System.out.println("频道名称: " + channel.getSnippet().getTitle());
//            System.out.println("频道描述: " + channel.getSnippet().getDescription());
//            System.out.println("频道创建时间: " + channel.getSnippet().getPublishedAt());
//            System.out.println("订阅者数: " + channel.getStatistics().getSubscriberCount());
//            System.out.println("观看次数: " + channel.getStatistics().getViewCount());
//        } else {
//            System.out.println("没有找到频道信息！");
//        }
//    }
//}
