package com.coderdream.util.mstts.sdkdemo;

import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.proxy.OperatingSystem;
import com.microsoft.cognitiveservices.speech.CancellationReason;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisCancellationDetails;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * <pre>
 *     设置口音 所有神经语音都是多语言的，并且能流利地使用自己的语言和英语。
 *     例如，如果英语输入文本是“I'm excited to try text to speech”，
 *     并且您选择了 es-ES-ElviraNeural，则文本将以带有西班牙口音的英语朗读。
 *  </pre>
 */
public class SpeechSynthesis02 {

  // This example requires environment variables named "SPEECH_KEY" and "SPEECH_REGION"
  private static String speechKey = "50vWNO4RVL41CEjbkm4aT5c8VPBjO1XNQOMWEYgX3IrrKQn37XTTJQQJ99BCACYeBjFXJ3w3AAAYACOGjmML";//  CdConstants.SPEECH_KEY_EAST_US;// System.getenv("SPEECH_KEY");
  private static String speechRegion = "eastus";// "eastasia";// System.getenv("SPEECH_REGION");

  public static void main(String[] args)
    throws InterruptedException, ExecutionException {
    SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechKey,
      speechRegion);

    // Set either the `SpeechSynthesisVoiceName` or `SpeechSynthesisLanguage`.

    speechConfig.setSpeechSynthesisLanguage("en-US");
    speechConfig.setSpeechSynthesisVoiceName(
      "en-US-AvaMultilingualNeural"); // zh-CN-XiaochenNeural

    SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig);

    // Get text from the console and synthesize to the default speaker.
    System.out.println("Enter some text that you want to speak >");
//        String text = "英文加中文配音，每次半小時，增强你的英文听力。";// new Scanner(System.in).nextLine();
    String text = "I usually go to bed at ten, but last night I went to bed at eleven.";// new Scanner(System.in).nextLine();
//        if (text.isEmpty())
//        {
//            return;
//        }

    SpeechSynthesisResult speechSynthesisResult = speechSynthesizer.SpeakTextAsync(
      text).get();

    if (speechSynthesisResult.getReason()
      == ResultReason.SynthesizingAudioCompleted) {
      System.out.println(
        "Speech synthesized to speaker for text [" + text + "]");
    } else if (speechSynthesisResult.getReason() == ResultReason.Canceled) {
      SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails.fromResult(
        speechSynthesisResult);
      System.out.println("CANCELED: Reason=" + cancellation.getReason());

      if (cancellation.getReason() == CancellationReason.Error) {
        System.out.println(
          "CANCELED: ErrorCode=" + cancellation.getErrorCode());
        System.out.println(
          "CANCELED: ErrorDetails=" + cancellation.getErrorDetails());
        System.out.println(
          "CANCELED: Did you set the speech resource key and region values?");
      }
    }

    System.exit(0);
  }
}
