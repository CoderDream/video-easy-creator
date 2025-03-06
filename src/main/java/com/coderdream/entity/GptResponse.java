package com.coderdream.entity;

import java.util.List;
import lombok.Data;

@Data
public class GptResponse {

  private List<Choice> choices;
  private Long created;
  private String id;
  private String model;
  private String object;
  private String system_fingerprint;
  private Usage usage;

  @Data
  public static class Choice {

    private String finish_reason;
    private Integer index;
    private Object logprobs; // Can be null
    private Message message;
  }

  @Data
  public static class Message {

    private String content;
    private Object refusal; // Can be null
    private String role;
  }

  @Data
  public static class Usage {

    private Integer completion_tokens;
    private CompletionTokensDetails completion_tokens_details;
    private Integer prompt_tokens;
    private PromptTokensDetails prompt_tokens_details;
    private Integer total_tokens;
  }

  @Data
  public static class CompletionTokensDetails {

    private Integer accepted_prediction_tokens;
    private Integer audio_tokens;
    private Integer reasoning_tokens;
    private Integer rejected_prediction_tokens;
  }

  @Data
  public static class PromptTokensDetails {

    private Integer audio_tokens;
    private Integer cached_tokens;
  }
}
