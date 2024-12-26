package com.coderdream.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

public class ProxyTest {

    public static void main(String[] args) {
        try {
            // 要访问的URL
            URL url = new URL("https://www.google.com");

            // 创建代理对象
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 7890)); // 本地代理

            // 创建连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);

            // 设置请求方法
            connection.setRequestMethod("GET");

            // 连接超时时间
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // 发起请求
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // 读取响应
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                System.out.println("Response Content: " + content.toString());
            } else {
                System.out.println("Failed to fetch the content.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
