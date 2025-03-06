package com.coderdream.util.chatgpt;

import com.coderdream.entity.GptRequest;
import com.coderdream.entity.GptResponse;
import com.coderdream.util.youtube.YouTubeApiUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import okhttp3.*;

import java.io.IOException;
import java.util.List;

public class ChatGPTClient02 {

    private static final String API_KEY = "sk-MQ5dB6vIMdg4YoknE3504750D35e49Cd9aD605132b2902Aa"; // 替换为你的 API Key
    private static final String API_URL = "https://free.v36.cm/v1/chat/completions";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient();

    public static GptResponse sendMessage(GptRequest request) throws IOException {
        YouTubeApiUtil.enableProxy();

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(request);

        RequestBody body = RequestBody.create(json, JSON);
        Request httpRequest = new Request.Builder()
          .url(API_URL)
          .header("Authorization", "Bearer " + API_KEY)
          .post(body)
          .build();

        try (Response response = client.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String responseBody = response.body().string();
            return objectMapper.readValue(responseBody, GptResponse.class);
        }
    }


    public static void main(String[] args) throws IOException {
        // 示例用法
        GptRequest request = new GptRequest();
        request.setModel("gpt-4o-mini");

        GptRequest.Message message = new GptRequest.Message();
        message.setRole("user");
        message.setContent("Say this is a test!");

        List<GptRequest.Message> messages = List.of(message);
        request.setMessages(messages);
        request.setTemperature(0.7);

        GptResponse response = ChatGPTClient.sendMessage(request);

        System.out.println("Response: " + response);

        if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
            System.out.println("ChatGPT Response Content: " + response.getChoices().get(0).getMessage().getContent());
        } else {
            System.out.println("No response received from ChatGPT.");
        }

    }
}
