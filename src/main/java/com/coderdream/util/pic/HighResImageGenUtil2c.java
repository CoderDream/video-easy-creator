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
import java.util.List;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HighResImageGenUtil2c {

  // 定义列配置类
  static class ColumnConfig {

    int width;
    Font font;
    Color color;

    public ColumnConfig(int width, Font font, Color color) {
      this.width = width;
      this.font = font;
      this.color = color;
    }
  }

  // 列宽常量
  private static final int COLUMN_WIDTH_WORD = 200;
  private static final int COLUMN_WIDTH_UK = 280;
  private static final int COLUMN_WIDTH_CN = 850;
  private static final int COLUMN_WIDTH_LEVEL = 120;
  private static final int COLUMN_WIDTH_TIMES = 100;
  private static final int COLUMN_GAP = 10; // 列之间的间隔

  // 列配置 (可自定义)
  private static final ColumnConfig[] columnConfigs = {
    new ColumnConfig(COLUMN_WIDTH_WORD, new Font("微软雅黑", Font.BOLD, 22),
      Color.BLACK), // 单词
    new ColumnConfig(COLUMN_WIDTH_UK, new Font("Arial Unicode MS", Font.PLAIN, 20),
      Color.DARK_GRAY), // 英音
    new ColumnConfig(COLUMN_WIDTH_CN,
      new Font("Times New Roman", Font.PLAIN, 18), Color.BLACK), // 释义
    new ColumnConfig(COLUMN_WIDTH_LEVEL, new Font("微软雅黑", Font.PLAIN, 22),
      Color.GREEN),  // 等级
    new ColumnConfig(COLUMN_WIDTH_TIMES, new Font("微软雅黑", Font.BOLD, 22),
      Color.RED)    // 次数
  };


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
      return imageFiles; //更稳健的错误处理方式
    }
    int templateWidth = templateImage.getWidth();
    int templateHeight = templateImage.getHeight();

    File dir = new File(outputDir);
    if (!dir.exists() && !dir.mkdirs()) {
      log.error("创建目录失败: {}", dir.getAbsolutePath());
      return imageFiles;
    }

    int tableX = 220;
    int tableY = 125;
    int currentPage = 1;
    int currentRowY = tableY;
    int pageStartRowY = tableY; // 记录每页的起始行Y坐标
    BufferedImage bufferedImage = null; // 声明 bufferedImage

    Graphics2D g2d = null; //声明g2d
    for (int i = 0; i < wordInfoList.size(); i++) {
          WordInfo wordInfo = wordInfoList.get(i);
      if (i == 0 || currentRowY > templateHeight - 100) {
        // 创建新的 BufferedImage 和 Graphics2D 对象，并绘制表头
        if (bufferedImage != null) {
            // 保存上一页图片
          g2d.dispose();
            File outputFile = new File(
                    outputDir + File.separator + fileName + "_" + MessageFormat.format(
                            "{0,number,000}", currentPage) + ".png");
            try {
                ImageIO.write(bufferedImage, "png", outputFile);
                imageFiles.add(outputFile);
            } catch (IOException e) {
                log.error("图片保存失败: {}", outputFile.getAbsolutePath(), e);
            }
        }

          bufferedImage = new BufferedImage(templateWidth, templateHeight, BufferedImage.TYPE_INT_RGB);
           g2d = bufferedImage.createGraphics();

          g2d.drawImage(templateImage, 0, 0, null);
          g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                  RenderingHints.VALUE_ANTIALIAS_ON);
          g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                  RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


        // 绘制表头 (每页都绘制)
        drawHeader(g2d, columnConfigs, tableX, pageStartRowY);
        currentRowY = tableY;  // 重置当前行Y坐标
        pageStartRowY = tableY; // 重置新页的起始Y坐标
        currentPage++;
      }

      // 绘制数据行 (自动换行，分页)
      int currentX = tableX;
      String[] data = {wordInfo.getWord(), wordInfo.getUk(), wordInfo.getCn(),
        wordInfo.getLevelStr(), String.valueOf(wordInfo.getTimes())};
      int rowHeight = calculateRowHeight(wordInfo.getCn(),
        columnConfigs[2].font,
        columnConfigs[2].width); // 计算当前行的高度，以释义列为准

      for (int j = 0; j < data.length; j++) {
        drawWrappedText(g2d, columnConfigs[j].font, columnConfigs[j].color,
          data[j], currentX, currentRowY, columnConfigs[j].width,
          rowHeight); // 使用计算出的行高
        currentX += columnConfigs[j].width + COLUMN_GAP; // 加上列间距
      }


      // 分页逻辑
      currentRowY += rowHeight + 10; //加上行间距


      if(i == wordInfoList.size()-1) {
          // 处理最后一页图片
          g2d.dispose();
          File outputFile = new File(
                  outputDir + File.separator + fileName + "_" + MessageFormat.format(
                          "{0,number,000}", currentPage) + ".png");
          try {
              ImageIO.write(bufferedImage, "png", outputFile);
              imageFiles.add(outputFile);
          } catch (IOException e) {
              log.error("图片保存失败: {}", outputFile.getAbsolutePath(), e);
          }
      }
    }


    long endTime = System.currentTimeMillis();
    long durationMillis = endTime - startTime;
    log.info("图片创建成功，共： {} 张，耗时： {}", imageFiles.size(),
      CdTimeUtil.formatDuration(durationMillis));
    return imageFiles;
  }


  private static void drawHeader(Graphics2D g2d, ColumnConfig[] columnConfigs,
    int x, int y) {
    String[] headers = {"单词", "英音", "释义", "等级", "次数"};
    int currentX = x;
    Font headerFont = new Font("微软雅黑", Font.BOLD, 36);
    for (int i = 0; i < headers.length; i++) {
      g2d.setFont(headerFont);
      g2d.setColor(columnConfigs[i].color);
      Rectangle2D bounds = g2d.getFontMetrics()
        .getStringBounds(headers[i], g2d);
      g2d.drawString(headers[i], currentX, y + (int) bounds.getHeight());
      currentX += columnConfigs[i].width + COLUMN_GAP; // 加上列间距
    }
  }

  private static void drawWrappedText(Graphics2D g2d, Font font, Color color,
    String text, int x, int y, int maxWidth, int maxHeight) {
    g2d.setFont(font);
    g2d.setColor(color);
    if (text == null || text.isEmpty()) {
      text = "空";
    }
    AttributedString as = new AttributedString(text);
    as.addAttribute(TextAttribute.FONT, font);
    AttributedCharacterIterator aci = as.getIterator();
    FontRenderContext frc = g2d.getFontRenderContext();
    LineBreakMeasurer lbm = new LineBreakMeasurer(aci, frc);

    float currentX = x;
    float currentY = y;
    while (lbm.getPosition() < aci.getEndIndex() && currentY < y + maxHeight) {
      TextLayout tl = lbm.nextLayout(maxWidth);
      currentY += tl.getAscent();
      tl.draw(g2d, currentX, currentY);
      currentY += tl.getDescent() + tl.getLeading();
    }
  }

  // 计算文本所需高度，用于分页
  private static int calculateRowHeight(String text, Font font, int maxWidth) {
    AttributedString as = new AttributedString(text);
    as.addAttribute(TextAttribute.FONT, font);
    AttributedCharacterIterator aci = as.getIterator();
    FontRenderContext frc = new FontRenderContext(null, true, true);
    LineBreakMeasurer lbm = new LineBreakMeasurer(aci, frc);
    float height = 0;
    while (lbm.getPosition() < aci.getEndIndex()) {
      TextLayout tl = lbm.nextLayout(maxWidth);
      height += tl.getAscent() + tl.getDescent() + tl.getLeading();
    }
    return (int) height;
  }

  public static List<WordInfo> generateWordInfoList() {
    List<WordInfo> wordInfoList = new ArrayList<>();

    wordInfoList.add(
      new WordInfo("adjective", "[ˈædʒɪktɪv]", "n. 形容词;adj. 形容词的；从属的",
        "四级", 1));
    wordInfoList.add(
      new WordInfo("certainly", "[ˈsɜːt(ə)nli]", "adv. 当然；行（用于回答）；必定",
        "四级", 1));
    wordInfoList.add(
      new WordInfo("cliff", "[klɪf]", "n. 悬崖；绝壁;", "四级", 3));
    wordInfoList.add(new WordInfo("compact", "[kəmˈpækt；ˈkɒmpækt]",
      "n. 合同，契约；小粉盒;adj. 紧凑的，紧密的；简洁的;vt. 使简洁；使紧密结合",
      "四级", 9));
    wordInfoList.add(
      new WordInfo("consumer", "[kənˈsjuːmə(r)]", "n. 消费者；用户，顾客", "四级",
        1));
    wordInfoList.add(new WordInfo("dramatic", "[drəˈmætɪk]",
      "adj. 戏剧的；急剧的；引人注目的；激动人心的", "四级", 1));
    wordInfoList.add(
      new WordInfo("increasingly", "[ɪnˈkriːsɪŋli]", "adv. 越来越多地；渐增地",
        "四级", 1));
    wordInfoList.add(new WordInfo("ladder", "[ˈlædə(r)]",
      "n. 阶梯；途径；梯状物;vi. 成名；发迹;vt. 在……上装设梯子", "四级", 1));
    wordInfoList.add(new WordInfo("lens", "[lenz]",
      "n. 透镜，镜头；眼睛中的水晶体；晶状体；隐形眼镜；汽车的灯玻璃;vt. 给……摄影",
      "四级", 4));
    wordInfoList.add(new WordInfo("limitation", "[ˌlɪmɪˈteɪʃn]",
      "n. 限制；限度；极限；追诉时效；有效期限；缺陷", "四级", 1));
    wordInfoList.add(
      new WordInfo("manual", "[ˈmænjuəl]", "adj. 手工的；体力的;n. 手册，指南",
        "四级", 1));
    wordInfoList.add(
      new WordInfo("meantime", "[ˈmiːntaɪm]", "n. 其时，其间;adv. 同时；其间",
        "四级", 1));
    wordInfoList.add(
      new WordInfo("mostly", "[ˈməʊstli]", "adv. 主要地；通常；多半地", "四级",
        1));
    wordInfoList.add(
      new WordInfo("radar", "[ˈreɪdɑː(r)]", "n. [雷达] 雷达，无线电探测器;",
        "四级", 1));
    wordInfoList.add(new WordInfo("release", "[rɪˈliːs]",
      "n. 释放；发布；让与;vt. 释放；发射；让与；允许发表", "四级", 3));
    wordInfoList.add(new WordInfo("sophisticated", "[səˈfɪstɪkeɪtɪd]",
      "adj. 复杂的；精致的；久经世故的；富有经验的;v. 使变得世故；使迷惑；篡改（sophisticate的过去分词形式）",
      "四级", 1));
    wordInfoList.add(
      new WordInfo("usually", "[ˈjuːʒuəli]", "adv. 通常，经常", "四级", 1));
    wordInfoList.add(new WordInfo("manufacturer", "[ˌmænjuˈfæktʃərə(r)]",
      "n. 制造商；[经] 厂商", "六级", 1));
    wordInfoList.add(new WordInfo("premium", "[ˈpriːmiəm]",
      "n. 额外费用；奖金；保险费;(商)溢价;adj. 高价的；优质的", "六级", 1));
    wordInfoList.add(new WordInfo("horizontal", "[ˌhɒrɪˈzɒnt(ə)l]",
      "n. 水平线，水平面；水平位置;adj. 水平的；地平线的；同一阶层的", "考研", 1));
    wordInfoList.add(new WordInfo("obsolete", "[ˈɒbsəliːt]",
      "adj. 废弃的；老式的;n. 废词；陈腐的人;vt. 淘汰；废弃", "考研", 3));
    wordInfoList.add(new WordInfo("opt", "[ɒpt]", "vi. 选择", "考研", 4));
    wordInfoList.add(
      new WordInfo("actually", null, "ad.实际上；竟然", "雅思", 2));
    wordInfoList.add(
      new WordInfo("broaden", null, "vt.&vi.放宽，变阔", "雅思", 1));
    wordInfoList.add(
      new WordInfo("completely", null, "ad.十分，完全地", "雅思", 2));
    wordInfoList.add(
      new WordInfo("interested", null, "adj.感兴趣的", "雅思", 2));
    wordInfoList.add(
      new WordInfo("listener", null, "n.听者，听众之一", "雅思", 1));
    wordInfoList.add(new WordInfo("maker", null, "n.制造者，制造商", "雅思", 1));
    wordInfoList.add(new WordInfo("photography", null, "n.摄影术", "雅思", 5));

    return wordInfoList;
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

    List<WordInfo> wordInfoList = generateWordInfoList();


    // 调用生成方法
    HighResImageGenUtil2c.generateImagesFromWordInfoList(wordInfoList,
      imagePath,
      fileName,
      outputDir);
  }
}
