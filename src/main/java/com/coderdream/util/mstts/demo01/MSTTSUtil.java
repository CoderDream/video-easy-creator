//package com.coderdream.util.mstts.demo01;
//
//import com.microsoft.cognitiveservices.speech.*;
//import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
//
//import java.util.concurrent.CancellationException;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ExecutionException;
//
//public class MSTTSUtil {
//
//    private final String speechKey;
//    private final String speechRegion;
//    private final SpeechConfig speechConfig;
//
//    public MTTSUtil(String speechKey, String speechRegion) {
//        this.speechKey = speechKey;
//        this.speechRegion = speechRegion;
//        this.speechConfig = SpeechConfig.fromSubscription(this.speechKey, this.speechRegion);
//        this.speechConfig.setSpeechSynthesisVoiceName("en-US-AriaNeural");
//        this.speechConfig.setSpeechSynthesisOutputFormat(SpeechSynthesisOutputFormat.WAVE); // 修改这里，注意大写
//    }
//
//    public boolean textToSpeech(String text, String outputFilename) {
//        try (AudioConfig audioConfig = AudioConfig.fromOutputFile(outputFilename); // 修改这里
//             SpeechSynthesizer synthesizer = new SpeechSynthesizer(this.speechConfig, audioConfig)) {
//
//            CompletableFuture<SpeechSynthesisResult> task = synthesizer.speakTextAsync(text);
//            SpeechSynthesisResult result = task.get();
//
//            if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
//                System.out.println(String.format("Text to speech successful. WAV file saved to %s", outputFilename));
//                return true;
//            } else if (result.getReason() == ResultReason.Canceled) {
//                SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails.fromResult(result);
//                System.out.println(String.format("Speech synthesis canceled: %s", cancellation.getReason()));
//
//                if (cancellation.getReason() == CancellationReason.Error) {
//                    System.out.println(String.format("Error details: %s", cancellation.getErrorDetails()));
//                }
//                return false;
//            } else {
//                System.out.println(String.format("Unexpected result: %s", result.getReason()));
//                return false;
//            }
//
//        } catch (InterruptedException | ExecutionException e) {
//            System.out.println(String.format("An error occurred: %s", e.getMessage()));
//            return false;
//        } catch (Exception e) {
//            System.out.println(String.format("An unexpected error occurred: %s", e.getMessage()));
//            return false;
//        }
//    }
//
//
//    public static void main(String[] args) {
//        String SPEECH_KEY = "YOUR_SPEECH_KEY";
//        String SPEECH_REGION = "YOUR_SPEECH_REGION";
//
//        MSTTSUtil ttsUtil = new MSTTSUtil(SPEECH_KEY, SPEECH_REGION);
//
//        String text = "Hello, how are you?";
//
//        String outputFilename = "hello.wav";
//
//        boolean success = ttsUtil.textToSpeech(text, outputFilename);
//
//        if (success) {
//            System.out.println(String.format("Successfully generated %s", outputFilename));
//        } else {
//            System.out.println("Failed to generate speech.");
//        }
//    }
//}