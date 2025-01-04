//package com.coderdream.util.pdf;
//
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.PdfWriter;
//import com.itextpdf.layout.Document;
//import com.itextpdf.layout.element.Paragraph;
//import com.itextpdf.kernel.font.PdfFont;
//import com.itextpdf.kernel.font.PdfFontFactory;
//import java.io.IOException;
//import java.nio.file.Paths;
//
//public class IText7FontUsage {
//
//  public static void main(String[] args) throws IOException {
//    String pdfPath = "font_usage.pdf";
//    String fontDir = "D:\\java_output";
//    PdfWriter writer = new PdfWriter(pdfPath);
//    PdfDocument pdf = new PdfDocument(writer);
//    Document document = new Document(pdf);
//
//    // 1. 加载所有字体，并且指定 Identity-H 编码和字体嵌入。
//    PdfFont arialFont = PdfFontFactory.createFont(
//      Paths.get(fontDir, "ArialMT.ttf").toString(), "Identity-H", true);
//    PdfFont arialBoldFont = PdfFontFactory.createFont(
//      Paths.get(fontDir, "Arial-BoldMT.ttf").toString(), "Identity-H", true);
//    PdfFont microsoftYaHeiFont = PdfFontFactory.createFont(
//      Paths.get(fontDir, "msyh.ttf").toString(), "Identity-H", true);
//    PdfFont microsoftYaHeiBoldFont = PdfFontFactory.createFont(
//      Paths.get(fontDir, "msyhbd.ttf").toString(), "Identity-H", true);
//    PdfFont openSansBoldFont = PdfFontFactory.createFont(
//      Paths.get(fontDir, "OpenSans-Bold.ttf").toString(), "Identity-H", true);
//    PdfFont openSansRegularFont = PdfFontFactory.createFont(
//      Paths.get(fontDir, "OpenSans-Regular.ttf").toString(), "Identity-H",
//      true);
//
//    // 2. 添加文本
//    document.add(new Paragraph("This is ArialMT Font.", arialFont));
//    document.add(new Paragraph("This is Arial-BoldMT Font.", arialBoldFont));
//    document.add(new Paragraph("你好，这是微软雅黑 Font.", microsoftYaHeiFont));
//    document.add(
//      new Paragraph("你好，这是微软雅黑 Bold Font.", microsoftYaHeiBoldFont));
//    document.add(
//      new Paragraph("This is OpenSans-Bold Font.", openSansBoldFont));
//    document.add(
//      new Paragraph("This is OpenSans-Regular Font.", openSansRegularFont));
//
//    document.close();
//    pdf.close();
//    writer.close();
//
//    System.out.println("PDF with custom fonts generated successfully!");
//  }
//}
