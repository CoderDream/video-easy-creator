package com.coderdream.util.pic.demo04;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FontDemo {

    public static void main(String[] args) {

        // 1. 列出所有可用字体
        List<String> availableFonts = getAvailableFonts();
        System.out.println("可用字体数量: " + availableFonts.size());

        // 过滤出包含中文的字体
        List<String> chineseFonts = filterChineseFonts(availableFonts);

        // 打印包含中文的字体
        System.out.println("\n包含中文的字体: ");
        for (String fontName : chineseFonts) {
            System.out.println(fontName);
        }

        // 2.  选择一个繁体字体
        String fontName = "MingLiU"; // 尝试使用明體或其他繁体字体，根据你实际安装的字体调整
        if (!chineseFonts.contains(fontName)) {
            System.out.println("字体 " + fontName + " 未找到，使用默认字体.");
            fontName = "Dialog"; // 如果找不到指定的繁体字体，使用一个默认字体
        }

        // 3. 要写入的繁体中文文字
        String text = "沉浸式英文聽力訓練"; //  示例繁体中文

        // 4. 生成图片
        try {
            generateImageWithText(fontName, text, "20250331.png");
            System.out.println("图片已生成: 20250331.png");
        } catch (IOException e) {
            System.err.println("生成图片失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 获取所有可用字体
     *
     * @return 字体名称列表
     */
    public static List<String> getAvailableFonts() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = ge.getAvailableFontFamilyNames();
        List<String> fonts = new ArrayList<>();
        for (String fontName : fontNames) {
            fonts.add(fontName);
        }
        return fonts;
    }

    /**
     * 过滤出支持中文的字体
     * @param fonts 所有字体列表
     * @return 支持中文的字体列表
     */
    public static List<String> filterChineseFonts(List<String> fonts) {
        List<String> chineseFonts = new ArrayList<>();
        for (String fontName : fonts) {
            Font font = new Font(fontName, Font.PLAIN, 12); // 使用字体创建Font对象
            if (font.canDisplayUpTo("你好")  != -1) { // 检查字体是否支持中文
                System.out.println(fontName + " 支持中文");
                chineseFonts.add(fontName);
            }
        }
        return chineseFonts;
    }

    /**
     *  生成带有文字的图片
     *
     * @param fontName 字体名称
     * @param text     要写入的文字
     * @param filePath 图片保存路径
     * @throws IOException IO异常
     */
    public static void generateImageWithText(String fontName, String text, String filePath) throws IOException {
        int width = 500;
        int height = 200;

        // 1. 创建BufferedImage对象
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // 2. 获取Graphics2D对象
        Graphics2D g2d = image.createGraphics();

        // 3. 设置背景色 (可选)
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // 4. 设置字体
        Font font = new Font(fontName, Font.PLAIN, 36);
        g2d.setFont(font);
        g2d.setColor(Color.BLACK);

        // 5. 计算文字的中心位置
        FontRenderContext frc = g2d.getFontRenderContext();
        Rectangle2D r = font.getStringBounds(text, frc);
        int x = (int) ((width - r.getWidth()) / 2);
        int y = (int) ((height - r.getHeight()) / 2 - r.getY()); // 考虑基线

        // 6. 绘制文字
        g2d.drawString(text, x, y);

        // 7. 释放Graphics2D资源
        g2d.dispose();

        // 8. 保存图片
        ImageIO.write(image, "png", new File(filePath));
    }
}
