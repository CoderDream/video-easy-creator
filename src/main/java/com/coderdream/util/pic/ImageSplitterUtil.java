package com.coderdream.util.pic;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ImageSplitterUtil {

  private static final AtomicInteger counter = new AtomicInteger(
    1); // 用于生成唯一的文件名序号
  private static final int EXTEND_Y = 100; // 垂直延伸量

  /**
   * 垂直分割图片
   *
   * @param imagePath   图片路径
   * @param splitPoints 分割点列表，垂直方向上的像素坐标
   * @return 分割后的图片路径列表
   */
  public List<String> splitImageVertical(String imagePath,
    List<Integer> splitPoints) {
    List<String> splitImagePaths = new ArrayList<>(); // 存储分割后的图片路径
    BufferedImage originalImage;

    try {
      // 1. 读取图片
      originalImage = ImageIO.read(new File(imagePath));
      if (originalImage == null) {
        log.error("读取图片失败，请检查图片路径:{}", imagePath);
        return splitImagePaths; // 返回空列表，表示分割失败
      }
    } catch (IOException e) {
      log.error("读取图片时发生异常:{}", e.getMessage(), e);
      return splitImagePaths; // 返回空列表，表示分割失败
    }

    int imageHeight = originalImage.getHeight(); // 获取图片高度
    int imageWidth = originalImage.getWidth(); // 获取图片宽度
    log.info("原始图片宽高：{} x {}", imageWidth, imageHeight);
    int startY = 0; // 初始分割起点
    int i = 0;

    // 2. 根据分割点进行分割
    for (Integer splitPoint : splitPoints) {
      i++;
      if (splitPoint > imageHeight) {
        log.warn("分割点 {} 大于图片高度 {}, 跳过当前分割", splitPoint,
          imageHeight);
        continue; // 跳过无效分割点
      }

      int height = splitPoint - startY;

      int subImageHeight = height;
      // 除了最后一张，其他图片都向下延伸100像素
      if (i < splitPoints.size()) {
        subImageHeight += EXTEND_Y;
        if (startY + subImageHeight > imageHeight) {
          subImageHeight = imageHeight - startY;
        }
      }
      BufferedImage subImage = originalImage.getSubimage(0, startY, imageWidth,
        subImageHeight); // 截取子图
      String subImagePath = generateSubImagePath(imagePath); // 生成子图路径
      try {
        // 3. 保存子图
        ImageIO.write(subImage, "png", new File(subImagePath));
        splitImagePaths.add(subImagePath); // 添加到列表
        log.info("分割图片成功，保存路径：{}", subImagePath);
      } catch (IOException e) {
        log.error("保存子图时发生异常:{}", e.getMessage(), e);
      }
      startY = splitPoint; // 更新下次分割的起点
    }

    // 4. 处理最后一部分图片
    if (startY < imageHeight) {
      BufferedImage subImage = originalImage.getSubimage(0, startY, imageWidth,
        imageHeight - startY);
      String subImagePath = generateSubImagePath(imagePath);
      try {
        ImageIO.write(subImage, "png", new File(subImagePath));
        splitImagePaths.add(subImagePath);
        log.info("分割最后一部分图片成功，保存路径：{}", subImagePath);
      } catch (IOException e) {
        log.error("保存最后一部分子图时发生异常:{}", e.getMessage(), e);
      }
    }

    return splitImagePaths;
  }

  /**
   * 生成子图的保存路径，使用自增序号命名，并保存到 subpic 文件夹下
   *
   * @param originalImagePath 原始图片路径
   * @return 子图路径
   */
  private String generateSubImagePath(String originalImagePath) {
    String extension = getFileExtension(originalImagePath); // 获取文件扩展名
    String dirPath = new File(originalImagePath).getParent();  // 获取文件路径（不包含文件名）
    String subDirName = "subpic";
    String subDirPath = dirPath + File.separator + subDirName; // subpic 文件夹路径

    // 创建 subpic 文件夹，如果不存在
    Path path = Paths.get(subDirPath);
    if (!Files.exists(path)) {
      try {
        Files.createDirectories(path);
        log.info("创建文件夹成功：{}", subDirPath);
      } catch (IOException e) {
        log.error("创建文件夹失败：{}", subDirPath, e);
        // 如果文件夹创建失败，则直接将文件保存在图片目录下，不影响分割
        return dirPath + File.separator + "subpic_" + String.format("%03d",
          counter.getAndIncrement()) + "." + extension;

      }
    }

    String filename =
      "subpic_" + String.format("%03d", counter.getAndIncrement()) + "."
        + extension; // 使用自增序号生成文件名
    return subDirPath + File.separator + filename;
  }

  /**
   * 获取文件扩展名
   *
   * @param filename 文件名
   * @return 文件扩展名
   */
  private String getFileExtension(String filename) {
    int dotIndex = filename.lastIndexOf('.');
    if (dotIndex > 0 && dotIndex < filename.length() - 1) {
      return filename.substring(dotIndex + 1);
    }
    return ""; // 返回空，如果没有扩展名
  }


  public static void main(String[] args) {
    String imagePath = "D:\\0000\\pdf\\花甲老头20241226岁末总结_pic\\Snapshot_20241229_001.png"; // 测试图片路径
    int height = 238112; // 设置分割点
    // 设置21个分割点
    int size = 101;
    int step = height / size;
    List<Integer> splitPoints = new ArrayList<>();
    for (int i = 0; i < size - 1; i++) {
      splitPoints.add(step * (i + 1));
    }

    ImageSplitterUtil splitter = new ImageSplitterUtil();
    List<String> splitImagePaths = splitter.splitImageVertical(imagePath,
      splitPoints);

    if (splitImagePaths.isEmpty()) {
      log.error("图片分割失败");
    } else {
      log.info("图片分割完成，共生成 {} 张图片", splitImagePaths.size());
      splitImagePaths.forEach(System.out::println);
    }

  }
}
