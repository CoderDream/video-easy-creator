package com.coderdream.util.chatgpt;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

public class ChatGPTExample2 {

    public static void main(String[] args) {
        try {
            // 读取 API 密钥文件
            List<String> apiKeys = readApiKeysFromFile("src/main/resources/api-key.txt");

            if (apiKeys.isEmpty()) {
                System.out.println("没有找到API密钥，请检查api-key.txt文件！");
                return;
            }

            // 随机选择一个API密钥
            String apiKey = apiKeys.get(new Random().nextInt(apiKeys.size()));
            System.out.println("使用的API密钥: " + apiKey);

            // 设置 ChatGPT API URL
            String urlString = "https://api.openai.com/v1/chat/completions";
            URL url = new URL(urlString);

            // 创建请求体内容（使用 Hutool 的 JSONObject 和 set 方法）
            JSONObject requestBody = new JSONObject();
            requestBody.set("model", "gpt-4");  // 选择使用的模型，例如 "gpt-4"

            JSONArray messages = new JSONArray();
            messages.add(new JSONObject().set("role", "system").set("content", "你是一个有帮助的助手。"));
            messages.add(new JSONObject().set("role", "user").set("content", "你好，ChatGPT！"));

            requestBody.set("messages", messages);

            // 创建代理对象
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 7890)); // 本地代理

            // 创建连接，设置代理
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);

            // 写入请求体内容
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 获取响应
            int statusCode = connection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                // 解析响应（使用 Hutool 的 JSONObject）
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    JSONObject responseJson = new JSONObject(response.toString());
                    String reply = responseJson.getJSONArray("choices")
                                                .getJSONObject(0)
                                                .getJSONObject("message")
                                                .getStr("content");
                    System.out.println("ChatGPT的回复: " + reply);
                }
            } else {
                System.out.println("请求失败，状态码: " + statusCode);
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"))) {
                    String inputLine;
                    StringBuilder errorResponse = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        errorResponse.append(inputLine);
                    }
                    System.out.println("错误响应内容: " + errorResponse.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 从文件中读取API密钥
    private static List<String> readApiKeysFromFile(String filePath) throws IOException {
        return Files.readAllLines(Paths.get(filePath));
    }
}
