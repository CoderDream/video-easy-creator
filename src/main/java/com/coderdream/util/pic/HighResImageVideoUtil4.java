package com.coderdream.util.pic;

import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.FontSizeConverter;
import com.coderdream.util.chatgpt.TextParserUtilChatgpt;
import com.coderdream.util.sentence.SentenceParser;
import com.coderdream.util.video.BatchCreateVideoCommonUtil;
import com.coderdream.vo.SentenceVO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HighResImageVideoUtil4 {

  // 左右留白
  private static final int PADDING = 10;
  // 三分之二的比例，用于计算英文和中文的垂直位置
  private static final float TWO_THIRD = 2.0f / 3.0f;

  /**
   * 生成带有文本的图片（使用默认背景图片）。
   *
   * @param fileName 文件名称 (不包含扩展名)
   * @return 图片文件列表
   */
  public static List<File> generateImages(String fileName) {
    // 获取默认背景图片路径
    String imagePath = CdConstants.RESOURCES_BASE_PATH + File.separator
      + CdConstants.BACKGROUND_IMAGE_FILENAME;
    return generateImages(imagePath, fileName); // 调用重载方法
  }

  /**
   * 生成带有文本的图片。
   *
   * @param backgroundImageName 背景图片名称 (包含扩展名)
   * @param fileName            文本文件名称 (不包含扩展名)
   * @return 图片文件列表
   */
  public static List<File> generateImages(String backgroundImageName,
    String fileName) {
    // 构建文本文件的完整路径
    String fullPath =
      CdConstants.RESOURCES_BASE_PATH + File.separator + fileName + ".txt";
    // 解析文本文件，获取句子列表
    List<SentenceVO> sentenceVOs = TextParserUtilChatgpt.parseFileToSentenceVOsSingleLine(
      fullPath);
    // 获取输出图片的目录
    String outputDir = BatchCreateVideoCommonUtil.getPicPath(fileName);
    return generateImages(sentenceVOs, backgroundImageName, fileName,
      outputDir);
  }

  /**
   * 生成带有文本的图片（指定文件路径）。
   *
   * @param backgroundImageName 背景图片名称 (包含扩展名)
   * @param filePath            文本文件所在路径
   * @param contentFileName     文本文件名称 (不包含扩展名)
   * @return 图片文件列表
   */
  public static List<File> generateImages(String backgroundImageName,
    String filePath, String contentFileName) {
    // 构建文本文件的完整路径
    String fullPath = filePath + contentFileName + ".txt";
    // 从文本文件解析句子列表
    List<SentenceVO> sentenceVOs = SentenceParser.parseSentencesFromFile(
      fullPath);
    // 设置输出图片的目录
    String outputDir = filePath + "/pic/";
    return generateImages(sentenceVOs, backgroundImageName, contentFileName,
      outputDir);
  }


  /**
   * 生成带有文本的图片的核心方法。
   *
   * @param sentenceVOs 要显示的文本内容列表
   * @param imagePath   背景图片路径
   * @param fileName    输出文件名称
   * @param outputDir   输出目录
   * @return 图片文件列表
   */
  public static List<File> generateImages(List<SentenceVO> sentenceVOs,
    String imagePath, String fileName, String outputDir) {
    long startTime = System.currentTimeMillis(); // 记录开始时间
    List<File> imageFiles = new ArrayList<>(); // 用于存储生成的图片文件

    // 检查文本内容是否为空
    if (sentenceVOs == null || sentenceVOs.isEmpty()) {
      log.warn("没有找到文本内容，不生成图片");
      return imageFiles;
    }

    BufferedImage templateImage; // 用于存储背景图片
    try {
      // 加载背景图片
      templateImage = ImageIO.read(new File(imagePath));
    } catch (IOException e) {
      log.error("加载背景图片失败: {}", e.getMessage(), e);
      throw new RuntimeException(e); // 抛出运行时异常，终止程序
    }

    // 获取背景图片的宽度和高度
    int width = templateImage.getWidth();
    int height = templateImage.getHeight();

    // 创建输出目录，如果不存在则创建
    File dir = new File(outputDir);
    if (!dir.exists() && dir.mkdirs()) {
      log.info("目录创建成功: {}", dir.getAbsolutePath());
    }

    // 设置字体
    Font noFont = new Font("Arial", Font.BOLD,
      FontSizeConverter.pixelToPoint(54)); // 页码字体
    Font englishFont = new Font("Arial", Font.PLAIN,
      FontSizeConverter.pixelToPoint(80)); // 英文字体
    Font phoneticsFont = new Font("Arial Unicode MS", Font.PLAIN,
      FontSizeConverter.pixelToPoint(80)); // 音标字体
    Font chineseFont = new Font("SimHei", Font.PLAIN,
      FontSizeConverter.pixelToPoint(96)); // 中文字体

    // 创建 FontRenderContext 对象，用于更精确地测量文本
    FontRenderContext frc = new FontRenderContext(null, true, true);

    // 循环处理每一条句子
    for (int i = 0; i < Objects.requireNonNull(sentenceVOs).size(); i++) {
      SentenceVO sentenceVO = sentenceVOs.get(i); // 获取当前句子对象
      // 创建一个用于绘制的 BufferedImage 对象，类型为 RGB
      BufferedImage bufferedImage = new BufferedImage(width, height,
        BufferedImage.TYPE_INT_RGB);
      Graphics2D g2d = bufferedImage.createGraphics(); // 创建 Graphics2D 对象

      g2d.drawImage(templateImage, 0, 0, null); // 绘制背景图片

      // 设置抗锯齿，提高文本渲染效果
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

      // 绘制页码
      drawPageNumber(g2d, noFont, width, sentenceVOs.size(), i + 1);

      int textY = height / 2 - 100; // 计算初始的文本 Y 坐标

      // 绘制文本内容
      drawText(g2d, sentenceVO, englishFont, phoneticsFont, chineseFont, frc,
        width, height, textY);

      g2d.dispose(); // 释放 Graphics2D 对象占用的资源

      // 构建输出文件的完整路径
      File outputFile = new File(
        outputDir + File.separator + fileName + "_" + MessageFormat.format(
          "{0,number,000}", i + 1) + ".png");
      try {
        // 将 BufferedImage 对象写入文件
        ImageIO.write(bufferedImage, "png", outputFile);
        imageFiles.add(outputFile); // 将文件对象添加到列表中
      } catch (IOException e) {
        log.error("图片保存失败: {}", outputFile.getAbsolutePath(), e);
        throw new RuntimeException(e); // 抛出运行时异常
      }
    }
    long endTime = System.currentTimeMillis(); // 记录结束时间
    long durationMillis = endTime - startTime; // 计算耗时
    log.info("图片创建成功，共： {} 张，耗时： {}", imageFiles.size(),
      CdTimeUtil.formatDuration(durationMillis)); // 打印日志信息

    return imageFiles; // 返回生成的图片文件列表
  }

  /**
   * 绘制文本（包括英文、音标、中文）。
   *
   * @param g2d           Graphics2D 对象
   * @param sentenceVO    句子对象，包含英文、音标和中文
   * @param englishFont   英文字体
   * @param phoneticsFont 音标字体
   * @param chineseFont   中文字体
   * @param frc           字体渲染上下文，用于更精确地测量文本尺寸
   * @param width         图片宽度
   * @param height        图片高度
   * @param startY        起始 Y 坐标
   */
  private static void drawText(Graphics2D g2d, SentenceVO sentenceVO,
    Font englishFont, Font phoneticsFont, Font chineseFont,
    FontRenderContext frc, int width, int height, int startY) {

    int textWidth = width - 2 * PADDING; // 计算文本可用宽度（减去左右留白）
    int currentY = startY; // 设置当前 Y 坐标为起始 Y 坐标

    // 绘制音标
    Rectangle2D phoneticsBounds = drawMultiLineText(g2d,
      sentenceVO.getPhonetics(), phoneticsFont, frc,
      textWidth, currentY, Color.YELLOW, true); // 绘制音标，并返回音标文本的边界
    int phoneticsHeight = (int) phoneticsBounds.getHeight(); // 获取音标文本的高度
    currentY = startY; // 重置当前 Y 坐标为起始 Y 坐标

    // 绘制英文
    int englishY =
      currentY - (int) (phoneticsHeight * TWO_THIRD); // 计算英文的 Y 坐标（在音标上方三分之二处）
    drawMultiLineText(g2d, sentenceVO.getEnglish(), englishFont, frc,
      textWidth, englishY, Color.WHITE, false); // 绘制英文

    // 绘制中文
    int chineseY = currentY + phoneticsHeight + (int) (phoneticsHeight
      * TWO_THIRD); // 计算中文的 Y 坐标（在音标下方三分之二处）
    drawMultiLineText(g2d, sentenceVO.getChinese(), chineseFont, frc,
      textWidth, chineseY, Color.WHITE, false); // 绘制中文
  }


  /**
   * 绘制多行文本，并处理自动换行。
   *
   * @param g2d       Graphics2D 对象
   * @param text      文本内容
   * @param font      字体
   * @param frc       字体渲染上下文，用于精确测量文本尺寸
   * @param textWidth 可用文本宽度
   * @param y         起始 Y 坐标
   * @param color     文本颜色
   * @param center    是否居中
   * @return 文本绘制的矩形边界
   */
  private static Rectangle2D drawMultiLineText(Graphics2D g2d, String text,
    Font font, FontRenderContext frc, int textWidth, int y, Color color,
    boolean center) {
    g2d.setFont(font); // 设置字体
    g2d.setColor(color); // 设置颜色

    // 创建 LineBreakMeasurer 对象，用于处理自动换行
    LineBreakMeasurer measurer = new LineBreakMeasurer(
      new java.text.AttributedString(text).getIterator(), frc);
    float x = PADDING; // 设置初始 X 坐标为 PADDING
    float currentY = y; // 设置当前 Y 坐标为传入的 y

    float height = 0; // 用于记录高度

    // 循环处理每一行文本
    while (measurer.getPosition() < text.length()) {
      TextLayout layout = measurer.nextLayout(textWidth); // 获取下一行的布局
      if (center) {
        // 如果需要居中显示，计算 x 坐标
        x = (textWidth - (float)layout.getBounds().getWidth()) / 2f + PADDING;
      }
      layout.draw(g2d, x, currentY + layout.getAscent()); // 绘制文本
      // 更新当前 Y 坐标
      currentY +=
        layout.getDescent() + layout.getLeading() + layout.getAscent();
      height += layout.getDescent() + layout.getLeading()
        + layout.getAscent(); // 更新文本整体高度
    }
    // 返回文本绘制的矩形边界
    return new Rectangle2D.Float(PADDING, y, textWidth, height);
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
    g2d.setFont(font); // 设置字体
    g2d.setColor(Color.GRAY); // 设置颜色
    String pageNumber = "Page " + current + " of " + total; // 构建页码文本
    int textWidth = g2d.getFontMetrics().stringWidth(pageNumber); // 获取页码文本的宽度
    int x = 40; // 设置 X 坐标
    int y = 60; // 设置 Y 坐标
    g2d.drawString(pageNumber, x, y); // 绘制页码
  }

  public static void main(String[] args) throws Exception {
    // 示例数据
    String filePath = "src/main/resources";
    String fileName = "CampingInvitation_cht";
    log.info("开始解析文件: {}", filePath); // 打印日志信息
    String fullPath = filePath + File.separator + fileName + ".txt"; // 构建完整文件路径
    // 从文本文件解析句子列表
    List<SentenceVO> sentenceVOs = TextParserUtilChatgpt.parseFileToSentenceVOsSingleLine(
      fullPath);
    String imagePath = "src/main/resources/background.png"; // 设置背景图片路径
    String outputDir = "src/main/resources/pic"; // 设置输出目录

    // 调用生成图片的方法
    HighResImageVideoUtil4.generateImages(sentenceVOs, imagePath, fileName,
      outputDir);
  }
}
