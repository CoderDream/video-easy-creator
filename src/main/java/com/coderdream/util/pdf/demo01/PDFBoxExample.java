package com.coderdream.util.pdf.demo01;

import com.coderdream.util.CommonUtil;
import com.coderdream.util.file.PdfFileFinder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;
import java.io.IOException;

@Slf4j
public class PDFBoxExample {

  public static void main(String[] args) {

    String folderName = "170202";

    List<String> stringList = readPdfFile(folderName);
    for (String s : stringList) {
      System.out.println("### " + s);
    }
  }

  public static List<String> readPdfFile(String folderName) {
    List<String> stringList = new ArrayList<>();
    try {
      String pdfFileName = PdfFileFinder.findPdfFileName(folderName);
      String fileNameWithPath =
        CommonUtil.getFullPath(folderName) + pdfFileName;
      PDDocument document = PDDocument.load(new File(fileNameWithPath));
      PDFTextStripper stripper = new PDFTextStripper();
      String text = stripper.getText(document);
      System.out.println(text);
      String[] split = text.split("\n");
      stringList.addAll(Arrays.asList(split));

//      stringList.addAll(text.split("\n").wait0 ());
      document.close();
    } catch (IOException e) {
      log.error("Error, {}", e.getMessage(), e);
    }

    return stringList;
  }
}
