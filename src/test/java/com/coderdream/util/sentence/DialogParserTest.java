package com.coderdream.util.sentence;

import static org.junit.jupiter.api.Assertions.*;

import com.coderdream.entity.DialogSingleEntity;
import org.junit.jupiter.api.Test;

class DialogParserTest {

  @Test
  void parseDialogFile() {
  }

  @Test
  void parseLine() {
    assertThrows(NullPointerException.class, () -> DialogParser.parseLine(""));
          assertNull(DialogParser.parseLine("abc"));
          assertNull(DialogParser.parseLine("abc:"));
          assertNull(DialogParser.parseLine(":abc"));
          assertNull(DialogParser.parseLine("abc:def ghi"));
          assertNull(DialogParser.parseLine("abc def:"));
          assertNull(DialogParser.parseLine("abc def:ghi"));
          assertNull(DialogParser.parseLine("abc def:  ghi"));
          DialogSingleEntity dialogEntity = DialogParser.parseLine("张三:你好");
          assertNotNull(dialogEntity);
          assertEquals("张三", dialogEntity.getHostEn());
          assertEquals("张三", dialogEntity.getHostCn());
          assertEquals("你好", dialogEntity.getContentCn());
  }
}
