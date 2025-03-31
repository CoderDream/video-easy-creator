package com.coderdream.util.pdf;

import com.coderdream.util.cd.CdTimeUtil;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


/**
 * 使用 iText 5.x 生成包含中文、图片和背景的 PDF 文档，并尝试使用 D:\\java_output\\fonts 目录下所有字体。
 * 解决重音符号（如ˈ）打印不出来的问题，并且使用两种字体，一种显示音标，一种显示中文
 */
@Slf4j
public class ChineseImagePdf_iText55 {

  /**
   * 主方法，用于生成 PDF 文档。
   *
   * @param args 命令行参数
   */
  public static void main(String[] args) {
    String dest = "D:\\java_output\\chinese_image_itext5_multi_phonetic.pdf"; // 输出的 PDF 文件名
    String doulosSILFontPath = "D:\\java_output\\fonts\\DoulosSIL-R.ttf"; // 明确指定 Doulos SIL 字体
    String simSunFontPath = "D:\\java_output\\fonts\\SourceHanSansCN-Bold.ttf"; //  SimSun 字体
    String imagePath = "D:\\java_output\\background.png";  // 图片文件路径 （作为背景图片使用）

    try {
      // 测试多个单词，每个单词都有音标
      String multiPhoneticText = "01 exclusive [ɪkˈskluːsɪv] adj. 独有的；02 example [ɪɡˈzɑːmpl] n. 例子; 03 difficult [ˈdɪfɪkəlt] adj. 困难的; 04 beautiful [ˈbjuːtɪfl] adj. 美丽的; 05 university [ˌjuːnɪˈvɜːrsəti] n. 大学; 06 photography [fəˈtɒɡrəfi] n. 摄影";
      generatePdfWithMixedFonts(dest, doulosSILFontPath, simSunFontPath,
        imagePath, multiPhoneticText);
      log.info("PDF 文件已成功生成: {}", dest);
    } catch (DocumentException | IOException e) {
      log.error("生成 PDF 文件时发生错误: ", e);
    }
  }


  /**
   * 生成 PDF 文档，使用两种字体，并添加背景图片。
   *
   * @param dest              输出的 PDF 文件路径
   * @param doulosSILFontPath Doulos SIL 字体文件路径
   * @param simSunFontPath    SimSun 字体文件路径
   * @param imagePath         图片文件路径（背景图片）
   * @param text              要生成的文本内容
   * @throws DocumentException iText 文档异常
   * @throws IOException       IO 异常
   */
  public static void generatePdfWithMixedFonts(String dest,
    String doulosSILFontPath, String simSunFontPath,
    String imagePath, String text)
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

      addBackground(writer, imagePath);

      //创建两种字体
      BaseFont doulosSILBaseFont = BaseFont.createFont(doulosSILFontPath,
        BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
      Font doulosSILFont = new Font(doulosSILBaseFont, 12);

      BaseFont simSunBaseFont = BaseFont.createFont(simSunFontPath,
        BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
      Font simSunFont = new Font(simSunBaseFont, 12);

      // 添加文本内容, 使用Chunk混合使用两种字体
      String[] words = text.split("(?<=\\d\\s)"); // 使用 lookbehind 正则表达式拆分
      for (String word : words) {
        Paragraph p = new Paragraph();
        String[] parts = word.split("(\\[.+?\\])");
        if (parts.length > 0) {
          p.add(new Chunk(parts[0], simSunFont));
        }
        String phoneticPart = "";
        if (word.contains("[") && word.contains("]")) {
          phoneticPart = word.substring(word.indexOf("["),
            word.indexOf("]") + 1);
        }
        p.add(new Chunk(phoneticPart, doulosSILFont));
        if (parts.length > 1) {
          p.add(new Chunk(parts[1], simSunFont));
        }
        document.add(p);
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

}
