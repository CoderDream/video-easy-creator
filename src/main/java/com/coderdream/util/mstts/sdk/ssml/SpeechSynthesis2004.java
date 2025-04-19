package com.coderdream.util.mstts.sdk.ssml;

import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.mstts.MsttsAudioUtil;

/**
 * <pre>
 *   <a href="https://learn.microsoft.com/en-us/azure/ai-services/speech-service/speech-synthesis-markup-voice">使用 SSML 自定义语音和声音</a>
 *  Audio effect example  音效示例
 * You use the effect attribute to optimize the auditory experience for scenarios
 * such as cars and telecommunications.
 * The following SSML example uses the effect attribute with the configuration in car scenarios.
 * 使用 effect 属性来优化汽车和电信等场景的听觉体验。
 * 以下 SSML 示例在汽车场景中将 effect 属性与配置一起使用。
 * </pre>
 */
public class SpeechSynthesis2004 {

  public static void main(String[] args) {
    String index = "2004";
    MsttsAudioUtil.genAudioFileWithIndex(CdConstants.SPEECH_KEY_EAST_US,
      CdConstants.SPEECH_REGION_EASTUS, index);
  }
}
