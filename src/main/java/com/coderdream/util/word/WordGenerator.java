//package com.coderdream.util.word;
//
//import org.apache.poi.xwpf.usermodel.*;
//import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
//
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.math.BigInteger;
//
//import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
//import org.apache.poi.util.Units;
//import org.openxmlformats.schemas.wordprocessingml.x2006.main.STStyleType.Enum;
//
//public class WordGenerator {
//
//    private XWPFDocument document;
//
//    public WordGenerator() {
//        this.document = new XWPFDocument();
//        addHeadingStyles(); // 添加标题样式
//    }
//
//    // 添加标题样式 (Heading1, Heading2, Heading3, Heading4)
//    private void addHeadingStyles() {
//        XWPFStyles styles = document.createStyles();
//
//        // Heading 1
//        addNewStyle(styles, "Heading1", "标题 1", STStyleType.Enum.PARAGRAPH, "Arial", 32, "000000", true);
//
//        // Heading 2
//        addNewStyle(styles, "Heading2", "标题 2", STStyleType.Enum.PARAGRAPH, "Arial", 28, "000000", true);
//
//        // Heading 3
//        addNewStyle(styles, "Heading3", "标题 3", STStyleType.Enum.PARAGRAPH, "Arial", 24, "000000", true);
//
//        // Heading 4
//        addNewStyle(styles, "Heading4", "标题 4", STStyleType.Enum.PARAGRAPH, "Arial", 20, "000000", true);
//    }
//
//    private void addNewStyle(XWPFStyles styles, String styleId, String styleName, Enum type, String fontName, int fontSize, String color, boolean bold) {
//        XWPFStyle style = styles.addStyle(styleId);
//        style.setStyleId(styleId);
//
//        CTStyle ctStyle = style.getCTStyle();
//        ctStyle.setName(CTString.Factory.newInstance());
//        ctStyle.getName().setVal(styleName);
//        ctStyle.setType(type);
//
//        CTPPr pPr = ctStyle.addNewPPr();
//        pPr.addNewSpacing().setAfter(new BigInteger("0"));
//        pPr.addNewSpacing().setBefore(new BigInteger("0"));
//
//        CTRPr rPr = ctStyle.addNewRPr();
//        CTFonts fonts = rPr.addNewRFonts();
//        fonts.setAscii(fontName);
//        fonts.setHAnsi(fontName);
//
//        // 设置字体大小
//        BigInteger fontSizeBi = new BigInteger(String.valueOf(fontSize * 2));
//        rPr.addNewSz().setVal(fontSizeBi);
//        rPr.addNewSzCs().setVal(fontSizeBi);
//
//        if (bold) {
//            rPr.addNewB().setVal(STOnOff.TRUE);
//            rPr.addNewBCs().setVal(STOnOff.TRUE);
//        }
//
//        rPr.addNewColor().setVal(color);
//        styles.addStyle(style);
//    }
//
//    // 添加 H4 标题
//    public void addHeading(String text, int level) {
//        XWPFParagraph paragraph = document.createParagraph();
//        paragraph.setStyle("Heading" + level);  // 使用内置的标题样式
//        XWPFRun run = paragraph.createRun();
//        run.setText(text);
//    }
//
//    // 添加普通段落，并对指定文字加粗
//    public void addParagraphWithBold(String text, String boldText) {
//        XWPFParagraph paragraph = document.createParagraph();
//        String[] parts = text.split(boldText); // 将文本分割为包含需要加粗文字的部分
//        if (parts.length > 1) { // 如果存在需要加粗的文字
//            XWPFRun run1 = paragraph.createRun();
//            run1.setText(parts[0]);
//            XWPFRun run2 = paragraph.createRun();
//            run2.setText(boldText);
//            run2.setBold(true);
//            XWPFRun run3 = paragraph.createRun();
//            run3.setText(parts[1]);
//        } else {
//            XWPFRun run = paragraph.createRun();
//            run.setText(text); // 如果没有需要加粗的文字，则添加整个段落
//        }
//    }
//
//    // 添加本地磁盘图片
//    public void addImage(String imagePath, int width, int height) throws IOException, InvalidFormatException {
//        XWPFParagraph paragraph = document.createParagraph();
//        XWPFRun run = paragraph.createRun();
//        FileInputStream fis = new FileInputStream(imagePath);
//        run.addPicture(fis, XWPFDocument.PICTURE_TYPE_PNG, imagePath, Units.toEMU(width), Units.toEMU(height));
//        fis.close();
//    }
//
//    // 添加简单文本段落
//    public void addParagraph(String text) {
//        XWPFParagraph paragraph = document.createParagraph();
//        XWPFRun run = paragraph.createRun();
//        run.setText(text);
//    }
//
//    // 保存 Word 文档
//    public void saveDocument(String filePath) throws IOException {
//        try (FileOutputStream out = new FileOutputStream(filePath)) {
//            document.write(out);
//        }
//    }
//
//    //关闭document资源
//    public void close() throws IOException {
//        document.close();
//    }
//
//    public static void main(String[] args) {
//        String outputFilePath = "output.docx";
//        String imagePath = "D:\\test\\20230925170956.png"; // 替换为你的图片路径
//
//        try {
//            WordGenerator generator = new WordGenerator();
//
//            // 添加标题
//            generator.addHeading("这是一个 H4 标题", 4);
//
//            // 添加加粗文字的段落
//            generator.addParagraphWithBold("这是一个普通的段落，只需要将“普通”两个字加粗。", "普通");
//
//            // 添加图片
//            generator.addImage(imagePath, 200, 100); // 宽度 200 像素，高度 100 像素
//
//            // 添加普通段落
//            generator.addParagraph("这是另一个普通段落。");
//
//            // 保存文档
//            generator.saveDocument(outputFilePath);
//
//            //关闭资源
//            generator.close();
//
//            System.out.println("Word 文档已生成： " + outputFilePath);
//
//        } catch (IOException | InvalidFormatException e) {
//            e.printStackTrace();
//        }
//    }
//}
