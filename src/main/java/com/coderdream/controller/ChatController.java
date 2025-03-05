//package com.coderdream.controller;
//
////import jakarta.annotation.Resource;
////import org.springframework.ai.chat.ChatResponse;
////import org.springframework.ai.chat.prompt.Prompt;
////import org.springframework.ai.openai.OpenAiChatClient;
////import org.springframework.ai.openai.OpenAiChatOptions;
////import org.springframework.web.bind.annotation.RequestMapping;
////import org.springframework.web.bind.annotation.RequestParam;
////import org.springframework.web.bind.annotation.RestController;
//
//import com.coderdream.config.OpenAiChatModelUtil;
//import jakarta.annotation.Resource;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.ai.chat.model.ChatResponse;
//import org.springframework.ai.chat.prompt.Prompt;
//import org.springframework.ai.openai.OpenAiChatModel;
//import org.springframework.ai.openai.OpenAiChatOptions;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import reactor.core.publisher.Flux;
//
///**
// * @author CoderDream
// */
//@RestController
//@Slf4j
//public class ChatController {
//
//    /**
//     * spring-ai 自动装配的，可以直接注入使用
//     */
//    @Resource
//    private OpenAiChatModel openAiChatModel;
//
//    /**
//     * v 调用OpenAI的接口
//     *
//     * @param msg 我们提的问题
//     * @return
//     */
//    @RequestMapping(value = "/ai/chat")
//    public String chat(@RequestParam(value = "msg") String msg) {
//        log.info("called chat : {}", msg);
//        // 先设置本地代理
//        System.setProperty("https.proxyHost", "127.0.0.1");
//        System.setProperty("https.proxyPort", "7890");
////        String baseUrl="https://api.chatanywhere.tech";
////        String apiKey="sk-F7HULAHnwwcfm8bzoa3ZLAPwZNXbf9GpM0rAsXyLHSH3IHjE";
////        OpenAiApi openAiApi = new OpenAiApi(baseUrl,apiKey);
////        OpenAiChatModel openAiChatModel1 = new OpenAiChatModel(openAiApi);
//        String called = OpenAiChatModelUtil.getInstance().call(msg);
//        System.out.println(called);
//        log.info("called: {}", called);
//        return called;
//    }
//
//    /**
//     * 调用OpenAI的接口
//     *
//     * @param msg 我们提的问题
//     * @return
//     */
//    @RequestMapping(value = "/ai/chat2")
//    public Object chat2(@RequestParam(value = "msg") String msg) {
//        OpenAiChatModel openAiChatModel1 = OpenAiChatModelUtil.getInstance();
//        ChatResponse chatResponse = openAiChatModel1.call(new Prompt(msg));
//        log.info("called: {}", chatResponse.toString());
//        return chatResponse.getResult().getOutput().getContent();
//    }
//
//    /**
//     * 调用OpenAI的接口
//     *
//     * @param msg 我们提的问题
//     * @return
//     */
//    @RequestMapping(value = "/ai/chat3")
//    public Object chat3(@RequestParam(value = "msg") String msg) {
//        //可选参数在配置文件中配置了，在代码中也配置了，那么以代码的配置为准，也就是代码的配置会覆盖掉配置文件中的配置
//        ChatResponse chatResponse = OpenAiChatModelUtil.getInstance().call(new Prompt(msg, OpenAiChatOptions.builder()
//            .withModel("gpt-3.5-turbo") //gpt的版本，32k是参数量 gpt-4-32k gpt-4o-2024-05-13
//            .withTemperature(0.4) //温度越高，回答得比较有创新性，但是准确率会下降，温度越低，回答的准确率会更好
//            .build()));
//        return chatResponse.getResult().getOutput().getContent();
//    }
//
//    /**
//     * 调用OpenAI的接口
//     *
//     * @param msg 我们提的问题
//     * @return
//     */
//    @RequestMapping(value = "/ai/chat4")
//    public Object chat4(@RequestParam(value = "msg") String msg) {
//        //可选参数在配置文件中配置了，在代码中也配置了，那么以代码的配置为准，也就是代码的配置会覆盖掉配置文件中的配置
//        Flux<ChatResponse> flux = OpenAiChatModelUtil.getInstance().stream(new Prompt(msg, OpenAiChatOptions.builder()
//            .withModel("gpt-3.5-turbo") ////.withModel("gpt-4-32k") //gpt的版本，32k是参数量
//            .withTemperature(0.4) //温度越高，回答得比较有创新性，但是准确率会下降，温度越低，回答的准确率会更好
//            .build()));
//
//        flux.toStream().forEach(chatResponse -> {
//            System.out.println(chatResponse.getResult().getOutput().getContent());
//        });
//        return flux.collectList(); //数据的序列，一序列的数据，一个一个的数据返回
//    }
//
//    @RequestMapping(value = "/test")
//    public String test(@RequestParam(value = "msg") String msg) {
//        log.info("test chat : {}", msg);
////        // 先设置本地代理
//////        System.setProperty("https.proxyHost", "127.0.0.1");
//////        System.setProperty("https.proxyPort", "7890");
////        String called = openAiChatModel.call(msg);
////        System.out.println(called);
////        log.info("called: {}", called);
//        return "OK";
//    }
//
//}
