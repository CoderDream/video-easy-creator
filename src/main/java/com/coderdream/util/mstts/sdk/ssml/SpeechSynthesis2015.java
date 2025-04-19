package com.coderdream.util.mstts.sdk.ssml;

import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.mstts.MsttsAudioUtil;

/**
 * <pre>
 *   <a href="https://learn.microsoft.com/en-us/azure/ai-services/speech-service/speech-synthesis-markup-voice">使用 SSML 自定义语音和声音</a>
 *
 *
 * Add background audio  添加背景音频
 * You can use the mstts:backgroundaudio element to add background audio to your SSML documents or mix an audio file with text to speech. With mstts:backgroundaudio, you can loop an audio file in the background, fade in at the beginning of text to speech, and fade out at the end of text to speech.
 * 您可以使用 mstts：backgroundaudio 元素将背景音频添加到 SSML 文档，或将音频文件与文本到语音混合。使用 mstts：backgroundaudio，您可以在后台循环播放音频文件，在文本到语音的开头淡入，在文本到语音的结尾淡出。
 *
 * If the background audio provided is shorter than the text to speech or the fade out, it loops. If it's longer than the text to speech, it stops when the fade out is finished.
 * 如果提供的背景音频短于文本到语音转换或淡出，则会循环播放。如果它比 text to speech 长，它会在淡出完成后停止。
 *
 * Only one background audio file is allowed per SSML document. You can intersperse audio tags within the voice element to add more audio to your SSML document.
 * 每个 SSML 文档只允许一个背景音频文件。您可以在 voice 元素中穿插 audio 标签，以向 SSML 文档添加更多音频。
 * </pre>
 */
public class SpeechSynthesis2015 {

  public static void main(String[] args) {
    String index = "2015";
    MsttsAudioUtil.genAudioFileWithIndex(CdConstants.SPEECH_KEY_EAST_US,
      CdConstants.SPEECH_REGION_EASTUS, index);
  }
}
