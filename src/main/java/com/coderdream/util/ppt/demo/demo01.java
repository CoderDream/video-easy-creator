package com.coderdream.util.ppt.demo;

import com.aspose.slides.IPPImage;
import com.aspose.slides.ISlide;
import com.aspose.slides.Presentation;
import com.aspose.slides.SaveFormat;
import com.aspose.slides.ShapeType;
import com.coderdream.util.ppt.LicenseUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * https://docs.aspose.com/slides/zh/java/picture-frame/
 */
public class demo01 {

  public static void main(String[] args) {

    LicenseUtil.loadLicense();
// 实例化表示 PPTX 文件的 Presentation 类
    Presentation pres = new Presentation();
    try {
      // 获取第一张幻灯片
      ISlide sld = pres.getSlides().get_Item(0);

      // 实例化图像类
      IPPImage imgx = pres.getImages()
        .addImage(new FileInputStream(new File("D:\\0000\\CoverSample\\c0004.png")));

      // 添加具有图片等效高度和宽度的图片框
      sld.getShapes()
        .addPictureFrame(ShapeType.Rectangle, 50, 150, imgx.getWidth(),
          imgx.getHeight(), imgx);

      // 将 PPTX 文件写入磁盘
      pres.save("RectPicFrame.pptx", SaveFormat.Pptx);
    } catch (FileNotFoundException e) {
    } finally {
      if (pres != null) {
        pres.dispose();
      }
    }

  }


}
