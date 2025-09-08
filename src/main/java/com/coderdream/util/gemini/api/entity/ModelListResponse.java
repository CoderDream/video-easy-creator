package com.coderdream.util.gemini.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelListResponse {
    private List<Model> models;
    // Getters and Setters
    public List<Model> getModels() { return models; }
    public void setModels(List<Model> models) { this.models = models; }
}
