package com.coderdream.entity;

import lombok.Data;

import java.util.List;

@Data
public class GptRequest {

    private String model;
    private List<Message> messages;
    private Double temperature;

    @Data
    public static class Message {
        private String role;
        private String content;
    }
}
