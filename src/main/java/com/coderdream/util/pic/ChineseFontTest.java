package com.coderdream.util.pic;

import javax.swing.*;
import java.awt.*;

public class ChineseFontTest extends JPanel {

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 设置反锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // 设置字体及大小
        g2d.setFont(new Font("Microsoft YaHei", Font.PLAIN, 20));

        // 绘制中文文本
        g2d.drawString("测试中文字体显示", 50, 50);

        // 改变字体继续绘制
        g2d.setFont(new Font("SimSun", Font.PLAIN, 20));
        g2d.drawString("宋体中文显示", 50, 100);

        g2d.setFont(new Font("KaiTi", Font.PLAIN, 20));
        g2d.drawString("楷体中文显示", 50, 150);

        g2d.setFont(new Font("SimHei", Font.PLAIN, 20));
        g2d.drawString("黑体中文显示", 50, 200);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("中文字体测试");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChineseFontTest());
        frame.setSize(400, 300);
        frame.setVisible(true);
    }
}
