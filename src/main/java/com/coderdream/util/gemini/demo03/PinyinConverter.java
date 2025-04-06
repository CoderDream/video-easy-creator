package com.coderdream.util.gemini.demo03;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdTimeUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class PinyinConverter {

  private static final String API_KEY = CdConstants.GEMINI_API_KEY; // 替换为你的 Gemini API 密钥
  private static final String MODEL_NAME = "gemini-2.5-pro-exp-03-25"; // 选择合适的模型
  private static final String API_URL =
    "https://generativelanguage.googleapis.com/v1beta/models/" + MODEL_NAME
      + ":generateContent?key=" + API_KEY;

  private static final int MAX_RETRIES = 10; // 最大重试次数
  private static final int RETRY_DELAY = 3000; // 重试延迟（毫秒）

  /**
   * 将中文句子转换为拼音 (无声调, zhe4 ge4 格式)
   *
   * @param chineseSentence 中文句子
   * @return 拼音字符串，如果转换失败则返回 null
   */
  public static String convertToPinyin(String chineseSentence) {
    log.info("正在转换中文句子: {}", chineseSentence);
    long startTime = System.currentTimeMillis();
    String pinyin;
    int attempt = 0;
    for (attempt = 0; attempt < MAX_RETRIES; attempt++) {
      try {
        // 设置请求体
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        Map<String, String> part = new HashMap<>();
        part.put("text",
          "请将以下中文句子转换为拼音，并在每个拼音后用数字 1-5 标明声调（1: 阴平, 2: 阳平, 3: 上声, 4: 去声, 5: 轻声）。字与字之间**不要有空格**，**不要输出任何其他内容**，只返回拼音：\n\n句子："
            + chineseSentence);
        content.put("parts", new Object[]{part});
        requestBody.put("contents", new Object[]{content});
        requestBody.put("generationConfig",
          createGenerationConfig()); // 添加 generationConfig

        // 设置代理
        Proxy proxy = new Proxy(Proxy.Type.SOCKS,
          new InetSocketAddress("127.0.0.1", 1080));

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
              if (contentResponse != null && contentResponse.containsKey(
                "parts")) {
                JSONArray parts = contentResponse.getJSONArray("parts");
                if (parts != null && !parts.isEmpty()) {
                  JSONObject partResponse = parts.getJSONObject(0);
                  if (partResponse != null) {
                    pinyin = partResponse.getStr("text", "")
                      .trim(); // 使用 getStr 并提供默认值

//                                     // 移除声调数字
//                                     pinyin = removeTones(pinyin);
//                                     // 移除空格
//                                     pinyin = pinyin.replaceAll("\\s+", "");
                    pinyin = PinyinSeparator.separatePinyin(pinyin);
                    log.info("拼音转换成功: {} -> {}", chineseSentence, pinyin);
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    String formattedTime = CdTimeUtil.formatDuration(duration);
                    log.info("转换拼音任务完成, 总耗时: {}，执行次数：{}",
                      formattedTime, attempt);
                    return pinyin;
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
        log.error("拼音转换出错 (尝试 {}/{}) : ", attempt + 1, MAX_RETRIES, e);
        log.error("异常详情:", e);  // 打印异常堆栈信息

        if (attempt < MAX_RETRIES - 1) {
          try {
            Thread.sleep(RETRY_DELAY); // 等待一段时间后重试
          } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.error("重试等待被中断！");
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            String formattedTime = CdTimeUtil.formatDuration(duration);
            log.info("转换拼音任务失败, 总耗时: {}，执行次数：{}", formattedTime,
              attempt);
            return null; // 中断，返回 null
          }
        }
      }
    }

    log.error("达到最大重试次数，拼音转换失败: {}", chineseSentence);
    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;
    String formattedTime = CdTimeUtil.formatDuration(duration);
    log.info("转换拼音任务完成, 总耗时: {}，执行次数：{}", formattedTime,
      attempt + 1);
    return null; // 达到最大重试次数，返回 null
  }


  /**
   * 创建 GenerationConfig 详细信息请参考：
   * https://ai.google.dev/api/rest/v1beta/GenerationConfig
   *
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
