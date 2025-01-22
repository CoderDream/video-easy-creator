package com.coderdream.util.ppt.demo;

import com.aspose.slides.FillType;
import com.aspose.slides.IAlphaModulateFixed;
import com.aspose.slides.ICell;
import com.aspose.slides.ICellFormat;
import com.aspose.slides.IImage;
import com.aspose.slides.IPPImage;
import com.aspose.slides.IPictureFrame;
import com.aspose.slides.IRow;
import com.aspose.slides.IShape;
import com.aspose.slides.ISlide;
import com.aspose.slides.ITable;
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

class Demo02TableTest {

  @Test
  void demo01() {
    LicenseUtil.loadLicense();
// 实例化表示 PPTX 文件的 Presentation 类
    // Instantiates a Presentation class that represents a PPTX file
    Presentation pres = new Presentation();
    try {
      // Accesses the first slide
      ISlide sld = pres.getSlides().get_Item(0);

      // Defines columns with widths and rows with heights
      double[] dblCols = {50, 50, 50};
      double[] dblRows = {50, 30, 30, 30, 30};

      // Adds a table shape to slide
      ITable tbl = sld.getShapes().addTable(100, 50, dblCols, dblRows);

      // Sets the border format for each cell
      for (int row = 0; row < tbl.getRows().size(); row++) {
        for (int cell = 0; cell < tbl.getRows().get_Item(row).size(); cell++) {
          ICellFormat cellFormat = tbl.getRows().get_Item(row).get_Item(cell)
            .getCellFormat();

          cellFormat.getBorderTop().getFillFormat().setFillType(FillType.Solid);
          cellFormat.getBorderTop().getFillFormat().getSolidFillColor()
            .setColor(Color.RED);
          cellFormat.getBorderTop().setWidth(5);

          cellFormat.getBorderBottom().getFillFormat()
            .setFillType(FillType.Solid);
          cellFormat.getBorderBottom().getFillFormat().getSolidFillColor()
            .setColor(Color.RED);
          cellFormat.getBorderBottom().setWidth(5);

          cellFormat.getBorderLeft().getFillFormat()
            .setFillType(FillType.Solid);
          cellFormat.getBorderLeft().getFillFormat().getSolidFillColor()
            .setColor(Color.RED);
          cellFormat.getBorderLeft().setWidth(5);

          cellFormat.getBorderRight().getFillFormat()
            .setFillType(FillType.Solid);
          cellFormat.getBorderRight().getFillFormat().getSolidFillColor()
            .setColor(Color.RED);
          cellFormat.getBorderRight().setWidth(5);
        }
      }
      // Merges cells 1 & 2 of row 1
      tbl.mergeCells(tbl.getRows().get_Item(0).get_Item(0),
        tbl.getRows().get_Item(1).get_Item(1), false);

      // Adds some text to the merged cell
      tbl.getRows().get_Item(0).get_Item(0).getTextFrame()
        .setText("Merged Cells");

      // Saves the presentation to Disk
      pres.save("table01.pptx", SaveFormat.Pptx);
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
    // Instantiates a Presentation class that represents a PPTX file
    Presentation pres = new Presentation();
    try {
      // Accesses first slide
      ISlide sld = pres.getSlides().get_Item(0);

      // Defines columns with widths and rows with heights
      double[] dblCols = {70, 70, 70, 70};
      double[] dblRows = {70, 70, 70, 70};

      // Adds a table shape to slide
      ITable tbl = sld.getShapes().addTable(100, 50, dblCols, dblRows);

      // Sets the border format for each cell
      for (IRow row : tbl.getRows()) {
        for (ICell cell : row) {
          cell.getCellFormat().getBorderTop().getFillFormat()
            .setFillType(FillType.Solid);
          cell.getCellFormat().getBorderTop().getFillFormat()
            .getSolidFillColor().setColor(Color.RED);
          cell.getCellFormat().getBorderTop().setWidth(5);

          cell.getCellFormat().getBorderBottom().getFillFormat()
            .setFillType(FillType.Solid);
          cell.getCellFormat().getBorderBottom().getFillFormat()
            .getSolidFillColor().setColor(Color.RED);
          cell.getCellFormat().getBorderBottom().setWidth(5);

          cell.getCellFormat().getBorderLeft().getFillFormat()
            .setFillType(FillType.Solid);
          cell.getCellFormat().getBorderLeft().getFillFormat()
            .getSolidFillColor().setColor(Color.RED);
          cell.getCellFormat().getBorderLeft().setWidth(5);

          cell.getCellFormat().getBorderRight().getFillFormat()
            .setFillType(FillType.Solid);
          cell.getCellFormat().getBorderRight().getFillFormat()
            .getSolidFillColor().setColor(Color.RED);
          cell.getCellFormat().getBorderRight().setWidth(5);
        }
      }

      // Saves presentation to disk
      pres.save("StandardTables_out.pptx", SaveFormat.Pptx);
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

    // Instantiates the Presentation class that represents a PPTX file
    Presentation pres = new Presentation("StandardTables_out.pptx");
    try {

      // Accesses the first slide
      ISlide sld = pres.getSlides().get_Item(0);

      // Initializes null TableEx
      ITable tbl = null;

      // Iterates through the shapes and sets a reference to the table found
      for (IShape shp : sld.getShapes()) {
        if (shp instanceof ITable) {
          tbl = (ITable) shp;
          // Sets the text for the first column of the second row
          tbl.get_Item(0, 1).getTextFrame().setText("New");
        }
      }

      // Saves the modified presentation to disk
      pres.save("table1_out.pptx", SaveFormat.Pptx);
    } finally {
      if (pres != null) {
        pres.dispose();
      }
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
    System.out.println("pictureFrame: " + pictureFrame);
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
      IPPImage imgx = pres.getImages().addImage(
        new FileInputStream(new File("D:\\0000\\CoverSample\\c0004.png")));

      // 添加与图片高度和宽度相等的图片框
      IPictureFrame pf = sld.getShapes()
        .addPictureFrame(ShapeType.Rectangle, 50, 150, imgx.getWidth(),
          imgx.getHeight(), imgx);

      // 对 PictureFrameEx 应用一些格式设置
      pf.getLineFormat().getFillFormat().setFillType(FillType.Solid);
      pf.getLineFormat().getFillFormat().getSolidFillColor()
        .setColor(Color.BLUE);
      pf.getLineFormat().setWidth(20);
      pf.setRotation(45);

      // 将 PPTX 文件写入磁盘
      pres.save("RectPicFrame_05.pptx", SaveFormat.Pptx);
    } catch (IOException e) {
    } finally {
      if (pres != null) {
        pres.dispose();
      }
    }

  }


  @Test
  void demo06() {
    LicenseUtil.loadLicense();
// 实例化表示 PPTX 文件的 Presentation 类
    Presentation pres = new Presentation();
    try {
      IShape shape = pres.getSlides().get_Item(0).getShapes()
        .addAutoShape(ShapeType.RoundCornerRectangle, 20, 20, 200, 150);

      shape.getEffectFormat().enableOuterShadowEffect();
      shape.getEffectFormat().getOuterShadowEffect().getShadowColor()
        .setColor(Color.DARK_GRAY);
      shape.getEffectFormat().getOuterShadowEffect().setDistance(10);
      shape.getEffectFormat().getOuterShadowEffect().setDirection(45);

      pres.save("output_06.pptx", SaveFormat.Pptx);
    } finally {
      if (pres != null) {
        pres.dispose();
      }
    }
  }


  @Test
  void demo07() {
    LicenseUtil.loadLicense();
// 实例化表示 PPTX 文件的 Presentation 类
    Presentation pres = new Presentation();
    try {
      IShape shape = pres.getSlides().get_Item(0).getShapes()
        .addAutoShape(ShapeType.RoundCornerRectangle, 20, 20, 200, 150);

      shape.getEffectFormat().enableReflectionEffect();
      shape.getEffectFormat().getReflectionEffect().setRectangleAlign(
        RectangleAlignment.Bottom);
      shape.getEffectFormat().getReflectionEffect().setDirection(90);
      shape.getEffectFormat().getReflectionEffect().setDistance(55);
      shape.getEffectFormat().getReflectionEffect().setBlurRadius(4);

      pres.save("reflection_07.pptx", SaveFormat.Pptx);
    } finally {
      if (pres != null) {
        pres.dispose();
      }
    }

  }


  @Test
  void demo08() {
    LicenseUtil.loadLicense();
// 实例化表示 PPTX 文件的 Presentation 类
    Presentation pres = new Presentation();
    try {
      IShape shape = pres.getSlides().get_Item(0).getShapes()
        .addAutoShape(ShapeType.RoundCornerRectangle, 20, 20, 200, 150);

      shape.getEffectFormat().enableGlowEffect();
      shape.getEffectFormat().getGlowEffect().getColor()
        .setColor(Color.MAGENTA);
      shape.getEffectFormat().getGlowEffect().setRadius(15);

      pres.save("glow.pptx", SaveFormat.Pptx);
    } finally {
      if (pres != null) {
        pres.dispose();
      }
    }


  }


  @Test
  void demo09() {
    LicenseUtil.loadLicense();
// 实例化表示 PPTX 文件的 Presentation 类
    Presentation pres = new Presentation();
    try {
      IShape shape = pres.getSlides().get_Item(0).getShapes()
        .addAutoShape(ShapeType.RoundCornerRectangle, 20, 20, 200, 150);

      shape.getEffectFormat().enableSoftEdgeEffect();
      shape.getEffectFormat().getSoftEdgeEffect().setRadius(15);

      pres.save("softEdges.pptx", SaveFormat.Pptx);
    } finally {
      if (pres != null) {
        pres.dispose();
      }
    }


  }


}
