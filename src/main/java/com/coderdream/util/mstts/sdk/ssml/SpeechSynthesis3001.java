package com.coderdream.util.mstts.sdk.ssml;

import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.mstts.MsttsAudioUtil;
import java.util.Arrays;
import java.util.List;

/**
 * <pre>
 *   <a href="https://learn.microsoft.com/en-us/azure/ai-services/speech-service/speech-synthesis-markup-pronunciation">使用 SSML 的发音</a>
 *
 * </pre>
 */
public class SpeechSynthesis3001 {

  public static void main(String[] args) {
    List<String> ssmlList = Arrays.asList(
      "3001","3002", "3003", "3004", "3005");

    for (String index : ssmlList) {
      MsttsAudioUtil.genAudioFileWithIndex(CdConstants.SPEECH_KEY_EAST_US,
        CdConstants.SPEECH_REGION_EASTUS, index);
    }
  }
}
