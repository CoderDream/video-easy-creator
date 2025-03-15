package com.coderdream.util.word;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class GenerateWordWithPOI {
    public static void main(String[] args) throws Exception {
        // 1. 创建一个新的 Word 文档
        XWPFDocument document = new XWPFDocument();

        // 2. 添加 H4 标题
        XWPFParagraph title = document.createParagraph();
        title.setStyle("Heading4"); // 设置为 H4 样式（需确保样式存在或自定义）
        XWPFRun titleRun = title.createRun();
        titleRun.setText("这是 H4 标题");

        // 3. 添加普通段落并包含加粗文字
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText("这是一个普通的段落，");
        run = paragraph.createRun(); // 创建新的 Run 用于加粗
        run.setBold(true); // 设置加粗
        run.setText("这里是加粗的文字");

        // 4. 嵌入本地图片
        File imageFile = new File("C:/path/to/your/image.jpg"); // 本地图片路径
        FileInputStream imageStream = new FileInputStream(imageFile);
        XWPFParagraph imageParagraph = document.createParagraph();
        XWPFRun imageRun = imageParagraph.createRun();
        imageRun.addPicture(imageStream, XWPFDocument.PICTURE_TYPE_JPEG, "image.jpg",
                           Units.toEMU(200), Units.toEMU(200)); // 宽度和高度，单位为 EMU

        // 5. 保存文档
        FileOutputStream out = new FileOutputStream("output.docx");
        document.write(out);
        out.close();
        document.close();

        System.out.println("Word 文档生成成功！");
    }
}
