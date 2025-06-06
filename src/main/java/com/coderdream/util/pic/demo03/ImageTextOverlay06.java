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

public class ImageTextOverlay06 {
    private static final Color MAIN_TITLE_COLOR = new Color(255, 247, 149); // 主标题颜色
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 80); // 主标题和副标题阴影颜色
    private static final Color SUB_TITLE_COLOR = Color.YELLOW; // 副标题颜色
    private static final Color BOTTOM_SHADOW_COLOR = new Color(50, 50, 50, 80); // 底部标题阴影颜色（深灰色）

    public static void main(String[] args) throws IOException {
        // 加载背景图
        BufferedImage image = ImageIO.read(new File("D:\\0000\\0007_Trump\\20250303\\cover_002.jpg"));
        Graphics2D g2d = image.createGraphics();

        // 开启抗锯齿，提升文字平滑度
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 设置字体：黑体 + 粗体，适配繁体字
        Font mainFont = new Font("Source Han Sans Heavy", Font.PLAIN, 110); // 主标题字体
        Font subFont = new Font("Microsoft YaHei", Font.BOLD, 74); // 副标题字体
        Font bottomFont = new Font("Source Han Sans Heavy", Font.PLAIN, 100); // 底部字体

        // 文字内容
        String mainText = "臺積電加碼千億赴美"; // 主标题
        String subText = "中英大字幕";          // 副标题
        String bottomText = "川普說 沒晶片世界無法運轉"; // 底部文字

        // 1. 绘制主标题 (黄色字+多层描边+薄阴影，居中靠上)
        g2d.setFont(mainFont);
        drawCentered3DOutlinedText(g2d, mainText, image.getWidth(), 400, MAIN_TITLE_COLOR, Color.BLACK);

        // 2. 绘制副标题 (黄色字+多层描边+薄阴影，右下角)
        g2d.setFont(subFont);
        draw3DOutlinedText(g2d, subText, image.getWidth() - 400, image.getHeight() - 210, SUB_TITLE_COLOR, Color.BLACK);

        // 3. 绘制底部背景 (蓝色矩形)
        int bottomHeight = 146; // 底部背景高度
        g2d.setColor(new Color(0, 57, 166)); // 深蓝色背景
        g2d.fillRect(0, image.getHeight() - bottomHeight, image.getWidth(), bottomHeight);

        // 4. 绘制底部大标题 (白色字+黑色描边+薄阴影，居中)
        g2d.setFont(bottomFont);
        drawCentered3DOutlinedText(g2d, bottomText, image.getWidth(), image.getHeight() - 45, Color.WHITE, Color.BLACK);

        // 保存新图像
        ImageIO.write(image, "jpg", new File("D:\\0000\\0007_Trump\\20250303\\cover_007.jpg"));
        g2d.dispose();
    }

    // 居中带描边和薄阴影的3D文字
    private static void drawCentered3DOutlinedText(Graphics2D g2d, String text, int width, int y, Color fillColor, Color outlineColor) {
        FontRenderContext frc = g2d.getFontRenderContext();
        Rectangle2D bounds = g2d.getFont().getStringBounds(text, frc);
        int x = (int) ((width - bounds.getWidth()) / 2);
        draw3DOutlinedText(g2d, text, x, y, fillColor, outlineColor);
    }

    // 绘制带多层描边和薄阴影的文字
    private static void draw3DOutlinedText(Graphics2D g2d, String text, int x, int y, Color fillColor, Color outlineColor) {
        // 选择阴影颜色：主标题和副标题用 SHADOW_COLOR，底部标题用 BOTTOM_SHADOW_COLOR
        Color shadowColor = (fillColor == Color.WHITE) ? BOTTOM_SHADOW_COLOR : SHADOW_COLOR;

        // 绘制薄阴影 (单层，偏移1像素)
        g2d.setColor(shadowColor);
        g2d.drawString(text, x + 1, y + 1); // 单层阴影，保持薄效果

        // 绘制多层描边
        g2d.setStroke(new BasicStroke(6)); // 保持描边宽度
        g2d.setColor(outlineColor);
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                if (i != 0 || j != 0) {
                    g2d.drawString(text, x + i, y + j); // 多次偏移绘制描边
                }
            }
        }

        // 绘制填充文字
        g2d.setColor(fillColor);
        g2d.drawString(text, x, y);
    }

    // 绘制基础描边文字 (保留备用)
    private static void drawOutlinedText(Graphics2D g2d, String text, int x, int y, Color fillColor, Color outlineColor) {
        g2d.setStroke(new BasicStroke(4)); // 设置基础描边宽度
        g2d.setColor(outlineColor); // 先画描边
        g2d.drawString(text, x, y);
        g2d.setColor(fillColor); // 再画填充
        g2d.drawString(text, x, y);
    }
}
