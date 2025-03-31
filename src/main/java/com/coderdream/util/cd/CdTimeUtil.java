package com.coderdream.util.cd;

public class CdTimeUtil {

  // 格式化毫秒为 时:分:秒
  public static String formatDuration(long durationMillis) {
    long milliseconds = durationMillis % 1000;
    long seconds = durationMillis / 1000;
    long minutes = seconds / 60;
    long hours = minutes / 60;
    seconds = seconds % 60;
    minutes = minutes % 60;
    return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds,
      milliseconds);
  }
}
