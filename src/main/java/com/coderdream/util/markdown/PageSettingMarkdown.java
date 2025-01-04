//package com.coderdream.util.markdown;
//
//import com.spire.doc.Document;
//import com.spire.doc.FileFormat;
//import com.spire.doc.PageSetup;
//import com.spire.doc.Section;
//import com.spire.doc.documents.MarginsF;
//import com.spire.doc.documents.PageOrientation;
//import com.spire.doc.documents.PageSize;
//
//public class PageSettingMarkdown {
//    public static void main(String[] args) {
//        // 创建一个 Document 实例
//        Document doc = new Document();
//
//        // 加载 Markdown 文件
//        doc.loadFromFile("示例.md");
//
//        // 获取第一个节
//        Section section = doc.getSections().get(0);
//
//        // 设置页面尺寸、方向和边距
//        PageSetup pageSetup = section.getPageSetup();
//        pageSetup.setPageSize(PageSize.Letter);
//        pageSetup.setOrientation(PageOrientation.Landscape);
//        pageSetup.setMargins(new MarginsF(100, 100, 100, 100));
//
//        // 将 Markdown 文件保存为 PDF 文件
//        doc.saveToFile("output/Markdown转PDF.pdf", FileFormat.PDF);
//        doc.dispose();
//    }
//}
