package com.coderdream.util.youtube;

import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.proxy.OperatingSystem;
import com.google.api.client.http.HttpRequest;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
public class YouTubeApiUtil {

    private static final String APPLICATION_NAME = "YouTube API Example";
    private static final String API_KEY = CdConstants.GOOGLE_API_KEY;

    private static final String PROXY_HOST = "127.0.0.1";
    private static final String PROXY_PORT = "7890";

    /**
     * 静态方法，接收用户名列表并进行处理
     *
     * @param usernames 用户名列表
     */
    public static void processUsernames(List<String> usernames) {
        NetHttpTransport transport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter("youtube_log.txt", true))) {
            log.info("日志文件已打开: youtube_log.txt");
            YouTube youtubeService = createYoutubeService(transport, jsonFactory);

            for (String username : usernames) {
                processUsername(youtubeService, username, writer);
            }
        } catch (IOException e) {
            log.error("处理用户名列表时发生异常", e);
        }
    }


    private static YouTube createYoutubeService(NetHttpTransport transport,
                                                JacksonFactory jsonFactory) {
        // 配置代理
        enableProxy();
        try {
            return new YouTube.Builder(transport, jsonFactory,
                    (HttpRequest request) -> {
                    }).setApplicationName(APPLICATION_NAME).build();
        } catch (Exception e) {
            log.error("初始化 YouTube 服务失败", e);
            return null;
        }
    }

    public static void enableProxy() {
        System.setProperty("http.proxyHost", PROXY_HOST);
        System.setProperty("http.proxyPort", OperatingSystem.getProxyPort() + "");
        System.setProperty("https.proxyHost", PROXY_HOST);
        System.setProperty("https.proxyPort", OperatingSystem.getProxyPort() + "");
        log.info("代理已启用: {}:{}", PROXY_HOST, OperatingSystem.getProxyPort() + "");
    }

    private static String getChannelIdByUsername(YouTube youtubeService,
                                                 String username)
            throws IOException {
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


    private static Channel getChannelDetails(YouTube youtubeService,
                                             String channelId)
            throws IOException {
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

    private static void printChannelDetails(Channel channel,
                                            BufferedWriter writer)
            throws IOException {
        if (channel != null) {
            String channelName = channel.getSnippet().getTitle();
            String subscriberCount = channel.getStatistics().getSubscriberCount()
                    .toString();
            String videoCount = channel.getStatistics().getVideoCount().toString();
            String viewCount = channel.getStatistics().getViewCount().toString();
            String currentTime = new SimpleDateFormat("yyyyMMddHHmmss").format(
                    new Date());

            String logEntry =
                    String.format(
                            "频道名称: %s, 订阅者数: %s, 视频个数: %s, 观看次数: %s, 当前时间: %s\n",
                            channelName, subscriberCount, videoCount, viewCount, currentTime);
            writer.write(logEntry);
            writer.flush();
            log.info(logEntry);
        } else {
            log.warn("频道详细信息为空！");
        }
    }

    /**
     * 处理单个用户名，获取并打印频道信息
     *
     * @param username YouTube用户名
     */
    private static void processUsername(YouTube youtubeService, String username,
                                        BufferedWriter writer) {
        try {
            String channelId = getChannelIdByUsername(youtubeService, username);
            if (channelId != null) {
                Channel channel = getChannelDetails(youtubeService, channelId);
                printChannelDetails(channel, writer);
            } else {
                log.warn("未找到频道: {}", username);
            }
        } catch (IOException e) {
            log.error("处理用户 {} 时发生异常: ", username, e);
        }
    }

    public static void main(String[] args) {
        List<String> usernames = List.of("GoogleDevelopers", "wangzhian",
                "lingdujieshuo", "Tankman2020", "SeattleLaohu", "cnliziqi", "mkbhd",
                "onehour_English", "EnglishEasyPractice");
        YouTubeApiUtil.processUsernames(usernames);
    }
}
