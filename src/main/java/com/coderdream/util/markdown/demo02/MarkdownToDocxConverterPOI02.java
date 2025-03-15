package com.coderdream.util.markdown.demo02;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.commons.io.IOUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MarkdownToDocxConverterPOI02 {

    /**
     * 使用 POI 将 Markdown 文件转换为 Docx 文件，包含图片。
     *
     * @param markdownFilePath Markdown 文件路径
     * @param docxFilePath   输出 Docx 文件路径
     * @throws Exception 如果转换过程中发生错误
     */
    public static void convert(String markdownFilePath, String docxFilePath) throws Exception {
        // 1. 读取 Markdown 文件内容
        String markdownContent = new String(Files.readAllBytes(Paths.get(markdownFilePath)));

        // 2. 将 Markdown 转换为 HTML
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdownContent);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String htmlContent = renderer.render(document);

        // 3. 使用 Jsoup 解析 HTML，并处理图片
        Document doc = Jsoup.parse(htmlContent);
        Elements images = doc.select("img");

        // 4. 创建 POI XWPFDocument
        XWPFDocument documentPOI = new XWPFDocument();

        // 5. 循环处理图片和文本
        for (Element image : images) {
            String src = image.attr("src");
            try {
                // a. 读取图片数据
                URL imageUrl = new URL(src);
                InputStream imageStream = imageUrl.openStream();
                byte[] imageBytes = IOUtils.toByteArray(imageStream);

                // b. 将 byte[] 转换为 InputStream
                ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);

                // c. 添加图片到 Word 文档
                XWPFParagraph paragraph = documentPOI.createParagraph();
                XWPFRun run = paragraph.createRun();
                // 添加图片
                run.addPicture(bis, XWPFDocument.PICTURE_TYPE_JPEG, src, 6000, 4000);

                // 关闭流
                bis.close();
                imageStream.close();

            } catch (Exception e) {
                System.err.println("处理图片失败: " + src + ", 错误信息: " + e.getMessage());
                e.printStackTrace();
                // 如果图片处理失败，可以添加一个简单的文本段落作为占位符
                XWPFParagraph paragraph = documentPOI.createParagraph();
                XWPFRun run = paragraph.createRun();
                run.setText("图片加载失败: " + src);
            }
        }

        if (images.isEmpty()) {
            XWPFParagraph paragraph = documentPOI.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(doc.text());
        }

        // 6. 保存 Docx 文件
        try (FileOutputStream out = new FileOutputStream(new File(docxFilePath))) {
            documentPOI.write(out);
        } catch (IOException e) {
            System.err.println("保存 Docx 文件失败: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Markdown 文件已成功转换为 Docx 文件: " + docxFilePath);
    }

    public static void main(String[] args) {
        String markdownFilePath = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\bai-ci-zan-071.md"; // 你的 Markdown 文件路径
        String docxFilePath = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\bai-ci-zan-071.docx";    // 输出 Docx 文件路径

        try {
            convert(markdownFilePath, docxFilePath);
        } catch (Exception e) {
            System.err.println("转换失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
