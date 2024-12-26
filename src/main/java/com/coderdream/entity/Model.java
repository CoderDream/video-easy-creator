package com.coderdream.entity;

import lombok.Data;

@Data
public class Model {
    private String modelName;
    private String modelId;
    private String inputCost;
    private String outputCost;
    private String contextTokens;
    private String moderation;
}
