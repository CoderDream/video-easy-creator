package com.coderdream.util.mdict;

import java.util.List;
import lombok.Data;

@Data
public class HtmlContentBean {
    /**
     * 单词
     */
    private String word;
    /**
     * 评分
     */
    private String ratingText;
    /**
     *
     */
    private List<String> captions;
    /**
     *  句子
     */
    private List<String> sentences;
    /**
     * 翻译
     */
    private List<String> translations;
    /**
     * 原始html字符串
     */
    private String rawHtml;
}
