package com.coderdream.util.excel;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import java.awt.Color;
import java.awt.Font;
import org.apache.poi.ss.usermodel.*;
//import org.jgraphx.mxGraph;
//import org.jgraphx.swing.mxGraphComponent;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class ExcelToImageConverter {

  public static void main(String[] args) {
    String excelFilePath = "D:\\14_LearnEnglish\\6MinuteEnglish\\2018\\180823\\180823_核心词汇表.xlsx"; // 替换为你的 Excel 文件路径
    String imageFilePath = "D:\\14_LearnEnglish\\6MinuteEnglish\\2018\\180823\\180823_核心词汇表.png"; // 替换为你的图片输出路径

    try {
      convertExcelToImage(excelFilePath, imageFilePath);
      System.out.println(
        "Excel successfully converted to image: " + imageFilePath);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void convertExcelToImage(String excelFilePath,
    String imageFilePath) throws IOException {
    // 1. 读取 Excel 文件
    Workbook workbook = WorkbookFactory.create(
      new FileInputStream(excelFilePath));
    Sheet sheet = workbook.getSheetAt(0);

    // 2. 创建图像
    mxGraph graph = new mxGraph();
    Object parent = graph.getDefaultParent();

    graph.getModel().beginUpdate();
    try {
      int startX = 50;
      int startY = 50;
      int cellHeight = 30; // 设置单元格高度
      int cellWidth = 150; // 设置单元格宽度
      Font font = new Font("Arial", Font.PLAIN, 14);

      for (Row row : sheet) {
        for (Cell cell : row) {
          String cellValue = getCellValue(cell);

          // 创建矩形顶点
          Object v1 = graph.insertVertex(parent, null, cellValue, startX,
            startY, cellWidth, cellHeight);

          // 设置顶点样式，比如边框、颜色和字体
          graph.setCellStyle(
            "fontColor=black;strokeColor=black;fillColor=white;fontSize=14;fontFamily=Arial;fontStyle=0",
            new Object[]{v1});

          // 更新 X 坐标
          startX += cellWidth + 10;
        }
        // 新行
        startX = 50;
        startY += cellHeight + 10;

      }
    } finally {
      graph.getModel().endUpdate();
    }

    // 使用 Swing 组件显示图形
    mxGraphComponent graphComponent = new mxGraphComponent(graph);
    // 将 graphComponent 转换为图像
    BufferedImage image = getBufferedImage(graphComponent);

    // 3. 保存为图像文件
    File outputFile = new File(imageFilePath);
    ImageIO.write(image, "png", outputFile);
    workbook.close(); // 关闭工作簿
  }


  private static String getCellValue(Cell cell) {
    if (cell == null) {
      return "";
    }
    DataFormatter formatter = new DataFormatter();
    return formatter.formatCellValue(cell);

  }

  private static BufferedImage getBufferedImage(JComponent component) {
    Dimension size = component.getPreferredSize();
    BufferedImage image = new BufferedImage(size.width, size.height,
      BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics = image.createGraphics();

    // 设置背景为白色
    graphics.setColor(Color.WHITE);
    graphics.fillRect(0, 0, size.width, size.height);

    component.paint(graphics);
    graphics.dispose();
    return image;
  }
}
