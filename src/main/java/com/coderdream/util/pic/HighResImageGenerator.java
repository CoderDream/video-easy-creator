package com.coderdream.util.pic;

import com.coderdream.entity.WordInfo;
import com.coderdream.util.cd.CdConstants;
import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.FontSizeConverter;
import com.coderdream.util.chatgpt.TextParserUtilChatgpt;
import com.coderdream.util.video.BatchCreateVideoCommonUtil;
import com.coderdream.vo.SentenceVO;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HighResImageGenerator {

  public static List<File> generateImagesFromWordInfoList(
    List<WordInfo> wordInfoList, String imagePath, String fileName,
    String outputDir) {
    long startTime = System.currentTimeMillis();
    List<File> imageFiles = new ArrayList<>();

    if (wordInfoList == null || wordInfoList.isEmpty()) {
      log.warn("WordInfo列表为空，不生成图片");
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

    // 确保输出目录存在
    File dir = new File(outputDir);
    if (!dir.exists() && !dir.mkdirs()) {
      log.error("创建目录失败: {}", dir.getAbsolutePath());
      return imageFiles; //或者抛出异常
    }

    // 字体设置
    Font headerFont = new Font("微软雅黑", Font.BOLD, 36);
    Font dataFont = new Font("微软雅黑", Font.PLAIN, 28);

    // 表格起始位置 (根据你的模板调整)
    int tableX = 50;
    int tableY = 100;
    int rowHeight = 40;
    int columnSpacing = 20;

    for (int i = 0; i < wordInfoList.size(); i++) {
      BufferedImage bufferedImage = new BufferedImage(width, height,
        BufferedImage.TYPE_INT_RGB);
      Graphics2D g2d = bufferedImage.createGraphics();
      g2d.drawImage(templateImage, 0, 0, null);
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

      // 绘制表头
      drawHeader(g2d, headerFont, tableX, tableY, width, columnSpacing);

      // 绘制数据行 (自动换行)
      WordInfo wordInfo = wordInfoList.get(i);
      int currentRowY = tableY + rowHeight;
      drawDataRow(g2d, dataFont, tableX, currentRowY, width, columnSpacing,
        wordInfo);

      // ... (保存图片的代码保持不变) ...
    }

    // ... (计算耗时和返回的代码保持不变) ...

    return imageFiles;
  }


  private static void drawHeader(Graphics2D g2d, Font font, int x, int y,
    int width, int columnSpacing) {
    g2d.setFont(font);
    g2d.setColor(Color.BLACK);
    String[] headers = {"单词", "英音", "释义", "等级", "次数"};
    int currentX = x;
    for (String header : headers) {
      Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(header, g2d);
      g2d.drawString(header, currentX, y + (int) bounds.getHeight());
      currentX += (int) bounds.getWidth() + columnSpacing;
    }
  }

  private static void drawDataRow(Graphics2D g2d, Font font, int x, int y,
    int width, int columnSpacing, WordInfo wordInfo) {
    g2d.setFont(font);
    g2d.setColor(Color.BLACK);
    String[] data = {wordInfo.getWord(), wordInfo.getUk(), wordInfo.getComment(),
      wordInfo.getLevelStr(), String.valueOf(wordInfo.getTimes())};
    int currentX = x;
    for (String item : data) {
      drawWrappedText(g2d, font, item, currentX, y, width, columnSpacing);
      Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(item, g2d);
      currentX += (int) bounds.getWidth() + columnSpacing;
    }
  }

  private static void drawWrappedText(Graphics2D g2d, Font font, String text,
    int x, int y, int maxWidth, int columnSpacing) {
    AttributedString as = new AttributedString(text);
    as.addAttribute(TextAttribute.FONT, font);
    AttributedCharacterIterator aci = as.getIterator();
    FontRenderContext frc = g2d.getFontRenderContext();
    LineBreakMeasurer lbm = new LineBreakMeasurer(aci, frc);

    float currentX = x;
    float currentY = y;
    while (lbm.getPosition() < aci.getEndIndex()) {
      TextLayout tl = lbm.nextLayout(maxWidth - currentX);
      currentY += tl.getAscent();
      tl.draw(g2d, currentX, currentY);
      currentY += tl.getDescent() + tl.getLeading();
    }
  }

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
      FontSizeConverter.pixelToPoint(68)); // 英文字体
    Font phoneticsFont = new Font("Arial Unicode MS", Font.PLAIN,
      FontSizeConverter.pixelToPoint(64)); // 音标字体
    Font chineseFont = new Font("SimHei", Font.PLAIN,
      FontSizeConverter.pixelToPoint(80)); // 中文字体

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
    HighResImageGenerator.generateImages(sentenceVOs, imagePath, fileName,
      outputDir);
  }
}
