//package com.coderdream.config;
//
//import io.micrometer.observation.Observation;
//import io.micrometer.observation.Observation.Scope;
//import io.micrometer.observation.ObservationRegistry;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.function.Function;
//
//// 假设这些类已经导入
//import org.springframework.ai.chat.model.ChatResponse;
//import org.springframework.ai.chat.prompt.Prompt;
//import org.springframework.ai.model.function.FunctionCallback;
//import org.springframework.ai.model.function.FunctionCallbackContext;
//import org.springframework.ai.ollama.*;
//import org.springframework.ai.ollama.api.OllamaApi;
//import org.springframework.ai.ollama.api.OllamaModel;
//import org.springframework.ai.ollama.api.OllamaOptions;
//import org.springframework.ai.ollama.management.ModelManagementOptions;
//
//public class OllamaModelCreator {
//
//    public static void main(String[] args) {
//
//        // 创建 OllamaApi 对象
//        OllamaApi ollamaApi = new OllamaApi();
//
//        // 创建 OllamaOptions 对象
//        OllamaOptions defaultOptions = OllamaOptions.create()
//                .withModel(OllamaModel.LLAMA3_1)
//                .withTemperature(0.9);
//
//        // 创建 FunctionCallbackContext 对象
//        FunctionCallbackContext functionCallbackContext = new FunctionCallbackContext();
//
//        // 创建 List<FunctionCallback> 对象
//        List<FunctionCallback> toolFunctionCallbacks = new ArrayList<>();
//        toolFunctionCallbacks.add(new FunctionCallback() {
//
//            /**
//             * @return Returns the Function name. Unique within the model.
//             */
//            @Override
//            public String getName() {
//                return "";
//            }
//
//            /**
//             * @return Returns the function description. This description is
//             * used by the model do decide if the function should be called
//             * or not.
//             */
//            @Override
//            public String getDescription() {
//                return "";
//            }
//
//            /**
//             * @return Returns the JSON schema of the function input type.
//             */
//            @Override
//            public String getInputTypeSchema() {
//                return "";
//            }
//
//            /**
//             * Called when a model detects and triggers a function call. The
//             * model is responsible to pass the function arguments in the
//             * pre-configured JSON schema format.
//             *
//             * @param functionInput JSON string with the function arguments
//             *                      to be passed to the function. The
//             *                      arguments are defined as JSON schema
//             *                      usually registered with the model.
//             * @return String containing the function call response.
//             */
//            @Override
//            public String call(String functionInput) {
//                return "";
//            }
//        });
//
//        // 创建 ObservationRegistry 对象
//        ObservationRegistry observationRegistry = new ObservationRegistry() {
//            @Override
//            public Observation getCurrentObservation() {
//                return null;
//            }
//
//            @Override
//            public Scope getCurrentObservationScope() {
//                return null;
//            }
//
//            @Override
//            public void setCurrentObservationScope(Scope scope) {
//
//            }
//
//            @Override
//            public ObservationConfig observationConfig() {
//                return null;
//            }
//        };
//
//        // 创建 ModelManagementOptions 对象
//        ModelManagementOptions modelManagementOptions = new ModelManagementOptions();
//
//        // 创建 OllamaChatModel 对象
//        OllamaChatModel chatModel = new OllamaChatModel(
//                ollamaApi,
//                defaultOptions,
//                functionCallbackContext,
//                toolFunctionCallbacks,
//                observationRegistry,
//                modelManagementOptions
//        );
//
//        // 使用 chatModel 对象进行后续操作
//        // 比如：调用模型
//        Prompt prompt = new Prompt("Generate the names of 5 famous pirates.");
//        ChatResponse response = chatModel.call(prompt);
//        System.out.println(response); // .getMessage()
//    }
//}
