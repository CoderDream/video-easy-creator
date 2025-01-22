package com.coderdream.util.pic;

import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.FontSizeConverter;
import com.coderdream.util.chatgpt.TextParserUtilChatgpt;
import com.coderdream.util.sentence.SentenceParser;
import com.coderdream.util.video.BatchCreateVideoCommonUtil;
import com.coderdream.vo.SentenceVO;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Objects;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HighResImageVideoUtil5 {

  /**
   * 生成带有文本的图片。
   *
   * @param fileName 文件名称
   * @return 图片文件列表
   */
  public static List<File> generateImages(String fileName) {
    // 设置路径
    String imagePath = CdConstants.RESOURCES_BASE_PATH + File.separator
      + CdConstants.BACKGROUND_IMAGE_FILENAME;// "src/main/resources/background.png"; // 背景图片
    return generateImages(imagePath, fileName);
  }

  /**
   * 生成带有文本的图片。
   *
   * @param backgroundImageName 背景图片名称
   * @param fileName            文件名称
   * @return 图片文件列表
   */
  public static List<File> generateImages(String backgroundImageName,
    String fileName) {
    String fullPath =
      CdConstants.RESOURCES_BASE_PATH + File.separator + fileName + ".txt";
//    fullPath = fileName + ".txt";
    List<SentenceVO> sentenceVOs = TextParserUtilChatgpt.parseFileToSentenceVOsSingleLine(
      fullPath);

    // 设置路径
    String outputDir = BatchCreateVideoCommonUtil.getPicPath(
      fileName);//  "src/main/resources/pic"; // 输出目录

    return generateImages(sentenceVOs, backgroundImageName, fileName,
      outputDir);
  }


  /**
   * 生成带有文本的图片。
   *
   * @param backgroundImageName 背景图片名称
   * @param contentFileName            文件名称
   * @return 图片文件列表
   */
  public static List<File> generateImages(String backgroundImageName,
    String filePath,
    String contentFileName) {
    String fullPath = filePath + contentFileName + ".txt";

    List<SentenceVO> sentenceVOs = SentenceParser.parseSentencesFromFile(
      fullPath);

    // 设置路径
    String outputDir = filePath + "/pic/"; // 输出目录

    return generateImages(sentenceVOs, backgroundImageName, contentFileName,
      outputDir);
  }


  /**
   * 生成带有文本的图片。
   *
   * @param sentenceVOs 要显示的文本内容列表
   * @param imagePath   背景图片路径
   * @param outputDir   输出目录
   * @return 图片文件列表
   */
  public static List<File> generateImages(List<SentenceVO> sentenceVOs,
    String imagePath, String fileName, String outputDir) {
    long startTime = System.currentTimeMillis(); // 记录开始时间
    List<File> imageFiles = new ArrayList<>();
    // 检查文本内容是否为空
    if (sentenceVOs == null || sentenceVOs.isEmpty()) {
      log.warn("没有找到文本内容，不生成图片");
      return imageFiles;
    }
    // 加载背景图片
    BufferedImage templateImage;
    try {
      templateImage = ImageIO.read(new File(imagePath));
    } catch (IOException e) {
      log.error("加载背景图片失败: {}", e.getMessage(), e);
      throw new RuntimeException(e);
    }
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
      FontSizeConverter.pixelToPoint(80)); // 英文字体 68
    Font phoneticsFont = new Font("Arial Unicode MS", Font.PLAIN,
      FontSizeConverter.pixelToPoint(80)); // 音标字体 64
    Font chineseFont = new Font("SimHei", Font.PLAIN,
      FontSizeConverter.pixelToPoint(96)); // 中文字体 80

    for (int i = 0; i < Objects.requireNonNull(sentenceVOs).size(); i++) {
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
        outputDir + File.separator + fileName + "_" + MessageFormat.format(
          "{0,number,000}",
          number) + ".png");
      try {
        ImageIO.write(bufferedImage, "png", outputFile);
        imageFiles.add(outputFile);
      } catch (IOException e) {
        log.error("图片保存失败: {}", outputFile.getAbsolutePath(), e);
        throw new RuntimeException(e);
      }
    }

    long endTime = System.currentTimeMillis(); // 记录视频生成结束时间
    long durationMillis = endTime - startTime; // 计算耗时（毫秒）
    log.info("图片创建成功，共： {} 张，耗时： {}", imageFiles.size(),
      CdTimeUtil.formatDuration(durationMillis));

    return imageFiles;
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
    String fileName = "CampingInvitation_cht";
    log.info("开始解析文件: {}", filePath);
    String fullPath = filePath + File.separator + fileName + ".txt";
    List<SentenceVO> sentenceVOs = TextParserUtilChatgpt.parseFileToSentenceVOsSingleLine(
      fullPath);

    // 设置路径
    String imagePath = "src/main/resources/background.png"; // 背景图片
    String outputDir = "src/main/resources/pic"; // 输出目录

    // 调用生成方法
    HighResImageVideoUtil5.generateImages(sentenceVOs, imagePath, fileName,
      outputDir);
  }
}
