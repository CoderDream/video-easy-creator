package com.coderdream.util.subtitle;

import com.coderdream.vo.SentenceDurationVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;



public class SubtitleGenerator {

    private static final DecimalFormat TIME_FORMAT = new DecimalFormat("00");

    public static void main(String[] args) {
        // 假设已经有一个SentenceDurationVO列表
        List<SentenceDurationVO> sentences =  SubtitleUtil.genSubtitle("CampingInvitation_cht_03") ;// getSentences(); // 获取句子列表，具体数据需要从输入源获取

        File outputDir = new File("output/");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // 根据列表生成4个字幕文件
        generateSubtitles(sentences, "output/"); // 指定输出路径
    }

    /**
     * 根据输入的句子列表生成4个字幕文件
     *
     * @param sentences 句子列表
     * @param outputDir 输出目录
     */
    public static void generateSubtitles(List<SentenceDurationVO> sentences, String outputDir) {
        File idFile = new File(outputDir + "id.srt");
        File phoneticsFile = new File(outputDir + "phonetics.srt");
        File chineseFile = new File(outputDir + "chinese.srt");
        File englishFile = new File(outputDir + "english.srt");

        try {
            BufferedWriter idWriter = new BufferedWriter(new FileWriter(idFile));
            BufferedWriter phoneticsWriter = new BufferedWriter(new FileWriter(phoneticsFile));
            BufferedWriter chineseWriter = new BufferedWriter(new FileWriter(chineseFile));
            BufferedWriter englishWriter = new BufferedWriter(new FileWriter(englishFile));

            double startTimeInSeconds = 0.0; // 起始时间（秒）
            for (SentenceDurationVO sentence : sentences) {
                // 根据句子的总时长计算字幕的开始时间和结束时间
                double endTimeInSeconds = startTimeInSeconds + sentence.getTotalDuration();

                // 转换为时分秒格式
                String startTime = convertSecondsToTimeFormat(startTimeInSeconds);
                String endTime = convertSecondsToTimeFormat(endTimeInSeconds);

                // 写入每个文件的字幕内容
                writeSubtitle(idWriter, sentence.getId().toString(), startTime, endTime);
                writeSubtitle(phoneticsWriter, sentence.getPhonetics(), startTime, endTime);
                writeSubtitle(chineseWriter, sentence.getChinese(), startTime, endTime);
                writeSubtitle(englishWriter, sentence.getEnglish(), startTime, endTime);

                // 更新起始时间为当前字幕的结束时间
                startTimeInSeconds = endTimeInSeconds;
            }

            // 关闭所有的Writer
            idWriter.close();
            phoneticsWriter.close();
            chineseWriter.close();
            englishWriter.close();

            System.out.println("字幕文件已成功生成！");

        } catch (IOException e) {
            System.err.println("生成字幕文件时出错: " + e.getMessage());
        }
    }

    /**
     * 将秒数转换为字幕需要的时分秒格式
     *
     * @param seconds 秒数
     * @return 格式化后的时间字符串
     */
    public static String convertSecondsToTimeFormat(double seconds) {
        int hours = (int) (seconds / 3600);
        int minutes = (int) ((seconds % 3600) / 60);
        int secs = (int) (seconds % 60);
        int millis = (int) ((seconds - (int) seconds) * 1000); // 毫秒

        return String.format("%02d:%02d:%02d,%03d", hours, minutes, secs, millis);
    }

    /**
     * 写入单条字幕到指定的文件
     *
     * @param writer    输出文件的Writer
     * @param text      字幕内容
     * @param startTime 字幕开始时间
     * @param endTime   字幕结束时间
     * @throws IOException 写入错误
     */
    private static void writeSubtitle(BufferedWriter writer, String text, String startTime, String endTime) throws IOException {
        writer.write(String.format("%d\n", writer.toString().hashCode()));  // ID
        writer.write(String.format("%s --> %s\n", startTime, endTime)); // 时间
        writer.write(text + "\n\n");  // 字幕文本
    }

    /**
     * 模拟获取句子数据（可以从文件或数据库中获取）
     *
     * @return 句子数据
     */
    public static List<SentenceDurationVO> getSentences() {
        // 你可以根据需求读取文件或数据库来填充这个列表
        return List.of(
            new SentenceDurationVO(1, "phonetic1", "顺便说一下，别忘了带手电筒", 5.5, "By the way, don't forget to bring a flashlight", 6.48, 4 * 6.48 + 5.5),
            new SentenceDurationVO(2, "phonetic2", "明白了，那驱虫剂呢？", 4.95, "Got it, what about bug spray?", 5.74, 4 * 5.74 + 4.95)
        );
    }
}
