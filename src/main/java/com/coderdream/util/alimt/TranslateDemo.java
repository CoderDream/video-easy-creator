//package com.coderdream.util.alimt;
//
//import com.aliyuncs.DefaultAcsClient;
//import com.aliyuncs.IAcsClient;
//import com.aliyuncs.alimt.model.v20181012.*;
//import com.aliyuncs.exceptions.ClientException;
//import com.aliyuncs.profile.DefaultProfile;
//
//public class TranslateDemo {
//
//    public static void main(String[] args) throws ClientException {
//
//        // 1. 创建 DefaultAcsClient 实例
//        DefaultProfile profile = DefaultProfile.getProfile(
//                "cn-hangzhou", // 你的 Region ID，例如 cn-hangzhou
//                "<your-access-key-id>", // 你的 AccessKey ID
//                "<your-access-key-secret>" // 你的 AccessKey Secret
//        );
//        IAcsClient client = new DefaultAcsClient(profile);
//
//        // 2. 创建 API 请求并设置参数
//        TranslateGeneralRequest request = new TranslateGeneralRequest();
//        request.setFormatType("text");   // 源文本格式：text 或 html
//        request.setSourceLanguage("en"); // 源语言
//        request.setTargetLanguage("zh"); // 目标语言
//        request.setSourceText("Hello, world!"); // 要翻译的文本
//        request.setScene("general");  //翻译场景，如general（通用）、social（社交）、title（标题）、description（描述）等等.
//
//        // 3. 发起请求并处理响应
//        try {
//            TranslateGeneralResponse response = client.getAcsResponse(request);
//            System.out.println(response.getData().getTranslated()); // 输出翻译结果
//        } catch (ClientException e) {
//            System.err.println("Error: " + e.getErrCode());
//            System.err.println("Error Message: " + e.getErrMsg());
//        }
//    }
//}
