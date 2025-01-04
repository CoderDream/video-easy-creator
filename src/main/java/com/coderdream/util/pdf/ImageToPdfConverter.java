//package com.coderdream.util.pdf;
//
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.pdmodel.PDPage;
//import org.apache.pdfbox.pdmodel.PDPageContentStream;
//import org.apache.pdfbox.pdmodel.common.PDRectangle;
//import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
//import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//
//public class ImageToPdfConverter {
//
//    public static void main(String[] args) {
//        try {
//            // 创建一个空白PDF文档
//            PDDocument document = PDDocument.load();
//
//            // 获取图片文件夹路径
//            String imagesFolder = "./images";
//            // 获取所有图片文件
//            File[] imageFiles = new File(imagesFolder).listFiles();
//
//            // 遍历图片文件
//            for (File imageFile : imageFiles) {
//                byte[] imageData = Files.readAllBytes(imageFile.toPath());
//                String fileName = imageFile.getName();
//
//                // 将图片添加到PDF页面
//                addImageToPdf(document, imageData, fileName);
//            }
//
//            // 保存PDF文档
//            String outputFileName = "./output.pdf";
//            document.save(outputFileName);
//
//            // 关闭文档
//            document.close();
//
//            System.out.println("图片转PDF成功：" + outputFileName);
//        } catch (IOException e) {
//            System.out.println("图片转PDF失败：" + e.getMessage());
//        }
//    }
//
//    private static void addImageToPdf(PDDocument document, byte[] imageData, String fileName) throws IOException {
//        // 创建一个新的页面
//        PDPage page = PDPage.newInstance();
//
//        // 将页面添加到文档
//        document.addPage(page);
//
//        // 创建一个包含图片的PDImageXObject对象
//        PDImageXObject image = PDImageXObject.createFromByteArray(document, imageData, fileName);
//
//        // 获取页面的大小
//        PDRectangle pageSize = page.getMediaBox();
//
//        // 计算图片的缩放比例，保持宽高比不变
//        float scale = Math.min(pageSize.getWidth() / image.getWidth(), pageSize.getHeight() / image.getHeight());
//
//        // 计算图片在页面上的位置
//        float x = (pageSize.getWidth() - image.getWidth() * scale) / 2;
//        float y = (pageSize.getHeight() - image.getHeight() * scale) / 2;
//
//        // 获取绘制图形的PDPageContentStream对象
//        PDPageContentStream contentStream = new PDPageContentStream(document, page);
//
//        // 绘制图片
//        contentStream.drawImage(image, x, y, image.getWidth() * scale, image.getHeight() * scale);
//
//        // 关闭PDPageContentStream对象
//        contentStream.close();
//    }
//}
