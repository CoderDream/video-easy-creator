package com.coderdream.util.mstts.sdk.ssml;

import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.mstts.MsttsAudioUtil;

/**
 * <pre>
 *   <a href="https://learn.microsoft.com/en-us/azure/ai-services/speech-service/speech-synthesis-markup-voice">使用 SSML 自定义语音和声音</a>
 *  Multiple voices example  多声部示例
 * </pre>
 */
public class SpeechSynthesis2002 {

  public static void main(String[] args) {
    String index = "2002";
    MsttsAudioUtil.genAudioFileWithIndex(CdConstants.SPEECH_KEY_EAST_US,
      CdConstants.SPEECH_REGION_EASTUS, index);
  }
}
