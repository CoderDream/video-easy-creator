package com.coderdream.util.pic;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class HighResImageGeneratorTest {

  @Test
  void generateImages() {
    String fileName = "CampingInvitation_02";
    List<File> files = HighResImageVideoUtil5.generateImages(fileName);
    for (File file : files) {
      log.info("file: {}", file.getAbsolutePath());
      assertTrue(file.exists());
    }
  }
}
