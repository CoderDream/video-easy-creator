//package com.coderdream.util.pdf;
//
//import com.itextpdf.io.image.ImageData;
//import com.itextpdf.io.image.ImageDataFactory;
//import com.itextpdf.kernel.font.PdfFont;
//import com.itextpdf.kernel.font.PdfFontFactory;
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.PdfWriter;
//import com.itextpdf.layout.Document;
//import com.itextpdf.layout.element.Image;
//import com.itextpdf.layout.element.Paragraph;
//import com.itextpdf.layout.element.Text;
//import com.itextpdf.layout.properties.HorizontalAlignment;
//import com.itextpdf.layout.properties.UnitValue;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class ChineseFontTest {
//
//    public static void main(String[] args) throws IOException {
//        // 1. 创建 PDF 文档对象
//        String dest = "font_test.pdf";
//        PdfWriter writer = new PdfWriter(dest);
//        PdfDocument pdf = new PdfDocument(writer);
//        Document document = new Document(pdf);
//
//        // 2. 设置图片路径
//        String imagePath = "src/main/resources/example.png"; // 替换成你的图片文件路径
//        ImageData imageData = ImageDataFactory.create(imagePath);
//        Image image = new Image(imageData);
//        image.setWidth(UnitValue.createPercentValue(50)); // 设置图片宽度为 50%
//        image.setHorizontalAlignment(HorizontalAlignment.CENTER); // 图片水平居中
//        document.add(image);
//
//        // 3. 遍历字体文件
//        String fontDir = "D:\\java_output\\fonts";
//        List<Path> fontFiles = getFontFiles(fontDir);
//
//        for (Path fontPath : fontFiles) {
//            try {
//                PdfFont chineseFont = PdfFontFactory.createFont(fontPath.toString(), "Identity-H", true);
//
//                Paragraph p = new Paragraph("使用字体: " + fontPath.getFileName()).setFont(chineseFont);
//                p.add("，你好，世界！");
//                document.add(p);
//            } catch (Exception e) {
//                Paragraph p = new Paragraph("字体加载失败: " + fontPath.getFileName()).setFont(PdfFontFactory.createFont());
//                document.add(p);
//                System.err.println("Error loading font: " + fontPath.getFileName() + ", Error: " + e.getMessage());
//            }
//        }
//
//
//        // 4. 关闭文档
//        document.close();
//        System.out.println("PDF 文件已生成: " + dest);
//    }
//
//    // 获取指定目录下所有字体文件
//    private static List<Path> getFontFiles(String fontDir) throws IOException {
//        Path dir = Paths.get(fontDir);
//        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
//            throw new IOException("Font directory not found: " + fontDir);
//        }
//
//        return Files.list(dir)
//                    .filter(path -> {
//                        String fileName = path.getFileName().toString().toLowerCase();
//                        return fileName.endsWith(".ttf") || fileName.endsWith(".otf");
//                    })
//                    .collect(Collectors.toList());
//    }
//}
