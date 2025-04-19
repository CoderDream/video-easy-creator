package com.coderdream.util.mstts.sdk.ssml;

import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.mstts.MsttsAudioUtil;

/**
 * <pre>
 *   <a href="https://learn.microsoft.com/en-us/azure/ai-services/speech-service/speech-synthesis-markup-voice">使用 SSML 自定义语音和声音</a>
 *
 * Style and degree example  风格和度数示例
 * You use the mstts:express-as element to express emotions like cheerfulness, empathy, and calm. You can also optimize the voice for different scenarios like customer service, newscast, and voice assistant.
 * 您使用 mstts：express-as 元素来表达快乐、同理心和平静等情绪。您还可以针对不同的场景（如客户服务、新闻广播和语音助手）优化语音。
 *
 * The following SSML example uses the <mstts:express-as> element with a sad style degree of 2.
 * 以下 SSML 示例使用 sad 样式度为 2 的 <mstts：express-as> 元素。
 * </pre>
 */
public class SpeechSynthesis2005 {

  public static void main(String[] args) {
    String index = "2005";
    MsttsAudioUtil.genAudioFileWithIndex(CdConstants.SPEECH_KEY_EAST_US,
      CdConstants.SPEECH_REGION_EASTUS, index);
  }
}
