package com.coderdream.util.cd;

import cn.hutool.core.util.StrUtil;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CdDateUtil {


  public static String formatDurationHMS(long millis) {
    Duration duration = Duration.ofMillis(millis);
    long hours = duration.toHours();
    long minutes = duration.minusHours(hours).toMinutes();
    long seconds = duration.minusHours(hours).minusMinutes(minutes)
      .getSeconds();
    return StrUtil.format("{}小时{}分钟{}秒", hours, minutes, seconds);
  }

  public static String formatDurationHMSS(long millis) {
    long seconds = millis / 1000;
    long minutes = seconds / 60;
    long hours = minutes / 60;
    long milliseconds = millis % 1000;
    seconds %= 60;
    minutes %= 60;

    return String.format("%d小时%d分钟%d秒%d毫秒", hours, minutes, seconds,
      milliseconds);
  }

  /**
   * 将 Duration 转换为 "HH:mm:ss.SSS" 格式的字符串
   *
   * @param duration  Duration 对象
   * @return  格式化后的字符串
   */
  public static String formatDuration(Duration duration) {
    long millis = duration.toMillis();
    long hours = millis / (3600 * 1000);
    millis %= (3600 * 1000);
    long minutes = millis / (60 * 1000);
    millis %= (60 * 1000);
    long seconds = millis / 1000;
    long remainingMillis = millis % 1000;
    return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, remainingMillis);
  }
}
