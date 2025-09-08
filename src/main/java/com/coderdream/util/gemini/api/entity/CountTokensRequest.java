package com.coderdream.util.gemini.api.entity;

import java.util.List;

// Request Body POJOs
public class CountTokensRequest {
    private List<Content> contents;
    public CountTokensRequest(List<Content> contents) { this.contents = contents; }
    public List<Content> getContents() { return contents; }
    public void setContents(List<Content> contents) { this.contents = contents; }
}


