package com.coderdream.util.mdict;

//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import redis.clients.jedis.Jedis;

public class HttpUtil {


    public static void main(String[] args) {
//POST请求

//        System.out.println(getUUID());
//        for (int i = 0; i <10 ; i++        ) {
//            System.out.println(getCodeFromRedis());
//        }

//        System.out.println(login());

        callApiUserList();
//        System.out.println(getCodeFromRedis());

        // edff192c42db4351b447e4db2c5c04d4
//        String uuid = "edff192c42db4351b447e4db2c5c04d4";
//        System.out.println(getCodeFromRedis(uuid));
    }

    public static String getUUID() {
        String uuid = "";
        String url = "http://localhost:8080/captchaImage";
        String result1 = cn.hutool.http.HttpUtil.get(url);
//        System.out.println(result1);
        JSONObject jsonObject = JSONUtil.parseObj(result1);
        uuid = (String) jsonObject.get("uuid");
        return uuid;
    }


//    public static String getCodeFromRedis() {
//        // Jedis jedis = new Jedis();
//
//        // 创建 Jedis 对象并连接 Redis 服务器
//        Jedis jedis = new Jedis("127.0.0.1", 6379);
//// 验证 Redis 密码
////jedis.auth("000000");
//
////        try {
////            jedis.set("1212", "redis存入去除这么简单");
////        } catch (Exception e) {
////            System.out.println(e);
////            //return R.fail("redis存入失败");
////        }
//        // return R.ok("存入成功");
//
//        String s = null;
//        try {
//            // captcha_codes:1160681134714b0d87d2bfd574297314
//            String uuid = getUUID();
//            System.out.println(uuid);
//            String verifyKey = "captcha_codes:" + uuid;
//            s = jedis.get(verifyKey);
//        } catch (Exception e) {
//            System.out.println(e);
//            System.out.println("redis取出失败");
//        }
////        System.out.println(s + " 获取成功");
//
//        return s.replaceAll("\"", "");
//    }


//    public static String getCodeFromRedis(String uuid) {
////        Jedis jedis = new Jedis();
//        // 指定IP地址、端口号和密码
//        String ip = "39.98.244.217";
//        int port = 6379;
//        String password = "Hxgz2019";
//
//        // 初始化Jedis对象
//        Jedis jedis = new Jedis(ip, port);
//
//        // 设置密码（如果Redis服务器配置了密码）
//        jedis.auth(password);
//
////        try {
////            jedis.set("1212", "redis存入去除这么简单");
////        } catch (Exception e) {
////            System.out.println(e);
////            //return R.fail("redis存入失败");
////        }
//        // return R.ok("存入成功");
//
//        String s = null;
//        try {
//            // captcha_codes:1160681134714b0d87d2bfd574297314
////            String uuid = getUUID();
//            String verifyKey = "captcha_codes:" + uuid;
//            s = jedis.get(verifyKey);
//        } catch (Exception e) {
//            System.out.println(e);
//            System.out.println("redis取出失败");
//        }
////        System.out.println(s + " 获取成功");
//
//        return s == null ? "" : s.replaceAll("\"", "");
//    }

    public static String login() {
        String uuid = getUUID();
        String code = "";// getCodeFromRedis(uuid);

        if (StrUtil.isBlank(code)) {
            return "验证码已过期";
        }

        Integer codeValue = Integer.parseInt(code);
//        System.out.println("codeValue: " + codeValue);
//
//        System.out.println("code: " + code);

        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("code", code);
        paramMap.put("uuid", uuid);
        paramMap.put("username", "admin");
        paramMap.put("password", "admin123");
        String url = "http://localhost:8080/login";
        String result1 = cn.hutool.http.HttpUtil.post(url, paramMap);
//        System.out.println(result1);
        JSONObject json = new JSONObject(paramMap);

//        System.out.println(json.toStringPretty());
        String sendPost = sendPost(url, json);
        JSONObject jsonObject = new JSONObject(sendPost);
        return (String) jsonObject.get("token");
    }

    private static final Logger logger = LoggerFactory.getLogger(cn.hutool.http.HttpUtil.class);

    /**
     * 发送post请求
     *
     * @param json
     * @param url
     * @return
     */
    public static String sendPost( String url, JSONObject json) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json");
//        post.addHeader("Authorization", "Basic YWRtaW46");
        String result;
        try {
            StringEntity s = new StringEntity(json.toString(), "utf-8");
            s.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post.setEntity(s);
            // 发送请求
            HttpResponse httpResponse = client.execute(post);
            // 获取响应输入流
            InputStream inStream = httpResponse.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                inStream, "utf-8"));
            StringBuilder strber = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                strber.append(line + "\n");
            }
            inStream.close();
            result = strber.toString();
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                System.out.println("请求服务器成功，做相应处理");
            } else {
                System.out.println("请求服务端失败");
            }
        } catch (Exception e) {
            logger.error("请求异常：" + e.getMessage());
            throw new RuntimeException(e);
        }
        System.out.println(result);
        return result;
    }

    /**
     * 发送 POST 请求
     *
     * @param url     请求的 URL
     * @param jsonStr 请求的 JSON 字符串
     * @return 响应结果
     */
    public static String sendPost(String url, String jsonStr) {
        // 创建 HttpClient 实例
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json");

        String result;
        try {
            // 设置请求实体
            StringEntity stringEntity = new StringEntity(jsonStr, "utf-8");
            post.setEntity(stringEntity);

            // 发送请求并获取响应
            HttpResponse httpResponse = client.execute(post);

            // 读取响应内容
            try (InputStream inStream = httpResponse.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"))) {

                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line).append("\n");
                }
                result = responseBuilder.toString();
            }

            // 检查响应状态码
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                System.out.println("请求服务器成功，做相应处理");
            } else {
                System.out.println("请求服务端失败");
            }
        } catch (Exception e) {
            logger.error("请求异常：" + e.getMessage());
            throw new RuntimeException(e);
        }

        System.out.println(result);
        return result;
    }



    /**
     * 发送post请求
     *
     * @param json
     * @param url
     * @return
     */
    public static String sendPost( String url, Map<String, Object> json) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json");
//        post.addHeader("Authorization", "Basic YWRtaW46");
        String result;
        try {
            StringEntity s = new StringEntity(json.toString(), "utf-8");
            s.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post.setEntity(s);
            // 发送请求
            HttpResponse httpResponse = client.execute(post);
            // 获取响应输入流
            InputStream inStream = httpResponse.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                inStream, "utf-8"));
            StringBuilder strber = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                strber.append(line + "\n");
            }
            inStream.close();
            result = strber.toString();
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                System.out.println("请求服务器成功，做相应处理");
            } else {
                System.out.println("请求服务端失败");
            }
        } catch (Exception e) {
            logger.error("请求异常：" + e.getMessage());
            throw new RuntimeException(e);
        }
        System.out.println(result);
        return result;
    }


    /**
     * 发送post请求
     *
     * @param json
     * @param URL
     * @return
     */
    public static String sendPostWithToken(JSONObject json, String URL) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(URL);
        String token = HttpUtil.login();
        post.setHeader("Authorization", "Bearer " + token);
        post.setHeader("Content-Type", "application/json");
//        post.addHeader("Authorization", "Basic YWRtaW46");
        String result;
        try {
            StringEntity s = new StringEntity(json.toString(), "utf-8");
            s.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post.setEntity(s);
            // 发送请求
            HttpResponse httpResponse = client.execute(post);
            // 获取响应输入流
            InputStream inStream = httpResponse.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                inStream, "utf-8"));
            StringBuilder strber = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                strber.append(line + "\n");
            }
            inStream.close();
            result = strber.toString();
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                System.out.println("请求服务器成功，做相应处理");
            } else {
                System.out.println("请求服务端失败");
            }
        } catch (Exception e) {
            logger.error("请求异常：" + e.getMessage());
            throw new RuntimeException(e);
        }
        System.out.println(result);
        return result;
    }

    public static String sendGet(String token, String URL) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(URL);
        get.setHeader("Authorization", "Bearer " + token);
        get.setHeader("Content-Type", "application/json");
//        post.addHeader("Authorization", "Basic YWRtaW46");
        String result;
        try {

            // 发送请求
            HttpResponse httpResponse = client.execute(get);
            // 获取响应输入流
            InputStream inStream = httpResponse.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                inStream, "utf-8"));
            StringBuilder strber = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                strber.append(line + "\n");
            }
            inStream.close();
            result = strber.toString();
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                System.out.println("请求服务器成功，做相应处理");
            } else {
                System.out.println("请求服务端失败");
            }
        } catch (Exception e) {
            logger.error("请求异常：" + e.getMessage());
            throw new RuntimeException(e);
        }
//        System.out.println(result);
        return result;
    }

    public static String sendGet(String URL) {
        String token = HttpUtil.login();
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(URL);
        get.setHeader("Authorization", "Bearer " + token);
        get.setHeader("Content-Type", "application/json");
//        post.addHeader("Authorization", "Basic YWRtaW46");
        String result;
        try {

            // 发送请求
            HttpResponse httpResponse = client.execute(get);
            // 获取响应输入流
            InputStream inStream = httpResponse.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                inStream, "utf-8"));
            StringBuilder strber = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                strber.append(line + "\n");
            }
            inStream.close();
            result = strber.toString();
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                System.out.println("请求服务器成功，做相应处理");
            } else {
                System.out.println("请求服务端失败");
            }
        } catch (Exception e) {
            logger.error("请求异常：" + e.getMessage());
            throw new RuntimeException(e);
        }
//        System.out.println(result);
        return result;
    }

    public static String callApiUserList() {
        String uuid = "";
        String url = "http://localhost:8080/system/user/list";
        String token = login();
        String result1 = sendGet(token, url);
//        System.out.println(result1);

        JSONObject jsonObject = new JSONObject(result1);
        System.out.println(jsonObject.toStringPretty());

        return result1;
    }

    public static String callApiDeptTree() {
        String uuid = "";
        String url = "http://localhost:8080/system/user/deptTree";
        String token = login();
        String result1 = sendGet(token, url);
//        System.out.println(result1);

        JSONObject jsonObject = new JSONObject(result1);
        System.out.println(jsonObject.toStringPretty());

        return result1;
    }


}

