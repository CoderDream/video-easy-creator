package com.coderdream.util.pic;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.coderdream.util.FontSizeConverter;
import com.coderdream.util.chatgpt.TextParserUtil;
import com.coderdream.vo.SentenceVO;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HighResImageGenerator4 {

  /**
   * 生成带有文本的图片。
   *
   * @param sentenceVOs 要显示的文本内容列表
   * @param imagePath   背景图片路径
   * @param outputDir   输出目录
   */
  public static void generateImages(List<SentenceVO> sentenceVOs,
    String imagePath, String fileName, String outputDir) throws Exception {
    // 加载背景图片
    BufferedImage templateImage = ImageIO.read(new File(imagePath));
    int width = templateImage.getWidth();
    int height = templateImage.getHeight();

    // 确保输出目录存在
    File dir = new File(outputDir);
    if (!dir.exists() && dir.mkdirs()) {
      log.info("目录创建成功: {}", dir.getAbsolutePath());
    }

    // 字体设置
    Font noFont = new Font("Arial", Font.BOLD,
      FontSizeConverter.pixelToPoint(54)); // 页码字体
    Font englishFont = new Font("Arial", Font.PLAIN,
      FontSizeConverter.pixelToPoint(72)); // 英文字体
    Font phoneticsFont = new Font("Arial Unicode MS", Font.PLAIN,
      FontSizeConverter.pixelToPoint(68)); // 音标字体
    Font chineseFont = new Font("SimHei", Font.PLAIN,
      FontSizeConverter.pixelToPoint(68)); // 中文字体

    for (int i = 0; i < sentenceVOs.size(); i++) {
      SentenceVO sentenceVO = sentenceVOs.get(i);

      // 创建高清缓冲区
      BufferedImage bufferedImage = new BufferedImage(width, height,
        BufferedImage.TYPE_INT_RGB);
      Graphics2D g2d = bufferedImage.createGraphics();

      // 绘制背景图片
      g2d.drawImage(templateImage, 0, 0, null);

      // 设置抗锯齿和其他渲染属性
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

      // 绘制页码
      drawPageNumber(g2d, noFont, width, sentenceVOs.size(), i + 1);

      // 测量文本宽度
      FontMetrics englishMetrics = g2d.getFontMetrics(englishFont);
      FontMetrics phoneticsMetrics = g2d.getFontMetrics(phoneticsFont);
      FontMetrics chineseMetrics = g2d.getFontMetrics(chineseFont);

      int englishWidth = englishMetrics.stringWidth(sentenceVO.getEnglish());
      int phoneticsWidth = phoneticsMetrics.stringWidth(
        sentenceVO.getPhonetics());
      int chineseWidth = chineseMetrics.stringWidth(sentenceVO.getChinese());

      // 文本整体居中位置
      int centerX = width / 2;
      int startY = height / 2 - 100; // 起始Y位置，整体上移一些

      // 绘制英文（第一行）
      g2d.setFont(englishFont);
      g2d.setColor(Color.WHITE);
      g2d.drawString(sentenceVO.getEnglish(), centerX - englishWidth / 2,
        startY);

      // 绘制音标（第二行）
      g2d.setFont(phoneticsFont);
      g2d.setColor(Color.YELLOW);
      g2d.drawString(sentenceVO.getPhonetics(), centerX - phoneticsWidth / 2,
        startY + 100);

      // 绘制中文（第三行）
      g2d.setFont(chineseFont);
      g2d.setColor(Color.WHITE);
      g2d.drawString(sentenceVO.getChinese(), centerX - chineseWidth / 2,
        startY + 200);

      // 释放资源
      g2d.dispose();

      int number = i + 1;
      // 输出文件
      File outputFile = new File(
        outputDir + File.separator + fileName + "_" + DateUtil.format(
          new Date(), DatePattern.PURE_DATETIME_PATTERN) + "_"
          + MessageFormat.format(
          "{0,number,000}",
          number) + ".png");
      ImageIO.write(bufferedImage, "png", outputFile);
    }
  }

  /**
   * 生成带有文本的图片。
   *
   * @param sentenceVOs 要显示的文本内容列表
   * @param imagePath   背景图片路径
   * @param outputDir   输出目录
   */
  public static void generateImages(List<SentenceVO> sentenceVOs,
    String imagePath, String fileName, String outputDir, String fontNameCn,
    String fontNameEn, String fontNamePh, String fontNameNo) throws Exception {
    // 加载背景图片
    BufferedImage templateImage = ImageIO.read(new File(imagePath));
    int width = templateImage.getWidth();
    int height = templateImage.getHeight();

    // 确保输出目录存在
    File dir = new File(outputDir);
    if (!dir.exists() && dir.mkdirs()) {
      log.info("目录创建成功: {}", dir.getAbsolutePath());
    }

    // 字体设置
    Font noFont = new Font(fontNameNo, Font.BOLD,
      FontSizeConverter.pixelToPoint(54)); // 页码字体
    Font englishFont = new Font(fontNameEn, Font.PLAIN,
      FontSizeConverter.pixelToPoint(72)); // 英文字体
    Font phoneticsFont = new Font(fontNamePh, Font.PLAIN,
      FontSizeConverter.pixelToPoint(68)); // 音标字体
    Font chineseFont = new Font(fontNameCn, Font.PLAIN,
      FontSizeConverter.pixelToPoint(68)); // 中文字体

    for (int i = 0; i < sentenceVOs.size(); i++) {
      SentenceVO sentenceVO = sentenceVOs.get(i);

      // 创建高清缓冲区
      BufferedImage bufferedImage = new BufferedImage(width, height,
        BufferedImage.TYPE_INT_RGB);
      Graphics2D g2d = bufferedImage.createGraphics();

      // 绘制背景图片
      g2d.drawImage(templateImage, 0, 0, null);

      // 设置抗锯齿和其他渲染属性
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

      // 绘制页码
      drawPageNumber(g2d, noFont, width, sentenceVOs.size(), i + 1);

      // 测量文本宽度
      FontMetrics englishMetrics = g2d.getFontMetrics(englishFont);
      FontMetrics phoneticsMetrics = g2d.getFontMetrics(phoneticsFont);
      FontMetrics chineseMetrics = g2d.getFontMetrics(chineseFont);

      int englishWidth = englishMetrics.stringWidth(sentenceVO.getEnglish());
      int phoneticsWidth = phoneticsMetrics.stringWidth(
        sentenceVO.getPhonetics());
      int chineseWidth = chineseMetrics.stringWidth(sentenceVO.getChinese());

      // 文本整体居中位置
      int centerX = width / 2;
      int startY = height / 2 - 100; // 起始Y位置，整体上移一些

      // 绘制英文（第一行）
      g2d.setFont(englishFont);
      g2d.setColor(Color.WHITE);
      g2d.drawString(sentenceVO.getEnglish(), centerX - englishWidth / 2,
        startY);

      // 绘制音标（第二行）
      g2d.setFont(phoneticsFont);
      g2d.setColor(Color.YELLOW);
      g2d.drawString(sentenceVO.getPhonetics(), centerX - phoneticsWidth / 2,
        startY + 100);

      // 绘制中文（第三行）
      g2d.setFont(chineseFont);
      g2d.setColor(Color.WHITE);
      g2d.drawString(sentenceVO.getChinese(), centerX - chineseWidth / 2,
        startY + 200);

      // 释放资源
      g2d.dispose();

      int number = i + 1;
      // 输出文件
      File outputFile = new File(
        outputDir + File.separator + fileName + "_" + fontNameCn + "_"
          + MessageFormat.format(
          "{0,number,000}",
          number) + ".png");
      ImageIO.write(bufferedImage, "png", outputFile);
    }
  }

  /**
   * 绘制页码。
   *
   * @param g2d     Graphics2D 对象
   * @param font    页码字体
   * @param width   图片宽度
   * @param total   总页数
   * @param current 当前页码
   */
  private static void drawPageNumber(Graphics2D g2d, Font font, int width,
    int total, int current) {
    g2d.setFont(font);
    g2d.setColor(Color.GRAY);
    String pageNumber = "Page " + current + " of " + total;
    int textWidth = g2d.getFontMetrics().stringWidth(pageNumber);
    int x = 40; // 左侧边距
    int y = 60; // 顶部边距
    g2d.drawString(pageNumber, x, y);
  }

  public static void main(String[] args) throws Exception {
    // 示例数据
    String filePath = "src/main/resources";
    String fileName = "CampingInvitation_02";
    log.info("开始解析文件: {}", filePath);
    String fullPath = filePath + File.separator + fileName + ".txt";
    List<SentenceVO> sentenceVOs = TextParserUtil.parseFileToSentenceVOs(
      fullPath);

    // 设置路径
    String imagePath = "src/main/resources/background.png"; // 背景图片
    String outputDir = "src/main/resources/pic"; // 输出目录

//    // 调用生成方法
//    generateImages(sentenceVOs, imagePath, fileName, outputDir);

    String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment()
      .getAvailableFontFamilyNames();

    for (String fontName : fontNames) {
      System.out.println(fontName);
      // 调用生成方法
      generateImages(sentenceVOs, imagePath, fileName, outputDir, fontName,
        fontName, fontName, fontName);

    }
  }
}
