package com.coderdream.util.mdict;

import java.util.List;

public class HtmlContentBean {
    private String word;
    private String ratingText;
    private List<String> captions;
    private List<String> sentences;
    private List<String> translations;

    // Getters and Setters
    public String getHelloText() {
        return word;
    }

    public void setHelloText(String word) {
        this.word = word;
    }

    public String getRatingText() {
        return ratingText;
    }

    public void setRatingText(String ratingText) {
        this.ratingText = ratingText;
    }

    public List<String> getCaptions() {
        return captions;
    }

    public void setCaptions(List<String> captions) {
        this.captions = captions;
    }

    public List<String> getSentences() {
        return sentences;
    }

    public void setSentences(List<String> sentences) {
        this.sentences = sentences;
    }

    public List<String> getTranslations() {
        return translations;
    }

    public void setTranslations(List<String> translations) {
        this.translations = translations;
    }

    @Override
    public String toString() {
        return "HtmlContentBean{" +
                "word='" + word + '\'' +
                ", ratingText='" + ratingText + '\'' +
                ", captions=" + captions +
                ", sentences=" + sentences +
                ", translations=" + translations +
                '}';
    }
}
