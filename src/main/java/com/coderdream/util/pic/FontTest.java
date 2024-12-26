package com.coderdream.util.pic;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class FontTest {
    public static void main(String[] args) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font[] allFonts = ge.getAllFonts();
        List<Font> validFonts = new ArrayList<>();
        for (Font font : allFonts) {
            try {
                // 创建一个BufferedImage作为绘图环境
                BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = image.createGraphics();
                g2d.setFont(font);
                g2d.drawString("测试文本", 10, 50);
                g2d.dispose();
                // 如果没有抛出异常，说明字体可以正常绘制，添加到有效字体列表
                validFonts.add(font);
            } catch (Exception e) {
                // 出现异常，可能是字体无法正常绘制，忽略此字体
            }
        }
        for (Font font : validFonts) {
            System.out.println(font.getFontName());
        }
    }
}
