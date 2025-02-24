package com.coderdream.util.mstts;

import com.coderdream.util.cd.CdConstants;
import com.microsoft.cognitiveservices.speech.*;

import java.util.List;

public class AzureSpeechService {

    public static void content2wav(List<String> textList, String voiceName,
                                   String pitch, String volume, String rate, String fileName, String speechKey, String region) {

        // Azure 配置信息
//        String speechKey = "AqtklKND6Vgov7e8PdoWQZMuquSVGRGjduSstrj41vV158QhwWnyJQQJ99ALACYeBjFXJ3w3AAAYACOGXflB"; // 替换为你的 Azure Key
//        String region = "eastus";            // 替换为你的 Azure 区域

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

        // Azure 配置信息
        String speechKey1 =  CdConstants.SPEECH_KEY_EAST_US;
        String region1 = CdConstants.SPEECH_REGION_EASTUS;
// Enhance your English listening with 30-minute sessions of English audio, paired with Chinese dubbing.
//        List<String> textList = List.of("Enhance your English listening with 30-minute sessions of English audio, paired with Chinese dubbing.");

        List<String> textList = List.of("Enhance your English listening with 30-minute sessions of English audio, paired with Chinese dubbing.");
        String fileName1 = "eng1234.wav";
        content2wav(textList, "en-US-JennyNeural", "default", "default", "default", fileName1, speechKey1, region1);

        List<String> textList2 = List.of("英文加中文配音，每次半小時，增强你的英文听力。");
//
        String fileName2 = "chn123.wav";
        String speechKey2 = CdConstants.SPEECH_KEY_EASTASIA;
        String region2 = CdConstants.SPEECH_REGION_EASTASIA;
        String voiceName2 = CdConstants.SPEECH_VOICE_ZH_CN_XIAOCHEN;
        content2wav(textList2, voiceName2, "default", "default", "default", fileName2, speechKey2, region2);
    }
}
