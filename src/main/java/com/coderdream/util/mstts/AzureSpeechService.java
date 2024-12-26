package com.coderdream.util.mstts;

import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.*;

import java.util.List;

public class AzureSpeechService {

    public static void content2wav(List<String> textList, String voiceName,
                                   String pitch, String volume, String rate, String fileName) {

        // Azure 配置信息
        String speechKey = "AqtklKND6Vgov7e8PdoWQZMuquSVGRGjduSstrj41vV158QhwWnyJQQJ99ALACYeBjFXJ3w3AAAYACOGXflB"; // 替换为你的 Azure Key
        String region = "eastus";            // 替换为你的 Azure 区域

        // 配置语音服务
        SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechKey, region);
        speechConfig.setSpeechSynthesisOutputFormat(
                SpeechSynthesisOutputFormat.Riff24Khz16BitMonoPcm);

        try (SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig, null)) {

            // 设置默认参数
            voiceName = voiceName != null ? voiceName : "en-US-JennyNeural"; // 默认美式女声
            pitch = pitch != null ? pitch : "default";                       // 默认音调
            volume = volume != null ? volume : "default";                    // 默认音量
            rate = rate != null ? rate : "default";                          // 默认语速

            // 生成 SSML 内容
            String ssml = generateSSML(textList, voiceName, pitch, volume, rate);

            // 合成语音
            SpeechSynthesisResult result = speechSynthesizer.SpeakSsml(ssml);
            if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                AudioDataStream stream = AudioDataStream.fromResult(result);
                stream.saveToWavFile(fileName);
                System.out.println("音频文件生成成功: " + fileName);
            } else {
                System.err.println("语音合成失败: " + result.getReason().toString());
            }
        } catch (Exception e) {
            System.err.println("语音合成出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 生成 SSML (Speech Synthesis Markup Language)
    private static String generateSSML(List<String> textList, String voiceName,
                                       String pitch, String volume, String rate) {
        StringBuilder ssmlBuilder = new StringBuilder();
        ssmlBuilder.append("<speak version='1.0' xml:lang='en-US'>");
        ssmlBuilder.append("<voice name='").append(voiceName).append("'>");
        ssmlBuilder.append("<prosody pitch='").append(pitch).append("' ")
                   .append("volume='").append(volume).append("' ")
                   .append("rate='").append(rate).append("'>");

        for (String text : textList) {
            ssmlBuilder.append(text).append(" ");
        }

        ssmlBuilder.append("</prosody>");
        ssmlBuilder.append("</voice>");
        ssmlBuilder.append("</speak>");
        return ssmlBuilder.toString();
    }

    public static void main(String[] args) {
        List<String> textList = List.of("Hi, friend. I'm planning a camping trip and I really hope you can come with me.");
        String fileName = "new_test_02.wav";

        content2wav(textList, "en-US-JennyNeural", "default", "default", "default", fileName);
    }
}
