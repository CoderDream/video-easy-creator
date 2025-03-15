package com.coderdream.util.gemini;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.coderdream.entity.VocInfo;
import com.coderdream.util.CommonUtil;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdDateTimeUtils;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.cd.CdTextUtil;
import com.coderdream.util.process.ListSplitterStream;
import com.coderdream.util.sentence.demo1.SentenceParser;
import com.coderdream.vo.SentenceVO;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class VocUtil {

  public static String V1 = "下面是CEFR的词汇表，这里是3列，给加上第4列，中文释义，例如单词ago，返回 adv. 以前，从前；adj. 以前的；第5列，用单词造句，最好用柯林斯词典中的例句，选稍微复杂一些的句子，记得是造句，不是给一段词组，如果柯林斯没有适合的例句，你就自己造句；第6列，整个例句的音标，不是单个单词的音标，第7列，句子的中文含义，记得把例句中的词汇单词和对应的单词中文加粗，返回格式为 Markdown 表格；返回只需要markdown中的内容，不要其他内容：";
  public static String V2 = "headword\tpos\tCEFR";

  public static void main(String[] args) {
    processVoc("D:\\0000\\CEFR\\octanove-vocabulary-profile-c1c2-1.0.txt");
  }

  public static void processVoc(String fileName) {
    long startTime = System.currentTimeMillis();
    List<String> vocStrList = FileUtil.readLines(fileName,
      StandardCharsets.UTF_8);
    List<List<String>> vocStrListSplit = ListSplitterStream.splitList(
      vocStrList, 50);
    int i = 0;
    for (List<String> vocStrListSplitItem : vocStrListSplit) {
      String content = String.join("\n", vocStrListSplitItem);
      i++;
      String indexStr = String.format("%03d", i);
      String fileName2 =
        "D:\\0000\\CEFR\\c1c2_gemini_output_" + indexStr + ".md";
      if (CdFileUtil.isFileEmpty(fileName2)) {

        String fi = GeminiApiClient.generateContent(
          V1 + "\n" + V2 + "\n" + content);
//     log.info("{}", fi);
        FileUtil.writeUtf8String(fi, fileName2);
      } else {
        log.info("{} 已存在，跳过。", fileName2);
      }
    }
    long endTime = System.currentTimeMillis();
    long elapsedTime = endTime - startTime;
    System.out.println("Elapsed time: " + elapsedTime + " ms");
    log.info("Elapsed time: {} ms", elapsedTime);
    log.error("耗时{}。", CdDateTimeUtils.genMessage(elapsedTime));
  }
}
