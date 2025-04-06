package com.coderdream.util.gemini.demo03;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.coderdream.util.cd.CdConstants;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PinyinConverter02 {

    private static final String API_KEY = CdConstants.GEMINI_API_KEY; // 替换为你的 Gemini API 密钥
    private static final String MODEL_NAME = "gemini-2.0-flash"; // 选择合适的模型 "gemini-2.5-pro-exp-03-25"; // 选择合适的模型
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/" + MODEL_NAME + ":generateContent?key=" + API_KEY;

    /**
     * 将中文句子转换为拼音 (带声调数字, 空格分隔)
     *
     * @param chineseSentence 中文句子
     * @return 拼音字符串，如果转换失败则返回 null
     */
    public static String convertToPinyin(String chineseSentence) {
        log.info("Converting chinese sentence: {}", chineseSentence);
        String pinyin = null;

        try {
            // 设置请求体
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> content = new HashMap<>();
            Map<String, String> part = new HashMap<>();
            part.put("text", "请将以下中文句子转换为拼音，并在每个拼音后用数字 1-5 标明声调（1: 阴平, 2: 阳平, 3: 上声, 4: 去声, 5: 轻声）。 字与字之间用空格分隔：\n\n句子：" + chineseSentence + "\n\n拼音：");
            content.put("parts", new Object[]{part});
            requestBody.put("contents", new Object[]{content});

            // 设置代理
            Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 1080));

            // 发送 POST 请求
            String response = HttpUtil.createPost(API_URL)
              .setProxy(proxy)
              .header("Content-Type", "application/json")
              .body(JSONUtil.toJsonStr(requestBody))
              .execute()
              .body();

            // 打印原始响应
            log.debug("Gemini API Response: {}", response);

            // 解析响应
            JSONObject jsonResponse = JSONUtil.parseObj(response);
            if (jsonResponse.containsKey("candidates")) {
                JSONObject candidate = jsonResponse.getJSONArray("candidates").getJSONObject(0);
                JSONObject contentResponse = candidate.getJSONObject("content");
                if (contentResponse.containsKey("parts")) {
                    JSONObject partResponse = contentResponse.getJSONArray("parts").getJSONObject(0);
                    pinyin = partResponse.getStr("text").trim();
                    log.info("Pinyin conversion successful: {} -> {}", chineseSentence, pinyin);
                } else {
                    log.error("Gemini API response missing 'parts' field.");
                }
            } else {
                log.error("Gemini API response missing 'candidates' field: {}", response);
            }

        } catch (Exception e) {
            log.error("Error during pinyin conversion: ", e);
            log.error("Exception details:", e);  // 打印异常堆栈信息
        }

        return pinyin;
    }

    public static void main(String[] args) {
        String chineseSentence = "这个物体的重量是五公斤，请重复一遍。";
        String pinyin = convertToPinyin(chineseSentence);
        if (pinyin != null) {
            System.out.println("Chinese: " + chineseSentence + "\nPinyin: " + pinyin);
        } else {
            System.out.println("Failed to convert chinese sentence: " + chineseSentence);
        }
    }
}
