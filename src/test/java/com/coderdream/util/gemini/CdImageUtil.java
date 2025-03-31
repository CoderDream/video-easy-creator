package com.coderdream.util.gemini;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CdImageUtil {

  public static boolean isImageSizeCorrect(String imagePath, int targetWidth, int targetHeight) {
    try {
      File imageFile = new File(imagePath);
      BufferedImage image = ImageIO.read(imageFile);
      if (image == null) {
        System.err.println("Could not read image file: " + imagePath);
        return false;
      }
      int width = image.getWidth();
      int height = image.getHeight();
      log.info("Image size: {} x {}", width, height);
      log.info("Target size: {} x {}", targetWidth, targetHeight);
      // 16~20
      double widthDouble = (double) width;
      double heightDouble = (double) height;
      double ratio = widthDouble / heightDouble;
      return ratio < 2.0 && ratio > 1.6;
      //  return width == targetWidth && height == targetHeight;
    } catch (IOException e) {
      System.err.println("Error reading image: " + e.getMessage());
      return false;
    }
  }
}
