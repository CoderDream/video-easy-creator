package com.coderdream.util.mstts.sdk.ssml;

import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.mstts.MsttsAudioUtil;

/**
 * <pre>
 *   <a href="https://learn.microsoft.com/en-us/azure/ai-services/speech-service/speech-synthesis-markup-structure#single-voice-example#bookmark-examples">书签示例</a>
 *  Bookmark examples  书签示例
 * As an example, you might want to know the time offset of each flower word in the following snippet:
 * 例如，您可能想知道以下代码片段中每个花词的时间偏移量：
 * </pre>
 */
public class SpeechSynthesis1007 {

  public static void main(String[] args) {
    String index = "1007";
    MsttsAudioUtil.genAudioFileWithIndex(CdConstants.SPEECH_KEY_EAST_US,
      CdConstants.SPEECH_REGION_EASTUS, index);
  }
}
