//package com.coderdream.util.word;
//
//import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
//
//import java.io.IOException;
//
//public class Main {
//    public static void main(String[] args) {
//        String outputFilePath = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\wechat-2025-03-13.docx";
//        String imagePath = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\wechat-2025-03-13\\snapshot_001.png"; // 替换为你的图片路径
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
