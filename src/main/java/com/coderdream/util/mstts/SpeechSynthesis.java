package com.coderdream.util.mstts;

import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.*;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class SpeechSynthesis {
    // This example requires environment variables named "SPEECH_KEY" and "SPEECH_REGION"
    private static String speechKey =  "AkKJ1ZPVFFPvujmwI5N1ik9J3mcl9dfK2y13gMRK4BFEurFcUPfTJQQJ99BAAC3pKaRXJ3w3AAAYACOGDyWB";// System.getenv("SPEECH_KEY");
    private static String speechRegion = "eastasia";// System.getenv("SPEECH_REGION");

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechKey, speechRegion);

//        speechConfig.setSpeechSynthesisVoiceName("en-US-AvaMultilingualNeural"); // zh-CN-XiaochenNeural

        speechConfig.setSpeechSynthesisVoiceName("zh-CN-XiaochenNeural"); //

        SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig);

        // Get text from the console and synthesize to the default speaker.
        System.out.println("Enter some text that you want to speak >");
        String text = "英文加中文配音，每次半小時，增强你的英文听力。";// new Scanner(System.in).nextLine();
//        if (text.isEmpty())
//        {
//            return;
//        }

        SpeechSynthesisResult speechSynthesisResult = speechSynthesizer.SpeakTextAsync(text).get();

        if (speechSynthesisResult.getReason() == ResultReason.SynthesizingAudioCompleted) {
            System.out.println("Speech synthesized to speaker for text [" + text + "]");
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

        System.exit(0);
    }
}
