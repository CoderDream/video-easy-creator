package com.coderdream.util.proxy;

import static com.coderdream.util.cd.CdConstants.KEYWORD_LINUX1;
import static com.coderdream.util.cd.CdConstants.KEYWORD_LINUX2;
import static com.coderdream.util.cd.CdConstants.KEYWORD_MAC1;
import static com.coderdream.util.cd.CdConstants.KEYWORD_MAC2;
import static com.coderdream.util.cd.CdConstants.KEYWORD_WINDOWS;
import static com.coderdream.util.cd.CdConstants.OS_LINUX;
import static com.coderdream.util.cd.CdConstants.OS_MAC;
import static com.coderdream.util.cd.CdConstants.OS_UNKNOWN;
import static com.coderdream.util.cd.CdConstants.OS_WINDOWS;

public class OperatingSystem {


    public static String getOS() {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains(KEYWORD_WINDOWS)) {
            return OS_WINDOWS;
        } else if (osName.contains(KEYWORD_MAC1) || osName.contains(KEYWORD_MAC2)) {
            return OS_MAC;
        } else if (osName.contains(KEYWORD_LINUX1) || osName.contains(KEYWORD_LINUX2)) {
            return OS_LINUX;
        } else {
            return OS_UNKNOWN;
        }
    }

    public static void main(String[] args) {
        String osType = getOS();
        System.out.println("操作系统类型: " + osType);

        // 可以根据操作系统类型执行不同的逻辑
      switch (osType) {
        case OS_WINDOWS -> System.out.println("执行 Windows 相关的操作...");
        case OS_MAC -> System.out.println("执行 Mac 相关的操作...");
        case OS_LINUX -> System.out.println("执行 Linux 相关的操作...");
        default -> System.out.println("无法识别的操作系统。");
      }
    }
}
