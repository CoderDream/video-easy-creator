// 包名请替换为你项目的实际包名
package com.coderdream.util.network;


import cn.hutool.core.io.FileUtil;
// 用于流操作，如果需要的话
import cn.hutool.core.io.resource.ResourceUtil; // 用于读取 classpath 资源
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONException; // Hutool 的 JSON 异常
import cn.hutool.json.JSONUtil;    // Hutool 的 JSON 工具类
import com.coderdream.entity.ApiResponse;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API 请求工具类，使用 Hutool 发送请求，
 * 从 classpath 读取请求头文件，使用 Hutool 解析 JSON 响应，并将响应保存到文件。
 * 使用 Slf4j 记录日志。
 */
@Slf4j
public class ApiRequestUtil {

    // --- 配置常量 ---
    /** API 请求的目标 URL */
    private static final String API_URL = "http://192.168.3.165:9200/api/v1/tunnels";
    /**
     * 存储请求头的资源文件路径 (位于 src/main/resources 下)
     * ResourceUtil 会自动从 classpath 根目录查找
     */
    private static final String HEADERS_RESOURCE_PATH = "headers.txt";
    /** 输出 JSON 响应的文件路径 (相对于项目运行的当前目录) */
    private static final String OUTPUT_JSON_PATH = "resp.json";

    /**
     * 私有构造函数，防止外部实例化该工具类
     */
    private ApiRequestUtil() {
    }

    /**
     * 从 classpath 读取请求头资源文件。
     * 文件中每行应遵循 "Header-Name: HeaderValue" 的格式。
     * 忽略没有冒号或空行。
     *
     * @param resourcePath 请求头资源文件的路径 (相对于 classpath 根目录)
     * @return 包含请求头的 Map 对象，如果资源不存在或为空则返回空 Map
     */
    private static Map<String, String> readHeadersFromResource(String resourcePath) {
        log.info("尝试从 classpath 资源读取请求头: {}", resourcePath);
        Map<String, String> headers = new HashMap<>();

        try {
            // 使用 Hutool 的 ResourceUtil 直接读取 classpath 资源为 UTF-8 字符串
            String content = ResourceUtil.readUtf8Str(resourcePath);

            if (StrUtil.isBlank(content)) {
                log.warn("请求头资源文件为空或未找到: {}", resourcePath);
                return headers; // 文件为空或未找到，返回空 Map
            }

            // 将读取到的内容按行分割
            // 注意：Windows 和 Linux 换行符可能不同，StrUtil.split 对 \n, \r, \r\n 都能处理
            List<String> lines = StrUtil.split(content, '\n'); // 或者 content.lines().collect(Collectors.toList());

            // 遍历每一行，解析键值对
            for (String line : lines) {
                // 去除可能的回车符（如果 split 按 \n 分割在 Windows 上可能残留 \r）
                line = line.replace("\r", "");
                // 跳过空行
                if (StrUtil.isBlank(line)) {
                    continue;
                }
                // 查找第一个冒号的位置
                int separatorIndex = line.indexOf(':');
                // 确保冒号存在且不在行首或行尾
                if (separatorIndex > 0 && separatorIndex < line.length() - 1) {
                    // 提取键和值，并去除首尾空格
                    String key = line.substring(0, separatorIndex).trim();
                    String value = line.substring(separatorIndex + 1).trim();
                    // 确保键不为空
                    if (StrUtil.isNotBlank(key)) {
                        headers.put(key, value);
                        log.debug("加载请求头: '{}' = '{}'", key, value);
                    } else {
                        log.warn("跳过键为空的请求头行: '{}'", line);
                    }
                } else {
                    // 格式不正确的行
                    log.warn("跳过格式错误的请求头行 (缺少有效的 ':' 分隔符): '{}'", line);
                }
            }
            log.info("成功从资源 {} 加载了 {} 个请求头", resourcePath, headers.size());

        } catch (Exception e) {
            // 捕获读取资源时可能发生的异常 (例如资源不存在的 IORuntimeException)
            log.error("读取请求头资源 '{}' 时出错: {}", resourcePath, e.getMessage(), e);
        }
        return headers;
    }

    /**
     * 从配置的 API URL 获取数据，使用从资源文件加载的请求头，
     * 使用 Hutool 将 JSON 响应解析为 ApiResponse 对象，并将原始或解析后的响应保存到 JSON 文件。
     *
     * @return 解析后的 ApiResponse 对象，如果发生错误则返回 null。
     */
    public static ApiResponse fetchAndSaveTunnelData() {
        // 1. 从资源文件读取请求头
        Map<String, String> headers = readHeadersFromResource(HEADERS_RESOURCE_PATH);
        if (MapUtil.isEmpty(headers)) {
            log.error("未能从资源 '{}' 加载到任何请求头，无法继续请求。", HEADERS_RESOURCE_PATH);
            return null;
        }

        log.info("准备向 {} 发送 GET 请求", API_URL);
        HttpResponse response = null;
        String responseBody = null;
        ApiResponse apiResponse = null; // 用于存储成功解析后的对象

        try {
            // 2. 创建并配置 Hutool 的 HTTP GET 请求
            HttpRequest request = HttpUtil.createGet(API_URL)
                    .charset(CharsetUtil.CHARSET_UTF_8)
                    .timeout(20000);

            // 3. 添加所有从文件读取的请求头
            request.headerMap(headers, true);
            log.debug("设置的请求头: {}", headers.keySet());

            // 4. 执行 HTTP 请求
            log.info("执行 HTTP 请求...");
            response = request.execute();

            // 5. 处理 HTTP 响应
            responseBody = response.body();
            int statusCode = response.getStatus();
            log.info("收到 HTTP 状态码: {}", statusCode);
            log.debug("收到的原始响应体: {}", responseBody);

            // 检查 HTTP 状态码是否表示成功
            if (response.isOk()) {
                log.info("HTTP 请求成功 (状态码 {})", statusCode);
                // 6. 使用 Hutool 的 JSONUtil 解析 JSON 响应体为 ApiResponse 对象
                try {
                    // toBean 方法尝试将 JSON 字符串映射到指定的 Java Bean 类
                    apiResponse = JSONUtil.toBean(responseBody, ApiResponse.class);
                    log.info("成功将 JSON 响应解析为 ApiResponse 对象。");
                    // log.debug("解析后的 ApiResponse 数据: {}", apiResponse); // 按需取消注释
                } catch (JSONException e) { // 捕获 Hutool 的 JSONException
                    log.error("使用 Hutool 解析 JSON 响应失败: {}", e.getMessage(), e);
                    // 解析失败，保持 apiResponse 为 null
                    apiResponse = null;
                }
            } else {
                log.error("HTTP 请求失败，状态码: {}，响应体: {}", statusCode, responseBody);
                apiResponse = null;
            }

        } catch (Exception e) {
            log.error("执行 HTTP 请求时发生错误: {}", e.getMessage(), e);
            apiResponse = null;
        } finally {
            // 7. 写入文件：
            //    - 如果 apiResponse 不为 null (解析成功)，则将该对象转为格式化的 JSON 字符串写入。
            //    - 否则 (请求失败或解析失败)，如果 responseBody 不为 null，则将原始响应体字符串写入。
            String contentToWrite = null;
            if (apiResponse != null) {
                try {
                    // 使用 Hutool 将解析后的 Java 对象转为格式化的 JSON 字符串
                    contentToWrite = JSONUtil.toJsonPrettyStr(apiResponse); // <-- 修改后的正确行
                    log.info("准备将解析后的对象写入文件...");
                } catch (Exception e) {
                    log.error("将 ApiResponse 对象转换为 JSON 字符串时出错: {}", e.getMessage(), e);
                    // 如果对象转 JSON 出错，回退到写入原始响应体（如果可用）
                    if (responseBody != null) {
                        log.warn("由于对象转 JSON 失败，将尝试写入原始响应体。");
                        contentToWrite = responseBody;
                    }
                }
            } else if (responseBody != null) {
                 log.info("准备将原始响应体写入文件...");
                 contentToWrite = responseBody; // 请求失败或解析失败，写入原始响应体
            }

            // 执行写入操作（如果 contentToWrite 有内容）
            if (contentToWrite != null) {
                try {
                    FileUtil.writeString(contentToWrite, OUTPUT_JSON_PATH, StandardCharsets.UTF_8);
                    log.info("响应内容已成功写入到文件: {}", OUTPUT_JSON_PATH);
                } catch (Exception e) {
                    log.error("将响应内容写入文件 '{}' 时失败: {}", OUTPUT_JSON_PATH, e.getMessage(), e);
                }
            } else {
                 log.warn("没有可写入文件的响应内容。");
            }

            // 8. 关闭响应对象
            if (response != null) {
                response.close();
            }
        }

        // 9. 返回解析后的 ApiResponse 对象或 null
        return apiResponse;
    }

    /**
     * 主方法，用于演示工具类的使用
     */
    public static void main(String[] args) {
        // 运行前请确保:
        // 1. 项目类路径下有 SLF4J 的实现和配置文件 (如 logback.xml)。
        // 2. headers.txt 文件位于 src/main/resources 目录下。

        log.info("开始执行 API 请求流程...");
        ApiResponse result = fetchAndSaveTunnelData();

        if (result != null) {
            log.info("API 请求成功并且响应已成功解析。");
            if (result.getData() != null) {
                 log.info("共获取到 {} 个隧道信息。", result.getData().getTotal());
                 if (result.getData().getItems() != null) {
                    result.getData().getItems().forEach(item -> log.info("  - 隧道名称: {}", item.getName()));
                 }
            } else {
                 log.warn("API 返回成功，但 'data' 字段为空或无法解析。");
            }
        } else {
            log.error("API 请求流程失败，或响应未能成功解析。请检查日志和 '{}' 文件。", OUTPUT_JSON_PATH);
        }
        log.info("API 请求流程结束。");
    }
}
