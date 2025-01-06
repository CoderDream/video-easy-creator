package com.coderdream.util.sentence;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class StanfordSentenceSplitter1 {

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
    // 创建属性对象
    Properties props = new Properties();
    // 设置句子分割器，这里也可以设置其他的分析器，例如分词器
    props.setProperty("annotators", "tokenize,ssplit");
    // 根据属性对象，构建CoreNLP对象
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    // 创建 CoreDocument 对象
    CoreDocument document = new CoreDocument(text);
    // 对 CoreDocument 对象进行句子分析
    pipeline.annotate(document);
    // 获取句子列表
    List<CoreSentence> coreSentences = document.sentences();
    // 将 CoreSentence 对象转化为 String 并且放入list 返回
    List<String> sentences = new ArrayList<>();
    for (CoreSentence coreSentence : coreSentences) {
      sentences.add(coreSentence.text());
    }
    return sentences;
  }
}
