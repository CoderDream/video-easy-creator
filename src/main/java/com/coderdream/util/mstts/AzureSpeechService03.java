package com.coderdream.util.mstts;

import cn.hutool.core.io.FileUtil;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdFileUtil;
import com.microsoft.cognitiveservices.speech.AudioDataStream;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisCancellationDetails;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisOutputFormat;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AzureSpeechService03 {

    public static void content2wav(List<String> textList, String voiceName,
                                   String pitch, String volume, String rate, String fileName,
                                   String speechKey, String region) {
        SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechKey, region);
        speechConfig.setSpeechSynthesisOutputFormat(SpeechSynthesisOutputFormat.Riff24Khz16BitMonoPcm);

        try (SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig, null)) {
            voiceName = voiceName != null ? voiceName : "en-US-JennyNeural";
            pitch = pitch != null ? pitch : "+0Hz";    // 使用具体值
            volume = volume != null ? volume : "100";
            rate = rate != null ? rate : "1.0";
            String ssmlFileName =
              CdFileUtil.getResourceRealPath() + File.separator + "ssml"
                + File.separator + "ssml003.xml";
//            List<String> stringList = FileUtil.readLines(ssmlFileName,
//              StandardCharsets.UTF_8);

            String ssml = FileUtil.readUtf8String(ssmlFileName);// generateSSML(textList, voiceName, pitch, volume, rate);
            System.out.println("生成的 SSML: " + ssml); // 调试用

            SpeechSynthesisResult result = speechSynthesizer.SpeakSsml(ssml);
            if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                AudioDataStream stream = AudioDataStream.fromResult(result);
                stream.saveToWavFile(fileName);
                System.out.println("音频文件生成成功: " + new File(fileName).getAbsolutePath());
                stream.close();
            } else if (result.getReason() == ResultReason.Canceled) {
                SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails.fromResult(result);
                System.err.println("语音合成被取消: " + cancellation.getReason());
                System.err.println("错误详情: " + cancellation.getErrorDetails());
            } else {
                System.err.println("语音合成失败: " + result.getReason().toString());
            }
            result.close();
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
            String escapedText = text.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&apos;");
            ssmlBuilder.append(escapedText).append(" ");
        }

        ssmlBuilder.append("</prosody>");
        ssmlBuilder.append("</voice>");
        ssmlBuilder.append("</speak>");
        return ssmlBuilder.toString();
    }

    public static void testEn() {
        // Azure 配置信息
        String speechKey1 =  CdConstants.SPEECH_KEY_EAST_US;//  "CYy4pk7VTJ4IhnhWaZMZseRTgOFYsAjy7MLmiGrhAlFnxXQZLpZzJQQJ99BBACYeBjFXJ3w3AAAYACOG31QZ";// "CFWk2q2sSLvSzIQ3TOnIBAsea1cN8psM5jTrmTy2AAg5lI9gSAEEJQQJ99BBACYeBjFXJ3w3AAAYACOGOcUN" ;//CdConstants.SPEECH_KEY_EAST_US;
        String region1 = "eastus";// CdConstants.SPEECH_REGION_EASTUS;
        List<String> textList = List.of("Enhance your English listening with 30-minute sessions of English audio, paired with Chinese dubbing.","I agree. We should focus on interactive content, like polls and live Q&A sessions.");
        String fileName1 = "eng1234.wav";
        content2wav(textList, "en-US-JennyNeural", "default", "default", "default", fileName1, speechKey1, region1);
    }

    public static void main(String[] args) {
//        testEn();
        testCn();
    }

    public static void testCn() {
        List<String> textList2 = List.of("英文加中文配音，每次半小時，增强你的英文听力。");
        String fileName2 = "chn123.wav";
        String speechKey2 =  CdConstants.SPEECH_KEY_EASTASIA;
        String region2 = CdConstants.SPEECH_REGION_EASTASIA;
        String voiceName2 = CdConstants.SPEECH_VOICE_ZH_CN_XIAOCHEN;
        content2wav(textList2, voiceName2, "default", "default", "default", fileName2, speechKey2, region2);
    }

}
