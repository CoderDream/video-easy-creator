package com.coderdream.util.pic.demo02;

import com.coderdream.util.cd.CdTimeUtil;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DurationFormatter {

    public static String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String positive = String.format(
                "%d:%02d:%02d.%03d",
                absSeconds / 3600,
                (absSeconds % 3600) / 60,
                absSeconds % 60,
                duration.toMillisPart());
        return seconds < 0 ? "-" + positive : positive;
    }

    public static void main(String[] args) {
        Duration duration = Duration.ofSeconds(3661, 500_000_000); // 1小时1分1秒.500毫秒
        String formattedDuration = CdTimeUtil.formatDuration(duration.toMillis());
        System.out.println("Formatted duration: " + formattedDuration);  // 输出： 1:01:01.500

        duration = Duration.ofSeconds(-7262, 250_000_000); // -2小时1分2秒.250毫秒
        formattedDuration = CdTimeUtil.formatDuration(duration.toMillis());
        System.out.println("Formatted duration: " + formattedDuration);  // 输出： -2:01:02.250
    }
}
