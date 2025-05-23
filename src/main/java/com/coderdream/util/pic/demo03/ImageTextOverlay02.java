package com.coderdream.util.pic.demo03;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageTextOverlay02 {
    public static void main(String[] args) throws IOException {
        // 加载背景图
        BufferedImage image = ImageIO.read(new File("D:\\0000\\0007_Trump\\20250303\\cover_002.jpg"));
        Graphics2D g2d = image.createGraphics();

        // 开启抗锯齿，提升文字平滑度
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 设置字体：黑体 + 粗体，适配繁体字
        Font mainFont = new Font("Microsoft YaHei", Font.BOLD, 80); // 第一、二行字体
        Font bottomFont = new Font("Microsoft YaHei", Font.BOLD, 70); // 底部字体

        // 文字内容
        String mainText = "臺積電加碼千億赴美"; // 第一行文字
        String subText = "中英大字幕";          // 第二行文字
        String bottomText = "川普說 沒晶片世界無法運轉"; // 第三行文字

        // 1. 绘制主标题 (黄色字+黑色描边，居中靠上)
        g2d.setFont(mainFont);
        drawCenteredOutlinedText(g2d, mainText, image.getWidth(), 450, Color.YELLOW, Color.BLACK);

        // 2. 绘制副标题 (黄色字+黑色描边，右下角，微调 x, y 控制位置)
        Font subFont = new Font("Microsoft YaHei", Font.BOLD, 70);
        g2d.setFont(subFont);
        drawOutlinedText(g2d, subText, image.getWidth() - 500, image.getHeight() - 300, Color.YELLOW, Color.BLACK);

        // 3. 绘制底部背景 (蓝色矩形)
        int bottomHeight = 164; // 底部背景高度
        g2d.setColor(new Color(0, 57, 166)); // 深蓝色背景
        g2d.fillRect(0, image.getHeight() - bottomHeight, image.getWidth(), bottomHeight);

        // 4. 绘制底部大标题 (白色字+黑色描边，居中)
        g2d.setFont(bottomFont);
        drawCenteredOutlinedText(g2d, bottomText, image.getWidth(), image.getHeight() - 80, Color.WHITE, Color.BLACK);

        // 保存新图像
        ImageIO.write(image, "jpg", new File("D:\\0000\\0007_Trump\\20250303\\cover_005.jpg"));
        g2d.dispose();
    }


    // 居中带描边文字
    private static void drawCenteredOutlinedText(Graphics2D g2d, String text, int width, int y, Color fillColor, Color outlineColor) {
        FontRenderContext frc = g2d.getFontRenderContext();
        Rectangle2D bounds = g2d.getFont().getStringBounds(text, frc);
        int x = (int) ((width - bounds.getWidth()) / 2);
        drawOutlinedText(g2d, text, x, y, fillColor, outlineColor);
    }

    // 绘制描边文字
    private static void drawOutlinedText(Graphics2D g2d, String text, int x, int y, Color fillColor, Color outlineColor) {
        g2d.setStroke(new BasicStroke(8)); // 设置描边宽度
        g2d.setColor(outlineColor); // 先画描边
        g2d.drawString(text, x, y);
        g2d.setColor(fillColor); // 再画填充
        g2d.drawString(text, x, y);
    }
}
