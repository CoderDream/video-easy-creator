package com.coderdream.util.ollama;

import com.coderdream.util.cd.CdTimeUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class OllamaApi {

    private static final String OLLAMA_URL = "http://192.168.3.165:11434/api/generate";
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 1000; // 1 second
    private static final int TIMEOUT = 60 * 1000; // 60 seconds
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 调用Ollama API生成文本
     *
     * @param model  模型名称
     * @param prompt 提示词
     * @return API响应，如果失败则返回null
     */
    public OllamaResponse generate(String model, String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("prompt", prompt);
        requestBody.put("stream", false);

        OllamaResponse response = null;
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                response = executeRequest(requestBody);
                if (response != null) {
                    return response; // 成功，退出循环
                } else {
                    log.warn("Attempt {} failed, retrying...", attempt);
                }
            } catch (IOException e) {
                log.error("Attempt {} failed with exception: {}", attempt, e.getMessage(), e);
            }

            if (attempt < MAX_RETRIES) {
                try {
                    Thread.sleep(RETRY_DELAY_MS); // 休眠后重试
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupted state.
                    log.error("Retry interrupted.", e);
                    return null;
                }
            }
        }

        log.error("Failed to generate text after {} retries.", MAX_RETRIES);
        return null;
    }

    /**
     * 执行HTTP请求并解析响应
     *
     * @param requestBody 请求体
     * @return OllamaResponse对象，如果发生错误则返回null
     * @throws IOException 如果发生IO异常
     */
    private OllamaResponse executeRequest(Map<String, Object> requestBody) throws IOException {
        Instant start = Instant.now();
        String jsonRequest = objectMapper.writeValueAsString(requestBody);

        HttpPost post = new HttpPost(OLLAMA_URL);
        post.setEntity(new StringEntity(jsonRequest, StandardCharsets.UTF_8));
        post.setHeader("Content-Type", "application/json");

        // 设置超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(TIMEOUT)
                .setConnectionRequestTimeout(TIMEOUT)
                .setSocketTimeout(TIMEOUT)
                .build();
        post.setConfig(requestConfig);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            try (CloseableHttpResponse httpResponse = httpClient.execute(post)) {
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);

                if (statusCode >= 200 && statusCode < 300) {
                    OllamaResponse ollamaResponse = objectMapper.readValue(responseBody, OllamaResponse.class);
                    Instant end = Instant.now();
                    Duration duration = Duration.between(start, end);
                    String time = CdTimeUtil.formatDuration(duration.toMillis());
                    log.info("Ollama API call successful. Time taken: {}", time);
                    return ollamaResponse;
                } else {
                    log.error("Ollama API call failed with status code: {}. Response body: {}", statusCode, responseBody);
                    return null;
                }
            }
        } catch (IOException e) {
            log.error("Error calling Ollama API: {}", e.getMessage(), e);
            throw e;
        }
    }

    public static void main(String[] args) {
        OllamaApi ollamaApi = new OllamaApi();
        OllamaResponse response = ollamaApi.generate(
                "SiliconBasedWorld/Qwen2.5-7B-Instruct-1M:latest",
                "将【Create a set of math, worksheets for parents.】翻译为中文，只给我文本的翻译，别添加其他的内容。"
        );

        if (response != null) {
            System.out.println("Response: " + response);
        } else {
            System.out.println("Failed to get response from Ollama API.");
        }
    }
}
