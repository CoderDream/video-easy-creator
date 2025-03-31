package com.coderdream.util.itext5;

import com.coderdream.util.cd.CdTimeUtil;
import com.coderdream.util.itext5.util.PathUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 使用 iText 和 XML Worker 将 HTML 文件转换为 PDF 文档。
 */
@Slf4j
public class JavaToPdfHtml {

  private static final String DEST = "target/HelloWorld_CN_HTML.pdf"; // 输出 PDF 文件路径
  private static final String HTML =
    PathUtil.getCurrentPath() + "/template.html"; // HTML 模板文件路径
  private static final String FONT = "D:\\java_output\\fonts\\simhei.ttf"; // 字体文件路径

  /**
   * 主方法，将 HTML 文件转换为 PDF 文档。
   *
   * @param args 命令行参数
   */
  public static void main(String[] args) {
    long startTime = System.nanoTime(); // 记录开始时间
    log.info("开始生成 PDF 文件...");
    try {
      generatePdfFromHtml();
      long endTime = System.nanoTime(); // 记录结束时间
      String duration = CdTimeUtil.formatDuration(endTime-startTime); // 计算方法耗时
      log.info("PDF 文件生成完成， 耗时：{}", duration);
    } catch (Exception e) {
      log.error("生成 PDF 文件时发生错误:", e);
    }
  }

  /**
   * 生成 PDF 文档的核心方法，内部处理异常。
   */
  private static void generatePdfFromHtml() {
    FileOutputStream fos = null;
    Document document = null;
    try {
      fos = new FileOutputStream(DEST);
      document = new Document();
      PdfWriter writer = PdfWriter.getInstance(document, fos);
      document.open();
      XMLWorkerFontProvider fontImp = new XMLWorkerFontProvider(
        XMLWorkerFontProvider.DONTLOOKFORFONTS);
      fontImp.register(FONT);
      try (FileInputStream fis = new FileInputStream(HTML)) {
        XMLWorkerHelper.getInstance().parseXHtml(writer, document,
          fis, null, StandardCharsets.UTF_8, fontImp);
      }

    } catch (DocumentException | IOException e) {
      log.error("生成PDF文档时发生异常:", e);
    } finally {
      if (document != null) {
        document.close();
      }
      if (fos != null) {
        try {
          fos.close();
        } catch (IOException e) {
          log.error("关闭FileOutputStream资源时发生异常:", e);
        }
      }
    }
  }

}
