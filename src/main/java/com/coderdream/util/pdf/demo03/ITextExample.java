package com.coderdream.util.pdf.demo03;

import com.coderdream.util.CommonUtil;
import com.coderdream.util.file.PdfFileFinder;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;

public class ITextExample {

  public static void main(String[] args) {
    try {
      String folderName = "170202";
      String pdfFileName = PdfFileFinder.findPdfFileName(folderName);
      String fileNameWithPath =
        CommonUtil.getFullPath(folderName) + pdfFileName;
      PdfDocument pdfDoc = new PdfDocument(
        new PdfReader(new File(fileNameWithPath)));
      StringBuilder text = new StringBuilder();
      for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
        text.append(PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i)));
      }
      System.out.println(text.toString());
      pdfDoc.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
