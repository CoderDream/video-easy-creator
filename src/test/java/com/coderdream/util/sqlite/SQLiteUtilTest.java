package com.coderdream.util.sqlite;

import com.coderdream.entity.WordEntity;
import com.coderdream.util.cd.CdDateTimeUtils;
import com.coderdream.util.process.PreparePublishUtil;
import com.coderdream.util.proxy.OperatingSystem;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class SQLiteUtilTest {

  @Test
  void getAllWords() {
    String tableName = "C01_初中词汇正序版";
    List<WordEntity> allWords = SQLiteUtil.getAllWords(tableName);
    assert allWords != null;
    for (WordEntity word : allWords) {
      log.info(word.toString());
    }
  }

  //
//  @Test
//  void initTableData() {
//    String tableName = "C06_雅思词汇正序版";
//   SQLiteUtil.initTableData(tableName);
//  }
//
//  @Test
//  void initData() {
//    // 创建数据库目录（如果不存在）
//    SQLiteUtil.initData();
//  }


  @Test
  void findWordsInSummaryTable() {

    // 假设你有一个包含 300 个单词的 List<String>
    List<String> wordsToFind = Arrays.asList("able", "about",
      "brain's" /* ... 其他单词 ... */);

// 调用 findWordsInSummaryTable 方法
    List<WordEntity> foundWords = SQLiteUtil.findWordsInSummaryTable(
      wordsToFind);

// 处理查询结果
    if (foundWords != null) {
      for (WordEntity word : foundWords) {
        System.out.println(word.getWord() + ": " + word.getComment());
      }
    }
  }


  @Test
  void importDataFromMarkdown_01() {
    long startTime = System.currentTimeMillis();
    String tableName = "cefr_c1_words";

    String bookFolderName = "CefrEnum";
    String folderPath =
      OperatingSystem.getBaseFolder() + File.separator + bookFolderName;

//    String bookName = "商務英語";
//    String chapterName = "900_cht_name.txt";
    List<String> subFolders = new ArrayList<>();
    int end = 157;//157;
    for (int i = 1; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("gemini_output_" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      // 1. 创建表
      String createTableTime = SQLiteUtil.createTable(tableName);
      System.out.println("创建表耗时: " + createTableTime);

      String markdownFilePath =
        folderPath + File.separator +  subFolder + ".md";
      // 2. 从 Markdown 文件导入数据
      String importDataTime = SQLiteUtil.importDataFromMarkdown(tableName,
        markdownFilePath);
      System.out.println("从 Markdown 文件导入数据耗时: " + importDataTime);
    }
    long endTime = System.currentTimeMillis();
    long elapsedTime = endTime - startTime;
    System.out.println("Elapsed time: " + elapsedTime + " ms");
    log.info("Elapsed time: {} ms", elapsedTime);
    log.error("耗时{}。", CdDateTimeUtils.genMessage(elapsedTime));
  }

  @Test
  void importDataFromMarkdown_02() {
    long startTime = System.currentTimeMillis();
    String tableName = "c1c2";

    String bookFolderName = "CefrEnum";
    String folderPath =
      OperatingSystem.getBaseFolder() + File.separator + bookFolderName;

//    String bookName = "商務英語";
//    String chapterName = "900_cht_name.txt";
    List<String> subFolders = new ArrayList<>();
    int end = 44;//157;
    for (int i = 1; i < end; i++) {
      String dayNumberString = String.format("%03d", i); // 格式化天数序号为3位字符串
      subFolders.add("c1c2_gemini_output_" + dayNumberString);
    }

    for (String subFolder : subFolders) {
      // 1. 创建表
      String createTableTime = SQLiteUtil.createTable(tableName);
      System.out.println("创建表耗时: " + createTableTime);

      String markdownFilePath =
        folderPath + File.separator +  subFolder + ".md";
      // 2. 从 Markdown 文件导入数据
      String importDataTime = SQLiteUtil.importDataFromMarkdown(tableName,
        markdownFilePath);
      System.out.println("从 Markdown 文件导入数据耗时: " + importDataTime);
    }
    long endTime = System.currentTimeMillis();
    long elapsedTime = endTime - startTime;
    System.out.println("Elapsed time: " + elapsedTime + " ms");
    log.info("Elapsed time: {} ms", elapsedTime);
    log.error("耗时{}。", CdDateTimeUtils.genMessage(elapsedTime));
  }
}
