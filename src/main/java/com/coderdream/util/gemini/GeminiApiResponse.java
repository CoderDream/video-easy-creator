package com.coderdream.util.gemini;

import lombok.Data;
import java.util.List;

/**
 * 谷歌 Gemini API 的返回结果实体类
 */
@Data
public class GeminiApiResponse {

    /**
     * 候选结果列表
     */
    private List<Candidate> candidates;

    /**
     * 使用元数据
     */
    private UsageMetadata usageMetadata;

    /**
     * 模型版本号
     */
    private String modelVersion;

    /**
     * 候选结果类
     */
    @Data
    public static class Candidate {
        /**
         * 内容信息
         */
        private Content content;

        /**
         * 完成原因
         */
        private String finishReason;

        /**
         * 平均日志概率
         */
        private double avgLogprobs;
    }

    /**
     * 内容类
     */
    @Data
    public static class Content {
        /**
         * 内容部分列表
         */
        private List<Part> parts;

        /**
         * 角色类型，例如模型角色
         */
        private String role;
    }

    /**
     * 内容部分类
     */
    @Data
    public static class Part {
        /**
         * 文本内容
         */
        private String text;
    }

    /**
     * 使用元数据类
     */
    @Data
    public static class UsageMetadata {
        /**
         * 提示符令牌计数
         */
        private int promptTokenCount;

        /**
         * 候选结果令牌计数
         */
        private int candidatesTokenCount;

        /**
         * 总令牌计数
         */
        private int totalTokenCount;
    }
}
