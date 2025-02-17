package com.coderdream.util.alimt;

//import org.apache.hc.client5.http.classic.methods.HttpPost;
//import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
//import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
//import org.apache.hc.client5.http.impl.classic.HttpClients;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class AliTranslateAPI {
    // 替换为你的 AccessKey ID 和 Secret
    private static final String ACCESS_KEY_ID = "";
    private static final String ACCESS_KEY_SECRET = "";

    // 阿里云机器翻译 API 地址
    private static final String API_URL = "https://mt.cn-hangzhou.aliyuncs.com/api/translate/web/general";

    public static void main(String[] args) {
        try {
            String sourceText = "Hello, how are you?";
            String translatedText = translate(sourceText, "en", "zh");
            System.out.println("Translated Text: " + translatedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String translate(String text, String sourceLang, String targetLang) throws Exception {
        // 创建 HttpClient
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(API_URL);

            // 设置请求头
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Authorization", "APPCODE " + ACCESS_KEY_ID);

            // 构造请求体
            JSONObject requestBody = new JSONObject();
            requestBody.put("sourceText", text);
            requestBody.put("sourceLanguage", sourceLang);
            requestBody.put("targetLanguage", targetLang);
            requestBody.put("formatType", "text");

            // 设置请求体
            StringEntity entity = new StringEntity(requestBody.toString());
            post.setEntity(entity);

            // 发送请求
            try (CloseableHttpResponse response = client.execute(post)) {
                // 读取响应
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                // 解析 JSON 响应
                JSONObject jsonResponse = JSONObject.parseObject(result.toString());
                return jsonResponse.getJSONObject("data").getString("translated");
            }
        }
    }
}
