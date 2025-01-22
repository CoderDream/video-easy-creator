package com.coderdream.util.ppt.demo;

import com.aspose.slides.FillType;
import com.aspose.slides.IAlphaModulateFixed;
import com.aspose.slides.IImage;
import com.aspose.slides.IPPImage;
import com.aspose.slides.IPictureFrame;
import com.aspose.slides.IShape;
import com.aspose.slides.ISlide;
import com.aspose.slides.ImageFormat;
import com.aspose.slides.Presentation;
import com.aspose.slides.RectangleAlignment;
import com.aspose.slides.SaveFormat;
import com.aspose.slides.ShapeType;
import com.coderdream.util.ppt.LicenseUtil;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class Demo01Test {

  @Test
  void demo01() {
    LicenseUtil.loadLicense();
// 实例化表示 PPTX 文件的 Presentation 类
    Presentation pres = new Presentation();
    try {
      // 获取第一张幻灯片
      ISlide sld = pres.getSlides().get_Item(0);

      // 实例化图像类
      IPPImage imgx = pres.getImages()
        .addImage(
          new FileInputStream(new File("D:\\0000\\CoverSample\\c0004.png")));

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

  @Test
  void demo02() {
    LicenseUtil.loadLicense();
// 实例化表示 PPTX 文件的 Presentation 类
    Presentation pres = new Presentation();
    try {
      // 获取第一张幻灯片
      ISlide sld = pres.getSlides().get_Item(0);

      // 实例化图像类
      IPPImage imgx = pres.getImages()
        .addImage(
          new FileInputStream(new File("D:\\0000\\CoverSample\\c0004.png")));

      // 添加具有图片等效高度和宽度的图片框
      IPictureFrame pf = sld.getShapes()
        .addPictureFrame(ShapeType.Rectangle, 50, 150, imgx.getWidth(),
          imgx.getHeight(), imgx);

      // 设置相对缩放高度和宽度
      pf.setRelativeScaleHeight(0.8f);
      pf.setRelativeScaleWidth(1.35f);

      // 将 PPTX 文件写入磁盘
      pres.save("RectPicFrame_02.pptx", SaveFormat.Pptx);
    } catch (FileNotFoundException e) {
    } finally {
      if (pres != null) {
        pres.dispose();
      }
    }
  }

  @Test
  void demo03() {
    LicenseUtil.loadLicense();
// 实例化表示 PPTX 文件的 Presentation 类

    Presentation presentation = new Presentation("RectPicFrame_02.pptx");

    try {
      ISlide firstSlide = presentation.getSlides().get_Item(0);
      IShape firstShape = firstSlide.getShapes().get_Item(0);

      if (firstShape instanceof IPictureFrame) {
        IPictureFrame pictureFrame = (IPictureFrame) firstShape;
        IImage slideImage = null;
        try {
          slideImage = pictureFrame.getPictureFormat().getPicture()
            .getImage().getImage();
          slideImage.save("slide_1_shape_1.png", ImageFormat.Png);
        } finally {
          if (slideImage != null) {
            slideImage.dispose();
          }
        }
      }
    } finally {
      presentation.dispose();
    }

  }

  /**
   * 获取图像的透明度
   */
  @Test
  void demo04() {
    LicenseUtil.loadLicense();
// 实例化表示 PPTX 文件的 Presentation 类
    Presentation presentation = new Presentation("RectPicFrame_02.pptx");

    var pictureFrame = (IPictureFrame) presentation.getSlides().get_Item(0)
      .getShapes().get_Item(0);
    System.out.println( "pictureFrame: " +  pictureFrame);
    var imageTransform = pictureFrame.getPictureFormat().getPicture()
      .getImageTransform();
    System.out.println("imageTransform: " + imageTransform);
    for (var effect : imageTransform) {
      System.out.println("effect: " + effect);
      if (effect instanceof IAlphaModulateFixed) {
        var alphaModulateFixed = (IAlphaModulateFixed) effect;
        var transparencyValue = 100 - alphaModulateFixed.getAmount();
        System.out.println("图片透明度: " + transparencyValue);
      }
    }
  }

  @Test
  void demo05() {
    LicenseUtil.loadLicense();
// 实例化表示 PPTX 文件的 Presentation 类
    Presentation pres = new Presentation();
    try {
      // 获取第一张幻灯片
      ISlide sld = pres.getSlides().get_Item(0);

      // 实例化图像类
      IPPImage imgx = pres.getImages().addImage(new FileInputStream(new File("D:\\0000\\CoverSample\\c0004.png")));

      // 添加与图片高度和宽度相等的图片框
      IPictureFrame pf = sld.getShapes().addPictureFrame(ShapeType.Rectangle, 50, 150, imgx.getWidth(), imgx.getHeight(), imgx);

      // 对 PictureFrameEx 应用一些格式设置
      pf.getLineFormat().getFillFormat().setFillType(FillType.Solid);
      pf.getLineFormat().getFillFormat().getSolidFillColor().setColor(Color.BLUE);
      pf.getLineFormat().setWidth(20);
      pf.setRotation(45);

      // 将 PPTX 文件写入磁盘
      pres.save("RectPicFrame_05.pptx", SaveFormat.Pptx);
    } catch (IOException e) {
    } finally {
      if (pres != null) pres.dispose();
    }

  }



  @Test
  void demo06() {
    LicenseUtil.loadLicense();
// 实例化表示 PPTX 文件的 Presentation 类
    Presentation pres = new Presentation();
    try {
      IShape shape = pres.getSlides().get_Item(0).getShapes().addAutoShape(ShapeType.RoundCornerRectangle, 20, 20, 200, 150);

      shape.getEffectFormat().enableOuterShadowEffect();
      shape.getEffectFormat().getOuterShadowEffect().getShadowColor().setColor(Color.DARK_GRAY);
      shape.getEffectFormat().getOuterShadowEffect().setDistance(10);
      shape.getEffectFormat().getOuterShadowEffect().setDirection(45);

      pres.save("output_06.pptx", SaveFormat.Pptx);
    } finally {
      if (pres != null) pres.dispose();
    }
  }


  @Test
  void demo07() {
    LicenseUtil.loadLicense();
// 实例化表示 PPTX 文件的 Presentation 类
    Presentation pres = new Presentation();
    try {
      IShape shape = pres.getSlides().get_Item(0).getShapes().addAutoShape(ShapeType.RoundCornerRectangle, 20, 20, 200, 150);

      shape.getEffectFormat().enableReflectionEffect();
      shape.getEffectFormat().getReflectionEffect().setRectangleAlign(
        RectangleAlignment.Bottom);
      shape.getEffectFormat().getReflectionEffect().setDirection(90);
      shape.getEffectFormat().getReflectionEffect().setDistance(55);
      shape.getEffectFormat().getReflectionEffect().setBlurRadius(4);

      pres.save("reflection_07.pptx", SaveFormat.Pptx);
    } finally {
      if (pres != null) pres.dispose();
    }

  }



  @Test
  void demo08() {
    LicenseUtil.loadLicense();
// 实例化表示 PPTX 文件的 Presentation 类
    Presentation pres = new Presentation();
    try {
      IShape shape = pres.getSlides().get_Item(0).getShapes().addAutoShape(ShapeType.RoundCornerRectangle, 20, 20, 200, 150);

      shape.getEffectFormat().enableGlowEffect();
      shape.getEffectFormat().getGlowEffect().getColor().setColor(Color.MAGENTA);
      shape.getEffectFormat().getGlowEffect().setRadius(15);

      pres.save("glow.pptx", SaveFormat.Pptx);
    } finally {
      if (pres != null) pres.dispose();
    }


  }


  @Test
  void demo09() {
    LicenseUtil.loadLicense();
// 实例化表示 PPTX 文件的 Presentation 类
    Presentation pres = new Presentation();
    try {
      IShape shape = pres.getSlides().get_Item(0).getShapes().addAutoShape(ShapeType.RoundCornerRectangle, 20, 20, 200, 150);

      shape.getEffectFormat().enableSoftEdgeEffect();
      shape.getEffectFormat().getSoftEdgeEffect().setRadius(15);

      pres.save("softEdges.pptx", SaveFormat.Pptx);
    } finally {
      if (pres != null) pres.dispose();
    }


  }






}
