package com.coderdream.util.pdf;

import com.coderdream.util.cd.CdTimeUtil;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 使用 iText 5.x 生成包含中文、图片和背景的 PDF 文档，并尝试使用 D:\\java_output\\fonts 目录下所有字体。
 * 解决重音符号（如ˈ）打印不出来的问题
 */
@Slf4j
public class ChineseImagePdf_iText52 {

  /**
   * 主方法，用于生成 PDF 文档。
   *
   * @param args 命令行参数
   */
  public static void main(String[] args) {
    String dest = "D:\\java_output\\chinese_image_itext5_allfonts02.pdf"; // 输出的 PDF 文件名
    String fontDir = "D:\\java_output\\fonts";  // 字体文件目录
    String imagePath = "D:\\java_output\\background.png";  // 图片文件路径 （作为背景图片使用）

    try {
      generatePdfWithAllFonts(dest, fontDir, imagePath);
      log.info("PDF 文件已成功生成: {}", dest);
    } catch (DocumentException | IOException e) {
      log.error("生成 PDF 文件时发生错误: ", e);
    }
  }

  /**
   * 生成 PDF 文档并尝试使用所有字体文件，并添加背景图片。
   *
   * @param dest      输出的 PDF 文件路径
   * @param fontDir   字体文件目录
   * @param imagePath 图片文件路径（背景图片）
   * @throws DocumentException iText 文档异常
   * @throws IOException       IO 异常
   */
  public static void generatePdfWithAllFonts(String dest, String fontDir,
    String imagePath)
    throws DocumentException, IOException {
    long startTime = System.nanoTime();
    log.info("开始生成 PDF 文件...");

    FileOutputStream fos = null;
    Document document = null;

    try {
      fos = new FileOutputStream(dest);
      document = new Document();

      PdfWriter writer = PdfWriter.getInstance(document, fos);
      document.open();

//      addBackground(writer, imagePath);

      // 获取字体文件列表
      List<Path> fontFiles = getFontFiles(fontDir);

      for (Path fontPath : fontFiles) {
        try {
          //尝试加载字体
          BaseFont baseFont = BaseFont.createFont(fontPath.toString(),
            BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
          Font chineseFont = new Font(baseFont, 12);

          // 添加文本内容
          String text = "01 exclusive [ɪkˈskluːsɪv] adj. 独有的；奢华的；排外的；n. 独家新闻";
          text = "01 exclusive [ɪkˈskluːsɪv] adj. 独有的；奢华的；排外的；n. 独家新闻" + fontPath.getFileName();
          Paragraph p = new Paragraph(text, chineseFont);
          document.add(p);
        } catch (Exception e) {
          log.error("字体加载失败: {}, 错误信息：{}", fontPath.getFileName(),
            e.getMessage());
          Paragraph p = new Paragraph("字体加载失败: " + fontPath.getFileName(),
            new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL,
              BaseColor.RED));
          document.add(p);
        }
        document.add(Chunk.NEWLINE); // 添加新行，使字体显示不紧凑
      }

      long endTime = System.nanoTime();
      String duration = CdTimeUtil.formatDuration(endTime - startTime);
      log.info("PDF 文件生成完成， 耗时：{}", duration);

    } finally {
      if (document != null) {
        document.close();
      }
      if (fos != null) {
        try {
          fos.close();
        } catch (IOException e) {
          log.error("关闭FileOutputStream资源时发生异常：", e);
        }

      }
    }
  }


  /**
   * 添加背景图片到 PDF 文档。
   *
   * @param writer    PDF Writer 对象
   * @param imagePath 背景图片路径
   * @throws DocumentException iText 文档异常
   * @throws IOException       IO 异常
   */
  private static void addBackground(PdfWriter writer, String imagePath)
    throws DocumentException, IOException {
    Image background = Image.getInstance(imagePath);
    Rectangle pageSize = writer.getPageSize();
    float backgroundWidth = pageSize.getWidth();
    float backgroundHeight = pageSize.getHeight();
    background.scaleToFit(backgroundWidth, backgroundHeight);
    background.setAbsolutePosition(0, 0);

    PdfContentByte canvas = writer.getDirectContentUnder();
    for (int i = 1; i <= writer.getPageNumber(); i++) {
      canvas.addImage(background);
    }

  }

  /**
   * 获取指定目录下所有字体文件.
   *
   * @param fontDir 字体目录路径
   * @return 字体文件路径列表
   * @throws IOException IO 异常
   */
  private static List<Path> getFontFiles(String fontDir) throws IOException {
    Path dir = Paths.get(fontDir);
    if (!Files.exists(dir) || !Files.isDirectory(dir)) {
      throw new IOException("Font directory not found: " + fontDir);
    }

    return Files.list(dir)
      .filter(path -> {
        String fileName = path.getFileName().toString().toLowerCase();
        return fileName.endsWith(".ttf") || fileName.endsWith(".otf");
      })
      .collect(Collectors.toList());
  }

}
