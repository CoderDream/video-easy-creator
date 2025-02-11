package com.coderdream.util.audio;

import cn.hutool.core.io.FileUtil;

import java.nio.charset.StandardCharsets;

import com.coderdream.util.proxy.OperatingSystem;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class AudioMergerUtil {


    /**
     * 合并指定目录下的所有 WAV 文件，并在每个文件前添加一个背景音乐。
     *
     * @param inputDir       输入 WAV 文件所在的目录
     * @param outputFilePath 合并后的 WAV 文件输出路径
     * @throws IOException 如果在读取文件或执行 ffmpeg 命令时发生错误
     */
    public static void mergeWavFiles(String inputDir, String outputFilePath,
                                     int size)
            throws IOException {
        mergeWavFiles(inputDir, outputFilePath, false, false, size);
    }

    /**
     * 合并指定目录下的所有 WAV 文件，并在每个文件前添加一个背景音乐。
     *
     * @param inputDir       输入 WAV 文件所在的目录
     * @param outputFilePath 合并后的 WAV 文件输出路径
     * @throws IOException 如果在读取文件或执行 ffmpeg 命令时发生错误
     */
    public static void mergeWavFiles(String inputDir, String outputFilePath)
            throws IOException {
        mergeWavFiles(inputDir, outputFilePath, false, false, 0);
    }

    /**
     * 合并指定目录下的所有 WAV 文件，并在每个文件前添加一个背景音乐。
     *
     * @param inputDir       输入 WAV 文件所在的目录
     * @param outputFilePath 合并后的 WAV 文件输出路径
     * @throws IOException 如果在读取文件或执行 ffmpeg 命令时发生错误
     */
    public static void mergeWavFiles(String inputDir, String outputFilePath,
                                     boolean addBeginMusic, boolean addPage, int size)
            throws IOException {
        long startTime = System.currentTimeMillis(); // 记录方法开始时间
        log.info("开始合并 WAV 文件，输入目录：{}, 输出路径：{}", inputDir,
                outputFilePath);

        List<String> wavFiles = listWavFiles(inputDir); // 获取 WAV 文件列表
        if (wavFiles.isEmpty()) {
            log.warn("目录 {} 下没有找到 wav 文件", inputDir);
            return;
        }

        String listFilePath = createListFile(wavFiles, inputDir, addBeginMusic,
                addPage,
                size); // 创建 ffmpeg 需要的 list 文件
        FfmpegUtil.executeFfmpegMerge(listFilePath,
                outputFilePath); // 执行 ffmpeg 合并操作

        File listFile = new File(listFilePath);
        if (listFile.exists()) {
            log.info("临时 list 文件路径：{}", FileUtil.readString(listFilePath, StandardCharsets.UTF_8));
            boolean delete = listFile.delete();
            log.info("临时 list 文件删除结果：{}", delete);
        }

        long endTime = System.currentTimeMillis(); // 记录方法结束时间
        long duration = endTime - startTime;
        String formattedTime = formatDuration(duration);
        log.info("WAV 文件合并完成，输出路径为：{}，总耗时：{}", outputFilePath,
                formattedTime);
    }

    /**
     * 格式化耗时为时分秒毫秒
     *
     * @param duration 耗时，单位毫秒
     * @return 格式化的耗时字符串
     */
    private static String formatDuration(long duration) {
        long milliseconds = duration % 1000;
        long seconds = (duration / 1000) % 60;
        long minutes = (duration / (1000 * 60)) % 60;
        long hours = (duration / (1000 * 60 * 60));
        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds,
                milliseconds);
    }

    /**
     * 列出指定目录下的所有 .wav 文件。
     *
     * @param inputDir 输入目录
     * @return WAV 文件路径列表
     */
    private static List<String> listWavFiles(String inputDir) {
        File dir = new File(inputDir);
        if (!dir.exists() || !dir.isDirectory()) {
            log.error("目录不存在或不是目录：{}", inputDir);
            return new ArrayList<>();
        }
        File[] files = dir.listFiles();
        if (Objects.isNull(files)) {
            return new ArrayList<>();
        }

        return Arrays.stream(files)
                .filter(
                        file -> file.isFile() && file.getName().toLowerCase().endsWith(".wav"))
                .map(File::getAbsolutePath)
                .collect(Collectors.toList());
    }

    /**
     * 创建 ffmpeg 需要的 list.txt 文件，每个音频文件前添加一个背景音乐。
     *
     * @param wavFiles 需要合并的 WAV 文件列表
     * @param inputDir 输入目录，用于生成 list 文件名
     * @return 生成的 list 文件路径
     * @throws IOException 如果写入文件时发生错误
     */
    private static String createListFile(List<String> wavFiles, String inputDir,
                                         boolean addBeginMusic, boolean addPage, int size)
            throws IOException {
        String listFilePath = inputDir + "list.txt"; // list 文件路径

        String pageFile = OperatingSystem.getBaseFolderWav("page.wav"); // 背景音乐文件路径，这里应为绝对路径，或写在配置中
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(listFilePath))) {

            if (addBeginMusic) {
                writer.write("file '" + OperatingSystem.getBaseFolderWav("eng.wav") + "'"); // 写入背景音乐
                writer.newLine();
                writer.write("file '" + OperatingSystem.getBaseFolderWav("chn.wav") + "'"); // 写入背景音乐
                writer.newLine();
            }

            //如果size为0全部，否则只合并前size个文件
            if (size != 0) {
                wavFiles = wavFiles.subList(0, size);
            }

            for (String file : wavFiles) {
                if (addPage) {
                    writer.write("file '" + pageFile + "'"); // 写入背景音乐
                    writer.newLine();
                }
                writer.write("file '" + file + "'"); // 写入当前音频文件
                writer.newLine();
            }
        }
        log.info("生成 list 文件成功：{}", listFilePath);
        return listFilePath;
    }

//  public static void main(String[] args) {
//    String inputDir = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics\\audio\\mix"; // 替换为你的输入目录
//    String outputFilePath = "D:\\0000\\EnBook001\\900\\ch01\\dialog_single_with_phonetics\\audio\\ch01_mix.wav"; // 替换为你的输出文件路径
//    try {
//      AudioMergerUtil.mergeWavFiles(inputDir, outputFilePath);
//    } catch (IOException e) {
//      log.error("合并文件失败：{}", e.getMessage());
//    }
//  }
}
