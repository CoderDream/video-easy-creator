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
public class SpeechSynthesis4001 {

  public static void main(String[] args) {
    List<String> ssmlList = Arrays.asList(
      "5001","5002","5003","5004");// ,"4002"

    for (String index : ssmlList) {
      MsttsAudioUtil.genAudioFileWithIndex(CdConstants.SPEECH_KEY_EAST_US,
        CdConstants.SPEECH_REGION_EASTUS, index);
    }
  }
}
