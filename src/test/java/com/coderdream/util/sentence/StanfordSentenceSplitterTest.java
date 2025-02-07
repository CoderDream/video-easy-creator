package com.coderdream.util.sentence;

import static org.junit.jupiter.api.Assertions.*;

import cn.hutool.core.io.FileUtil;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

class StanfordSentenceSplitterTest {

  @Test
  void splitSentences() {

    String text = "好的。这是您的火腿三明治，芥末，番茄酱和咖啡。三明治每份10美元，咖啡3.50美元，这两样13.50美元，还有20%的服务费。总计16.20美元。这是您的账单。";
//    text = "Let me see. 'Wanted: manager for upand-coming firm. Must have good organizational skills. Experience a plus. Please contact Susan Lee.' Oh, I don't know... ";
//    text = "\"I'm going to the store,\" she said.He exclaimed, \"That's amazing!\"Have you read \"The Lord of the Rings\"?My favorite song is \"Bohemian Rhapsody.\"She called him a \"genius,\" but I think he's just lucky.The \"expert\" didn't know what he was talking about.He said it was \"totally awesome.\"Let's go \"hang out\" later.The string variable was set to \"Hello World!\".The command is \"ls -l\".";
//    text = "Yes, I will. Thank you, Mr.White. Good-bye. 好的，我会的。谢谢您，怀特先生。再见。";
//
//    text = FileUtil.readString("D:\\0000\\【中英雙語】2025川普就職演講\\【中英雙語】2025川普就職演講.txt", StandardCharsets.UTF_8);
    List<String> sentences = StanfordSentenceSplitter.splitSentences(text);
    sentences.forEach(System.out::println);
  }
}
