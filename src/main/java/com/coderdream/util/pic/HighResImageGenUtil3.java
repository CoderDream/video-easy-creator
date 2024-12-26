package com.coderdream.util.pic;

import com.coderdream.entity.WordInfo;
import com.coderdream.util.CdTimeUtil;
import java.awt.Color;
import java.awt.Font;
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
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HighResImageGenUtil3 {

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
      // ... (保存图片的代码保持不变) ...
    }

    // ... (计算耗时和返回的代码保持不变) ...
    long endTime = System.currentTimeMillis(); // 记录视频生成结束时间
    long durationMillis = endTime - startTime; // 计算耗时（毫秒）
    log.info("图片创建成功，共： {} 张，耗时： {}", imageFiles.size(),
      CdTimeUtil.formatDuration(durationMillis));

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
    String[] data = {wordInfo.getWord(), wordInfo.getUk(), wordInfo.getCn(),
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


  public static void main(String[] args) throws Exception {
    // 示例数据
    String filePath = "src/main/resources";
    String fileName = "CampingInvitation_cht";
    log.info("开始解析文件: {}", filePath);
    String fullPath = filePath + File.separator + fileName + ".txt";


    // 设置路径
    String imagePath = "D:\\bcz\\word1_temp.png"; // 背景图片
    String outputDir = "D:\\bcz\\word1_temp"; // 输出目录

    List<WordInfo> wordInfoList = Arrays.asList(
      new WordInfo("adjective", "[ˈædʒɪktɪv]", "n. 形容词;adj. 形容词的；从属的",
        "四级", 1),
      new WordInfo("certainly", "[ˈsɜːt(ə)nli]", "adv. 当然；行（用于回答）；必定",
        "四级", 1),
      new WordInfo("cliff", "[klɪf]", "n. 悬崖；绝壁;", "四级", 3),
      new WordInfo("compact", "[kəmˈpækt；ˈkɒmpækt]",
        "n. 合同，契约；小粉盒;adj. 紧凑的，紧密的；简洁的;vt. 使简洁；使紧密结合",
        "四级", 9),
      new WordInfo("consumer", "[kənˈsjuːmə(r)]", "n. 消费者；用户，顾客", "四级",
        1)
    );

    // 调用生成方法
    HighResImageGenUtil3.generateImagesFromWordInfoList(wordInfoList, imagePath,
      fileName,
      outputDir);
  }
}
