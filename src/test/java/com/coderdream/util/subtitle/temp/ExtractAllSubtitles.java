package com.coderdream.util.subtitle.temp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ExtractAllSubtitles {

    /**
     * 使用 FFmpeg 从 MP4 文件中提取所有软字幕。
     *
     * @param mp4FilePath MP4 文件的路径
     * @return 一个列表，其中每个元素都是一个字幕文本的列表（对应于一个字幕流），
     *         如果出错则返回 null。
     */
    public static List<List<String>> extractAllSubtitles(String mp4FilePath) {
        List<List<String>> allSubtitles = new ArrayList<>();

        // 首先确定视频有多少个字幕流
        int subtitleStreamCount = getSubtitleStreamCount(mp4FilePath);

        if (subtitleStreamCount == -1) {
            System.err.println("无法获取字幕流数量。");
            return null;
        }

        System.out.println("检测到 " + subtitleStreamCount + " 个字幕流。");

        // 循环遍历所有字幕流并提取
        for (int i = 0; i < subtitleStreamCount; i++) {
            System.out.println("正在提取字幕流 #" + i);
            List<String> subtitles = extractSubtitles(mp4FilePath, i);
            if (subtitles != null) {
                allSubtitles.add(subtitles);
                System.out.println("成功提取字幕流 #" + i);
            } else {
                System.err.println("提取字幕流 #" + i + " 失败。");
                // 可以选择在这里返回 null 如果希望一个字幕流提取失败就整体失败
                // 或者继续尝试提取其他字幕流
            }
        }

        return allSubtitles;
    }

    /**
     * 使用 FFmpeg 获取视频文件中的字幕流数量。
     *
     * @param mp4FilePath MP4 文件路径
     * @return 字幕流的数量，如果出错返回 -1
     */
    private static int getSubtitleStreamCount(String mp4FilePath) {
        String ffmpegCommand = String.format("ffmpeg -i \"%s\"", mp4FilePath); //只需 -i 即可

        try {
            Process process = Runtime.getRuntime().exec(ffmpegCommand);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream())); //流信息在getErrorStream
            String line;
            int subtitleCount = 0;
            while ((line = reader.readLine()) != null) {
                // 寻找包含 "Subtitle:" 的行
                if (line.contains("Subtitle:")) {
                    subtitleCount++;
                }
            }

            process.waitFor(); //等待进程结束，否则进程可能占用资源

            return subtitleCount;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
    }


    /**
     * 使用 FFmpeg 从 MP4 文件中提取特定索引的软字幕。
     *
     * @param mp4FilePath   MP4 文件的路径
     * @param subtitleIndex 字幕流的索引 (从 0 开始)
     * @return 字幕文本的列表，每一行代表一个字幕条目，如果出错则返回 null。
     */
    private static List<String> extractSubtitles(String mp4FilePath, int subtitleIndex) {
        List<String> subtitles = new ArrayList<>();
        String ffmpegCommand = String.format(
                "ffmpeg -i \"%s\" -map 0:s:%d -c:s:0 copy -f srt -",  //-map 0:s:%d 指定索引为 subtitleIndex 的字幕流
                mp4FilePath, subtitleIndex
        );

        try {
            Process process = Runtime.getRuntime().exec(ffmpegCommand);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                subtitles.add(line);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String errorLine;
                StringBuilder errorMessage = new StringBuilder();
                while ((errorLine = errorReader.readLine()) != null) {
                    errorMessage.append(errorLine).append("\n");
                }
                System.err.println("FFmpeg 执行出错 (退出码: " + exitCode + "):\n" + errorMessage);
                return null; // 返回 null 表示提取失败
            }

            return subtitles;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null; // 返回 null 表示提取失败
        }
    }

    public static void main(String[] args) {
        String mp4FilePath = "D:\\0000\\0004_AppleEvents\\250219\\250219.mp4"; // 替换为你的 MP4 文件路径

        List<List<String>> allSubtitles = extractAllSubtitles(mp4FilePath);

        if (allSubtitles != null) {
            System.out.println("所有字幕提取成功:");
            for (int i = 0; i < allSubtitles.size(); i++) {
                System.out.println("--- 字幕流 #" + i + " ---");
                List<String> subtitles = allSubtitles.get(i);
                for (String subtitleLine : subtitles) {
                    System.out.println(subtitleLine);
                }
                //可选: 将每个字幕流保存到单独的 SRT 文件
                writeToFile(subtitles, "subtitles_" + i + ".srt");
            }
        } else {
            System.err.println("字幕提取失败.");
        }
    }

    //可选方法：将字幕列表写入 SRT 文件
    public static void writeToFile(List<String> subtitles, String filePath) {
        try (java.io.PrintWriter out = new java.io.PrintWriter(filePath)) {
            for (String subtitleLine : subtitles) {
                out.println(subtitleLine);
            }
        } catch (java.io.IOException e) {
            System.err.println("写入文件出错: " + e.getMessage());
        }
    }
}
