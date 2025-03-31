package com.coderdream.util.youtube.demo04;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.youtube.YouTubeApiUtil;
import java.util.ArrayList;
import java.util.List;

public class YouTubePlaylistVideoIds {

    private static final String API_ENDPOINT = "https://www.googleapis.com/youtube/v3/playlistItems";

    public static void main(String[] args) {
        // Replace with your actual API key
        final String API_KEY = CdConstants.YOUTUBE_API_KEY;

        // Replace with your playlist ID
        final String PLAYLIST_ID = "PLyeSHmN8RS0PvZDqNGYZwbWzdwL1-cZ32";

        try {
            List<String> videoIds = getAllVideoIdsFromPlaylist(API_KEY, PLAYLIST_ID);

            if (videoIds != null) {
                System.out.println("Playlist video IDs:");
                for (String videoId : videoIds) {
                    System.out.println(videoId);
                }
                System.out.println("Total video count: " + videoIds.size());
            } else {
                System.err.println("Failed to retrieve video IDs.");
            }
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<String> getAllVideoIdsFromPlaylist(String apiKey, String playlistId) {
        List<String> videoIds = new ArrayList<>();
        String nextPageToken = null;

        YouTubeApiUtil.enableProxy();
        do {
            try {
                JSONObject responseJson = getPlaylistItems(apiKey, playlistId, nextPageToken);

                if (responseJson != null) {
                    JSONArray items = responseJson.getJSONArray("items");

                    if (items != null && !items.isEmpty()) {
                        for (int i = 0; i < items.size(); i++) {
                            JSONObject item = items.getJSONObject(i);
                            JSONObject contentDetails = item.getJSONObject("contentDetails");

                            String videoId = contentDetails.getStr("videoId");
                            videoIds.add(videoId);
                        }
                    }

                    nextPageToken = responseJson.getStr("nextPageToken");
                } else {
                    System.err.println("Failed to retrieve playlist items.");
                    return null; // Indicate failure
                }
            } catch (Exception e) {
                System.err.println("Error processing API response: " + e.getMessage());
                e.printStackTrace();
                return null; // Indicate failure
            }
        } while (nextPageToken != null);

        return videoIds;
    }

    private static JSONObject getPlaylistItems(String apiKey, String playlistId, String pageToken) {
        StringBuilder urlBuilder = new StringBuilder(API_ENDPOINT);
        urlBuilder.append("?part=contentDetails"); // Only need contentDetails for videoId
        urlBuilder.append("&playlistId=").append(playlistId);
        urlBuilder.append("&key=").append(apiKey);
        urlBuilder.append("&maxResults=50");

        if (pageToken != null) {
            urlBuilder.append("&pageToken=").append(pageToken);
        }

        String url = urlBuilder.toString();

        try {
            String responseBody = HttpRequest.get(url).execute().body();
            return JSONUtil.parseObj(responseBody);
        } catch (Exception e) {
            System.err.println("Error making API request: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
