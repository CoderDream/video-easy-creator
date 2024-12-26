package com.coderdream.util.video;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VideoCreator2 {

    private static final String IMAGE_DIR = "D:\\04_GitHub\\video-easy-creator\\src\\main\\resources\\output";
    private static final String MP3_DIR = "D:\\04_GitHub\\video-easy-creator\\src\\main\\resources\\mp3";
    private static final String OUTPUT_DIR = "D:\\04_GitHub\\video-easy-creator\\src\\main\\resources\\output_videos";
    private static final String CONCAT_FILE = "D:\\04_GitHub\\video-easy-creator\\src\\main\\resources\\concat.txt";

    public static void main(String[] args) {
        List<File> imageFiles = getImageFiles();
        List<File> mp3Files = getMp3Files();

        if (imageFiles.size() != mp3Files.size()) {
            System.out.println("Number of images does not match number of MP3 files.");
            return;
        }


        for (int i = 0; i < imageFiles.size(); i++) {
            File imageFile = imageFiles.get(i);
            File mp3File = mp3Files.get(i);

            String outputFileName = OUTPUT_DIR + File.separator + "output_" + (i + 1) + ".mp4";
            createVideo(imageFile, mp3File, outputFileName);
        }

        concatVideos();
    }

    private static List<File> getImageFiles() {
        File imageDir = new File(IMAGE_DIR);
        List<File> imageFiles = new ArrayList<>();
        for (File file : imageDir.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".png")) {
                imageFiles.add(file);
            }
        }
        return imageFiles;
    }

    private static List<File> getMp3Files() {
        File mp3Dir = new File(MP3_DIR);
        List<File> mp3Files = new ArrayList<>();
        for (File file : mp3Dir.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".mp3")) {
                mp3Files.add(file);
            }
        }
        return mp3Files;
    }

    private static void createVideo(File imageFile, File mp3File, String outputFileName) {
        try {
            String[] cmd = {
                    "ffmpeg",
                    "-loop", "1",
                    "-i", imageFile.getAbsolutePath(),
                    "-i", mp3File.getAbsolutePath(),
                    "-c:v", "libx264",
                    "-t", "00:00:10", // Set the duration to 10 seconds, you can calculate this dynamically
                    "-c:a", "aac",
                    "-strict", "experimental",
                    "-b:a", "192k",
                    "-shortest",
                    outputFileName
            };

            Process process = Runtime.getRuntime().exec(cmd);
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void concatVideos() {
        try {
            File concatFile = new File(CONCAT_FILE);
            concatFile.createNewFile();

            List<File> videoFiles = getVideoFiles();
            StringBuilder concatContent = new StringBuilder();

            for (File videoFile : videoFiles) {
                concatContent.append("file '").append(videoFile.getAbsolutePath()).append("'\n");
            }

            java.nio.file.Files.write(concatFile.toPath(), concatContent.toString().getBytes());

            String[] cmd = {
                    "ffmpeg",
                    "-f", "concat",
                    "-safe", "0",
                    "-i", CONCAT_FILE,
                    "-c", "copy",
                    OUTPUT_DIR + File.separator + "final_output.mp4"
            };

            Process process = Runtime.getRuntime().exec(cmd);
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static List<File> getVideoFiles() {
        File outputDir = new File(OUTPUT_DIR);
        List<File> videoFiles = new ArrayList<>();
        for (File file : outputDir.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".mp4")) {
                videoFiles.add(file);
            }
        }
        return videoFiles;
    }
}
