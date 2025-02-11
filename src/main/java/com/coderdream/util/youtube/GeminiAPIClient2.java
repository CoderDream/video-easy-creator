package com.coderdream.util.youtube;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONObject;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.proxy.OperatingSystem;

public class GeminiAPIClient2 {

    private static final String API_KEY = CdConstants.GEMINI_API_KEY;  // 替换为你的 GEMINI API Key
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";
    // 设置代理服务器地址
    private static final String PROXY_HOST = "127.0.0.1";
//    private static final int PROXY_PORT = 7890;

    public static String generateContent(String prompt) {
        // 构造请求体
        JSONObject requestBody = new JSONObject();
        JSONObject content = new JSONObject();
        content.put("text", prompt);

        requestBody.put("contents", new JSONObject[]{content});

        // 发送 POST 请求，添加代理设置
        HttpResponse response = HttpRequest.post(API_URL)
          .header("Content-Type", "application/json")
          .form("key", API_KEY)
          .body(requestBody.toString())
          .setHttpProxy(PROXY_HOST, OperatingSystem.getProxyPort())  // 设置代理
          .execute();

//        String result2 = HttpRequest.post(API_URL)
//          .setHttpProxy(PROXY_HOST, PROXY_PORT)
//          .body(requestBody.toString())
//          .execute().body();


        // 检查响应
        if (response.getStatus() == 200) {
            return response.body();  // 返回 API 返回的内容
        } else {
            return "请求失败，状态码：" + response.getStatus();
        }
    }

    public static void main(String[] args) {
        // 调用生成内容的功能
        String prompt = "Explain how AI works";
        String result = generateContent(prompt);
        System.out.println("API 返回结果: " + result);
    }
}
