package com.coderdream.util.gemini.demo01;

import com.google.gson.annotations.SerializedName;
import java.util.List;

// 顶层类
@lombok.Data
public class ResponseBean {

  private List<Candidate> candidates;
  private UsageMetadata usageMetadata;
  private String modelVersion;


  // Candidate 类
  @lombok.Data
  public static class Candidate {

    private Content content;
    private String finishReason;
    private double avgLogprobs;
  }

  // Content 类
  @lombok.Data
  public static class Content {

    private List<Part> parts;
    private String role;
  }

  // Part 类
  @lombok.Data
  public static class Part {

    private String text;
  }

  // UsageMetadata 类
  @lombok.Data
  public static class UsageMetadata {

    private int promptTokenCount;
    private int candidatesTokenCount;
    private int totalTokenCount;

    @SerializedName("promptTokensDetails")
    private List<TokenDetails> promptTokensDetails;

    @SerializedName("candidatesTokensDetails")
    private List<TokenDetails> candidatesTokensDetails;
  }

  // TokenDetails 类
  @lombok.Data
  public static class TokenDetails {

    private String modality;
    private int tokenCount;
  }
}
