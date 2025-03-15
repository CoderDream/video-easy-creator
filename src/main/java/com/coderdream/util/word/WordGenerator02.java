package com.coderdream.util.word;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class WordGenerator02 {

    private XWPFDocument document;

    public WordGenerator02() {
        this.document = new XWPFDocument();
    }




    // 添加 H4 标题
    public void addHeading(String text, int level) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setStyle("Heading" + level);  // 使用内置的标题样式
        XWPFRun run = paragraph.createRun();
        run.setText(text);




    }

    // 添加普通段落，并对指定文字加粗
    public void addParagraphWithBold(String text, String boldText) {
        XWPFParagraph paragraph = document.createParagraph();
        String[] parts = text.split(boldText); // 将文本分割为包含需要加粗文字的部分
        if (parts.length > 1) { // 如果存在需要加粗的文字
            XWPFRun run1 = paragraph.createRun();
            run1.setText(parts[0]);
            XWPFRun run2 = paragraph.createRun();
            run2.setText(boldText);
            run2.setBold(true);
            XWPFRun run3 = paragraph.createRun();
            run3.setText(parts[1]);
        } else {
            XWPFRun run = paragraph.createRun();
            run.setText(text); // 如果没有需要加粗的文字，则添加整个段落
        }
    }

    // 添加本地磁盘图片
    public void addImage(String imagePath, int width, int height) throws IOException, InvalidFormatException {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        FileInputStream fis = new FileInputStream(imagePath);
        run.addPicture(fis, XWPFDocument.PICTURE_TYPE_PNG, imagePath, Units.toEMU(width), Units.toEMU(height));
        fis.close();
    }

    // 添加简单文本段落
    public void addParagraph(String text) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(text);
    }

    // 保存 Word 文档
    public void saveDocument(String filePath) throws IOException {
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            document.write(out);
        }
    }

    //关闭document资源
    public void close() throws IOException {
        document.close();
    }
}
