//package com.coderdream.util.markdown;
//
//import com.itextpdf.text.Document;
//import com.itextpdf.text.PageSize;
//import com.itextpdf.text.pdf.PdfWriter;
//
//import com.vladsch.flexmark.util.ast.Node;
//import java.util.Arrays;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Entities;
//import org.jsoup.nodes.Entities.EscapeMode;
//import org.xhtmlrenderer.pdf.ITextRenderer;
//import java.io.*;
//
//import com.vladsch.flexmark.html.HtmlRenderer;
//import com.vladsch.flexmark.parser.Parser;
//import com.vladsch.flexmark.util.data.MutableDataSet;
//import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
//import com.vladsch.flexmark.ext.tables.TablesExtension;
//import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
//import com.vladsch.flexmark.ext.anchorlink.AnchorLinkExtension;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//
//import com.vladsch.flexmark.html.HtmlRenderer;
//import com.vladsch.flexmark.parser.Parser;
//import com.vladsch.flexmark.util.data.MutableDataSet;
//import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
//import com.vladsch.flexmark.ext.tables.TablesExtension;
//import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
//import com.vladsch.flexmark.ext.anchorlink.AnchorLinkExtension;
//
//public class MarkdownToPdfConverter {
//
//  public static void main(String[] args) throws Exception {
//    String markdownFilePath = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\bai-ci-zan-001.md"; // 替换为你的 Markdown 文件路径
//    String pdfOutputPath = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\bai-ci-zan-001.pdf";  // 替换为你的 PDF 输出路径
//
//    markdownToPdf(markdownFilePath, pdfOutputPath);
//
//  }
//
//  public static void markdownToPdf(String markdownFilePath,
//    String pdfOutputPath) throws Exception {
//    // 读取 markdown 文件
//    String markdownContent = new String(
//      Files.readAllBytes(Paths.get(markdownFilePath)));
//    // 转换为 html
//    String htmlContent = markdownToHtml(markdownContent);
//    htmlToPdf(htmlContent, pdfOutputPath);
//  }
//
//  private static void htmlToPdf(String htmlContent, String pdfOutputPath)
//    throws Exception {
//    // 创建 Document
//    try (OutputStream os = new FileOutputStream(pdfOutputPath)) {
//      Document document = new Document(PageSize.A4);
//      PdfWriter writer = PdfWriter.getInstance(document, os);
//      document.open();
//      ITextRenderer renderer = new ITextRenderer();
//      renderer.setDocumentFromString(htmlContent);
//      renderer.layout();
//      renderer.createPDF(os);
//
//      document.close();
//    }
//  }
//
//
////  private static String markdownToHtml(String markdown) {
////    MutableDataSet options = new MutableDataSet();
////    options.set(Parser.EXTENSIONS, Arrays.asList(
////      StrikethroughExtension.create(),
////      TablesExtension.create(),
////      TaskListExtension.create(),
////      AnchorLinkExtension.create()
////    ));
////    options.set(HtmlRenderer.RENDER_HEADER_ID, true); // 生成html header id
//////    options.set(HtmlRenderer.XHTML_CLOSE_EMPTY_TAGS, true); // 输出xhtml格式
////    options.set(HtmlRenderer.HTML_BLOCK_OPEN_TAG_EOL, true); // 输出xhtml格式
////    Parser parser = Parser.builder(options).build();
////    HtmlRenderer renderer = HtmlRenderer.builder(options).build();
////
////    Node document = parser.parse(markdown);
////    String html = renderer.render(document);
////    org.jsoup.nodes.Document document1 = Jsoup.parse(html);
////    document1.outputSettings().escapeMode(EscapeMode.xhtml);
////    return document1.html();
////  }
//
//
//
//
//  private static String markdownToHtml(String markdown) {
//    MutableDataSet options = new MutableDataSet();
//    options.set(Parser.EXTENSIONS,java.util.Arrays.asList(
//      StrikethroughExtension.create(),
//      TablesExtension.create(),
//      TaskListExtension.create(),
//      AnchorLinkExtension.create()
//    ));
//    //   options.set(HtmlRenderer.RENDER_HEADER_ID, true); // 这个属性是在 SharedDataKeys 类定义的
//    //   options.set(HtmlRenderer.XHTML_CLOSE_EMPTY_TAGS, true); // 这个属性定义在 HtmlRenderer 类里
//    Parser parser = Parser.builder(options).build();
//    HtmlRenderer renderer = HtmlRenderer.builder(options)
//      .escapeHtml(false)    // 是否转义html
//      .percentEncodeUrls(true) // 是否对url进行编码
//      //    .softBreak("<br />") // 设置软换行的显示
//      .nodeRendererFactory(new  NodeRenderFactory()) // 自定义渲染器
//      .build();
//
//    Node document = parser.parse(markdown);
//    return renderer.render(document);
//  }
//}
