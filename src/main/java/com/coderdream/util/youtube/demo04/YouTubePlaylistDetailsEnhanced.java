package com.coderdream.util.youtube.demo04;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.proxy.OperatingSystem;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Proxy;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class YouTubePlaylistDetailsEnhanced {

  private static final Logger log = LoggerFactory.getLogger(
    YouTubePlaylistDetailsEnhanced.class);
  private static final String API_ENDPOINT = "https://www.googleapis.com/youtube/v3/playlistItems";

  @Data  // 使用 Lombok 的 @Data 注解，自动生成 getter、setter、toString 等方法
  public static class PlaylistItemInfo {

    private String videoId; // 视频ID
    private String title; // 视频标题
    private String description; // 视频描述
    private String playlistItemId; // 播放列表条目的ID
    private String publishedAt; // 视频发布时间
  }

  // 使用常量类获取 API 密钥
  final String API_KEY = CdConstants.YOUTUBE_API_KEY;

  public static void main(String[] args) {

    // 替换为你的播放列表ID
    final String PLAYLIST_ID = "PLyeSHmN8RS0PvZDqNGYZwbWzdwL1-cZ32";

    try {
      List<PlaylistItemInfo> playlistItems = getAllPlaylistItems(PLAYLIST_ID);

      if (playlistItems != null) {
        log.info("播放列表信息:");
        for (PlaylistItemInfo item : playlistItems) {
          log.info(item.toString()); // 使用 Lombok 自动生成的 toString 方法
        }
        log.info("总条目数: {}", playlistItems.size());
      } else {
        log.error("无法获取播放列表信息.");
      }
    } catch (Exception e) {
      log.error("发生错误: {}", e.getMessage(), e); // 记录异常堆栈信息
    }
  }

  public static List<PlaylistItemInfo> getAllPlaylistItems(String playlistId) {
    List<PlaylistItemInfo> playlistItems = new ArrayList<>();
    String nextPageToken = null;

    do {
      try {
        JSONObject responseJson = getPlaylistItems(playlistId, nextPageToken);

        if (responseJson != null) {
          JSONArray items = responseJson.getJSONArray("items");

          if (items != null && !items.isEmpty()) {
            for (int i = 0; i < items.size(); i++) {
              JSONObject item = items.getJSONObject(i);
              JSONObject snippet = item.getJSONObject("snippet");
              JSONObject contentDetails = item.getJSONObject("contentDetails");

              String videoId = contentDetails.getStr("videoId");
              String title = snippet.getStr("title");
              String description = snippet.getStr("description");
              String playlistItemId = item.getStr("id"); // 播放列表条目的ID
              String publishedAt = snippet.getStr("publishedAt");

              PlaylistItemInfo playlistItemInfo = new PlaylistItemInfo();
              playlistItemInfo.setVideoId(videoId);
              playlistItemInfo.setTitle(title);
              playlistItemInfo.setDescription(description);
              playlistItemInfo.setPlaylistItemId(playlistItemId);
              playlistItemInfo.setPublishedAt(publishedAt);

              playlistItems.add(playlistItemInfo);
            }
          }

          nextPageToken = responseJson.getStr("nextPageToken");
        } else {
          log.error("无法获取播放列表条目，响应为null.");
          return null;
        }
      } catch (Exception e) {
        log.error("处理API响应时发生错误: {}", e.getMessage(), e);
        return null;
      }
    } while (nextPageToken != null);

    return playlistItems;
  }

  private static JSONObject getPlaylistItems(String playlistId,
    String pageToken) {
    StringBuilder urlBuilder = new StringBuilder(API_ENDPOINT);
    urlBuilder.append(
      "?part=snippet,contentDetails");  // 需要 snippet 获取 title 和 description
    urlBuilder.append("&playlistId=").append(playlistId);
    urlBuilder.append("&key=").append(CdConstants.YOUTUBE_API_KEY);
    urlBuilder.append("&maxResults=150");

    if (pageToken != null) {
      urlBuilder.append("&pageToken=").append(pageToken);
    }

    String url = urlBuilder.toString();
    log.debug("API请求URL: {}", url);

    try {
      // 使用代理
      Proxy proxy = new Proxy(Proxy.Type.HTTP,
        new InetSocketAddress("127.0.0.1", OperatingSystem.getProxyPort()));
      String responseBody = HttpRequest.get(url)
        .setProxy(proxy)
        .execute()
        .body();

      log.debug("API响应体: {}",
        responseBody);  // Log the response body for debugging

      return JSONUtil.parseObj(responseBody);
    } catch (Exception e) {
      log.error("进行API请求时发生错误: {}", e.getMessage(), e);
      return null;
    }
  }

}
