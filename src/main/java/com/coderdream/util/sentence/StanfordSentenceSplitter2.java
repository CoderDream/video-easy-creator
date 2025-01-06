package com.coderdream.util.sentence;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class StanfordSentenceSplitter2 {

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
        props.setProperty("ssplit.boundaryTokenRegex", "[.?!。？！]+(?=[\\s]|$)(?<!\\d\\.\\d)"); // 自定义句子分割器
         props.setProperty("ssplit.newlineIsSentenceBreak", "always"); // 将换行符也认为是句子分割
       StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        CoreDocument document = new CoreDocument(text);
        pipeline.annotate(document);
        List<CoreSentence> coreSentences = document.sentences();
        List<String> sentences = new ArrayList<>();
        for (CoreSentence coreSentence : coreSentences) {
           sentences.add(coreSentence.text().trim());
        }
        return sentences;
    }
}
