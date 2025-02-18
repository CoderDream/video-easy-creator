package com.coderdream.util.ppt;

import com.aspose.slides.*;

import com.coderdream.util.cd.CdTimeUtil;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PptToImageConverter {

  /**
   * 将 PPT/PPTX 文件转换为图片
   *
   * @param pptFileDir PPT/PPTX 文件路径
   * @param outputDir  输出目录
   */
  public static void convertPptToImages(String pptFileDir, String outputDir,
    int width, int height) {
    // 检查输出目录是否存在，如果不存在则创建
    File outDir = new File(outputDir);
    if (!outDir.exists()) {
      boolean mkdir = outDir.mkdirs();
      if (mkdir) {
        log.info("封面图创建目录成功，路径：{}", outputDir);
      } else {
        log.error("封面图创建目录失败，路径：{}", outputDir);
        return;
      }
    }
    String imageFormat = "png";
    LicenseUtil.loadLicense(MicrosoftConstants.PPTX_TO_OTHER);

    Presentation presentation; // 声明在 try 外面
    try {
      presentation = new Presentation(pptFileDir);

      // 获取幻灯片数量
      ISlideCollection slides = presentation.getSlides();
      int slideCount = slides.size();

      // 遍历每一张幻灯片
      for (int i = 0; i < slideCount; i++) {
        ISlide slide = slides.get_Item(i);

        // 获取幻灯片的缩略图（可以自定义尺寸）
        // 方式一： 快速，简单
//                BufferedImage image = slide.getThumbnail(1f, 1f); // 缩放比例

//                // 方式二：可以自定义图片大小
//                Dimension2D dimension = presentation.getSlideSize().getSize();
//                BufferedImage image = slide.getThumbnail(new Dimension((int) dimension.getWidth(), (int) dimension.getHeight()));
        BufferedImage image = slide.getThumbnail(new Dimension(width, height));

        // 构建输出文件名
        String outputFileName = String.format("slide_%d.%s", i + 1,
          imageFormat);
        File outputFile = new File(outputDir, outputFileName);

        // 保存图片
        try {
          ImageIO.write(image, imageFormat, outputFile);
        } catch (IOException e) {
          log.error("Error writing image:{} ", outputFile.getAbsolutePath());
        }
      }
    } catch (Exception e) {
      log.error("Error converting PPT to images: {} ", pptFileDir);
    }
  }

  /**
   * 将 PPT/PPTX 文件转换为图片
   *
   * @param pptFileDir PPT/PPTX 文件路径
   */
  public static void convertPptToImages(String pptFileDir, int i,
    String outputFileName, int width, int height) {
    long startTime = System.currentTimeMillis();
    String imageFormat = "png";
    LicenseUtil.loadLicense(MicrosoftConstants.PPTX_TO_OTHER);

    Presentation presentation; // 声明在 try 外面
    try {
      presentation = new Presentation(pptFileDir);

      // 获取幻灯片数量
      ISlideCollection slides = presentation.getSlides();
      int slideCount = slides.size();
      if (i >= slideCount) {
        log.error("幻灯片索引超出范围");
        return;
      }

      // 遍历每一张幻灯片
      ISlide slide = slides.get_Item(i);

      // 获取幻灯片的缩略图（可以自定义尺寸）
      // 方式一： 快速，简单
//                BufferedImage image = slide.getThumbnail(1f, 1f); // 缩放比例

//                // 方式二：可以自定义图片大小
//                Dimension2D dimension = presentation.getSlideSize().getSize();
//                BufferedImage image = slide.getThumbnail(new Dimension((int) dimension.getWidth(), (int) dimension.getHeight()));
      BufferedImage image = slide.getThumbnail(new Dimension(width, height));

      // 构建输出文件名
      File outputFile = new File(outputFileName);
      // 保存图片
      try {
        ImageIO.write(image, imageFormat, outputFile);
      } catch (IOException e) {
        log.error("Error writing image:{} ", outputFile.getAbsolutePath());
      }
    } catch (Exception e) {
      log.error("Error converting PPT to images: {} ", pptFileDir);
    }
    long endTime = System.currentTimeMillis(); // 记录视频生成结束时间
    long durationMillis = endTime - startTime; // 计算耗时（毫秒）
    log.info("封面创建成功，路径：{}，耗时: {}", outputFileName,
      CdTimeUtil.formatDuration(durationMillis));
  }

  /**
   * 将 PPT/PPTX 文件转换为图片
   *
   * @param pptFileDir PPT/PPTX 文件路径
   */
  public static void convertPptToImages(String pptFileDir, int i,
    String outputFileName) {
    convertPptToImages(pptFileDir, i, outputFileName, 1920, 1080);
  }

  /**
   * 将 PPT/PPTX 文件转换为图片
   *
   * @param pptFileDir PPT/PPTX 文件路径
   */
  public static void convertFirstSlideToImage(String pptFileDir,
    String outputFileName) {
    convertPptToImages(pptFileDir, 0, outputFileName, 1920, 1080);
  }

  /**
   * 将 PPT/PPTX 文件转换为图片
   *
   * @param pptFileDir PPT/PPTX 文件路径
   * @param outputDir  输出目录
   */
  public static void convertPptToImages(String pptFileDir, String outputDir) {
    convertPptToImages(pptFileDir, outputDir, 1920, 1080);
  }

  /**
   * 测试
   */
  public static void main(String[] args) {
    String pptFileDir = "D:\\0000\\ppt\\Book02\\Book02模板_03.pptx"; // 替换为您的 PPT 文件路径
    String outputDir = "D:\\0000\\ppt\\Book02\\directory";      // 替换为您希望保存图片的目录
    // 图片格式 (png, jpg, etc.)

    PptToImageConverter.convertPptToImages(pptFileDir, outputDir);
  }
}
