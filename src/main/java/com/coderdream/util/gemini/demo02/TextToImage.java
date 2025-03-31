//package com.coderdream.util.gemini.demo02;
//
//import com.coderdream.util.cd.CdConstants;
//import com.google.cloud.aiplatform.v1.EndpointName;
//import com.google.cloud.aiplatform.v1.PredictResponse;
////import com.google.cloud.aiplatform.v1.PredictionServiceClient;
////import com.google.cloud.aiplatform.v1.PredictionServiceSettings;
//import com.google.cloud.vertexai.api.PredictionServiceClient;
//import com.google.cloud.vertexai.api.PredictionServiceSettings;
//import com.google.protobuf.ByteString;
//import com.google.protobuf.ListValue;
//import com.google.protobuf.Struct;
//import com.google.protobuf.Value;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class TextToImage {
//
//    private static final String PROJECT_ID = "你的 Google Cloud 项目 ID"; // 替换为你的项目ID
//    private static final String LOCATION = "us-central1"; // 替换为模型部署的区域 (例如：us-central1)
//    private static final String ENDPOINT_ID = "你的端点ID";  // 替换为你的 Vertex AI 端点ID
//    private static final String API_KEY = CdConstants.GEMINI_API_KEY; // 替换为你的 API Key
//    private static final String IMAGE_SAVE_PATH = "generated_image.png"; //  图像保存的路径
//
//    public static void main(String[] args) throws IOException {
//        String prompt = "A beautiful sunset over a calm ocean";  // 你想要生成的图像的描述
//
//        try {
//            generateImage(prompt);
//            System.out.println("Image generated and saved to: " + IMAGE_SAVE_PATH);
//        } catch (Exception e) {
//            System.err.println("Error generating image: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//
//    public static void generateImage(String prompt) throws IOException {
//        // 1. 配置 API 连接
//        PredictionServiceSettings settings = PredictionServiceSettings.newBuilder()
//                .setEndpoint(LOCATION + "-aiplatform.googleapis.com:443") // Vertex AI endpoint
//                .build();
//
//        // 2. 创建 PredictionServiceClient
//        try (PredictionServiceClient predictionServiceClient = PredictionServiceClient.create(settings)) {
//
//            // 3. 构建请求数据 (Instance)
//            ListValue.Builder instanceBuilder = ListValue.newBuilder();
//            Struct.Builder structBuilder = Struct.newBuilder();
//            structBuilder.putFields("prompt", Value.newBuilder().setStringValue(prompt).build());
//            instanceBuilder.addValues(Value.newBuilder().setStructValue(structBuilder.build()).build());
//
//            // 4. 构建参数 (Parameters)
//            Map<String, Value> parameters = new HashMap<>();
//            parameters.put("sampleCount", Value.newBuilder().setNumberValue(1).build()); // 生成图像的数量
//            parameters.put("seed", Value.newBuilder().setNumberValue(0).build());  // 设置随机种子以获得可重复的结果
//            parameters.put("width", Value.newBuilder().setNumberValue(1024).build()); // 图像宽度
//            parameters.put("height", Value.newBuilder().setNumberValue(1024).build());// 图像高度
//
//            // 5. 调用 PredictionService
//            EndpointName endpointName = EndpointName.of(PROJECT_ID, LOCATION, ENDPOINT_ID);
//
//            PredictResponse response = predictionServiceClient.predict(
//                    endpointName,
//                    instanceBuilder.build(),
//                    Struct.newBuilder().putAllFields(parameters).build()
//            );
//
//            // 6. 处理结果
//            List<Value> predictions = response.getPredictionsList();
//            if (predictions != null && !predictions.isEmpty()) {
//                Struct predictionStruct = predictions.get(0).getStructValue();
//                ListValue images = predictionStruct.getFieldsMap().get("images").getListValue(); // 获取图像数据
//
//                if (images != null && !images.getValuesList().isEmpty()) {
//                    String base64Image = images.getValuesList().get(0).getStringValue(); // 获取 base64 编码的图像
//
//                    // 7. 解码 Base64 并保存图像
//                    byte[] decodedImageBytes = java.util.Base64.getDecoder().decode(base64Image);
//                    Path path = Paths.get(IMAGE_SAVE_PATH);
//                    Files.write(path, decodedImageBytes);
//                } else {
//                    System.err.println("No images found in the prediction.");
//                }
//            } else {
//                System.err.println("No predictions received.");
//            }
//
//        } catch (Exception e) {
//            System.err.println("Error during prediction: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//}
