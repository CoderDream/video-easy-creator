package com.coderdream.util.gemini.api.entity;

import java.util.List;

public class GenerateContentRequest {
    private List<Content> contents;

    public GenerateContentRequest(List<Content> contents) {
        this.contents = contents;
    }

    // Getters and Setters
    public List<Content> getContents() { return contents; }
    public void setContents(List<Content> contents) { this.contents = contents; }
}
