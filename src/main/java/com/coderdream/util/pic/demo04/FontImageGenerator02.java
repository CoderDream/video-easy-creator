package com.coderdream.util.pic.demo04;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FontImageGenerator02 {

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

    // 生成單張圖片
    public static void generateImage(List<String> fonts, String text, int fontSize, String imageName, int width, int height) throws Exception {
        // 創建1920x1080的圖片
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();

        // 設置白色背景
        graphics.setBackground(Color.WHITE);
        graphics.clearRect(0, 0, width, height);

        // 計算垂直間距
        int totalFonts = fonts.size();
        int lineHeight = height / (totalFonts + 1); // 均勻分佈

        // 繪製每行文字
        for (int i = 0; i < totalFonts; i++) {
            String fontName = fonts.get(i);
            Font font = new Font(fontName, Font.PLAIN, fontSize);
            graphics.setFont(font);
            graphics.setColor(Color.BLACK);

            // 組合文字
            String displayText = text + " + " + fontName;

            // 水平居中
            FontMetrics metrics = graphics.getFontMetrics();
            int textWidth = metrics.stringWidth(displayText);
            int x = (width - textWidth) / 2;
            int y = (i + 1) * lineHeight;

            // 繪製文字
            graphics.drawString(displayText, x, y);
        }

        graphics.dispose(); // 釋放資源

        // 保存圖片
        ImageIO.write(image, "png", new File(imageName));
    }

    // 主方法
    public static void main(String[] args) throws Exception {
        // 獲取字體列表
        List<String> fonts = getTraditionalChineseFonts();
        if (fonts.isEmpty()) {
            System.out.println("未找到支持繁體字的字體。");
            return;
        }

        // 打印字體數量
        System.out.println("可用的繁體字體數量：" + fonts.size());

        // 設置參數
        String text = "沉浸式英文聽力訓練";
        int fontSize = 36;
        int width = 1920;
        int height = 1080;
        int fontsPerImage = 5;

        // 分組生成圖片
        for (int i = 0, imageIndex = 1; i < fonts.size(); i += fontsPerImage, imageIndex++) {
            List<String> currentFonts = fonts.subList(i, Math.min(i + fontsPerImage, fonts.size()));
            String imageName = String.format("%03d.png", imageIndex);
            generateImage(currentFonts, text, fontSize, imageName, width, height);
            System.out.println("已生成圖片：" + imageName);
        }
    }
}
