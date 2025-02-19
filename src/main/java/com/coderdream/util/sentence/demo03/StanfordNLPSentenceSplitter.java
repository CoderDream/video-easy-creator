package com.coderdream.util.sentence.demo03;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import java.util.ArrayList;
import java.util.List;

public class StanfordNLPSentenceSplitter {
    public static List<String> splitIntoSentences(String paragraph) {
        List<String> sentences = new ArrayList<>();
        Document doc = new Document(paragraph);
        for (Sentence sentence : doc.sentences()) {
            sentences.add(sentence.text());
        }
        return sentences;
    }

    public static void main(String[] args) {
        String paragraph = "你好！今天是个好日子。我们去公园吧！";
        List<String> sentences = StanfordNLPSentenceSplitter.splitIntoSentences(paragraph);
        for (String sentence : sentences) {
            System.out.println(sentence);
        }
    }
}
