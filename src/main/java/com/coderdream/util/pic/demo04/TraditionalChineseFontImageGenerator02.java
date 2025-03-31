package com.coderdream.util.pic.demo04;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TraditionalChineseFontImageGenerator02 {

    // 获取支持繁体字的字体列表
    public static List<String> getTraditionalChineseFonts() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontFamilies = ge.getAvailableFontFamilyNames();
        List<String> traditionalChineseFonts = new ArrayList<>();
        for (String fontFamily : fontFamilies) {
            Font font = new Font(fontFamily, Font.PLAIN, 12);
            if (font.canDisplay('聽')) { // 检查是否支持繁体字“聽”
                traditionalChineseFonts.add(fontFamily);
            }
        }
        return traditionalChineseFonts;
    }

    // 生成图片并绘制文字
    public static void generateImage(String text, String fontName, int fontSize) throws Exception {
        int width = 800;  // 图片宽度
        int height = 600; // 图片高度
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();

        // 设置白色背景
        graphics.setBackground(Color.WHITE);
        graphics.clearRect(0, 0, width, height);

        // 设置字体和黑色文字颜色
        Font font = new Font(fontName, Font.PLAIN, fontSize);
        graphics.setFont(font);
        graphics.setColor(Color.BLACK);

        // 计算文字的宽度和高度以居中显示
        FontMetrics metrics = graphics.getFontMetrics();
        int textWidth = metrics.stringWidth(text);
        int textHeight = metrics.getHeight();
        int x = (width - textWidth) / 2;           // 水平居中
        int y = (height - textHeight) / 2 + metrics.getAscent(); // 垂直居中，考虑基线

        // 绘制文字
        graphics.drawString(text, x, y);
        graphics.dispose(); // 释放资源

        // 保存图片到文件
        ImageIO.write(image, "png", new File("20250331.png"));
    }

    // 主方法
    public static void main(String[] args) throws Exception {
        // 获取支持繁体字的字体列表
        List<String> fonts = getTraditionalChineseFonts();
        if (fonts.isEmpty()) {
            System.out.println("未找到支持繁体字的字体。");
            return;
        }

        // 打印所有支持繁体字的字体（供参考）
        System.out.println("可用的繁体字体：");
        for (String font : fonts) {
            System.out.println(font);
        }

        // 选择第一个支持繁体字的字体
        String fontName = fonts.get(0);
        System.out.println("使用的字体：" + fontName);

        // 设置文字内容和字体大小
        String text = "沉浸式英文聽力訓練";
        int fontSize = 36;

        // 生成图片
        generateImage(text, fontName, fontSize);
        System.out.println("图片已生成并保存为 20250331.png");
    }
}
