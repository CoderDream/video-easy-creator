package com.coderdream.util.mstts;

import com.coderdream.util.CdConstants;
import com.coderdream.util.chatgpt.TextParserUtil;
import com.coderdream.vo.SentenceVO;
import com.microsoft.cognitiveservices.speech.AudioDataStream;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisOutputFormat;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;
import java.io.File;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpeechUtil5 {

    private static final ExecutorService executorService = new ThreadPoolExecutor(
            4,  // 核心线程数
            10, // 最大线程数
            60L, TimeUnit.SECONDS, // 空闲线程存活时间
            new LinkedBlockingQueue<>(), // 阻塞队列
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略
    );

    public static void content2Audio(List<String> textList, String voiceName,
                                     String pitch, String volume, String rate, String fileName,
                                     String audioType, String lang) {
        executorService.submit(() -> {
            try {
                String speechKey = null;
                SpeechConfig speechConfig = null;

                switch (lang.toLowerCase()) {
                    case "zh-cn" -> {
                        speechKey = CdConstants.SPEECH_KEY;
                        speechConfig = SpeechConfig.fromSubscription(speechKey, CdConstants.SPEECH_REGION_EASTASIA);
                    }
                    case "en-us" -> {
                        speechKey = CdConstants.SPEECH_KEY_EAST_US;
                        speechConfig = SpeechConfig.fromSubscription(speechKey, CdConstants.SPEECH_REGION_EASTUS);
                    }
                    default -> throw new IllegalArgumentException("不支持的语言代码: " + lang);
                }

                switch (audioType.toLowerCase()) {
                    case "wav":
                        speechConfig.setSpeechSynthesisOutputFormat(SpeechSynthesisOutputFormat.Riff24Khz16BitMonoPcm);
                        break;
                    case "mp3":
                        speechConfig.setSpeechSynthesisOutputFormat(SpeechSynthesisOutputFormat.Audio24Khz160KBitRateMonoMp3);
                        break;
                    default:
                        throw new IllegalArgumentException("不支持的音频格式: " + audioType);
                }

                try (SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig, null)) {
                    String ssml = SsmlGenerator.genXmlString(textList, voiceName, pitch, volume, rate);
                    SpeechSynthesisResult result = speechSynthesizer.SpeakSsml(ssml);

                    if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                        AudioDataStream stream = AudioDataStream.fromResult(result);
                        stream.saveToWavFile(fileName);
                        log.info("音频文件生成成功: {}", fileName);
                    } else {
                        log.error("语音合成失败: {}，文件名：{}", result.getReason(), fileName);
                    }
                }
            } catch (Exception e) {
                log.error("语音合成出错: {}", e.getMessage(), e);
            }
        });
    }

    public static void genDialog2Audio(String folderName, String fileName, String audioType) {
        String fullFileName = folderName + File.separator + fileName + ".txt";
        log.info("开始解析文件: {}", fullFileName);

        String outputDirCn = folderName + File.separator + audioType + File.separator + "cn";
        String outputDirEn = folderName + File.separator + audioType + File.separator + "en";
        File dirCn = new File(outputDirCn);
        if (!dirCn.exists() && dirCn.mkdirs()) {
            log.info("目录创建成功: {}", dirCn.getAbsolutePath());
        }
        File dirEn = new File(outputDirEn);
        if (!dirEn.exists() && dirEn.mkdirs()) {
            log.info("目录创建成功: {}", dirEn.getAbsolutePath());
        }

        List<SentenceVO> sentenceVOs = TextParserUtil.parseFileToSentenceVOs(fullFileName);
        int number = 0;
        for (SentenceVO sentenceVO : sentenceVOs) {
            number++;
            String cnFile = outputDirCn + File.separator + fileName + "_" + MessageFormat.format("{0,number,000}", number) + "_cn." + audioType;
            String enFile = outputDirEn + File.separator + fileName + "_" + MessageFormat.format("{0,number,000}", number) + "_en." + audioType;

            content2Audio(List.of(sentenceVO.getChinese()), "zh-CN-XiaochenNeural", "medium", "medium", "medium", cnFile, audioType, "zh-cn");
            content2Audio(List.of(sentenceVO.getEnglish()), "en-US-JennyNeural", "default", "default", "default", enFile, audioType, "en-us");
        }
    }

    public static void shutdownExecutor() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
