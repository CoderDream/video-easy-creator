package com.coderdream.util.pic;

import com.coderdream.util.cd.CdTimeUtil;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;

/**
 * 图片文字叠加工具类
 * <p>
 * 用于在背景图片上添加带有立体效果的文字，包括主标题、副标题和底部大标题。 支持自定义主标题和底部大标题，副标题固定为“中英大字幕”。
 * </p>
 */
@Slf4j
public class ImageTextOverlayUtil04 {

  private static final Color MAIN_TITLE_COLOR = new Color(255, 247,
    149); // 主标题颜色
  private static final Color SHADOW_COLOR = new Color(0, 0, 0,
    80); // 主标题和副标题阴影颜色
  private static final Color SUB_TITLE_COLOR = Color.YELLOW; // 副标题颜色
  private static final Color BOTTOM_SHADOW_COLOR = new Color(50, 50, 50,
    80); // 底部标题阴影颜色
  private static final int BOTTOM_HEIGHT = 146; // 底部背景高度

  /**
   * 在背景图片上添加文字叠加效果
   *
   * @param backgroundImagePath 背景图片地址
   * @param outputImagePath     输出文件地址
   * @param mainTitle           主标题字符串
   * @param bottomTitle         底部大标题字符串
   */
  public static void addTextOverlay(String backgroundImagePath,
    String outputImagePath,
    String mainTitle, String bottomTitle) {
    long startTime = System.currentTimeMillis(); // 记录方法开始时间
    log.info(
      "开始处理图片文字叠加，背景图片: {}, 输出图片: {}, 主标题: {}, 底部标题: {}",
      backgroundImagePath, outputImagePath, mainTitle, bottomTitle);

    BufferedImage image; // 初始化 image 变量
    Graphics2D g2d = null; // 初始化 g2d 变量
    try {
      // 加载背景图
      try {
        image = ImageIO.read(new File(backgroundImagePath));
        if (image == null) {
          log.error("无法加载背景图片: {}", backgroundImagePath);
          return; // 方法结束
        }
      } catch (IOException e) {
        log.error("读取背景图片失败: {}", e.getMessage(), e);
        return; // 方法结束
      }

      // 创建 Graphics2D 对象
      g2d = image.createGraphics();

      // 开启抗锯齿，提升文字平滑度
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      log.debug("已开启抗锯齿设置");

      // 设置字体：黑体 + 粗体，适配繁体字
      Font mainFont = new Font("Source Han Sans Heavy", Font.PLAIN,
        110); // 主标题字体
      Font subFont = new Font("Microsoft YaHei", Font.BOLD, 74); // 副标题字体
      Font bottomFont = new Font("Source Han Sans Heavy", Font.PLAIN,
        100); // 底部字体

      // 1. 绘制主标题 (黄色字+多层描边+薄阴影，居中靠上)
      g2d.setFont(mainFont);
      drawCentered3DOutlinedText(g2d, mainTitle, image.getWidth(), 400,
        MAIN_TITLE_COLOR, Color.BLACK);
      log.debug("主标题绘制完成: {}", mainTitle);

      // 2. 绘制副标题 (黄色字+多层描边+薄阴影，右下角)
      g2d.setFont(subFont);
      draw3DOutlinedText(g2d, "中英大字幕", image.getWidth() - 400,
        image.getHeight() - 210, SUB_TITLE_COLOR, Color.BLACK);
      log.debug("副标题绘制完成: 中英大字幕");

      // 3. 绘制底部背景 (蓝色矩形)
      g2d.setColor(new Color(0, 57, 166)); // 深蓝色背景
      g2d.fillRect(0, image.getHeight() - BOTTOM_HEIGHT, image.getWidth(),
        BOTTOM_HEIGHT);
      log.debug("底部蓝色背景绘制完成");

      // 4. 绘制底部大标题 (白色字+黑色描边+薄阴影，垂直居中)
      g2d.setFont(bottomFont);
      // 计算垂直居中的 y 坐标
      FontMetrics metrics = g2d.getFontMetrics(bottomFont);
      int ascent = metrics.getAscent(); // 文字上部高度
      int descent = metrics.getDescent(); // 文字下部高度
      int blueBottomTop = image.getHeight() - BOTTOM_HEIGHT; // 蓝底顶部 y 坐标
      int blueBottomCenter = blueBottomTop + (BOTTOM_HEIGHT / 2); // 蓝底垂直中心
      int centeredY = blueBottomCenter - ((ascent - descent) / 2); // 调整基线

      // 调试信息
      log.debug("Image Height: {}", image.getHeight());
      log.debug("Blue Bottom Top: {}", blueBottomTop);
      log.debug("Blue Bottom Bottom: {}", image.getHeight());
      log.debug("Blue Bottom Center: {}", blueBottomCenter);
      log.debug("Ascent: {}", ascent);
      log.debug("Descent: {}", descent);
      log.debug("Centered Y (before adjustment): {}", centeredY);

      // 边界检查
      if (centeredY + descent > image.getHeight()) {
        centeredY = image.getHeight() - descent - 5; // 留出 5 像素底部边距
        log.debug("调整 y 坐标以避免超出底部: {}", centeredY);
      }
      if (centeredY - ascent < blueBottomTop) {
        centeredY = blueBottomTop + ascent + 5; // 留出 5 像素顶部边距
        log.debug("调整 y 坐标以避免超出顶部: {}", centeredY);
      }

      // 微调：向上调整 10 像素
      centeredY -= 10;
      log.debug("Centered Y (after adjustment): {}", centeredY);

      drawCentered3DOutlinedText(g2d, bottomTitle, image.getWidth(), centeredY,
        Color.WHITE, Color.BLACK);
      log.debug("底部大标题绘制完成: {}", bottomTitle);

      // 保存新图像
      try {
        ImageIO.write(image, "jpg", new File(outputImagePath));
        log.info("图片保存成功: {}", outputImagePath);
      } catch (IOException e) {
        log.error("保存图片失败: {}", e.getMessage(), e);
        return; // 方法结束
      }

    } finally {
      // 手动释放 Graphics2D 资源
      if (g2d != null) {
        g2d.dispose();
        log.debug("Graphics2D 资源已释放");
      }
    }

    // 计算并记录耗时
    long endTime = System.currentTimeMillis(); // 记录方法结束时间
    long durationMillis = endTime - startTime; // 计算耗时（毫秒）
    log.info("图片文字叠加处理完成，耗时: {}",
      CdTimeUtil.formatDuration(durationMillis));
  }

  /**
   * 绘制居中的3D描边文字
   *
   * @param g2d          Graphics2D 绘图对象
   * @param text         要绘制的文字
   * @param width        居中宽度
   * @param y            垂直位置（基线）
   * @param fillColor    填充颜色
   * @param outlineColor 描边颜色
   */
  private static void drawCentered3DOutlinedText(Graphics2D g2d, String text,
    int width, int y, Color fillColor, Color outlineColor) {
    FontRenderContext frc = g2d.getFontRenderContext();
    Rectangle2D bounds = g2d.getFont().getStringBounds(text, frc);
    int x = (int) ((width - bounds.getWidth()) / 2);
    draw3DOutlinedText(g2d, text, x, y, fillColor, outlineColor);
  }

  /**
   * 绘制带多层描边和薄阴影的文字
   *
   * @param g2d          Graphics2D 绘图对象
   * @param text         要绘制的文字
   * @param x            水平位置
   * @param y            垂直位置（基线）
   * @param fillColor    填充颜色
   * @param outlineColor 描边颜色
   */
  private static void draw3DOutlinedText(Graphics2D g2d, String text, int x,
    int y, Color fillColor, Color outlineColor) {
    // 选择阴影颜色：主标题和副标题用 SHADOW_COLOR，底部标题用 BOTTOM_SHADOW_COLOR
    Color shadowColor =
      (fillColor == Color.WHITE) ? BOTTOM_SHADOW_COLOR : SHADOW_COLOR;

    // 绘制薄阴影 (单层，偏移1像素)
    g2d.setColor(shadowColor);
    g2d.drawString(text, x + 1, y + 1); // 单层阴影，保持薄效果

    // 绘制多层描边
    g2d.setStroke(new BasicStroke(6)); // 保持描边宽度
    g2d.setColor(outlineColor);
    for (int i = -2; i <= 2; i++) {
      for (int j = -2; j <= 2; j++) {
        if (i != 0 || j != 0) {
          g2d.drawString(text, x + i, y + j); // 多次偏移绘制描边
        }
      }
    }

    // 绘制填充文字
    g2d.setColor(fillColor);
    g2d.drawString(text, x, y);
  }

  /**
   * 示例调用
   */
  public static void main(String[] args) {
    String backgroundImagePath = "D:\\0000\\0007_Trump\\20250303\\cover_002.jpg";
    String outputImagePath = "D:\\0000\\0007_Trump\\20250303\\cover_008.jpg";
    String mainTitle = "新主标题示例";
    String bottomTitle = "新底部标题示例";

    addTextOverlay(backgroundImagePath, outputImagePath, mainTitle,
      bottomTitle);
    System.out.println("Image processing completed!");
  }
}
