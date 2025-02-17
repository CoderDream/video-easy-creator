//package com.coderdream.util.alimt;
////
////import com.aliyun.tea.*;
//////import com.aliyun.alimt20181012.*;
//////import com.aliyun.alimt20181012.models.*;
//////import com.aliyun.teaopenapi.*;
//////import com.aliyun.teaopenapi.models.*;
//////import com.aliyun.darabonba.env.*;
//////import com.aliyun.teaconsole.*;
////
////public class Sample {
////    public static Client createClient() throws Exception {
////        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
////                // 将 ALIBABA_CLOUD_ACCESS_KEY_ID 替换为 AccessKey ID。
////                .setAccessKeyId ("ALIBABA_CLOUD_ACCESS_KEY_ID")
////                // 将 ALIBABA_CLOUD_ACCESS_KEY_SECRET 替换为 AccessKey Secret。
////                .setAccessKeySecret ("ALIBABA_CLOUD_ACCESS_KEY_SECRET");
////        // Endpoint 请参考 https://api.aliyun.com/product/alimt
////        config.endpoint = "mt.cn-hangzhou.aliyuncs.com";
////        return new com.aliyun.alimt20181012.Client(config);
////    }
////    public static void main(String[] args_) throws Exception {
////        java.util.List<String> args = java.util.Arrays.asList(args_);
////        com.aliyun.alimt20181012.Client client = Sample.createClient();
////        TranslateGeneralRequest request = new TranslateGeneralRequest()
////                .setFormatType("text")
////                .setSourceLanguage("zh")
////                .setTargetLanguage("en")
////                .setSourceText("你好")
////                .setScene("general");
////        TranslateGeneralResponse response = client.translateGeneral(request);
////        com.aliyun.teaconsole.Client.log(response.body.data.translated);
////    }
//// }
//
//
//import com.aliyun.tea.*;
//import com.aliyun.alimt20181012.*;
//import com.aliyun.alimt20181012.models.*;
//import com.aliyun.teaopenapi.*;
//import com.aliyun.teaopenapi.models.*;
//import com.aliyun.darabonba.env.*;
//import com.aliyun.teaconsole.*;
//
//public class Sample {
//  public static com.aliyun.alimt20181012.Client createClient() throws Exception {
//    com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
//      // 将 ALIBABA_CLOUD_ACCESS_KEY_ID 替换为 AccessKey ID。
//      .setAccessKeyId ("ALIBABA_CLOUD_ACCESS_KEY_ID")
//      // 将 ALIBABA_CLOUD_ACCESS_KEY_SECRET 替换为 AccessKey Secret。
//      .setAccessKeySecret ("ALIBABA_CLOUD_ACCESS_KEY_SECRET");
//    // Endpoint 请参考 https://api.aliyun.com/product/alimt
//    config.endpoint = "mt.cn-hangzhou.aliyuncs.com";
//    return new com.aliyun.alimt20181012.Client(config);
//  }
//  public static void main(String[] args_) throws Exception {
//    java.util.List<String> args = java.util.Arrays.asList(args_);
//    com.aliyun.alimt20181012.Client client = Sample.createClient();
//    TranslateGeneralRequest request = new TranslateGeneralRequest()
//      .setFormatType("text")
//      .setSourceLanguage("zh")
//      .setTargetLanguage("en")
//      .setSourceText("你好")
//      .setScene("general");
//    TranslateGeneralResponse response = client.translateGeneral(request);
//    com.aliyun.teaconsole.Client.log(response.body.data.translated);
//  }
//}
