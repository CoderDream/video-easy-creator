package com.coderdream.util.sqlite;

import com.coderdream.entity.WordEntity;
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
  @Test
  void initTableData() {
    String tableName = "C06_雅思词汇正序版";
   SQLiteUtil.initTableData(tableName);
  }

  @Test
  void initData() {
    // 创建数据库目录（如果不存在）
    SQLiteUtil.initData();
  }


  @Test
  void findWordsInSummaryTable() {

    // 假设你有一个包含 300 个单词的 List<String>
    List<String> wordsToFind = Arrays.asList("able", "about",      "brain's" /* ... 其他单词 ... */);

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
}
