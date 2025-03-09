package com.coderdream.util.ollama;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class OllamaResponse {

    private String model;
    @JsonProperty("created_at")
    private String createdAt;
    private String response;
    private boolean done;
    @JsonProperty("done_reason")
    private String doneReason;
    private List<Integer> context;
    @JsonProperty("total_duration")
    private long totalDuration;
    @JsonProperty("load_duration")
    private long loadDuration;
    @JsonProperty("prompt_eval_count")
    private int promptEvalCount;
    @JsonProperty("prompt_eval_duration")
    private long promptEvalDuration;
    @JsonProperty("eval_count")
    private int evalCount;
    @JsonProperty("eval_duration")
    private long evalDuration;
}
