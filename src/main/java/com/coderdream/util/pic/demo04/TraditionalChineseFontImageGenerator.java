package com.coderdream.util.pic.demo04;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TraditionalChineseFontImageGenerator {

  // 獲取支持繁體字的字體列表
  public static List<String> getTraditionalChineseFonts() {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    String[] fontFamilies = ge.getAvailableFontFamilyNames();
    List<String> traditionalChineseFonts = new ArrayList<>();
    for (String fontFamily : fontFamilies) {
      Font font = new Font(fontFamily, Font.PLAIN, 12);
      if (font.canDisplay('聽')) {
        traditionalChineseFonts.add(fontFamily);
      }
    }
    return traditionalChineseFonts;
  }

  // 生成一張圖片，顯示指定字體的文字
  public static void generateImage(List<String> fonts, String text,
    int fontSize, String imageName) throws Exception {
    int width = 800;  // 圖片寬度
    int lineHeight = 60; // 每行高度（包括文字和間距）
    int height = lineHeight * fonts.size(); // 圖片高度根據字體數量動態調整
    BufferedImage image = new BufferedImage(width, height,
      BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics = image.createGraphics();

    // 設置白色背景
    graphics.setBackground(Color.WHITE);
    graphics.clearRect(0, 0, width, height);

    // 逐行繪製文字
    for (int i = 0; i < fonts.size(); i++) {
      String fontName = fonts.get(i);
      Font font = new Font(fontName, Font.PLAIN, fontSize);
      graphics.setFont(font);
      graphics.setColor(Color.BLACK);

      // 獲取字體度量，實現水平居中
      FontMetrics metrics = graphics.getFontMetrics();
      int textWidth = metrics.stringWidth(text);
      int x = (width - textWidth) / 2; // 水平居中
      int y = (i + 1) * lineHeight - (lineHeight - metrics.getHeight()) / 2
        + metrics.getAscent(); // 垂直位置

      // 繪製文字
      graphics.drawString(text, x, y);
    }

    graphics.dispose(); // 釋放資源

    // 保存圖片
    ImageIO.write(image, "png", new File(imageName));
  }

  // 主方法
  public static void main(String[] args) throws Exception {
    // 獲取支持繁體字的字體列表
    List<String> fonts = getTraditionalChineseFonts();
    if (fonts.isEmpty()) {
      System.out.println("未找到支持繁體字的字體。");
      return;
    }

    // 打印字體數量
    System.out.println("可用的繁體字體數量：" + fonts.size());

    // 設置文字內容和字體大小
    String text = "沉浸式英文聽力訓練";
    int fontSize = 36;

    // 每張圖片顯示的字體數量
    int fontsPerImage = 5;

    // 分組並生成圖片
    for (int i = 0, imageIndex = 1; i < fonts.size();
      i += fontsPerImage, imageIndex++) {
      // 獲取當前組的字體（最多5個）
      List<String> currentFonts = fonts.subList(i,
        Math.min(i + fontsPerImage, fonts.size()));

      // 生成圖片名稱，例如 001.png
      String imageName = String.format("%03d.png", imageIndex);

      // 生成圖片
      generateImage(currentFonts, text, fontSize, imageName);
      System.out.println("已生成圖片：" + imageName);
    }
  }
}
