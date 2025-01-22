package com.coderdream.util.pic;

import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.FontSizeConverter;
import com.coderdream.util.chatgpt.TextParserUtilChatgpt;
import com.coderdream.vo.SentenceVO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HighResImageGenUtil {


  /**
   * 生成带有文本的图片。
   *
   * @param dayCount   要显示的文本内容列表
   * @param totalCount 要显示的文本内容列表
   * @param imagePath  背景图片路径
   * @param outputDir  输出目录
   * @return 图片文件列表
   */
  public static List<File> generateImages(int dayCount, int totalCount,
    String imagePath, String fileName, String outputDir) {
    long startTime = System.currentTimeMillis(); // 记录开始时间
    List<File> imageFiles = new ArrayList<>();

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
      FontSizeConverter.pixelToPoint(184)); // 页码字体

    for (int i = 0; i < totalCount; i++) {
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
      drawPageNumber(g2d, noFont, dayCount + i);

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
   * @param dayCount  页码数字
   */
  private static void drawPageNumber(Graphics2D g2d, Font font, int dayCount) {
    g2d.setFont(font);
    g2d.setColor(Color.YELLOW);
    String pageNumber = dayCount + "";
//    int textWidth = g2d.getFontMetrics().stringWidth(pageNumber);
    int x = 880; // 左侧边距
    int y = 700; // 顶部边距
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
    String imagePath = "D:\\bcz\\bcz_temp.png"; // 背景图片
    String outputDir = "D:\\bcz\\pic"; // 输出目录

    // 调用生成方法
    HighResImageGenUtil.generateImages(110, 20, imagePath, fileName,
      outputDir);
  }
}
