package com.coderdream.util.download;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TsFileDownloaderTest {

  @Test
  void downloadAndMergeTsFilesWithFFmpeg_01() {
                   // https://v-tos-k.xiaoeknow.com/2919df88vodtranscq1252524126/0990d09d1397757895844475236/v.f146750_1567.ts?&sign=47f8292fbc9be7d94128d20d71585ea0&t=67caf55f&us=YApCSMPVta
                   // https://v-tos-k.xiaoeknow.com/2919df88vodtranscq1252524126/0990d09d1397757895844475236/v.f146750_0.ts?&sign=47f8292fbc9be7d94128d20d71585ea0&t=67caf55f&us=YApCSMPVta
    String baseUrl = "https://v-tos-k.xiaoeknow.com/2919df88vodtranscq1252524126/0990d09d1397757895844475236/v.f146750_";
    int startIndex = 0;
    int endIndex = 1580;
    String day = "Day1";
    String tempDir = "D:\\Download\\小鹅通视频下载器\\temp_" + day; // 临时文件目录
    String outputFile = "D:\\Download\\小鹅通视频下载器\\" + day
      + "-求职、跳槽、转行.ts";  // 修改后的路径
    String suffix = ".ts?&sign=47f8292fbc9be7d94128d20d71585ea0&t=67caf55f&us=YApCSMPVta";

    String elapsedTime = TsFileDownloader.downloadAndMergeTsFilesWithFFmpeg(
      baseUrl, startIndex, endIndex, tempDir, outputFile, suffix);
    System.out.println("耗时: " + elapsedTime);
  }

  @Test
  void downloadAndMergeTsFilesWithFFmpeg_02() {
    // https://v-tos-k.xiaoeknow.com/2919df88vodtranscq1252524126/340b365b1397757895831375833/v.f146750_17.ts?&sign=52ac4fdbfed3467a3d39a299083fc21f&t=67caf6c8&us=LfHkMMEObq
    String baseUrl = "https://v-tos-k.xiaoeknow.com/2919df88vodtranscq1252524126/340b365b1397757895831375833/v.f146750_";
    int startIndex = 0;
    int endIndex = 1550;
    String day = "Day2";
    String tempDir = "D:\\Download\\小鹅通视频下载器\\temp_" + day; // 临时文件目录
    String outputFile = "D:\\Download\\小鹅通视频下载器\\" + day
      + "-高薪行业岗位要求解读.ts";  // 修改后的路径
    String suffix = ".ts?&sign=52ac4fdbfed3467a3d39a299083fc21f&t=67caf6c8&us=LfHkMMEObq";

    String elapsedTime = TsFileDownloader.downloadAndMergeTsFilesWithFFmpeg(
      baseUrl, startIndex, endIndex, tempDir, outputFile, suffix);
    System.out.println("耗时: " + elapsedTime);
  }

  @Test
  void downloadAndMergeTsFilesWithFFmpeg_03() {
    // https://v-tos-k.xiaoeknow.com/2919df88vodtranscq1252524126/340b365b1397757895831375833/v.f146750_17.ts?&sign=52ac4fdbfed3467a3d39a299083fc21f&t=67caf6c8&us=LfHkMMEObq
    String baseUrl = "https://v-tos-k.xiaoeknow.com/2919df88vodtranscq1252524126/340b365b1397757895831375833/v.f146750_";
    int startIndex = 0;
    int endIndex = 1550;
    String day = "Day2";
    String tempDir = "D:\\Download\\小鹅通视频下载器\\temp_" + day; // 临时文件目录
    String outputFile = "D:\\Download\\小鹅通视频下载器\\" + day
      + "-高薪行业岗位要求解读.ts";  // 修改后的路径
    String suffix = ".ts?&sign=52ac4fdbfed3467a3d39a299083fc21f&t=67caf6c8&us=LfHkMMEObq";

    String elapsedTime = TsFileDownloader.downloadAndMergeTsFilesWithFFmpeg(
      baseUrl, startIndex, endIndex, tempDir, outputFile, suffix);
    System.out.println("耗时: " + elapsedTime);
  }

  @Test
  void downloadAndMergeTsFilesWithFFmpeg_04() {
    // https://v-tos-k.xiaoeknow.com/2919df88vodtranscq1252524126/0990d09d1397757895844475236/v.f146750_1567.ts?&sign=47f8292fbc9be7d94128d20d71585ea0&t=67caf55f&us=YApCSMPVta
    String baseUrl = "https://v-tos-k.xiaoeknow.com/2919df88vodtranscq1252524126/643ba70e1397757896078503022/v.f146750_";
    int startIndex = 0;
    int endIndex = 1358;
    String tempDir = "D:\\Download\\小鹅通视频下载器\\temp"; // 临时文件目录
    String outputFile = "D:\\Download\\小鹅通视频下载器\\Day1-求职、跳槽、转行.ts";  // 修改后的路径
    String suffix = ".ts?&sign=159a7b89ad630c02ca84de2e925e8d74&t=67caec08&us=OpyQOlqRJQ";

    String elapsedTime = TsFileDownloader.downloadAndMergeTsFilesWithFFmpeg(
      baseUrl, startIndex, endIndex, tempDir, outputFile, suffix);
    System.out.println("耗时: " + elapsedTime);
  }
}
