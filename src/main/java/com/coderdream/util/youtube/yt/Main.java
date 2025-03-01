package com.coderdream.util.youtube.yt;

//import com.litongjava.yt.broker.LongProcessBroker;
//import com.litongjava.yt.builder.YtDlpOption;
//import com.litongjava.yt.builder.YtDlpOptionBuilder;

import com.coderdream.util.youtube.YouTubeApiUtil;
import com.coderdream.util.youtube.yt.broker.LongProcessBroker;
import com.coderdream.util.youtube.yt.builder.YtDlpOption;
import com.coderdream.util.youtube.yt.builder.YtDlpOptionBuilder;

public class Main {

  public static void main(String[] args) {
    YouTubeApiUtil.enableProxy();
//    downloadMp3();
    // listFormat();
    // test1();
     test2();
  }

  private static void downloadMp3() {
    // 示例：下载视频为音频并转换为 mp3 格式
    YtDlpOption options = new YtDlpOptionBuilder()
        .url("https://www.youtube.com/watch?v=PnHMAVXpKg8")
        .audio()                   // 启用音频提取
        .audioFormat("mp3")        // 设置输出音频格式为 mp3
        .build();

    // 调用下载音频方法
    YtDlp.execute(options);
  }

  private static void listFormat() {
    String format = YtDlp.getAvailableFormats("https://www.youtube.com/watch?v=PnHMAVXpKg8");
    System.out.println("format: " + format);
  }

  private static void test2() {

    YouTubeApiUtil.enableProxy();
    LongProcessBroker longProcessBroker = new LongProcessBroker("yt-dlp.exe", "https://www.youtube.com/watch?v=aayJ6wlyfII");
    longProcessBroker.addProcessStreamChangeEventListener(
        // 将日志输出中的变化行打印到控制台
        event -> System.out.println("event.getChangedString() = " + event.getChangedString()));
    longProcessBroker.execute();
  }

  private static void test1() {
    YtDlpOptionBuilder ytDlpOptionBuilder = new YtDlpOptionBuilder();
    ytDlpOptionBuilder.url("https://www.youtube.com/watch?v=PnHMAVXpKg8")
                      .output("%(title)s.%(ext)s");
    YtDlpOption options = ytDlpOptionBuilder.build();
    YtDlp.execute(options);
  }
}
