package com.coderdream.util.grok;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdTimeUtil;
import java.net.InetSocketAddress;
import java.net.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrokApiUtil02 {
    private static final Logger logger = LoggerFactory.getLogger(GrokApiUtil02.class);
    private static final String GROK_API_URL = " https://api.x.ai/v1/chat/completions";//"https://api.xai.com/v1/grok";
    private static final int MAX_RETRY = 10;
    private static final long SLEEP_TIME = 3000;
    private static final String PROXY_HOST = "127.0.0.1";
    private static final int PROXY_PORT = 7890;//1080;
    private static final int TIMEOUT = 20000;

    /**
     * 调用Grok API
     * @param query 用户查询内容
     * @return API返回结果
     * @throws RuntimeException 如果达到最大重试次数仍失败
     */
    public static String callGrokApi(String query) {
        int retryCount = 0;
        String apiKey = CdConstants.GROK_API_KEY;
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_HOST, PROXY_PORT));
        long startTime = System.currentTimeMillis(); // 记录开始时间

        while (retryCount < MAX_RETRY) {
            HttpResponse response = null;
            try {
                JSONObject requestBody = new JSONObject();
                requestBody.set("query", query);
                logger.error("Grok API调用请求体: {}", requestBody);

                response = HttpRequest.post(GROK_API_URL)
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Content-Type", "application/json")
                        .setProxy(proxy)
                        .body(requestBody.toString())
                        .timeout(TIMEOUT)
                        .execute();

                if (response.isOk()) {
                    String result = response.body();
                    long endTime = System.currentTimeMillis();
                    String formattedTime = CdTimeUtil.formatDuration(endTime - startTime);
                    logger.info("Grok API调用成功，总耗时: {}, 返回结果: {}", formattedTime, result);
                    return result;
                } else {
                    long endTime = System.currentTimeMillis();
                    String formattedTime = CdTimeUtil.formatDuration(endTime - startTime);
                    logger.warn("Grok API调用失败，状态码: {}, 重试次数: {}, 耗时: {}",
                            response.getStatus(), retryCount + 1, formattedTime);
                }

            } catch (Exception e) {
                long endTime = System.currentTimeMillis();
                String formattedTime = CdTimeUtil.formatDuration(endTime - startTime);
                logger.error("Grok API调用异常，重试次数: {}, 耗时: {}, 错误: {}",
                        retryCount + 1, formattedTime, e.getMessage());
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
            String result = callGrokApi("你好，Grok!");
            System.out.println("API返回结果: " + result);
        } catch (Exception e) {
            logger.error("测试调用失败：{}", e.getMessage(), e);
        }
    }
}
