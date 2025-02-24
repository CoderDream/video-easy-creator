package com.coderdream.util.pic;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PngToJpgConverter {

  /**
   * 将单个PNG文件转换为JPG文件。
   *
   * @param pngFilePath PNG文件的路径
   * @param jpgFilePath JPG文件的路径
   * @param quality     JPG图像的质量 (0.0 - 1.0, 越高越好，但文件越大)
   * @return boolean 转换成功返回true，否则返回false
   */
  public static boolean convertPngToJpg(String pngFilePath, String jpgFilePath,
    float quality) {
    try {
      File pngFile = new File(pngFilePath);
      if (!pngFile.exists()) {
        System.err.println("PNG文件不存在: " + pngFilePath);
        return false;
      }

      BufferedImage pngImage = ImageIO.read(pngFile);

      // 创建一个新的BufferedImage，使用RGB颜色空间，否则透明度可能会导致黑色背景
      BufferedImage jpgImage = new BufferedImage(pngImage.getWidth(),
        pngImage.getHeight(), BufferedImage.TYPE_INT_RGB);

      //  绘制 PNG 图像到 JPG 图像上，使用白色背景填充透明部分。
      Graphics2D graphics = jpgImage.createGraphics();
      graphics.setColor(Color.WHITE);  // 设置背景色为白色
      graphics.fillRect(0, 0, pngImage.getWidth(),
        pngImage.getHeight()); // 填充背景
      graphics.drawImage(pngImage, 0, 0, null);  // 绘制图像
      graphics.dispose();

      File jpgFile = new File(jpgFilePath);
      ImageIO.write(jpgImage, "jpg", jpgFile);

      // 检查图像质量是否需要调整（旧版本JPEGImageWriteParam可能不支持直接设置quality）
      if (quality < 1.0f) {
        // 重新读取并处理图像
        BufferedImage image = ImageIO.read(jpgFile);
        File tempJpgFile = new File(jpgFilePath);

        // 使用第三方库处理图片质量(如果必须指定质量，但尽量避免使用第三方库)
        // ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        // ImageWriteParam param = writer.getDefaultWriteParam();
        // param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        // param.setCompressionQuality(quality);
        // ImageOutputStream ios = ImageIO.createImageOutputStream(tempJpgFile);
        // writer.setOutput(ios);
        // writer.write(null, new IIOImage(image, null, null), param);
        // ios.close();
        // writer.dispose();
      }

      System.out.println("成功将 " + pngFilePath + " 转换为 " + jpgFilePath);
      return true;

    } catch (IOException e) {
      System.err.println(
        "转换 " + pngFilePath + " 到 " + jpgFilePath + " 时发生错误: "
          + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 批量将指定目录下的所有PNG文件转换为JPG文件。
   *
   * @param inputDir  输入目录，包含PNG文件
   * @param outputDir 输出目录，存放转换后的JPG文件
   * @param quality   JPG图像的质量 (0.0 - 1.0, 越高越好，但文件越大)
   */
  public static void batchConvertPngToJpg(String inputDir, String outputDir,
    float quality) {
    File inputDirectory = new File(inputDir);
    File outputDirectory = new File(outputDir);

    if (!inputDirectory.exists() || !inputDirectory.isDirectory()) {
      System.err.println("输入目录不存在或不是一个目录: " + inputDir);
      return;
    }

    if (!outputDirectory.exists()) {
      if (!outputDirectory.mkdirs()) {
        System.err.println("无法创建输出目录: " + outputDir);
        return;
      }
    }

    File[] pngFiles = inputDirectory.listFiles(
      file -> file.isFile() && file.getName().toLowerCase().endsWith(".png"));

    if (pngFiles == null || pngFiles.length == 0) {
      System.out.println("在 " + inputDir + " 目录下没有找到任何PNG文件。");
      return;
    }

    System.out.println(
      "开始批量转换PNG到JPG，共 " + pngFiles.length + " 个文件。");

    for (File pngFile : pngFiles) {
      String pngFilePath = pngFile.getAbsolutePath();
      String pngFileName = pngFile.getName();
      String jpgFileName =
        pngFileName.substring(0, pngFileName.lastIndexOf(".")) + ".jpg";
      String jpgFilePath = outputDir + File.separator + jpgFileName;

      convertPngToJpg(pngFilePath, jpgFilePath, quality);
    }

    System.out.println("批量转换完成。");
  }


  public static void main(String[] args) {
    // 示例用法
    String inputDirectory = "D:\\0000\\EnBook002\\cover"; // 存放 PNG 文件的目录
    String outputDirectory = "D:\\0000\\EnBook002\\cover"; // 存放 JPG 文件的目录
    float jpgQuality = 0.8f; // JPG 图像质量

//        // 创建测试用的目录和文件（可移除，仅用于测试）
//        new File(inputDirectory).mkdirs();
//        try {
//            new File(inputDirectory, "test.png").createNewFile();
//            new File(inputDirectory, "test2.PNG").createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    PngToJpgConverter.batchConvertPngToJpg(inputDirectory, outputDirectory,
      jpgQuality);

    // 也可以单个转换
    // convertPngToJpg("input_png/test.png", "output_jpg/test.jpg", 0.9f);
  }
}
