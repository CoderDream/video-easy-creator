package com.coderdream.util.audio;

import static org.junit.jupiter.api.Assertions.*;

import com.coderdream.util.proxy.OperatingSystem;
import java.io.File;
import java.nio.file.Files;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class AudioMergerSingleBatchTest {

  @Test
  void mergeWavFile() {
    String inputDir =
      OperatingSystem.getBaseFolder() + "bgmusic"
        + File.separator + "head" + File.separator;
    String outputFileDir =  OperatingSystem.getBaseFolder()  + "bgmusic"
      + File.separator;
    String outputFileName = "head.wav";
    File outputFile = AudioMergerSingleBatch.mergeWavFile(inputDir, outputFileDir,
      outputFileName);
    // Verify that the merged file exists
    assert outputFile != null;
    assertTrue(Files.exists(outputFile.toPath()), "The merged file should exist");
  }
}
