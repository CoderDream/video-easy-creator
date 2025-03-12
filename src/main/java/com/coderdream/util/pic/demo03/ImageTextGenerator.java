package com.coderdream.util.pic.demo03;

import com.github.houbb.opencc4j.util.ZhConverterUtil;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageTextGenerator {
    public static void main(String[] args) throws IOException {
        // 1. 加载背景图片
        BufferedImage image = ImageIO.read(new File("D:\\0000\\0007_Trump\\20250303\\cover_002.jpg"));

        // 2. 创建 Graphics2D 对象
        Graphics2D g2d = image.createGraphics();

        // 抗锯齿设置，提升文字平滑度
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 3. 设置第一行文字样式
        g2d.setFont(new Font("SansSerif", Font.BOLD, 80));
        g2d.setColor(new Color(255, 255, 153)); // 黄色字体

        // 绘制第一行文字
        drawCenteredText(g2d, ZhConverterUtil.toTraditional("台積電加碼千億赴美"), image.getWidth(), 460);

        // 4. 设置右侧小标题文字样式
        g2d.setFont(new Font("SansSerif", Font.BOLD, 50));
        g2d.setColor(new Color(255, 204, 0)); // 金黄色
        drawText(g2d, ZhConverterUtil.toTraditional("中英大字幕"), image.getWidth() - 340, 580);

        // 5. 设置底部蓝色背景和白色文字
        g2d.setColor(new Color(0, 51, 153)); // 深蓝色背景
        g2d.fillRect(0, image.getHeight() - 150, image.getWidth(), 150);

        g2d.setFont(new Font("SansSerif", Font.BOLD, 100));
        g2d.setColor(Color.WHITE);
        drawCenteredText(g2d, ZhConverterUtil.toTraditional("川普說 沒晶片世界無法運轉"), image.getWidth(), image.getHeight() - 50);

        // 6. 释放资源
        g2d.dispose();

        // 7. 保存输出图片
        ImageIO.write(image, "jpg", new File("D:\\0000\\0007_Trump\\20250303\\cover_003.jpg"));
        System.out.println("图片生成成功！");
    }

    // 居中绘制文字方法
    private static void drawCenteredText(Graphics2D g2d, String text, int width, int y) {
        FontRenderContext frc = g2d.getFontRenderContext();
        Rectangle2D bounds = g2d.getFont().getStringBounds(text, frc);
        int x = (int) ((width - bounds.getWidth()) / 2);
        g2d.drawString(text, x, y);
    }

    // 指定位置绘制文字方法
    private static void drawText(Graphics2D g2d, String text, int x, int y) {
        g2d.drawString(text, x, y);
    }
}
