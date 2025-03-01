package com.coderdream.util.sentence.demo03;

import cn.hutool.core.io.FileUtil;
import com.coderdream.util.cd.CdStringUtil;
import com.coderdream.util.sentence.demo04.ChineseSentenceSplitter;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StanfordNLPSentenceSplitter {
    public static List<String> splitIntoSentences(String paragraph) {
        List<String> sentences = new ArrayList<>();
        Document doc = new Document(paragraph);
        for (Sentence sentence : doc.sentences()) {
            sentences.add(sentence.text());
        }
        return sentences;
    }

    public static List<String> splitIntoShortSentences(String paragraph) {
        List<String> shortSentences = new ArrayList<>();
        List<String> sentences = StanfordNLPSentenceSplitter.splitIntoSentences(paragraph);
        for (String sentence : sentences) {
            log.info("sentence: {}", sentence);
            shortSentences.addAll(ChineseSentenceSplitter.split(sentence));
        }
        return shortSentences;
    }

    public static List<String> splitIntoShortSentencesFromFile(String bookNameWithPath) {
        List<String> stringList = FileUtil.readLines(bookNameWithPath, StandardCharsets.UTF_8);
        StringBuilder paragraph = new StringBuilder();
        for (String line : stringList) {
            paragraph.append(CdStringUtil.ensureEndsWithChinesePunctuation(line));
        }
        return splitIntoShortSentences(paragraph.toString());
    }

    public static void main(String[] args) {
        String paragraph = "你好！今天是个好日子。我们去公园吧！";
        List<String> sentences = StanfordNLPSentenceSplitter.splitIntoSentences(paragraph);
        for (String sentence : sentences) {
            System.out.println(sentence);
        }
    }
}
