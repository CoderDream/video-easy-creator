package com.coderdream.util.sentence;

import cn.hutool.core.io.FileUtil;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Stack;

public class StanfordSentenceSplitter {

  public static void main(String[] args) {
    String text = "这是一个段落。“它包含多个句子。”句子的结尾通常有标点符号！还有一些句子没有标点？";
    text = "Let me see. 'Wanted: manager for upand-coming firm. Must have good organizational skills. Experience a plus. Please contact Susan Lee.' Oh, I don't know... ";
    text = "\"I'm going to the store,\" she said.He exclaimed, \"That's amazing!\"Have you read \"The Lord of the Rings\"?My favorite song is \"Bohemian Rhapsody.\"She called him a \"genius,\" but I think he's just lucky.The \"expert\" didn't know what he was talking about.He said it was \"totally awesome.\"Let's go \"hang out\" later.The string variable was set to \"Hello World!\".The command is \"ls -l\".";
    text = "Yes, I will. Thank you, Mr.White. Good-bye. 好的，我会的。谢谢您，怀特先生。再见。";

    text = FileUtil.readString("D:\\0000\\【中英雙語】2025川普就職演講\\【中英雙語】2025川普就職演講.txt", StandardCharsets.UTF_8);
    List<String> sentences = StanfordSentenceSplitter.splitSentences(text);
    sentences.forEach(System.out::println);
    FileUtil.writeLines(sentences, "D:\\0000\\【中英雙語】2025川普就職演講\\【中英雙語】2025川普就職演講_split.txt", StandardCharsets.UTF_8);
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
    Stack<String> quoteStack = new Stack<>();

    for (int i = 0; i < coreSentences.size(); i++) {
      CoreSentence sentence = coreSentences.get(i);
      if (currentSentence.length() > 0) {
        currentSentence.append(" ");
      }
      currentSentence.append(sentence.text());

      List<CoreLabel> tokens = sentence.tokens();
      if (!tokens.isEmpty()) {
        for (CoreLabel token : tokens) {
          String tokenText = token.originalText();
          if (tokenText.equals("\"") || tokenText.equals("”")) {
            if (quoteStack.isEmpty()) {
              quoteStack.push(tokenText);
            } else {
              quoteStack.pop();
            }
          }
        }
        CoreLabel lastToken = tokens.get(tokens.size() - 1);
        if (lastToken != null) {
          String lastTokenText = lastToken.originalText();
          if ((lastTokenText.matches("[.?!。？！]") || lastTokenText.equals("\"")
            || lastTokenText.equals("”")) && quoteStack.isEmpty()) {
            sentences.add(currentSentence.toString().trim());
            currentSentence.setLength(0);
          }
        }
      }
      if (i == coreSentences.size() - 1 && currentSentence.length() > 0) {
        sentences.add(currentSentence.toString().trim());
      }
    }

    return sentences;
  }
}
