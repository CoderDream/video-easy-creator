package com.coderdream.util.sentence;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class StanfordSentenceSplitter4 {

  public static void main(String[] args) {
    String text = "这是一个段落。“它包含多个句子。”句子的结尾通常有标点符号！还有一些句子没有标点？";
    text = "Let me see. 'Wanted: manager for upand-coming firm. Must have good organizational skills. Experience a plus. Please contact Susan Lee.' Oh, I don't know... ";
    text = "\"I'm going to the store,\" she said.He exclaimed, \"That's amazing!\"Have you read \"The Lord of the Rings\"?My favorite song is \"Bohemian Rhapsody.\"She called him a \"genius,\" but I think he's just lucky.The \"expert\" didn't know what he was talking about.He said it was \"totally awesome.\"Let's go \"hang out\" later.The string variable was set to \"Hello World!\".The command is \"ls -l\".";

    List<String> sentences = splitSentences(text);
    sentences.forEach(System.out::println);
    //    System.out.println("-------------------");
    //    String text2 = "这是一个段落：“它包含多个句子”。句子的结尾通常有标点符号！还有一些句子没有标点？";
    //    List<String> sentences2 = splitSentences(text2);
    //    sentences2.forEach(System.out::println);
  }

  public static List<String> splitSentences(String text) {
    Properties props = new Properties();
    props.setProperty("annotators", "tokenize,ssplit");
    props.setProperty("ssplit.newlineIsSentenceBreak", "always");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    CoreDocument document = new CoreDocument(text);
    pipeline.annotate(document);
    List<CoreSentence> coreSentences = document.sentences();
    List<String> sentences = new ArrayList<>();
     StringBuilder currentSentence = new StringBuilder();
      for (int i = 0; i < coreSentences.size(); i++) {
          CoreSentence sentence = coreSentences.get(i);
          if (currentSentence.length() > 0) {
            currentSentence.append(" ");
          }
          currentSentence.append(sentence.text());
          List<CoreLabel> tokens = sentence.tokens();
          if (!tokens.isEmpty()) {
            CoreLabel lastToken = tokens.get(tokens.size() - 1);
              String lastTokenText = lastToken.originalText();
             if (lastTokenText.matches("[.?!。？！]")  || lastTokenText.equals("\"") || lastTokenText.equals("”")) {
                  sentences.add(currentSentence.toString().trim());
                currentSentence.setLength(0);
               }
              if(i == coreSentences.size() - 1 && currentSentence.length() > 0){
                  sentences.add(currentSentence.toString().trim());
              }
           }

      }
    return sentences;
  }
}
