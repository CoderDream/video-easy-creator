package com.coderdream.util.pic;

import com.coderdream.util.FontSizeConverter;
import com.coderdream.util.chatgpt.TextParserUtilChatgpt;
import com.coderdream.vo.SentenceVO;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HighResImageGenerator3 {

    public static void generateImages(List<SentenceVO> sentenceVOs, String imagePath, String outputDir) throws Exception {
        // 加载背景图片
        BufferedImage templateImage = ImageIO.read(new File(imagePath));
        int width = templateImage.getWidth();
        int height = templateImage.getHeight();

        // 确保输出目录存在
        File dir = new File(outputDir);
        if (!dir.exists()) {
            boolean createDictionaryResult = dir.mkdirs();
            if (!createDictionaryResult) {
                log.error("目录创建失败: {}", dir.getAbsolutePath());
            }
            log.info("目录创建成功: {}", dir.getAbsolutePath());
        }

        // 字体设置
        Font chineseFont = new Font("SimHei", Font.PLAIN, FontSizeConverter.pixelToPoint(18)); // 中文字体
        Font englishFont = new Font("Arial", Font.PLAIN, FontSizeConverter.pixelToPoint(22)); // 英文字体
        Font phoneticsFont = new Font("Arial Unicode MS", Font.PLAIN, FontSizeConverter.pixelToPoint(18)); // 音标字体

        for (int i = 0; i < sentenceVOs.size(); i++) {
            SentenceVO sentenceVO = sentenceVOs.get(i);

            // 创建高清缓冲区
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = bufferedImage.createGraphics();

            // 绘制背景图片
            g2d.drawImage(templateImage, 0, 0, null);

            // 设置抗锯齿和其他渲染属性
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // 测量每一段文本的宽度
            FontMetrics chineseMetrics = g2d.getFontMetrics(chineseFont);
            FontMetrics englishMetrics = g2d.getFontMetrics(englishFont);
            FontMetrics phoneticsMetrics = g2d.getFontMetrics(phoneticsFont);

            int chineseWidth = chineseMetrics.stringWidth(sentenceVO.getChinese());
            int englishWidth = englishMetrics.stringWidth(sentenceVO.getEnglish());
            int phoneticsWidth = phoneticsMetrics.stringWidth(sentenceVO.getPhonetics());

            // 文本整体居中位置
            int centerX = width / 2;
            int startY = height / 2 - 100; // 起始Y位置，整体上移一些

            // 绘制中文（第一行）
            g2d.setFont(chineseFont);
            g2d.setColor(Color.BLACK);
            g2d.drawString(sentenceVO.getChinese(), centerX - chineseWidth / 2, startY);

            // 绘制英文（第二行）
            g2d.setFont(englishFont);
            g2d.setColor(Color.BLUE);
            g2d.drawString(sentenceVO.getEnglish(), centerX - englishWidth / 2, startY + 100);

            // 绘制音标（第三行）
            g2d.setFont(phoneticsFont);
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawString(sentenceVO.getPhonetics(), centerX - phoneticsWidth / 2, startY + 200);

            // 释放资源
            g2d.dispose();

            // 输出文件
            File outputFile = new File(outputDir + File.separator + "output_" + (i + 1) + ".png");
            ImageIO.write(bufferedImage, "png", outputFile);
        }
    }


    public static void main(String[] args) throws Exception {
        // 示例数据
//        List<SentenceVO> sentenceVOs = List.of(
//            new SentenceVO("你好", "Hello", "[həˈloʊ]"),
//            new SentenceVO("谢谢", "Thank you", "[θæŋk juː]")
//        );

        String filePath = "src/main/resources/CampingInvitation.txt";
        log.info("开始解析文件: {}", filePath);

        List<SentenceVO> sentenceVOs = TextParserUtilChatgpt.parseFileToSentenceVOsSingleLine(filePath);

        // 设置路径
        String imagePath = "src/main/resources/background.png"; // 背景图片
        String outputDir = "src/main/resources/output"; // 输出目录

        // 调用生成方法
        generateImages(sentenceVOs, imagePath, outputDir);
    }
}
