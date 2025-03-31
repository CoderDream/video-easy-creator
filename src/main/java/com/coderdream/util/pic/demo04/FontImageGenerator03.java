package com.coderdream.util.pic.demo04;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FontImageGenerator03 {

    // 獲取支持繁體字的字體列表
    public static List<String> getTraditionalChineseFonts() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontFamilies = ge.getAvailableFontFamilyNames();
        List<String> traditionalChineseFonts = new ArrayList<>();
        for (String fontFamily : fontFamilies) {
            Font font = new Font(fontFamily, Font.PLAIN, 12);
            if (font.canDisplay('聽')) { // 以「聽」字測試是否支持繁體字
                traditionalChineseFonts.add(fontFamily);
            }
        }
        return traditionalChineseFonts;
    }

    // 在圖片上添加文字並保存
    public static void addTextToImage(String imagePath, String text, String fontName, int fontSize, String outputPath) throws Exception {
        // 讀取圖片
        BufferedImage image = ImageIO.read(new File(imagePath));
        Graphics2D graphics = image.createGraphics();

        // 設置字體（加粗，80號）
        Font font = new Font(fontName, Font.BOLD, fontSize);
        graphics.setFont(font);
        graphics.setColor(Color.YELLOW); // 設置文字顏色為黃色

        // 組合文字
        String displayText = text + " + " + fontName;

        // 計算文字位置（水平居中，垂直居中）
        FontMetrics metrics = graphics.getFontMetrics();
        int textWidth = metrics.stringWidth(displayText);
        int textHeight = metrics.getHeight();
        int x = (image.getWidth() - textWidth) / 2;
        int y = (image.getHeight() - textHeight) / 2 + metrics.getAscent();

        // 繪製文字
        graphics.drawString(displayText, x, y);
        graphics.dispose(); // 釋放資源

        // 保存圖片
        ImageIO.write(image, "png", new File(outputPath));
    }

    // 主方法
    public static void main(String[] args) throws Exception {
        // 獲取支持繁體字的字體列表
        List<String> fonts = getTraditionalChineseFonts();
        if (fonts.isEmpty()) {
            System.out.println("未找到支持繁體字的字體。");
            return;
        }

        // 設置參數
        String imagePath = "D:\\0000\\logo\\head0004.png";
        String text = "沉浸式英文聽力訓練";
        int fontSize = 80; // 字體大小設置為80

        // 為每種字體生成一張圖片
        for (int i = 0; i < fonts.size(); i++) {
            String fontName = fonts.get(i);
            String outputPath = String.format("D:\\0000\\logo\\head0004_%03d.png", i + 1);
            addTextToImage(imagePath, text, fontName, fontSize, outputPath);
            System.out.println("已生成圖片：" + outputPath);
        }
    }
}
