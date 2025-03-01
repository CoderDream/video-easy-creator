package com.coderdream.util.pdf.demo01;

import com.coderdream.util.CommonUtil;
import com.coderdream.util.file.PdfFileFinder;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFBoxParagraphExtractor extends PDFTextStripper {

  public PDFBoxParagraphExtractor() throws IOException {
    super();
    // 设置段落分隔符
//    setParagraphStart("\n");
    setParagraphEnd("\n");
  }

  @Override
  protected void writeString(String text, List<TextPosition> textPositions)
    throws IOException {
    // 处理每一行文本
    super.writeString(text, textPositions);
  }

  public static void main(String[] args) {
    try {
      String folderName = "170202";
      String pdfFileName = PdfFileFinder.findPdfFileName(folderName);
      String fileNameWithPath =
        CommonUtil.getFullPath(folderName) + pdfFileName;
      PDDocument document = PDDocument.load(new File(fileNameWithPath));
      PDFBoxParagraphExtractor stripper = new PDFBoxParagraphExtractor();
      String text = stripper.getText(document);
      System.out.println(text);
      document.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
