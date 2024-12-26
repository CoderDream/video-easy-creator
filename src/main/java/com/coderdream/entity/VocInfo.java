package com.coderdream.entity;


import lombok.Data;

/**
 * 单词信息
 * @author CoderDream
 */
@Data
public class VocInfo {
    /**
     * 单词
     */
    private String word;
    /**
     * 英文解释
     */
    private String wordExplainEn;
    /**
     * 中文翻译
     */
    private String wordCn;
    /**
     * 英文解释翻译
     */
    private String wordExplainCn;
    /**
     * 英文例句
     */
    private String sampleSentenceEn;
    /**
     * 中文例句
     */
    private String sampleSentenceCn;
}
