package com.coderdream.util.mstts.demo01;

import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class TextToSpeechUtil {
    private static final String SUBSCRIPTION_KEY =  "50vWNO4RVL41CEjbkm4aT5c8VPBjO1XNQOMWEYgX3IrrKQn37XTTJQQJ99BCACYeBjFXJ3w3AAAYACOGjmML"; // 替换为你的 Azure API Key
    private static final String REGION = "eastus"; // 替换为你的服务区域，例如 "eastus"

    public static void synthesizeSpeech(String text, String outputFilePath) {
        try {
            // 配置 Speech SDK
            SpeechConfig speechConfig = SpeechConfig.fromSubscription(SUBSCRIPTION_KEY, REGION);
            
            // 设置语音合成的语言和声音（这里使用默认的英文女声 en-US-JennyNeural）
            speechConfig.setSpeechSynthesisVoiceName("en-US-JennyNeural");
            
            // 配置输出音频文件
            AudioConfig audioConfig = AudioConfig.fromWavFileOutput(outputFilePath);
            
            // 创建语音合成器
            try (SpeechSynthesizer synthesizer = new SpeechSynthesizer(speechConfig, audioConfig)) {
                // 执行语音合成
                Future<SpeechSynthesisResult> task = synthesizer.SpeakTextAsync(text);
                SpeechSynthesisResult result = task.get();
                
                // 检查合成结果
                if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                    System.out.println("语音合成完成，文件已保存到: " + outputFilePath);
                } else {
                    System.out.println("语音合成失败: " + result.getReason());
                }
                
                // 释放资源
                result.close();
            }
            
            // 释放配置资源
            speechConfig.close();
            audioConfig.close();
            
        } catch (InterruptedException e) {
            System.err.println("线程被中断: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            System.err.println("语音合成执行失败: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String inputText = "hello, how are you";
        String outputFile = "output.wav";
        
        // 删除已存在的输出文件
        File file = new File(outputFile);
        if (file.exists()) {
            file.delete();
        }
        
        // 调用语音合成方法
        synthesizeSpeech(inputText, outputFile);
    }
}