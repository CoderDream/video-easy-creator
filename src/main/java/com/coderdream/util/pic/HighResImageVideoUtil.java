package com.coderdream.util.pic;

import cn.hutool.core.util.StrUtil;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.FontSizeConverter;
import com.coderdream.util.chatgpt.TextParserUtilChatgpt;
import com.coderdream.util.sentence.SentenceParser;
import com.coderdream.util.video.BatchCreateVideoCommonUtil;
import com.coderdream.vo.SentenceVO;
import java.awt.*;
import java.awt.font.FontRenderContext;
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
public class HighResImageVideoUtil {

  private static final int LINE_SPACING = 20;   // 垂直间距，设置为原来的两倍
  private static final int LEFT_MARGIN = 50;    // 左边距
  private static final int RIGHT_MARGIN = 50;   // 右边距

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
   * @param contentFileName     文件名称
   * @return 图片文件列表
   */
  public static List<File> generateImages(String backgroundImageName,
    String filePath,    String contentFileName) {
    return generateImages(backgroundImageName, contentFileName,
      filePath, null);
  }

  /**
   * 生成带有文本的图片。
   *
   * @param backgroundImageName 背景图片名称
   * @param contentFileName     文件名称
   * @return 图片文件列表
   */
  public static List<File> generateImages(String backgroundImageName,
    String filePath, String contentFileName, String lang) {
    String fullPath = filePath + contentFileName + ".txt";
    List<SentenceVO> sentenceVOs = SentenceParser.parseSentencesFromFile(
      fullPath);
    String outputDir = filePath + "/pic/"; // 输出目录
    // 设置路径
    if (StrUtil.isNotEmpty(lang)) {
      outputDir = filePath + "/pic_" + lang + "/"; // 输出目录
    }

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
    long startTime = System.currentTimeMillis();
    List<File> imageFiles = new ArrayList<>();

    if (sentenceVOs == null || sentenceVOs.isEmpty()) {
      log.warn("没有找到文本内容，不生成图片");
      return imageFiles;
    }

    BufferedImage templateImage;
    try {
      templateImage = ImageIO.read(new File(imagePath));
    } catch (IOException e) {
      log.error("加载背景图片失败: {}", e.getMessage(), e);
      throw new RuntimeException(e);
    }

    int width = templateImage.getWidth();
    int height = templateImage.getHeight();

    File dir = new File(outputDir);
    if (!dir.exists() && dir.mkdirs()) {
      log.info("目录创建成功: {}", dir.getAbsolutePath());
    }

    Font noFont = new Font("Arial", Font.BOLD,
      FontSizeConverter.pixelToPoint(54));
    Font englishFont = new Font("Arial", Font.PLAIN,
      FontSizeConverter.pixelToPoint(80));
    Font phoneticsFont = new Font("Arial Unicode MS", Font.PLAIN,
      FontSizeConverter.pixelToPoint(80));
    Font chineseFont = new Font("SimHei", Font.PLAIN,
      FontSizeConverter.pixelToPoint(96));

    for (int i = 0; i < Objects.requireNonNull(sentenceVOs).size(); i++) {
      SentenceVO sentenceVO = sentenceVOs.get(i);
      int number = i + 1;
      File outputFile = new File(
        outputDir + File.separator + fileName + "_" + MessageFormat.format(
          "{0,number,000}", number) + ".png");
      if(outputFile.exists() || outputFile.length() > 0) {
        log.info("图片文件已存在，跳过: {}", outputFile.getAbsolutePath());
        continue;
      }

      BufferedImage bufferedImage = new BufferedImage(width, height,
        BufferedImage.TYPE_INT_RGB);
      Graphics2D g2d = bufferedImage.createGraphics();
      g2d.drawImage(templateImage, 0, 0, null);

      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

      drawPageNumber(g2d, noFont, width, sentenceVOs.size(), i + 1);

      int startY = height / 2 - 100;
      int currentY = startY;

      currentY = drawWrappedText(g2d, englishFont, Color.WHITE,
        sentenceVO.getEnglish(), currentY, width, false);
      // #fbc531    Color.getColor("#FBC531")
      currentY = drawWrappedText(g2d, phoneticsFont, new Color(251, 197, 49),
        sentenceVO.getPhonetics(), currentY + LINE_SPACING, width, false);

      drawWrappedText(g2d, chineseFont, Color.WHITE,
        sentenceVO.getChinese(), currentY + LINE_SPACING, width, true);

      g2d.dispose();


      try {
        ImageIO.write(bufferedImage, "png", outputFile);
        imageFiles.add(outputFile);
      } catch (IOException e) {
        log.error("图片保存失败: {}", outputFile.getAbsolutePath(), e);
        throw new RuntimeException(e);
      }
    }

    long endTime = System.currentTimeMillis();
    long durationMillis = endTime - startTime;
    log.info("图片创建成功，共： {} 张，耗时： {}", imageFiles.size(),
      CdTimeUtil.formatDuration(durationMillis));

    return imageFiles;
  }


  /**
   * 绘制自动换行且居中的文本。
   *
   * @param g2d       Graphics2D 对象
   * @param font      文本字体
   * @param color     文本颜色
   * @param text      要绘制的文本
   * @param y         起始 y 坐标
   * @param maxWidth  文本最大宽度
   * @param isChinese 是否是中文
   * @return 下一行绘制的 y 坐标
   */
  private static int drawWrappedText(Graphics2D g2d, Font font, Color color,
    String text, int y, int maxWidth, boolean isChinese) {
    g2d.setFont(font);
    g2d.setColor(color);
    FontMetrics metrics = g2d.getFontMetrics(font);
    FontRenderContext frc = g2d.getFontRenderContext();

    int currentY = y;
    // 计算文本可用宽度
    int availableWidth = maxWidth - LEFT_MARGIN - RIGHT_MARGIN;
    StringBuilder line = new StringBuilder();

    if (!isChinese) {
      String[] words = text.split(" ");
      for (String word : words) {
        String testLine = line + word + " ";
        Rectangle2D bounds = font.getStringBounds(testLine, frc);
        if (bounds.getWidth() > availableWidth && line.length() > 0) {
          String currentLine = line.toString();
          Rectangle2D lineBounds = font.getStringBounds(currentLine, frc);
          int lineWidth = (int) lineBounds.getWidth();
          int startX = LEFT_MARGIN + (availableWidth - lineWidth) / 2;
          g2d.drawString(currentLine, startX, currentY);
          line = new StringBuilder(word + " ");
          currentY += metrics.getHeight();
        } else {
          line.append(word).append(" ");
        }
      }
    } else {
      for (int i = 0; i < text.length(); i++) {
        char c = text.charAt(i);
        String testLine = line.toString() + c;
        Rectangle2D bounds = font.getStringBounds(testLine, frc);
        if (bounds.getWidth() > availableWidth && line.length() > 0) {
          String currentLine = line.toString();
          Rectangle2D lineBounds = font.getStringBounds(currentLine, frc);
          int lineWidth = (int) lineBounds.getWidth();
          int startX = LEFT_MARGIN + (availableWidth - lineWidth) / 2;
          g2d.drawString(currentLine, startX, currentY);
          line = new StringBuilder("" + c);
          currentY += metrics.getHeight();
        } else {
          line.append(c);
        }
      }
    }
    String currentLine = line.toString();
    Rectangle2D lineBounds = font.getStringBounds(currentLine, frc);
    int lineWidth = (int) lineBounds.getWidth();
    int startX = LEFT_MARGIN + (availableWidth - lineWidth) / 2;
    g2d.drawString(currentLine, startX, currentY);
    return currentY + metrics.getHeight();
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
    String pageNumber = current + "";
    int textWidth = g2d.getFontMetrics().stringWidth(pageNumber);
    int x = 40; // 左侧边距
    int y = 60; // 顶部边距
    g2d.drawString(pageNumber, x, y);
  }

  public static void main(String[] args) throws Exception {
    String filePath = "src/main/resources";
    String fileName = "CampingInvitation_cht";
    log.info("开始解析文件: {}", filePath);
    String fullPath = filePath + File.separator + fileName + ".txt";
    List<SentenceVO> sentenceVOs = TextParserUtilChatgpt.parseFileToSentenceVOsSingleLine(
      fullPath);

    String imagePath = "src/main/resources/background.png";
    String outputDir = "src/main/resources/pic";

    HighResImageVideoUtil.generateImages(sentenceVOs, imagePath, fileName,
      outputDir);
  }
}
