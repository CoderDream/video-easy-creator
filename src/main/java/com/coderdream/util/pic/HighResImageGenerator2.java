package com.coderdream.util.pic;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class HighResImageGenerator2 {

    // 定义图片生成分辨率
    private static final int IMAGE_WIDTH = 3840;  // 4K 宽度
    private static final int IMAGE_HEIGHT = 2160; // 4K 高度
    private static final int DPI = 300;           // 分辨率

    public static void main(String[] args) throws IOException {
        // 输入资源路径
        Path resourceDir = Paths.get("src", "main", "resources");
        Path backgroundImagePath = resourceDir.resolve("background.png");
        Path outputDir = resourceDir.resolve("output");

        // 创建输出目录
        File outputDirectory = outputDir.toFile();
        if (!outputDirectory.exists()) {
            boolean created = outputDirectory.mkdirs();
            if (!created) {
                System.err.println("无法创建输出目录: " + outputDirectory.getAbsolutePath());
                return;
            }
        }

        // 对象列表示例
        List<Sentence> sentences = List.of(
            new Sentence("你好", "Hello", "[həˈloʊ]"),
            new Sentence("再见", "Goodbye", "[ɡʊdˈbaɪ]"),
            new Sentence("谢谢", "Thank you", "[θæŋk ju]")
        );

        // 加载背景图片
        BufferedImage backgroundImage = ImageIO.read(backgroundImagePath.toFile());

        // 遍历对象列表，生成图片
        for (int i = 0; i < sentences.size(); i++) {
            Sentence sentence = sentences.get(i);
            BufferedImage outputImage = createImageWithText(backgroundImage, sentence);

            // 保存为 PNG 文件
            File outputFile = outputDir.resolve("output_image_" + (i + 1) + ".png").toFile();
            ImageIO.write(outputImage, "png", outputFile);
            System.out.println("已生成图片: " + outputFile.getAbsolutePath());
        }
    }

    /**
     * 在背景图片上绘制文字
     *
     * @param backgroundImage 背景图片
     * @param sentence        要绘制的句子对象
     * @return 生成的图片
     */
    private static BufferedImage createImageWithText(BufferedImage backgroundImage, Sentence sentence) {
        // 创建一个与背景图片大小相同的新 BufferedImage
        BufferedImage outputImage = new BufferedImage(
            IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = outputImage.createGraphics();

        // 渲染背景图片
        graphics.drawImage(backgroundImage, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, null);

        // 设置文字渲染属性
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setFont(new Font("Microsoft YaHei", Font.BOLD, 48));
        graphics.setColor(Color.BLACK);

        // 计算文字位置并绘制
        int x = 100; // 左上角起点X
        int y = 300; // 左上角起点Y
        int lineHeight = 60; // 每行间隔

        graphics.drawString("中文: " + sentence.getChinese(), x, y);
        graphics.drawString("英文: " + sentence.getEnglish(), x, y + lineHeight);
        graphics.drawString("音标: " + sentence.getPhonetic(), x, y + 2 * lineHeight);

        graphics.dispose();
        return outputImage;
    }

    /**
     * 句子对象
     */
    public static class Sentence {
        private final String chinese;
        private final String english;
        private final String phonetic;

        public Sentence(String chinese, String english, String phonetic) {
            this.chinese = chinese;
            this.english = english;
            this.phonetic = phonetic;
        }

        public String getChinese() {
            return chinese;
        }

        public String getEnglish() {
            return english;
        }

        public String getPhonetic() {
            return phonetic;
        }
    }
}
