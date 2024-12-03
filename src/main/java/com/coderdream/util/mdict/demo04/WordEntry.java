package com.coderdream.util.mdict.demo04;

import java.util.List;

public class WordEntry {
    private String title; // 标题
    private String explanation; // 解释
    private List<ExampleSentence> exampleSentences; // 例句列表

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public List<ExampleSentence> getExampleSentences() {
        return exampleSentences;
    }

    public void setExampleSentences(List<ExampleSentence> exampleSentences) {
        this.exampleSentences = exampleSentences;
    }
}
