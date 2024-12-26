package com.coderdream.util.youtube;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class YoutubeApiCaller {

    // 定义请求的URL、方法、请求头和请求体
    private static final String URL = "https://www.youtube.com/youtubei/v1/browse?prettyPrint=false";
    private static final String METHOD = "POST";
    private static final Map<String, String> HEADERS = new HashMap<>();

    // 使用StringBuilder构建请求体
    private static final String PAYLOAD = new StringBuilder()
          .append("{\n")
          .append("    \"context\": {\n")
          .append("        \"client\": {\n")
          .append("            \"hl\": \"zh-CN\",\n")
          .append("            \"gl\": \"FR\",\n")
          .append("            \"remoteHost\": \"195.154.200.40\",\n")
          .append("            \"deviceMake\": \"\",\n")
          .append("            \"deviceModel\": \"\",\n")
          .append("            \"visitorData\": \"Cgtsd1p5ZmZycTI3USie3qe7BjIKCgJUVxIEGgAgZw%3D%3D\",\n")
          .append("            \"userAgent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36,gzip(gfe)\",\n")
          .append("            \"clientName\": \"WEB\",\n")
          .append("            \"clientVersion\": \"2.20241219.01.01\",\n")
          .append("            \"osName\": \"Windows\",\n")
          .append("            \"osVersion\": \"10.0\",\n")
          .append("            \"originalUrl\": \"https://www.youtube.com/\",\n")
          .append("            \"screenPixelDensity\": 2,\n")
          .append("            \"platform\": \"DESKTOP\",\n")
          .append("            \"clientFormFactor\": \"UNKNOWN_FORM_FACTOR\",\n")
          .append("            \"screenDensityFloat\": 1.875,\n")
          .append("            \"userInterfaceTheme\": \"USER_INTERFACE_THEME_DARK\",\n")
          .append("            \"timeZone\": \"Asia/Shanghai\",\n")
          .append("            \"browserName\": \"Chrome\",\n")
          .append("            \"browserVersion\": \"131.0.0.0\",\n")
          .append("            \"acceptHeader\": \"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\",\n")
          .append("            \"deviceExperimentId\": \"ChxOelExTVRjME9UazNNakU1TWpRNU1EQTBOQT09EJ7ep7sGGJ7ep7sG\",\n")
          .append("            \"rolloutToken\": \"CJrek-T4mvqOYhCyyJiKua6KAxiPkrS9w7yKAw%3D%3D\",\n")
          .append("            \"screenWidthPoints\": 1289,\n")
          .append("            \"screenHeightPoints\": 1035,\n")
          .append("            \"utcOffsetMinutes\": 480,\n")
          .append("            \"connectionType\": \"CONN_CELLULAR_3G\",\n")
          .append("            \"memoryTotalKbytes\": \"8000000\",\n")
          .append("            \"mainAppWebInfo\": {\n")
          .append("                \"graftUrl\": \"https://www.youtube.com/@onehour_English\",\n")
          .append("                \"pwaInstallabilityStatus\": \"PWA_INSTALLABILITY_STATUS_CAN_BE_INSTALLED\",\n")
          .append("                \"webDisplayMode\": \"WEB_DISPLAY_MODE_BROWSER\",\n")
          .append("                \"isWebNativeShareAvailable\": true\n")
          .append("            }\n")
          .append("        },\n")
          .append("        \"user\": {\n")
          .append("            \"lockedSafetyMode\": false\n")
          .append("        },\n")
          .append("        \"request\": {\n")
          .append("            \"useSsl\": true,\n")
          .append("            \"internalExperimentFlags\": [],\n")
          .append("            \"consistencyTokenJars\": []\n")
          .append("        },\n")
          .append("        \"clickTracking\": {\n")
          .append("            \"clickTrackingParams\": \"CB0Quy8YACITCPCux-2Cv4oDFXBbegUdwIEJzQ==\"\n")
          .append("        },\n")
          .append("        \"adSignalsInfo\": {\n")
          .append("            \"params\": [\n")
          .append("                {\n")
          .append("                    \"key\": \"dt\",\n")
          .append("                    \"value\": \"1734995742856\"\n")
          .append("                },\n")
          .append("                {\n")
          .append("                    \"key\": \"flash\",\n")
          .append("                    \"value\": \"0\"\n")
          .append("                },\n")
          .append("                {\n")
          .append("                    \"key\": \"frm\",\n")
          .append("                    \"value\": \"0\"\n")
          .append("                },\n")
          .append("                {\n")
          .append("                    \"key\": \"u_tz\",\n")
          .append("                    \"value\": \"480\"\n")
          .append("                },\n")
          .append("                {\n")
          .append("                    \"key\": \"u_his\",\n")
          .append("                    \"value\": \"3\"\n")
          .append("                },\n")
          .append("                {\n")
          .append("                    \"key\": \"u_h\",\n")
          .append("                    \"value\": \"1440\"\n")
          .append("                },\n")
          .append("                {\n")
          .append("                    \"key\": \"u_w\",\n")
          .append("                    \"value\": \"2560\"\n")
          .append("                },\n")
          .append("                {\n")
          .append("                    \"key\": \"u_ah\",\n")
          .append("                    \"value\": \"1416\"\n")
          .append("                },\n")
          .append("                {\n")
          .append("                    \"key\": \"u_aw\",\n")
          .append("                    \"value\": \"2560\"\n")
          .append("                },\n")
          .append("                {\n")
          .append("                    \"key\": \"u_cd\",\n")
          .append("                    \"value\": \"24\"\n")
          .append("                },\n")
          .append("                {\n")
          .append("                    \"key\": \"bc\",\n")
          .append("                    \"value\": \"31\"\n")
          .append("                },\n")
          .append("                {\n")
          .append("                    \"key\": \"bih\",\n")
          .append("                    \"value\": \"1035\"\n")
          .append("                },\n")
          .append("                {\n")
          .append("                    \"key\": \"biw\",\n")
          .append("                    \"value\": \"1276\"\n")
          .append("                },\n")
          .append("                {\n")
          .append("                    \"key\": \"brdim\",\n")
          .append("                    \"value\": \"0,24,0,24,2560,24,2560,1416,1289,1035\"\n")
          .append("                },\n")
          .append("                {\n")
          .append("                    \"key\": \"vis\",\n")
          .append("                    \"value\": \"1\"\n")
          .append("                },\n")
          .append("                {\n")
          .append("                    \"key\": \"wgl\",\n")
          .append("                    \"value\": \"true\"\n")
          .append("                },\n")
          .append("                {\n")
          .append("                    \"key\": \"ca_type\",\n")
          .append("                    \"value\": \"image\"\n")
          .append("                }\n")
          .append("            ]\n")
          .append("        }\n")
          .append("    }\n")
          .append("}")
          .toString();

    public static void callYoutubeApi() {
        try {
            // 设置代理（如果需要）
//            HttpUtil.createProxy("127.0.0.1", 7890);
            String proxyHost = "localhost";
            int proxyPort = 7890;
            HttpRequest httpRequest = HttpUtil.createPost(URL).timeout(10000);
            httpRequest.setProxy(new Proxy(
              Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)));

            // 创建请求对象
//            HttpRequest request = HttpUtil.createRequest(METHOD, URL)
            httpRequest.headerMap(HEADERS, true);
            httpRequest.body(PAYLOAD);

            // 发送请求并获取响应
            HttpResponse response = httpRequest.execute();
            if (response.isOk()) {
                String result = response.body();
                // 将结果保存为JSON文件
                FileUtil.writeString(result, "D:\\0003_油管\\description_response.json", CharsetUtil.CHARSET_UTF_8);
                log.info("接口调用成功，结果已保存到description_response.json文件。");
            } else {
                log.error("接口调用失败，状态码：{}", response.getStatus());
            }
        } catch (Exception e) {
            log.error("接口调用出错：", e);
        }
    }

    public static void main(String[] args) {
        callYoutubeApi();
    }
}
