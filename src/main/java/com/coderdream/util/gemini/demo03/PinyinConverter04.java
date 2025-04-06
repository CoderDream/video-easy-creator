package com.coderdream.util.gemini.demo03;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.coderdream.util.cd.CdConstants;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PinyinConverter04 {

    private static final String API_KEY = CdConstants.GEMINI_API_KEY; // 替换为你的 Gemini API 密钥
    private static final String MODEL_NAME = "gemini-2.5-pro-exp-03-25"; // 选择合适的模型
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/" + MODEL_NAME + ":generateContent?key=" + API_KEY;

    /**
     * 将中文句子转换为拼音 (无声调, zhe4ge4 格式)
     *
     * @param chineseSentence 中文句子
     * @return 拼音字符串，如果转换失败则返回 null
     */
    public static String convertToPinyin(String chineseSentence) {
        log.info("正在转换中文句子: {}", chineseSentence);
        String pinyin = null;

        try {
            // 设置请求体
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> content = new HashMap<>();
            Map<String, String> part = new HashMap<>();
            part.put("text", "请将以下中文句子转换为拼音，并在每个拼音后用数字 1-5 标明声调（1: 阴平, 2: 阳平, 3: 上声, 4: 去声, 5: 轻声）。字与字之间**不要有空格**，**不要输出任何其他内容**，只返回拼音：\n\n句子：" + chineseSentence);
            content.put("parts", new Object[]{part});
            requestBody.put("contents", new Object[]{content});
            requestBody.put("generationConfig", createGenerationConfig()); // 添加 generationConfig

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
            log.debug("Gemini API 响应: {}", response);

            // 解析响应
            JSONObject jsonResponse = JSONUtil.parseObj(response);
            if (jsonResponse.containsKey("candidates")) {
                JSONArray candidates = jsonResponse.getJSONArray("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    JSONObject candidate = candidates.getJSONObject(0);
                    if (candidate != null && candidate.containsKey("content")) {
                         JSONObject contentResponse = candidate.getJSONObject("content");
                         if (contentResponse != null && contentResponse.containsKey("parts")) {
                             JSONArray parts = contentResponse.getJSONArray("parts");
                             if (parts != null && !parts.isEmpty()) {
                                 JSONObject partResponse = parts.getJSONObject(0);
                                 if (partResponse != null) {
                                      pinyin = partResponse.getStr("text", "").trim(); // 使用 getStr 并提供默认值

//                                     // 移除声调数字
//                                     pinyin = removeTones(pinyin);
//                                     // 移除空格
//                                     pinyin = pinyin.replaceAll("\\s+", "");
                                     pinyin = PinyinSeparator.separatePinyin(pinyin);
                                     log.info("拼音转换成功: {} -> {}", chineseSentence, pinyin);
                                 } else {
                                     log.error("Gemini API 响应 'partResponse' 为空.");
                                 }

                             } else {
                                 log.error("Gemini API 响应 'parts' 数组为空.");
                             }
                         } else {
                             log.error("Gemini API 响应缺少 'content.parts' 字段.");
                         }
                    } else {
                        log.error("Gemini API 响应缺少 'candidate.content' 字段.");
                    }

                } else {
                    log.error("Gemini API 响应 'candidates' 数组为空.");
                }
            } else {
                log.error("Gemini API 响应缺少 'candidates' 字段: {}", response);
            }

        } catch (Exception e) {
            log.error("拼音转换出错: ", e);
            log.error("异常详情:", e);  // 打印异常堆栈信息
        }

        return pinyin;
    }

    /**
     * 移除拼音字符串中的声调数字
     *
     * @param pinyinWithTones 带有声调数字的拼音字符串
     * @return 移除声调数字后的拼音字符串
     */
    private static String removeTones(String pinyinWithTones) {
        // 使用正则表达式移除声调数字
        Pattern tonePattern = Pattern.compile("[1-5]");
        Matcher matcher = tonePattern.matcher(pinyinWithTones);
        return matcher.replaceAll("");
    }

    /**
     * 创建 GenerationConfig
     * 详细信息请参考： https://ai.google.dev/api/rest/v1beta/GenerationConfig
     * @return
     */
    private static Map<String, Object> createGenerationConfig() {
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.2);
        generationConfig.put("topK", 32);
        generationConfig.put("topP", 0.9);
        generationConfig.put("maxOutputTokens", 1024);
        return generationConfig;
    }


    public static void main(String[] args) {
        String chineseSentence = "这个物体的重量是五公斤，请重复一遍。";
        String pinyin = convertToPinyin(chineseSentence);
        if (pinyin != null) {
            log.debug("中文: " + chineseSentence + "\n拼音: " + pinyin);
        } else {
            log.error("转换中文句子失败: " + chineseSentence);
        }
    }
}
