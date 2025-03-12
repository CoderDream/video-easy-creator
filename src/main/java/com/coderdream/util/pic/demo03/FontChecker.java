package com.coderdream.util.pic.demo03;

import java.awt.*;
import java.util.Arrays;

public class FontChecker {
    public static void main(String[] args) {
        String targetFont = "Source Han Sans TC Heavy";
        if (isFontInstalled(targetFont)) {
            System.out.println("已安装字体: " + targetFont);
        } else {
            System.out.println("未找到字体: " + targetFont);
        }
    }

    // 检测系统中是否安装了指定字体
    public static boolean isFontInstalled(String fontName) {
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String font : fonts) {
            if(font.toLowerCase().contains("sans")) {
                System.out.println(font);
            }
        }
        return Arrays.asList(fonts).contains(fontName);
    }
}
