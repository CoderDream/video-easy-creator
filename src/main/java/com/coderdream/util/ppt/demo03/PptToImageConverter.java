package com.coderdream.util.ppt.demo03;

import com.aspose.slides.*;
import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.ppt.LicenseUtil;
import com.coderdream.util.ppt.MicrosoftConstants;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.imageio.IIOImage;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

@Slf4j
public class PptToImageConverter {

  private static final int DEFAULT_WIDTH = 1920;
  private static final int DEFAULT_HEIGHT = 1080;
  private static final String DEFAULT_IMAGE_FORMAT = "png";

  /**
   * 将 PPT/PPTX 文件转换为图片
   *
   * @param pptFileDir  PPT/PPTX 文件路径
   * @param outputDir   输出目录
   * @param width       图片宽度
   * @param height      图片高度
   * @param imageFormat 图片格式，例如 "png", "jpg"
   * @param dpi         图片DPI (Dots Per Inch)
   */
  public static void convertPptToImages(String pptFileDir, String outputDir,
    int width, int height, String imageFormat, int dpi) {
    // 检查输出目录是否存在，如果不存在则创建
    File outDir = new File(outputDir);
    if (!outDir.exists()) {
      if (outDir.mkdirs()) {
        log.info("封面图创建目录成功，路径：{}", outputDir);
      } else {
        log.error("封面图创建目录失败，路径：{}", outputDir);
        return;
      }
    }

    LicenseUtil.loadLicense(MicrosoftConstants.PPTX_TO_OTHER);
    Presentation presentation;
    try { // 使用 try-with-resources 确保资源关闭
      presentation = new Presentation(pptFileDir);
      ISlideCollection slides = presentation.getSlides();
      int slideCount = slides.size();

      for (int i = 0; i < slideCount; i++) {
        ISlide slide = slides.get_Item(i);

        BufferedImage image = slide.getThumbnail(new Dimension(width, height));

        String outputFileName = String.format("slide_%d.%s", i + 1,
          imageFormat);
        File outputFile = new File(outputDir, outputFileName);

        try {
          saveImage(image, imageFormat, outputFile, dpi,
            (imageFormat.equalsIgnoreCase("jpg")
              || imageFormat.equalsIgnoreCase("jpeg")) ? 0.9f
              : 1.0f); //JPEG格式可以设置压缩质量
        } catch (IOException e) {
          log.error("Error writing image:{} ", outputFile.getAbsolutePath(),
            e); // 打印异常堆栈信息
        }
      }
    } catch (Exception e) {
      log.error("Error converting PPT to images: {} ", pptFileDir,
        e); // 打印异常堆栈信息
    }
  }

  /**
   * 保存图片
   *
   * @param image      BufferedImage 对象
   * @param formatName 图片格式，例如 "png", "jpg"
   * @param outputFile 输出文件
   * @param dpi        图片DPI
   * @param quality    图片质量(仅对jpg有效, 0-1, 1为最好)
   * @throws IOException
   */
  private static void saveImage(BufferedImage image, String formatName,
    File outputFile, int dpi, float quality) throws IOException {
    if (formatName.equalsIgnoreCase("jpg") || formatName.equalsIgnoreCase(
      "jpeg")) {
      // JPEG 保存，可以设置压缩质量
      ImageWriter writer = null;
      Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
      if (iter.hasNext()) {
        writer = iter.next();
      }
      if (writer != null) {
        ImageWriteParam iwp = writer.getDefaultWriteParam();
        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        iwp.setCompressionQuality(quality); // 设置图片质量
        try (ImageOutputStream output = ImageIO.createImageOutputStream(
          outputFile)) {
          writer.setOutput(output);
          IIOImage iioImage = new IIOImage(image, null, null);
          writer.write(null, iioImage, iwp);
          writer.dispose();
        }
      } else {
        throw new IOException("No suitable ImageWriter found for JPEG format");
      }

    } else {
      // PNG 等其他格式
      try (ImageOutputStream output = ImageIO.createImageOutputStream(
        outputFile)) {
        ImageIO.write(image, formatName, output);
      }
    }

    // 设置DPI (需要考虑平台兼容性，并非所有平台都支持)
    setDPI(outputFile, formatName, dpi);
  }

  /**
   * 设置DPI (Dots Per Inch) 这种方式设置DPI依赖于具体的图片格式和操作系统.  某些图片格式(如JPG)并不直接支持DPI元数据,
   * 有些操作系统对DPI的支持也不完善. 对于更可靠的DPI设置, 你可能需要考虑使用专门的图像处理库, 例如TwelveMonkeys ImageIO,
   * 它可以提供更广泛的格式支持和更精确的DPI控制.
   *
   * @param outputFile
   * @param formatName
   * @param dpi
   * @throws IOException
   */
  private static void setDPI(File outputFile, String formatName, int dpi)
    throws IOException {
    // 这种方式设置DPI依赖于具体的图片格式和操作系统, 并非总是有效
    if (formatName.equalsIgnoreCase("png")) {
      // PNG does not support DPI natively, so this might not work as expected.
      // Consider using metadata libraries for more robust DPI handling.
      return;
    }

    // DPI设置，并非所有格式都支持
    // 获取BufferedImage
    BufferedImage bufferedImage = ImageIO.read(outputFile);

    if (bufferedImage != null) {
      // 获取当前DPI
      int currentDPIX = (int) bufferedImage.getProperty("dpi x");
      int currentDPIY = (int) bufferedImage.getProperty("dpi y");
      log.info("图片原始DPI：X={}, Y={}", currentDPIX, currentDPIY);
      // 创建Graphics2D对象
      Graphics2D g2d = bufferedImage.createGraphics();

      // 设置RenderingHints
      RenderingHints rh = new RenderingHints(RenderingHints.KEY_RENDERING,
        RenderingHints.VALUE_RENDER_QUALITY);
      g2d.setRenderingHints(rh);

      // 设置DPI
//      bufferedImage.setResolution(dpi);

      // 释放Graphics2D对象
      g2d.dispose();

      // 写回文件
      ImageIO.write(bufferedImage, formatName, outputFile);
    } else {
      log.warn("无法读取图片，无法设置DPI");
    }
  }


  // 其他重载方法，使用默认值
  public static void convertPptToImages(String pptFileDir, String outputDir) {
    convertPptToImages(pptFileDir, outputDir, DEFAULT_WIDTH, DEFAULT_HEIGHT,
      DEFAULT_IMAGE_FORMAT, 300);
  }

  public static void convertPptToImages(String pptFileDir, String outputDir,
    int width, int height) {
    convertPptToImages(pptFileDir, outputDir, width, height,
      DEFAULT_IMAGE_FORMAT, 300);
  }

  public static void convertPptToImages(String pptFilePath, String picFileDir,
    String prefix) {
    convertPptToImages(pptFilePath, picFileDir, prefix, DEFAULT_WIDTH,
      DEFAULT_HEIGHT);
  }

  public static void convertPptToImages(String pptFilePath, String picFileDir,
    String prefix, int width, int height) {
    convertPptToImages(pptFilePath, picFileDir, prefix, width, height,
      DEFAULT_IMAGE_FORMAT, 300);
  }

  public static void convertPptToImages(String pptFilePath, String picFileDir,
    String prefix, int width, int height, String imageFormat, int dpi) {
    long startTime = System.currentTimeMillis();
    // 检查输出目录是否存在，如果不存在则创建
    File outDir = new File(picFileDir);
    if (!outDir.exists()) {
      if (outDir.mkdirs()) {
        log.info("封面图创建目录成功，路径：{}", picFileDir);
      } else {
        log.error("封面图创建目录失败，路径：{}", picFileDir);
        return;
      }
    }

    LicenseUtil.loadLicense(MicrosoftConstants.PPTX_TO_OTHER);

    try{
      Presentation presentation = new Presentation(pptFilePath);
      ISlideCollection slides = presentation.getSlides();
      int slideCount = slides.size();
      for (int i = 0; i < slideCount; i++) {
        ISlide slide = slides.get_Item(i);
        BufferedImage image = slide.getThumbnail(new Dimension(width, height));

        String outputFileName = String.format("%s%s_%03d.%s", picFileDir,
          prefix, i + 1, imageFormat);
        File outputFile = new File(outputFileName);

        try {
          saveImage(image, imageFormat, outputFile, dpi,
            (imageFormat.equalsIgnoreCase("jpg")
              || imageFormat.equalsIgnoreCase("jpeg")) ? 0.9f
              : 1.0f); //JPEG格式可以设置压缩质量
        } catch (IOException e) {
          log.error("Error writing image:{} ", outputFile.getAbsolutePath(), e);
        }
      }
      long endTime = System.currentTimeMillis();
      long durationMillis = endTime - startTime;
      log.info("PPT转图片成功，路径：{}，共计{}张图片，耗时: {}", pptFilePath,
        slideCount, CdTimeUtil.formatDuration(durationMillis));

    } catch (Exception e) {
      log.error("Error converting PPT to images: {} ", pptFilePath, e);
    }
  }

  public static void convertPptToImages(String pptFileDir, int i,
    String outputFileName) {
    convertPptToImages(pptFileDir, i, outputFileName, DEFAULT_WIDTH,
      DEFAULT_HEIGHT);
  }

  public static void convertPptToImages(String pptFileDir, int i,
    String outputFileName, int width, int height) {
    convertPptToImages(pptFileDir, i, outputFileName, width, height,
      DEFAULT_IMAGE_FORMAT, 300);
  }

  public static void convertPptToImages(String pptFileDir, int i,
    String outputFileName, int width, int height, String imageFormat, int dpi) {
    long startTime = System.currentTimeMillis();
    LicenseUtil.loadLicense(MicrosoftConstants.PPTX_TO_OTHER);

    try {
      Presentation presentation = new Presentation(pptFileDir);
      ISlideCollection slides = presentation.getSlides();
      int slideCount = slides.size();
      if (i >= slideCount) {
        log.error("幻灯片索引超出范围");
        return;
      }

      ISlide slide = slides.get_Item(i);
      BufferedImage image = slide.getThumbnail(new Dimension(width, height));

      File outputFile = new File(outputFileName);

      try {
        saveImage(image, imageFormat, outputFile, dpi,
          (imageFormat.equalsIgnoreCase("jpg") || imageFormat.equalsIgnoreCase(
            "jpeg")) ? 0.9f : 1.0f); //JPEG格式可以设置压缩质量
      } catch (IOException e) {
        log.error("Error writing image:{} ", outputFile.getAbsolutePath(), e);
      }
    } catch (Exception e) {
      log.error("Error converting PPT to images: {} ", pptFileDir, e);
    }
    long endTime = System.currentTimeMillis();
    long durationMillis = endTime - startTime;
    log.info("封面创建成功，路径：{}，耗时: {}", outputFileName,
      CdTimeUtil.formatDuration(durationMillis));
  }


  public static void convertFirstSlideToImage(String pptFileDir,
    String outputFileName) {
    convertPptToImages(pptFileDir, 0, outputFileName, DEFAULT_WIDTH,
      DEFAULT_HEIGHT);
  }

  // 默认DPI为300
  public static void convertFirstSlideToImage(String pptFileDir,
    String outputFileName, int width, int height) {
    convertPptToImages(pptFileDir, 0, outputFileName, width, height,
      DEFAULT_IMAGE_FORMAT, 300);
  }

  // 可以指定DPI
  public static void convertFirstSlideToImage(String pptFileDir,
    String outputFileName, int width, int height, String imageFormat, int dpi) {
    convertPptToImages(pptFileDir, 0, outputFileName, width, height,
      imageFormat, dpi);
  }

  /**
   * 测试
   */
  public static void main(String[] args) {
    String pptFileDir = "D:\\0000\\ppt\\Book02\\Book02模板_03.pptx"; // 替换为您的 PPT 文件路径
    String outputDir = "D:\\0000\\ppt\\Book02\\directory";      // 替换为您希望保存图片的目录
    String imageFormat = "jpg"; // 图片格式 (png, jpg, etc.)
    int dpi = 300; // 设置DPI

    //PptToImageConverter.convertPptToImages(pptFileDir, outputDir);
    PptToImageConverter.convertPptToImages(pptFileDir, outputDir, DEFAULT_WIDTH,
      DEFAULT_HEIGHT, imageFormat, dpi);
  }
}
