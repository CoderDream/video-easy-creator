package com.coderdream.util.sentence.demo03;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SentenceSplitterWithJDK {

  public static List<String> splitIntoSentences(String text) {
    List<String> sentences = new ArrayList<>();
    BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.ENGLISH);
    iterator.setText(text);

    int start = iterator.first();
    for (int end = iterator.next(); end != BreakIterator.DONE;
      start = end, end = iterator.next()) {
      sentences.add(text.substring(start, end).trim());
    }
    return sentences;
  }

  public static void main(String[] args) {
    String paragraph = "Hello world! This is a test. How are you doing today?";
    List<String> sentences = SentenceSplitterWithJDK.splitIntoSentences(paragraph);
    sentences.forEach(System.out::println);
  }
}
