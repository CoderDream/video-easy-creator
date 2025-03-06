package com.coderdream.util.chatgpt;

import com.coderdream.entity.GptRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) throws IOException {
        String json = "{\n" +
                "   \"model\": \"gpt-4o-mini\",\n" +
                "   \"messages\": [\n" +
                "        {\n" +
                "            \"role\": \"user\",\n" +
                "            \"content\": \"Say this is a test!\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"temperature\": 0.7\n" +
                "}";

        ObjectMapper objectMapper = new ObjectMapper();
        GptRequest gptRequest = objectMapper.readValue(json, GptRequest.class);

        System.out.println(gptRequest); // 打印整个对象
        System.out.println("Model: " + gptRequest.getModel()); // 打印 model 属性
    }
}
