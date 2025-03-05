package com.coderdream.util.sentence.demo03;

import static org.junit.jupiter.api.Assertions.*;

import cn.hutool.core.io.FileUtil;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.cd.CdStringUtil;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class StanfordNLPSentenceSplitterTest {

  @Test
  void splitIntoSentences() {
    String bookName = "ReadBook_0002";
    String filePath = "D:\\0000\\ReadBook\\";
    String bookNameWithPath =
      filePath + bookName + File.separator + bookName + ".txt";

    List<String> stringList = FileUtil.readLines(bookNameWithPath,
      StandardCharsets.UTF_8);
    StringBuilder paragraph = new StringBuilder();
    for (String line : stringList) {
      paragraph.append(CdStringUtil.ensureEndsWithChinesePunctuation(line));
    }
    List<String> sentences = StanfordNLPSentenceSplitter.splitIntoSentences(
      paragraph.toString());
    for (String sentence : sentences) {
      log.info(sentence);
      assertNotNull(sentence);
    }
  }

  //


  @Test
  void splitIntoShortSentences() {
    String bookName = "ReadBook_0002";
    String filePath = "D:\\0000\\ReadBook\\";
    String bookNameWithPath =
      filePath + bookName + File.separator + bookName + ".txt";

    List<String> stringList = FileUtil.readLines(bookNameWithPath,
      StandardCharsets.UTF_8);
    StringBuilder paragraph = new StringBuilder();
    for (String line : stringList) {
      paragraph.append(CdStringUtil.ensureEndsWithChinesePunctuation(line));
    }
    List<String> sentences = StanfordNLPSentenceSplitter.splitIntoShortSentences(
      paragraph.toString());
    for (String sentence : sentences) {
      log.info(sentence);
      assertNotNull(sentence);
    }
  }

  @Test
  void splitIntoShortSentencesFromFile() {
    String bookName = "ReadBook_0002";
    String filePath = "D:\\0000\\ReadBook\\";
    String bookNameWithPath =
      filePath + bookName + File.separator + bookName + ".txt";
    String bookNameWithPathSrtRaw =
      filePath + bookName + File.separator + bookName + "_srt_raw.txt";

    List<String> sentences = StanfordNLPSentenceSplitter.splitIntoShortSentencesFromFile(
      bookNameWithPath);
    for (String sentence : sentences) {
      log.info(sentence);
      assertNotNull(sentence);
    }
    if (CdFileUtil.isFileEmpty(bookNameWithPathSrtRaw)) {
      CdFileUtil.writeToFile(bookNameWithPathSrtRaw, sentences);
    } else {
      log.info("文件已存在，不再写入");
    }
  }

  @Test
  void splitIntoShortSentencesFromFile_0003() {
    String bookFolderName = "ReadBook_0003";
    String bookName = "ReadBook_000301";
    String filePath = "D:\\0000\\ReadBook\\";
    String bookNameWithPath =
      filePath + bookFolderName + File.separator + bookName + ".txt";
    String bookNameWithPathSrtRaw =
      filePath + bookFolderName + File.separator + bookName + "_srt_raw.txt";

    List<String> sentences = StanfordNLPSentenceSplitter.splitIntoShortSentencesFromFile(
      bookNameWithPath);
    for (String sentence : sentences) {
      log.info(sentence);
      assertNotNull(sentence);
    }
    if (CdFileUtil.isFileEmpty(bookNameWithPathSrtRaw)) {
      CdFileUtil.writeToFile(bookNameWithPathSrtRaw, sentences);
    } else {
      log.info("文件已存在，不再写入");
    }
  }
}
