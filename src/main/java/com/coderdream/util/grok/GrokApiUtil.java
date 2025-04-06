package com.coderdream.util.grok;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.Proxy;

@Slf4j
public class GrokApiUtil {
    private static final Logger logger = LoggerFactory.getLogger(GrokApiUtil.class);
    private static final String GROK_API_URL = "https://api.x.ai/v1/chat/completions";
    private static final int MAX_RETRY = 10;
    private static final long SLEEP_TIME = 3000;
    private static final String PROXY_HOST = "127.0.0.1";
    private static final int PROXY_PORT = 1080;
    private static final int TIMEOUT = 20000;
    private static final String DEFAULT_MODEL = "grok-2-latest";

    /**
     * 调用Grok API
     * @param systemPrompt 系统提示词
     * @param userPrompt 用户输入
     * @return API返回结果
     * @throws RuntimeException 如果达到最大重试次数仍失败或认证失败
     */
    public static String callGrokApi(String systemPrompt, String userPrompt) {
        int retryCount = 0;
        String apiKey = CdConstants.GROK_API_KEY;
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_HOST, PROXY_PORT));
        long startTime = System.currentTimeMillis();

        while (retryCount < MAX_RETRY) {
            HttpResponse response = null;
            try {
                // 构建消息数组
                JSONArray messages = new JSONArray();
                messages.add(new JSONObject()
                        .set("role", "system")
                        .set("content", systemPrompt));
                messages.add(new JSONObject()
                        .set("role", "user")
                        .set("content", userPrompt));

                // 构建请求体
                JSONObject requestBody = new JSONObject();
                requestBody.set("messages", messages);
                requestBody.set("model", DEFAULT_MODEL);
                requestBody.set("stream", false);
                requestBody.set("temperature", 0);

                log.error("Grok API请求体: {}", requestBody.toString());
                response = HttpRequest.post(GROK_API_URL)
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Content-Type", "application/json")
                        .setProxy(proxy)
                        .body(requestBody.toString())
                        .timeout(TIMEOUT)
                        .execute();

                long endTime = System.currentTimeMillis();
                String formattedTime = CdTimeUtil.formatDuration(endTime - startTime);
                String responseBody = response.body();

                if (response.isOk()) {
                    JSONObject jsonResponse = JSONUtil.parseObj(responseBody);
                    if (jsonResponse.containsKey("error")) {
                        logger.error("Grok API调用失败，耗时: {}, 错误: {}", formattedTime, jsonResponse.getJSONObject("error"));
                        throw new RuntimeException("Grok API错误: " + jsonResponse.getJSONObject("error").getStr("message"));
                    }

//                    logger.info("Grok API调用成功，总耗时: {}, 返回结果: {}", formattedTime, responseBody);
//                    return responseBody;
                    // 提取content
                    JSONArray choices = jsonResponse.getJSONArray("choices");
                    if (choices == null || choices.isEmpty()) {
                        logger.error("Grok API返回无有效choices，耗时: {}, 返回: {}", formattedTime, responseBody);
                        throw new RuntimeException("Grok API返回无有效choices");
                    }

                    JSONObject firstChoice = choices.getJSONObject(0);
                    JSONObject message = firstChoice.getJSONObject("message");
                    String content = message.getStr("content");

                    if (content == null) {
                        logger.error("Grok API返回content为空，耗时: {}, 返回: {}", formattedTime, responseBody);
                        throw new RuntimeException("Grok API返回content为空");
                    }

                    logger.info("Grok API调用成功，总耗时: {}, 返回content: {}", formattedTime, content);
                    return content;
                } else {
                    logger.warn("Grok API调用失败，状态码: {}, 重试次数: {}, 耗时: {}, 返回: {}",
                            response.getStatus(), retryCount + 1, formattedTime, responseBody);
                }

            } catch (Exception e) {
                long endTime = System.currentTimeMillis();
                String formattedTime = CdTimeUtil.formatDuration(endTime - startTime);
                logger.error("Grok API调用异常，重试次数: {}, 耗时: {}, 错误: {}",
                        retryCount + 1, formattedTime, e.getMessage());
                if (e.getMessage().contains("认证失败")) {
                    throw e;
                }
            } finally {
                if (response != null) {
                    response.close();
                }
            }

            retryCount++;
            if (retryCount < MAX_RETRY) {
                sleepBeforeRetry();
            }
        }

        long endTime = System.currentTimeMillis();
        String formattedTime = CdTimeUtil.formatDuration(endTime - startTime);
        logger.error("Grok API调用失败，已达到最大重试次数: {}, 总耗时: {}", MAX_RETRY, formattedTime);
        throw new RuntimeException("Grok API调用失败，已达到最大重试次数");
    }

    /**
     * 重试前的休眠
     */
    private static void sleepBeforeRetry() {
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("线程休眠被中断", e);
        }
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) {
        try {
            String result = callGrokApi(
                    "You are a test assistant.",
                    "Testing. Just say hi and hello world and nothing else."
            );
            System.out.println("API返回结果: " + result);
        } catch (Exception e) {
            logger.error("测试调用失败：{}",e.getMessage(), e);
        }
    }
}
