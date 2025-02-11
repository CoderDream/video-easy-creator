package com.coderdream.util.process;

import com.coderdream.entity.Book002ChapterInfoEntity;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class ChapterStringParserTest {

  @Test
  void getChapterInfoEntity() {
    String filePath = "D:\\0000\\EnBook002\\Chapter008\\Chapter008_temp.txt";
    Book002ChapterInfoEntity chapterInfoEntity = ChapterStringParser.genChapterInfoEntity(
      filePath);
//    log.info("chapterInfoEntity: {}", chapterInfoEntity);

    // 打印解析结果 (示例)
    log.info(
      new GsonBuilder().setPrettyPrinting().create().toJson(chapterInfoEntity));
  }
}
