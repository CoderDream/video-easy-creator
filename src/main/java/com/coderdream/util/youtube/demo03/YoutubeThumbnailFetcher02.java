package com.coderdream.util.youtube.demo03;

import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.youtube.YouTubeApiUtil;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ThumbnailDetails;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import java.util.List;

public class YoutubeThumbnailFetcher02 {

    private static final String API_KEY = CdConstants.GOOGLE_API_KEY; // 替换为你的 API 密钥
    private static final String VIDEO_ID = "5yjf1RvhflI"; // 示例视频ID
    private static final String APPLICATION_NAME = "YoutubeThumbnailFetcher";

    public static String getYoutubeThumbnailUrl(String videoId, String apiKey) {
        try {
            NetHttpTransport transport = new NetHttpTransport();
            JacksonFactory jsonFactory = new JacksonFactory();
            YouTube youtubeService = YouTubeApiUtil.createYoutubeService(transport, jsonFactory);

            YouTube.Videos.List request = youtubeService.videos().list("snippet");
            request.setId(videoId);
            request.setKey(apiKey);
            VideoListResponse response = request.execute();

            List<Video> videos = response.getItems();
            if (videos != null && !videos.isEmpty()) {
                Video video = videos.get(0);
                ThumbnailDetails thumbnails = video.getSnippet().getThumbnails();

                // 封面优先级: maxres, standard, high, medium
                if (thumbnails.getMaxres() != null) {
                    return thumbnails.getMaxres().getUrl();
                } else if (thumbnails.getStandard() != null) {
                    return thumbnails.getStandard().getUrl();
                } else if (thumbnails.getHigh() != null) {
                    return thumbnails.getHigh().getUrl();
                } else if (thumbnails.getMedium() != null) {
                    return thumbnails.getMedium().getUrl();
                } else {
                    return null; // 没有找到任何合适的封面
                }
            } else {
                return null; // 视频不存在
            }

        } catch (Exception e) {
            System.err.println("发生错误: " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        String thumbnailUrl = getYoutubeThumbnailUrl(VIDEO_ID, API_KEY);

        if (thumbnailUrl != null) {
            System.out.println("视频 " + VIDEO_ID + " 的封面 URL: " + thumbnailUrl);
            // 在这里你可以使用 URL 下载图片，例如使用 java.net.URL 和 java.io.InputStream
        } else {
            System.out.println("无法获取视频 " + VIDEO_ID + " 的封面 URL");
        }
    }
}
