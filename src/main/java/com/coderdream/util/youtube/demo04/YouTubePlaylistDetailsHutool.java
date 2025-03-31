package com.coderdream.util.youtube.demo04;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.youtube.YouTubeApiUtil;

public class YouTubePlaylistDetailsHutool {

    private static final String API_ENDPOINT = "https://www.googleapis.com/youtube/v3/playlistItems";

    public static void main(String[] args) {
        // Replace with your actual API key
        final String API_KEY = CdConstants.YOUTUBE_API_KEY;//"YOUR_API_KEY";

        // Replace with your playlist ID
        final String PLAYLIST_ID = "PLyeSHmN8RS0PvZDqNGYZwbWzdwL1-cZ32";

        try {
            getAllPlaylistItems(API_KEY, PLAYLIST_ID);
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void getAllPlaylistItems(String apiKey, String playlistId) {
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
                            JSONObject snippet = item.getJSONObject("snippet");
                            JSONObject contentDetails = item.getJSONObject("contentDetails");

                            String videoId = contentDetails.getStr("videoId");
                            String title = snippet.getStr("title");
                            String description = snippet.getStr("description");

                            System.out.println("Title: " + title);
                            System.out.println("Video ID: " + videoId);
                            System.out.println("Description: " + description);
                            System.out.println("---------------------");
                        }
                    }

                    nextPageToken = responseJson.getStr("nextPageToken");
                } else {
                    System.err.println("Failed to retrieve playlist items.");
                    return;
                }
            } catch (Exception e) {
                System.err.println("Error processing API response: " + e.getMessage());
                e.printStackTrace();
                return;
            }
        } while (nextPageToken != null);
    }


    public static JSONObject getPlaylistItems(String apiKey, String playlistId, String pageToken) {
        StringBuilder urlBuilder = new StringBuilder(API_ENDPOINT);
        urlBuilder.append("?part=snippet,contentDetails");
        urlBuilder.append("&playlistId=").append(playlistId);
        urlBuilder.append("&key=").append(apiKey);
        urlBuilder.append("&maxResults=50"); // Adjust as needed

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
