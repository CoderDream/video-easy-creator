package com.coderdream.util.ppt;

import com.aspose.slides.IImage;
import com.aspose.slides.ISlide;
import com.aspose.slides.ImageFormat;
import com.aspose.slides.Presentation;
import com.coderdream.util.cd.CdFileUtil;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class PptToPng {

  /**
   * 将PPT演示文稿保存为1080p分辨率的PNG图像
   *
   * @param pptFilePath PPT文件路径
   * @param outputDir   输出目录
   */
  public static void savePptToPng(String pptFilePath, String outputDir,
    int width, int height) {
    String imageName = CdFileUtil.getPureFileNameWithoutExtensionWithPath(
      pptFilePath);
    LicenseUtil.loadLicense(MicrosoftConstants.PPTX_TO_OTHER);
    Presentation pres = new Presentation(pptFilePath);
    int number = 0;
    try {
//      Dimension size = new Dimension(width, height);
//      for (int index = 0; index < pres.getSlides().size(); index++) {
//        ISlide slide = pres.getSlides().get_Item(index);
//        IImage slideImage = slide.getImage(size);
//        number = index + 1;
//        try {
//          slideImage.save(outputDir + imageName + "_" + number + ".png",
//            ImageFormat.Png);
//        } finally {
//          if (slideImage != null) {
//            slideImage.dispose();
//          }
//        }
//      }

      // 遍历每一张幻灯片
      for (int i = 0; i < pres.getSlides().size(); i++) {
        ISlide slide = pres.getSlides().get_Item(i);
        number = i + 1;
        // 将幻灯片渲染为指定大小的图片
//        BufferedImage image = slide.getThumbnail(width, height);

        // 创建 BufferedImage 对象
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // 将幻灯片渲染到 BufferedImage 上
//        slide.getThumbnail(image);

        // 保存为 PNG 文件
        try {
          ImageIO.write(image, "png", new File(outputDir + "output_slide_" + number + ".png"));
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }

    } finally {
      pres.dispose();
    }

  }

}
