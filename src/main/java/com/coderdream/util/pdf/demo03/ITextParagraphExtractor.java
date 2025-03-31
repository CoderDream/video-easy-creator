package com.coderdream.util.pdf.demo03;

import com.coderdream.util.CommonUtil;
import com.coderdream.util.file.PdfFileFinder;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy;

import java.io.File;
import java.io.IOException;

public class ITextParagraphExtractor {

  public static void main(String[] args) {
    try {
      String folderName = "170202";
      String pdfFileName = PdfFileFinder.findPdfFileName(folderName);
      String fileNameWithPath =
        CommonUtil.getFullPath(folderName) + File.separator + pdfFileName;
      PdfDocument pdfDoc = new PdfDocument(
        new PdfReader(new File(fileNameWithPath)));
      StringBuilder text = new StringBuilder();

      for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
        PdfPage page = pdfDoc.getPage(i);
        ITextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
        PdfCanvasProcessor parser = new PdfCanvasProcessor(strategy);
        parser.processPageContent(page);
        text.append(strategy.getResultantText()).append("\n"); // 添加段落分隔符
      }

      System.out.println(text.toString());
      pdfDoc.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
