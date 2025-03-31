package com.coderdream.util.pic.demo04;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FontImageGenerator {

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

        // 開啟抗鋸齒
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 設置字體（80號加粗）
        Font font = new Font(fontName, Font.BOLD, fontSize);
        graphics.setFont(font);

        // 組合文字
        String displayText = text + " + " + fontName;

        // 計算文字居中位置
        FontMetrics metrics = graphics.getFontMetrics();
        int textWidth = metrics.stringWidth(displayText);
        int textHeight = metrics.getHeight();
        int x = (image.getWidth() - textWidth) / 2;
        int y = (image.getHeight() - textHeight) / 2 + metrics.getAscent();

        // 繪製帶 3D 描邊效果的文字
        draw3DOutlinedText(graphics, displayText, x, y, Color.YELLOW, Color.BLACK);

        graphics.dispose(); // 釋放資源

        // 保存圖片
        ImageIO.write(image, "png", new File(outputPath));
    }

    // 繪製帶多層描邊和陰影的文字
    private static void draw3DOutlinedText(Graphics2D g2d, String text, int x, int y, Color fillColor, Color outlineColor) {
        // 設置陰影
        g2d.setColor(new Color(0, 0, 0, 80)); // 半透明黑色陰影
        g2d.drawString(text, x + 1, y + 1);

        // 設置描邊
        g2d.setStroke(new BasicStroke(6));
        g2d.setColor(outlineColor);
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                if (i != 0 || j != 0) {
                    g2d.drawString(text, x + i, y + j);
                }
            }
        }

        // 填充文字
        g2d.setColor(fillColor);
        g2d.drawString(text, x, y);
    }

    // 主方法
    public static void main(String[] args) throws Exception {
        // 獲取支持繁體字的字體列表
        List<String> fonts = getTraditionalChineseFonts(); // 修正錯誤，直接調用 getTraditionalChineseFonts
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
