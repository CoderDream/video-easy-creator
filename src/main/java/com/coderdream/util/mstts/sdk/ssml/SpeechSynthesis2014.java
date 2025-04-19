package com.coderdream.util.mstts.sdk.ssml;

import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.mstts.MsttsAudioUtil;

/**
 * <pre>
 *   <a href="https://learn.microsoft.com/en-us/azure/ai-services/speech-service/speech-synthesis-markup-voice">使用 SSML 自定义语音和声音</a>
 *
 * Adjust prosody  调整韵律
 * You can use the prosody element to specify changes to pitch, contour, range,
 * rate, and volume for the text to speech output.
 * The prosody element can contain text and the following elements:
 * audio, break, p, phoneme, prosody, say-as, sub, and s.
 * 您可以使用 prosody 元素指定对文本到语音输出的音高、轮廓、范围、速率和音量的更改。
 * prosody 元素可以包含 text 和以下元素：
 * audio、break、p、phoneme、prosody、say-as、sub 和 s。
 *
 * Because prosodic attribute values can vary over a wide range,
 * the speech recognizer interprets the assigned values as a suggestion of what the actual prosodic values of the selected voice should be. Text to speech limits or substitutes values that aren't supported. Examples of unsupported values are a pitch of 1 MHz or a volume of 120.
 * 由于韵律属性值可能在很宽的范围内变化，因此语音识别器将分配的值解释为所选语音的实际韵律值的建议。
 * Text to speech 限制或替换不支持的值。不支持的值的示例包括 1 MHz 的间距或 120 的音量。
 * Change speaking rate example
 *
 * Audio examples  音频示例
 * For information about the supported values for attributes of the audio element, see Add recorded audio.
 * 有关 audio 元素属性支持的值的信息，请参阅添加录制的音频 。
 *
 * This SSML snippet illustrates how to use src attribute to insert audio from two .wav files.
 * 此 SSML 代码段说明了如何使用 src 属性插入来自两个 .wav 文件的音频。
 * </pre>
 */
public class SpeechSynthesis2014 {

  public static void main(String[] args) {
    String index = "2014";
    MsttsAudioUtil.genAudioFileWithIndex(CdConstants.SPEECH_KEY_EAST_US,
      CdConstants.SPEECH_REGION_EASTUS, index);
  }
}
