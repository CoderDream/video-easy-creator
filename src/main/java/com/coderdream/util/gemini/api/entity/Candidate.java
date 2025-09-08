package com.coderdream.util.gemini.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Candidate {
    private Content content;
    private String finishReason;

    // Getters and Setters
    public Content getContent() { return content; }
    public void setContent(Content content) { this.content = content; }
    public String getFinishReason() { return finishReason; }
    public void setFinishReason(String finishReason) { this.finishReason = finishReason; }
}
