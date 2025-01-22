package com.coderdream.util.ppt;

import com.aspose.slides.IImage;
import com.aspose.slides.ISlide;
import com.aspose.slides.ImageFormat;
import com.aspose.slides.Presentation;
import com.coderdream.util.cd.CdFileUtil;
import java.awt.Dimension;

public class PptToPng1080p {

  /**
   * 将PPT演示文稿保存为1080p分辨率的PNG图像
   *
   * @param pptFilePath PPT文件路径
   * @param outputDir   输出目录
   */
  public static void savePptToPng1080p(String pptFilePath, String outputDir) {
    String imageName = CdFileUtil.getPureFileNameWithoutExtensionWithPath(
      pptFilePath);
    LicenseUtil.loadLicense(MicrosoftConstants.PPTX_TO_OTHER);
    Presentation pres = new Presentation(pptFilePath);
    try {
      Dimension size = new Dimension(1920, 1080);
      for (int index = 0; index < pres.getSlides().size(); index++) {
        ISlide slide = pres.getSlides().get_Item(index);
        IImage slideImage = slide.getImage(size);
        try {
          slideImage.save(outputDir + imageName + ".png",
            ImageFormat.Png);
        } finally {
          if (slideImage != null) {
            slideImage.dispose();
          }
        }
      }
    } finally {
      pres.dispose();
    }

  }

}
