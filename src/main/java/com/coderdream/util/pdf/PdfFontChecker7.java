//package com.coderdream.util.pdf;
//
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.PdfReader;
//import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
//import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
//import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
//import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
//import com.itextpdf.kernel.pdf.canvas.parser.EventType;
//import com.itextpdf.kernel.font.PdfFont;
//import java.io.IOException;
//import java.util.*;
//
//public class PdfFontChecker7 {
//
////  public static void main(String[] args) throws IOException {
////    String pdfPath = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\bai-ci-zan-001.pdf"; // 替换为你的 PDF 文件路径
////    getFontNames(pdfPath);
////  }
//
////  public static Set<String> getFontNames(String pdfPath) throws IOException {
////    PdfReader reader = new PdfReader(pdfPath);
////    PdfDocument pdfDoc = new PdfDocument(reader);
////    Set<String> fontSet = new HashSet<>();
////    for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
////      PdfCanvasProcessor processor = new PdfCanvasProcessor(
////        new IEventListener() {
////
////          @Override
////          public void eventOccurred(
////            com.itextpdf.kernel.pdf.canvas.parser.EventType eventType,
////            com.itextpdf.kernel.pdf.canvas.parser.data.IEventData iEventData) {
////            if (eventType == EventType.RENDER_TEXT) {
////              TextRenderInfo renderInfo = (TextRenderInfo) iEventData;
////              PdfFont font = renderInfo.getFont();
////              if (font != null) {
////                String fontName = font.getFontProgram().getFontName();
////                if (fontName != null) {
////                  fontSet.add(fontName);
////                }
////              }
////            }
////          }
////
////          @Override
////          public Set<EventType> getSupportedEvents() {
////            Set<EventType> eventTypes = new HashSet<>();
////            eventTypes.add(EventType.RENDER_TEXT);
////            return eventTypes;
////          }
////        });
////      processor.processPageContent(pdfDoc.getPage(i));
////    }
////
////    pdfDoc.close();
////    reader.close();
////    System.out.println("PDF used fonts: " + fontSet);
////    return fontSet;
////  }
////}
//public static void main(String[] args) throws IOException {
//  String pdfPath = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\bai-ci-zan-001.pdf"; // 替换为你的 PDF 文件路径
//  getFontNames(pdfPath);
//}
//
//  public static Set<String> getFontNames(String pdfPath) throws IOException {
//    PdfReader reader = new PdfReader(pdfPath);
//    PdfDocument pdfDoc = new PdfDocument(reader);
//    Set<String> fontSet = new HashSet<>();
//    for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
//      PdfCanvasProcessor processor = new PdfCanvasProcessor(new IEventListener() {
//
//        public void eventOccurred(EventType eventType, IEventData iEventData) {
//          if (eventType == EventType.RENDER_TEXT) {
//            TextRenderInfo renderInfo = (TextRenderInfo) iEventData;
//            PdfFont font = renderInfo.getFont();
//            if (font != null) {
//              // 获取字体名称方法修改了
//              String fontName = font.getFontProgram().getFontNames().getFontName();
//              if (fontName != null) {
//                fontSet.add(fontName);
//              }
//            }
//          }
//        }
//
//
//        /**
//         * Called when some event occurs during parsing a
//         * content stream.
//         *
//         * @param data Combines the data required for processing
//         *             corresponding event type.
//         * @param type Event type.
//         */
//        @Override
//        public void eventOccurred(IEventData data, EventType type) {
//          if (type == EventType.RENDER_TEXT) {
//            TextRenderInfo renderInfo = (TextRenderInfo) data;
//            PdfFont font = renderInfo.getFont();
//            if (font != null) {
//              // 获取字体名称方法修改了
//              String fontName = font.getFontProgram().getFontNames().getFontName();
//              if (fontName != null) {
//                fontSet.add(fontName);
//              }
//            }
//          }
//        }
//
//        @Override
//        public Set<EventType> getSupportedEvents() {
//          Set<EventType> eventTypes = new HashSet<>();
//          eventTypes.add(EventType.RENDER_TEXT);
//          return eventTypes;
//        }
//      });
//      processor.processPageContent(pdfDoc.getPage(i));
//    }
//
//    pdfDoc.close();
//    reader.close();
//    System.out.println("PDF used fonts: " + fontSet);
//    return fontSet;
//  }
//}
