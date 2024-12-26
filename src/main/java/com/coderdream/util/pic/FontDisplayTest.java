package com.coderdream.util.pic;

import javax.swing.*;
import java.awt.*;

public class FontDisplayTest extends JPanel {

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 获取所有字体名称
        String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();

        int y = 20; // 初始 Y 坐标

        for (String fontName : fontNames) {
            // 设置字体
            g2d.setFont(new Font(fontName, Font.PLAIN, 16));

            // 在屏幕上显示当前字体的名称和示例文字
            g2d.drawString(fontName + ": 测试中文 English 123", 10, y);

            // Y 坐标下移
            y += 20;

            // 避免超过窗口高度
            if (y > getHeight() - 20) {
                break;
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("字体测试");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        FontDisplayTest panel = new FontDisplayTest();
        frame.add(panel);
        frame.setSize(800, 600); // 设置窗口大小
        frame.setVisible(true);
    }
}
