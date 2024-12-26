//package com.coderdream.service;
//
//import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
//import com.google.api.client.json.jackson2.JacksonFactory;
////import com.google.api.services.youtube.YouTube;
////import com.google.api.services.youtube.model.ChannelListResponse;
////import com.google.api.services.youtube.model.Channel;
//import com.google.api.services.youtube.YouTube;
//import com.google.api.services.youtube.model.ChannelListResponse;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.util.List;
//
//@Service
//public class YouTubeService {
//
//    @Value("${google.api.key}")
//    private String apiKey;
//
//    private YouTube youtube;
//
//    public YouTubeService() throws IOException {
//        this.youtube = new YouTube.Builder(
//                GoogleNetHttpTransport.newTrustedTransport(),
//                JacksonFactory.getDefaultInstance(),
//                request -> {}
//        )
//        .setApplicationName("YouTube API Spring Boot")
//        .build();
//    }
//
//    // 获取某个账号的详情
//    public Channel getChannelDetails(String channelId) throws IOException {
//        YouTube.Channels.List request = youtube.channels()
//                .list("snippet,contentDetails,statistics")
//                .setId(channelId)
//                .setKey(apiKey);
//
//        ChannelListResponse response = request.execute();
//        List<Channel> channels = response.getItems();
//
//        if (channels != null && !channels.isEmpty()) {
//            return channels.get(0); // 返回第一个频道的信息
//        }
//
//        return null;
//    }
//}
