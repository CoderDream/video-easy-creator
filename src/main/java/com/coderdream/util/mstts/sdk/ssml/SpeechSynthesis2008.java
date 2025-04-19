package com.coderdream.util.mstts.sdk.ssml;

import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.mstts.MsttsAudioUtil;

/**
 * <pre>
 *   <a href="https://learn.microsoft.com/en-us/azure/ai-services/speech-service/speech-synthesis-markup-voice">使用 SSML 自定义语音和声音</a>
 *
 * Within the speak element, you can specify multiple languages including en-US for text to speech output.
 * For each adjusted language, the text must match the language and be wrapped in a voice element.
 * This SSML snippet shows how to use <lang xml:lang> to change the speaking languages to es-MX, en-US, and fr-FR.
 * 在 speak 元素中，您可以指定多种语言，包括 en-US 进行文本到语音输出。
 * 对于每种调整后的语言，文本必须与语言匹配，并包含在 voice 元素中。
 * 此 SSML 代码段显示了如何使用 <lang xml：lang> 将朗读语言更改为 es-MX、en-US 和 fr-FR。
 * </pre>
 */
public class SpeechSynthesis2008 {

  public static void main(String[] args) {
    String index = "2008";
    MsttsAudioUtil.genAudioFileWithIndex(CdConstants.SPEECH_KEY_EAST_US,
      CdConstants.SPEECH_REGION_EASTUS, index);
  }
}
