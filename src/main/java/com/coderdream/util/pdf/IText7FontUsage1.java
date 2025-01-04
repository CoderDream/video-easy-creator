//package com.coderdream.util.pdf;
//
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.PdfWriter;
//import com.itextpdf.layout.Document;
//import com.itextpdf.layout.element.Paragraph;
//import com.itextpdf.kernel.font.PdfFont;
//import com.itextpdf.kernel.font.PdfFontFactory;
//import java.io.IOException;
//
//public class IText7FontUsage1 {
//
//  public static void main(String[] args) throws IOException {
//    String pdfPath = "font_usage.pdf";
//    PdfWriter writer = new PdfWriter(pdfPath);
//    PdfDocument pdf = new PdfDocument(writer);
//    Document document = new Document(pdf);
//
//    // 1. 使用系统字体 (ArialMT 和 Arial-BoldMT)
//    PdfFont arialFont = PdfFontFactory.createFont("ArialMT");
//    PdfFont arialBoldFont = PdfFontFactory.createFont("Arial-BoldMT");
//
//    // 2. 使用自定义字体 (假设你已经把字体文件放在项目 resources 目录下)
//    String microsoftYaHeiPath = "D:\\java_output\\msyh.ttf"; // 替换为你的微软雅黑字体文件路径
//    String openSansBoldPath = "D:\\java_output\\OpenSans-Bold.ttf"; // 替换为你的 Open Sans Bold 字体文件路径
//    String openSansRegularPath = "D:\\java_output\\OpenSans-Regular.ttf"; // 替换为你的 Open Sans Regular 字体文件路径
//
//    PdfFont microsoftYaHeiFont = PdfFontFactory.createFont(microsoftYaHeiPath,
//      "Identity-H", true);
//    PdfFont openSansBoldFont = PdfFontFactory.createFont(openSansBoldPath,
//      "Identity-H", true);
//    PdfFont openSansRegularFont = PdfFontFactory.createFont(openSansRegularPath,
//      "Identity-H", true);
//
//    //  3. 使用自定义字体 (假设你已经把字体文件放在项目 resources 目录下), 可以不使用 `Identity-H` ，默认字体也是可以的。
//    String microsoftYaHeiBoldPath = "msyhbd.ttf";
//    PdfFont microsoftYaHeiBoldFont = PdfFontFactory.createFont(
//      microsoftYaHeiBoldPath);
//
//    // 4. 添加文本
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
