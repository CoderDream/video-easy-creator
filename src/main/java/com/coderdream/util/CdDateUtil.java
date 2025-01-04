package com.coderdream.util;

import cn.hutool.core.util.StrUtil;
import java.time.Duration;

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
}
