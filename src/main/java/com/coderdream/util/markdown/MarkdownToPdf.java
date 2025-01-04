package com.coderdream.util.markdown;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MarkdownToPdf {
    public static void main(String[] args) {
        // Markdown文件的路径
        String markdownFilePath = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\bai-ci-zan-001.md";
        // 生成的PDF文件的路径
        String pdfFilePath = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\bai-ci-zan-001.pdf";

        try {
            // 1. 读取Markdown文件
            String markdown = new String(Files.readAllBytes(Paths.get(markdownFilePath)));

            // 2. 使用flexmark-java将Markdown转换为HTML
            MutableDataSet options = new MutableDataSet();
            Parser parser = Parser.builder(options).build();
            HtmlRenderer renderer = HtmlRenderer.builder(options).build();
            String html = renderer.render(parser.parse(markdown));

            // 3. 使用openhtmltopdf将HTML转换为PDF
            try (FileOutputStream os = new FileOutputStream(pdfFilePath)) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.useFastMode();
                builder.withHtmlContent(html, null);
                builder.toStream(os);
                builder.run();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
