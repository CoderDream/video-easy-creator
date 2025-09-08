package com.coderdream.util.gemini.api;

import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.gemini.api.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gemini API 工具类
 * 提供查询可用模型、计算Token和生成内容的功能，并支持通过HTTP代理发送请求。
 */
public final class GeminiApiUtil {

    private static final Logger log = LoggerFactory.getLogger(GeminiApiUtil.class);
    private static final String API_BASE_URL = "https://generativelanguage.googleapis.com/v1beta";
    private static final String API_KEY = "AIzaSyCZ2zF0ZBenMe1qvqGm3l-c8_CbZfUgNEY";// CdConstants.GEMINI_API_KEY;

    // --- 代理配置 ---
    // 设置为 true 以启用代理, false 则禁用
    private static final boolean USE_PROXY = true;
    private static final String PROXY_HOST = "127.0.0.1";
    private static final int PROXY_PORT = 7890;

    private static final HttpClient httpClient = createHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 工具类不应被实例化
     */
    private GeminiApiUtil() {
    }

    /**
     * 创建一个配置好的 HttpClient 实例。
     * 如果 USE_PROXY 为 true，则会配置代理。
     * @return HttpClient 实例
     */
    private static HttpClient createHttpClient() {
        HttpClient.Builder builder = HttpClient.newBuilder()
          .version(HttpClient.Version.HTTP_2)
          .connectTimeout(Duration.ofSeconds(20));

        if (USE_PROXY) {
            log.info("Using proxy for Gemini API requests: {}:{}", PROXY_HOST, PROXY_PORT);
            builder.proxy(ProxySelector.of(new InetSocketAddress(PROXY_HOST, PROXY_PORT)));
        } else {
            log.info("Direct connection for Gemini API requests (no proxy).");
        }

        return builder.build();
    }

    /**
     * 获取所有可用的 Gemini 模型列表。
     *
     * @return 模型列表
     * @throws IOException          当网络请求失败时抛出
     * @throws InterruptedException 当线程被中断时抛出
     */
    public static List<Model> listAvailableModels() throws IOException, InterruptedException {
        String url = String.format("%s/models?key=%s", API_BASE_URL, API_KEY);
        HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(url))
          .GET()
          .header("Content-Type", "application/json")
          .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to list models. Status: " + response.statusCode() + ", Body: " + response.body());
        }

        ModelListResponse modelListResponse = objectMapper.readValue(response.body(), ModelListResponse.class);
        return modelListResponse.getModels();
    }

    /**
     * 计算给定文本在特定模型下会消耗的Token数量。
     *
     * @param modelName 模型的名称, 例如 "models/gemini-pro"
     * @param text      需要计算Token的文本
     * @return Token总数
     * @throws IOException          当网络请求失败时抛出
     * @throws InterruptedException 当线程被中断时抛出
     */
    public static int countTokens(String modelName, String text) throws IOException, InterruptedException {
        String url = String.format("%s/%s:countTokens?key=%s", API_BASE_URL, modelName, API_KEY);

        Part part = new Part(text);
        Content content = new Content(List.of(part));
        CountTokensRequest countTokensRequest = new CountTokensRequest(List.of(content));
        String requestBody = objectMapper.writeValueAsString(countTokensRequest);

        HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(url))
          .POST(HttpRequest.BodyPublishers.ofString(requestBody))
          .header("Content-Type", "application/json")
          .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to count tokens. Status: " + response.statusCode() + ", Body: " + response.body());
        }

        CountTokensResponse countTokensResponse = objectMapper.readValue(response.body(), CountTokensResponse.class);
        return countTokensResponse.getTotalTokens();
    }

    /**
     * 【新增】向 Gemini 模型发送POST请求以生成内容。
     *
     * @param modelName 模型的名称, 例如 "models/gemini-1.5-flash-latest"
     * @param text      作为输入的提示词 (prompt)
     * @return 模型生成的文本内容。如果内容被安全策略阻止，会返回提示信息。
     * @throws IOException          当网络请求失败时抛出
     * @throws InterruptedException 当线程被中断时抛出
     */
    public static String generateContent(String modelName, String text) throws IOException, InterruptedException {
        String url = String.format("%s/%s:generateContent?key=%s", API_BASE_URL, modelName, API_KEY);

        // 构建请求体
        Part part = new Part(text);
        Content content = new Content(List.of(part));
        GenerateContentRequest requestBodyObject = new GenerateContentRequest(List.of(content));
        String requestBody = objectMapper.writeValueAsString(requestBodyObject);

        HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(url))
          .POST(HttpRequest.BodyPublishers.ofString(requestBody))
          .header("Content-Type", "application/json")
          .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("API call failed with status: " + response.statusCode() + " and body: " + response.body());
        }

        GenerateContentResponse apiResponse = objectMapper.readValue(response.body(), GenerateContentResponse.class);

        // 健壮性检查：确保能安全地提取内容
        if (apiResponse != null && apiResponse.getCandidates() != null && !apiResponse.getCandidates().isEmpty()) {
            Candidate firstCandidate = apiResponse.getCandidates().get(0);
            if (firstCandidate.getContent() != null && firstCandidate.getContent().getParts() != null && !firstCandidate.getContent().getParts().isEmpty()) {
                return firstCandidate.getContent().getParts().get(0).getText();
            }
        }

        // 如果没有有效内容（例如被安全策略拦截），则记录日志并返回提示信息
        log.warn("Gemini API did not return valid content. Full response: {}", response.body());
        return "No content generated. The response might have been blocked by safety settings.";
    }
}
