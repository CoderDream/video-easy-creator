package com.coderdream.util.mstts.sdkdemo;

import com.coderdream.util.proxy.OperatingSystem;
import com.microsoft.cognitiveservices.speech.AudioDataStream;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisOutputFormat;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;
import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * <pre>
 *   <a href="https://learn.microsoft.com/en-us/azure/ai-services/speech-service/how-to-speech-synthesis?tabs=browserjs%2Cterminal&pivots=programming-language-java#customize-audio-format">Customize audio format</a>
 * Customize audio format  自定义音频格式
 * </pre>
 */
public class SpeechSynthesis06 {
    // This example requires environment variables named "SPEECH_KEY" and "SPEECH_REGION"
    private static String speechKey = "50vWNO4RVL41CEjbkm4aT5c8VPBjO1XNQOMWEYgX3IrrKQn37XTTJQQJ99BCACYeBjFXJ3w3AAAYACOGjmML";//  CdConstants.SPEECH_KEY_EAST_US;// System.getenv("SPEECH_KEY");
    private static String speechRegion =  "eastus" ;// "eastasia";// System.getenv("SPEECH_REGION");

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechKey, speechRegion);
//        AudioConfig audioConfig = AudioConfig.fromDefaultSpeakerOutput();
//        AudioConfig audioConfig = AudioConfig.fromWavFileOutput(
//          OperatingSystem.getBaseFolder() + File.separator + "mstts" + File.separator + "file001.wav");

           // Set either the `SpeechSynthesisVoiceName` or `SpeechSynthesisLanguage`.
//        speechConfig.setSpeechSynthesisLanguage("en-US");
//        speechConfig.setSpeechSynthesisVoiceName("en-US-AvaMultilingualNeural"); // zh-CN-XiaochenNeural

        // set the output format
        speechConfig.setSpeechSynthesisOutputFormat(SpeechSynthesisOutputFormat.Riff24Khz16BitMonoPcm);

        SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig, null);
        SpeechSynthesisResult result = speechSynthesizer.SpeakText("I'm excited to try text to speech");
        AudioDataStream stream = AudioDataStream.fromResult(result);
        stream.saveToWavFile(
          OperatingSystem.getBaseFolder() + File.separator + "mstts" + File.separator + "file002.wav");

//        speechConfig.setSpeechSynthesisVoiceName("zh-CN-XiaochenNeural"); //
//        50vWNO4RVL41CEjbkm4aT5c8VPBjO1XNQOMWEYgX3IrrKQn37XTTJQQJ99BCACYeBjFXJ3w3AAAYACOGjmML
//        SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig,
//          audioConfig);
//        speechSynthesizer.SpeakText("I'm excited to try text to speech");

//        SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig, null);
//
//        SpeechSynthesisResult result = speechSynthesizer.SpeakText("I'm excited to try text to speech");
//        AudioDataStream stream = AudioDataStream.fromResult(result);
//        System.out.println("stream: " + stream);
//        System.out.println("stream.getStatus: " + stream.getStatus());
//
//        // Get text from the console and synthesize to the default speaker.
//        System.out.println("Enter some text that you want to speak >");
////        String text = "英文加中文配音，每次半小時，增强你的英文听力。";// new Scanner(System.in).nextLine();
//        String text = "I usually go to bed at ten, but last night I went to bed at eleven.";// new Scanner(System.in).nextLine();
////        if (text.isEmpty())
////        {
////            return;
////        }
//
//        SpeechSynthesisResult speechSynthesisResult = speechSynthesizer.SpeakTextAsync(text).get();
//
//        if (speechSynthesisResult.getReason() == ResultReason.SynthesizingAudioCompleted) {
//            System.out.println("Speech synthesized to speaker for text [" + text + "]");
//        }
//        else if (speechSynthesisResult.getReason() == ResultReason.Canceled) {
//            SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails.fromResult(speechSynthesisResult);
//            System.out.println("CANCELED: Reason=" + cancellation.getReason());
//
//            if (cancellation.getReason() == CancellationReason.Error) {
//                System.out.println("CANCELED: ErrorCode=" + cancellation.getErrorCode());
//                System.out.println("CANCELED: ErrorDetails=" + cancellation.getErrorDetails());
//                System.out.println("CANCELED: Did you set the speech resource key and region values?");
//            }
//        }
//
//        System.exit(0);
    }
}
