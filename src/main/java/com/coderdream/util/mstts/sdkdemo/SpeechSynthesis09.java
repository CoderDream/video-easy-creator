package com.coderdream.util.mstts.sdkdemo;

import com.microsoft.cognitiveservices.speech.CancellationReason;
import com.microsoft.cognitiveservices.speech.PropertyId;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisCancellationDetails;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;
import java.util.concurrent.ExecutionException;

/**
 * <pre>
 *   <a href="https://learn.microsoft.com/en-us/azure/ai-services/speech-service/how-to-speech-synthesis?tabs=browserjs%2Cterminal&pivots=programming-language-java#use-a-custom-endpoint">使用自定义终端节点</a>
 * Use a custom endpoint  使用自定义终端节点
 * </pre>
 */
public class SpeechSynthesis09 {

  // This example requires environment variables named "SPEECH_KEY" and "SPEECH_REGION"
  private static String speechKey = "50vWNO4RVL41CEjbkm4aT5c8VPBjO1XNQOMWEYgX3IrrKQn37XTTJQQJ99BCACYeBjFXJ3w3AAAYACOGjmML";//  CdConstants.SPEECH_KEY_EAST_US;// System.getenv("SPEECH_KEY");
  private static String speechRegion = "eastus";// "eastasia";// System.getenv("SPEECH_REGION");

  public static void main(String[] args)
    throws InterruptedException, ExecutionException {
    SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechKey,
      speechRegion);
    // Required for WordBoundary event sentences.
    speechConfig.setProperty(PropertyId.SpeechServiceResponse_RequestSentenceBoundary, "true");

    String speechSynthesisVoiceName = "en-US-AvaMultilingualNeural";

    String ssml = String.format("<speak version='1.0' xml:lang='en-US' xmlns='http://www.w3.org/2001/10/synthesis' xmlns:mstts='http://www.w3.org/2001/mstts'>"
      .concat(String.format("<voice name='%s'>", speechSynthesisVoiceName))
      .concat("<mstts:viseme type='redlips_front'/>")
      .concat("The rainbow has seven colors: <bookmark mark='colors_list_begin'/>Red, orange, yellow, green, blue, indigo, and violet.<bookmark mark='colors_list_end'/>.")
      .concat("</voice>")
      .concat("</speak>"));

    SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig);
    {
      // Subscribe to events

      speechSynthesizer.BookmarkReached.addEventListener((o, e) -> {
        System.out.println("BookmarkReached event:");
        System.out.println("\tAudioOffset: " + ((e.getAudioOffset() + 5000) / 10000) + "ms");
        System.out.println("\tText: " + e.getText());
      });

      speechSynthesizer.SynthesisCanceled.addEventListener((o, e) -> {
        System.out.println("SynthesisCanceled event");
      });

      speechSynthesizer.SynthesisCompleted.addEventListener((o, e) -> {
        SpeechSynthesisResult result = e.getResult();
        byte[] audioData = result.getAudioData();
        System.out.println("SynthesisCompleted event:");
        System.out.println("\tAudioData: " + audioData.length + " bytes");
        System.out.println("\tAudioDuration: " + result.getAudioDuration());
        result.close();
      });

      speechSynthesizer.SynthesisStarted.addEventListener((o, e) -> {
        System.out.println("SynthesisStarted event");
      });

      speechSynthesizer.Synthesizing.addEventListener((o, e) -> {
        SpeechSynthesisResult result = e.getResult();
        byte[] audioData = result.getAudioData();
        System.out.println("Synthesizing event:");
        System.out.println("\tAudioData: " + audioData.length + " bytes");
        result.close();
      });

      speechSynthesizer.VisemeReceived.addEventListener((o, e) -> {
        System.out.println("VisemeReceived event:");
        System.out.println("\tAudioOffset: " + ((e.getAudioOffset() + 5000) / 10000) + "ms");
        System.out.println("\tVisemeId: " + e.getVisemeId());
      });

      speechSynthesizer.WordBoundary.addEventListener((o, e) -> {
        System.out.println("WordBoundary event:");
        System.out.println("\tBoundaryType: " + e.getBoundaryType());
        System.out.println("\tAudioOffset: " + ((e.getAudioOffset() + 5000) / 10000) + "ms");
        System.out.println("\tDuration: " + e.getDuration());
        System.out.println("\tText: " + e.getText());
        System.out.println("\tTextOffset: " + e.getTextOffset());
        System.out.println("\tWordLength: " + e.getWordLength());
      });

      // Synthesize the SSML
      System.out.println("SSML to synthesize:");
      System.out.println(ssml);
      SpeechSynthesisResult speechSynthesisResult = speechSynthesizer.SpeakSsmlAsync(ssml).get();

      if (speechSynthesisResult.getReason() == ResultReason.SynthesizingAudioCompleted) {
        System.out.println("SynthesizingAudioCompleted result");
      }
      else if (speechSynthesisResult.getReason() == ResultReason.Canceled) {
        SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails.fromResult(speechSynthesisResult);
        System.out.println("CANCELED: Reason=" + cancellation.getReason());

        if (cancellation.getReason() == CancellationReason.Error) {
          System.out.println("CANCELED: ErrorCode=" + cancellation.getErrorCode());
          System.out.println("CANCELED: ErrorDetails=" + cancellation.getErrorDetails());
          System.out.println("CANCELED: Did you set the speech resource key and region values?");
        }
      }
    }
    speechSynthesizer.close();

    System.exit(0);
  }

}
