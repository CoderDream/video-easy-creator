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
public class ImageTextOverlayUtil03 {

  private static final Color MAIN_TITLE_COLOR = new Color(255, 247, 149);
  private static final Color SHADOW_COLOR = new Color(0, 0, 0, 80);
  private static final Color SUB_TITLE_COLOR = Color.YELLOW;
  private static final Color BOTTOM_SHADOW_COLOR = new Color(50, 50, 50, 80);
  private static final int BOTTOM_HEIGHT = 160;

  // *** 新增的常量，用于控制字幕垂直位置 ***
  private static final int MAIN_TITLE_Y = 400; // 默认主标题 Y 坐标
  private static final int SUB_TITLE_Y = 0; // 0 用来标记需要自动计算，如果需要固定值，就设置具体的数值
  private static final int BOTTOM_TITLE_Y_OFFSET = 10; // 底部标题的 Y 轴偏移量（用于精细调整）

  /**
   * 在背景图片上添加文字叠加效果
   *
   * @param backgroundImagePath 背景图片地址
   * @param outputImagePath 输出文件地址
   * @param mainTitle 主标题字符串
   * @param bottomTitle 底部大标题字符串
   */
  public static void addTextOverlay(String backgroundImagePath,
      String outputImagePath,
      String mainTitle, String bottomTitle) {
    long startTime = System.currentTimeMillis();
    log.info(
        "开始处理图片文字叠加，背景图片: {}, 输出图片: {}, 主标题: {}, 底部标题: {}",
        backgroundImagePath, outputImagePath, mainTitle, bottomTitle);

    BufferedImage image;
    Graphics2D g2d = null;
    try {
      // 加载背景图
      try {
        image = ImageIO.read(new File(backgroundImagePath));
        if (image == null) {
          log.error("无法加载背景图片: {}", backgroundImagePath);
          return;
        }
      } catch (IOException e) {
        log.error("读取背景图片失败: {}", e.getMessage(), e);
        return;
      }

      // 创建 Graphics2D 对象
      g2d = image.createGraphics();

      // 开启抗锯齿
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
          RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      log.debug("已开启抗锯齿设置");

      // 设置字体
      Font mainFont = new Font("Source Han Sans Heavy", Font.PLAIN, 110);
      Font subFont = new Font("Microsoft YaHei", Font.BOLD, 74);
      Font bottomFont = new Font("Source Han Sans Heavy", Font.PLAIN, 100);

      // 绘制副标题
      g2d.setFont(subFont);
      int subTitleY = (SUB_TITLE_Y == 0) ? image.getHeight() - 210
          : SUB_TITLE_Y; //自动计算或者设置为静态值
      draw3DOutlinedText(g2d, "中英大字幕", image.getWidth() - 400, subTitleY,
          SUB_TITLE_COLOR, Color.BLACK);
      log.debug("副标题绘制完成: 中英大字幕");
      log.debug("副标题 Y 坐标: {}", subTitleY);

      // 绘制主标题
      g2d.setFont(mainFont);
      drawCentered3DOutlinedText(g2d, mainTitle, image.getWidth(), MAIN_TITLE_Y,
          MAIN_TITLE_COLOR, Color.BLACK);
      log.debug("主标题绘制完成: {}", mainTitle);
      log.debug("主标题 Y 坐标: {}", MAIN_TITLE_Y);

      // 绘制底部背景
      g2d.setColor(new Color(0, 57, 166));
      g2d.fillRect(0, image.getHeight() - BOTTOM_HEIGHT, image.getWidth(),
          BOTTOM_HEIGHT);
      log.debug("底部蓝色背景绘制完成");

      // 绘制底部大标题
      g2d.setFont(bottomFont);
      FontMetrics metrics = g2d.getFontMetrics(bottomFont);
      int ascent = metrics.getAscent();
      int descent = metrics.getDescent();
      int blueBottomTop = image.getHeight() - BOTTOM_HEIGHT;
      int blueBottomCenter = blueBottomTop + (BOTTOM_HEIGHT / 2);
      int centeredY = blueBottomCenter - ((ascent - descent) / 2);

      // 边界检查
      if (centeredY + descent > image.getHeight()) {
        centeredY = image.getHeight() - descent - 5;
      }
      if (centeredY - ascent < blueBottomTop) {
        centeredY = blueBottomTop + ascent + 5;
      }

      centeredY -= BOTTOM_TITLE_Y_OFFSET; // 应用偏移量

      drawCentered3DOutlinedText(g2d, bottomTitle, image.getWidth(), centeredY,
          Color.WHITE, Color.BLACK);
      log.debug("底部大标题绘制完成: {}", bottomTitle);
      log.debug("底部大标题 Y 坐标: {}", centeredY);

      // 保存新图像
      try {
        ImageIO.write(image, "jpg", new File(outputImagePath));
        log.info("图片保存成功: {}", outputImagePath);
      } catch (IOException e) {
        log.error("保存图片失败: {}", e.getMessage(), e);
        return;
      }

    } finally {
      if (g2d != null) {
        g2d.dispose();
        log.debug("Graphics2D 资源已释放");
      }
    }

    long endTime = System.currentTimeMillis();
    long durationMillis = endTime - startTime;
    log.info("图片文字叠加处理完成，耗时: {}",
        CdTimeUtil.formatDuration(durationMillis));
  }

  /**
   * 绘制居中的3D描边文字
   *
   * @param g2d Graphics2D 绘图对象
   * @param text 要绘制的文字
   * @param width 居中宽度
   * @param y 垂直位置
   * @param fillColor 填充颜色
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
   * @param g2d Graphics2D 绘图对象
   * @param text 要绘制的文字
   * @param x 水平位置
   * @param y 垂直位置
   * @param fillColor 填充颜色
   * @param outlineColor 描边颜色
   */
  private static void draw3DOutlinedText(Graphics2D g2d, String text, int x,
      int y, Color fillColor, Color outlineColor) {
    Color shadowColor =
        (fillColor == Color.WHITE) ? BOTTOM_SHADOW_COLOR : SHADOW_COLOR;

    g2d.setColor(shadowColor);
    g2d.drawString(text, x + 1, y + 1);

    g2d.setStroke(new BasicStroke(6));
    g2d.setColor(outlineColor);
    for (int i = -2; i <= 2; i++) {
      for (int j = -2; j <= 2; j++) {
        if (i != 0 || j != 0) {
          g2d.drawString(text, x + i, y + j);
        }
      }
    }

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
