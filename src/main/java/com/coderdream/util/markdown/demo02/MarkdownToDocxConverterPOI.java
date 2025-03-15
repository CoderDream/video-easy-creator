package com.coderdream.util.markdown.demo02;

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
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class MarkdownToDocxConverterPOI {

    /**
     * 使用 POI 将 Markdown 文件转换为 Docx 文件，包含图片。
     * 假设图片在 Markdown 同级目录的子文件夹下。
     *
     * @param markdownFilePath Markdown 文件路径
     * @param docxFilePath   输出 Docx 文件路径
     * @throws Exception 如果转换过程中发生错误
     */
    public static void convert(String markdownFilePath, String docxFilePath) throws Exception {
        log.info("开始转换 Markdown 文件: {} 到 Docx 文件: {}", markdownFilePath, docxFilePath);

        // 1. 读取 Markdown 文件内容
        String markdownContent = new String(Files.readAllBytes(Paths.get(markdownFilePath)));
        log.debug("Markdown 文件内容: {}", markdownContent);

        // 2. 将 Markdown 转换为 HTML
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdownContent);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String htmlContent = renderer.render(document);
        log.debug("转换后的 HTML 内容: {}", htmlContent);

        // 3. 使用 Jsoup 解析 HTML，并处理图片
        Document doc = Jsoup.parse(htmlContent);
        Elements images = doc.select("img");
        log.info("找到 {} 个图片", images.size());

        // 获取 Markdown 文件所在的目录
        File markdownFile = new File(markdownFilePath);
        File markdownDir = markdownFile.getParentFile();

        // 4. 创建 POI XWPFDocument
        XWPFDocument documentPOI = new XWPFDocument();

        // 5. 循环处理图片和文本
        for (Element image : images) {
            String src = image.attr("src");
            log.info("处理图片: {}", src);
            try {
                // 修改：构建图片文件的绝对路径 (图片在 Markdown 同级目录的子文件夹下)
                File imageFile = new File(markdownDir, src);
                if (!imageFile.exists()) {
                    log.error("图片不存在: {}", imageFile.getAbsolutePath());
                    continue; // 跳过不存在的图片
                }

                URL imageUrl = imageFile.toURI().toURL();
                log.debug("图片 URL: {}", imageUrl);

                // a. 读取图片数据
                InputStream imageStream = imageUrl.openStream();
                byte[] imageBytes = org.apache.commons.io.IOUtils.toByteArray(imageStream);

                // b. 将 byte[] 转换为 ByteArrayInputStream
                ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);

                // c. 添加图片到 Word 文档
                XWPFParagraph paragraph = documentPOI.createParagraph();
                XWPFRun run = paragraph.createRun();
                // 添加图片
                run.addPicture(bis, XWPFDocument.PICTURE_TYPE_JPEG, src, 6000, 4000);

                // 关闭流
                bis.close();
                imageStream.close();
                log.info("成功将图片添加到 Word 文档: {}", src);

            } catch (Exception e) {
                log.error("处理图片失败: {}", src, e);
                // 如果图片处理失败，可以添加一个简单的文本段落作为占位符
                XWPFParagraph paragraph = documentPOI.createParagraph();
                XWPFRun run = paragraph.createRun();
                run.setText("图片加载失败: " + src);
                log.info("添加图片加载失败的占位符文本: {}", src);
            }
        }

        // 添加文本内容
        if (images.isEmpty()) {
            XWPFParagraph paragraph = documentPOI.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(doc.text());
            log.info("添加文本内容到 Word 文档");
        }

        // 7. 保存 Docx 文件
        try (FileOutputStream out = new FileOutputStream(new File(docxFilePath))) {
            documentPOI.write(out);
            log.info("成功保存 Docx 文件: {}", docxFilePath);
        } catch (IOException e) {
            log.error("保存 Docx 文件失败: {}", e.getMessage(), e);
        }

        log.info("Markdown 文件已成功转换为 Docx 文件: {}", docxFilePath);
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
