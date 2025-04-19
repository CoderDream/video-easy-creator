package com.coderdream.util.mstts.sdk.ssml;

import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.mstts.MsttsAudioUtil;

/**
 * <pre>
 *   <a href="https://learn.microsoft.com/en-us/azure/ai-services/speech-service/speech-synthesis-markup-voice">使用 SSML 自定义语音和声音</a>
 *
 * You must specify en-US as the default language within the speak element, whether or not the language is adjusted elsewhere. In this example, the primary language for en-US-AvaMultilingualNeural is en-US.
 * 您必须在 speak 元素中指定 en-US 作为默认语言，无论该语言是否在其他位置进行了调整。在此示例中，en-US-AvaMultilingualNeural 的主要语言是 en-US。
 *
 * This SSML snippet shows how to use <lang xml:lang> to speak de-DE with the en-US-AvaMultilingualNeural neural voice.
 * 此 SSML 代码片段演示如何使用 <lang xml：lang> 通过 en-US-AvaMultilingualNeural 神经语音说出 de-DE。
 * </pre>
 */
public class SpeechSynthesis2007 {

  public static void main(String[] args) {
    String index = "2007";
    MsttsAudioUtil.genAudioFileWithIndex(CdConstants.SPEECH_KEY_EAST_US,
      CdConstants.SPEECH_REGION_EASTUS, index);
  }
}
