package com.coderdream.util.youtube;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.HashMap;
import java.util.Map;

public class HutoolProxyPostExample {
    public static void main(String[] args) {
        String proxyHost = "localhost";
        int proxyPort = 7890;

//        Proxy proxy = new Proxy(proxyHost, proxyPort);
//
//        // 设置POST请求参数
//        Map<String, Object> paramMap = new HashMap<>();
//        paramMap.put("key1", "value1");
//        paramMap.put("key2", "value2");
//
//        // 发送HTTP POST请求并添加代理
//        HttpResponse response = HttpRequest.post("http://example.com")
//              .setProxy(proxy)
//              .form(paramMap)
//              .execute();
        HttpRequest httpRequest = HttpUtil.createGet("https://www.youtube.com").timeout(10000);
        httpRequest.setProxy(new Proxy(Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)));
        String body = httpRequest.execute().body();
        System.out.println(body);
    }
}
