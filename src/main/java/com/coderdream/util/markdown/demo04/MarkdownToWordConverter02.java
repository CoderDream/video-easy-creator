package com.coderdream.util.markdown.demo04;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.BlockQuote;
import org.commonmark.node.Document;
import org.commonmark.node.Heading;
import org.commonmark.node.Image;
import org.commonmark.node.Node;
import org.commonmark.node.Paragraph;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;

/**
 * Markdown转换为Word文档的工具类
 */
@Slf4j
public class MarkdownToWordConverter02 {

    /**
     * 将Markdown文件转换为Word文档
     *
     * @param markdownFilePath Markdown文件路径
     * @param wordFilePath     输出Word文件路径
     * @throws IOException 文件操作异常
     */
    public void convert(String markdownFilePath, String wordFilePath) throws IOException {
        Instant start = Instant.now();
        log.info("开始转换Markdown文件: {}", markdownFilePath);

        // 读取Markdown文件内容
        String markdownContent;
        try (BufferedReader reader = Files.newBufferedReader(new File(markdownFilePath).toPath())) {
            markdownContent = reader.lines().collect(Collectors.joining("\n"));
        }

        // 解析Markdown
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdownContent);  // 这里使用 Node，因为 parser.parse 返回的是 Node

        // 创建Word文档
        try (XWPFDocument doc = new XWPFDocument();
             FileOutputStream out = new FileOutputStream(wordFilePath)) {

            // 渲染Markdown到Word
            WordRenderer renderer = new WordRenderer(doc);
            renderer.render(document);

            // 写入文件
            doc.write(out);
        }

        Duration duration = Duration.between(start, Instant.now());
        log.info("转换完成，输出到: {}，耗时: {}时{}分{}秒{}毫秒",
                wordFilePath,
                duration.toHoursPart(),
                duration.toMinutesPart(),
                duration.toSecondsPart(),
                duration.toMillisPart());
    }

    /**
     * 自定义Word渲染器
     */
    private static class WordRenderer extends AbstractVisitor implements NodeRenderer {
        private final XWPFDocument document;
        private XWPFParagraph currentParagraph;

        public WordRenderer(XWPFDocument document) {
            this.document = document;
            this.currentParagraph = document.createParagraph();
        }

        @Override
        public void render(Node node) {
            node.accept(this);
        }

        @Override
        public Set<Class<? extends Node>> getNodeTypes() {
            return Set.of(
                    Document.class,
                    Heading.class,
                    Paragraph.class,
                    Text.class,
                    StrongEmphasis.class,
                    BlockQuote.class,
                    Image.class
            );
        }

        @Override
        public void visit(Heading heading) {
            currentParagraph = document.createParagraph();
            currentParagraph.setStyle("Heading" + heading.getLevel());
            visitChildren(heading);
        }

        @Override
        public void visit(Paragraph paragraph) {
            currentParagraph = document.createParagraph();
            visitChildren(paragraph);
        }

        @Override
        public void visit(BlockQuote blockQuote) {
            currentParagraph = document.createParagraph();
            currentParagraph.setIndentationLeft(500); // 设置引用缩进
            visitChildren(blockQuote);
        }

        @Override
        public void visit(Text text) {
            XWPFRun run = currentParagraph.createRun();
            run.setText(text.getLiteral());
        }

        @Override
        public void visit(StrongEmphasis strongEmphasis) {
            XWPFRun run = currentParagraph.createRun();
            run.setBold(true);
            visitChildren(strongEmphasis);
        }

        @Override
        public void visit(Image image) {
            String imagePath = image.getDestination();
            File imageFile = new File(imagePath);

            if (imageFile.exists()) {
                try (FileInputStream fis = new FileInputStream(imageFile)) {
                    XWPFRun run = currentParagraph.createRun();
                    byte[] bytes = fis.readAllBytes();
                    // 添加图片，指定宽度和高度
                    run.addPicture(new ByteArrayInputStream(bytes), XWPFDocument.PICTURE_TYPE_PNG, imagePath, Units.toEMU(400), Units.toEMU(300));
//                    run.addPicture(bytes, XWPFDocument.PICTURE_TYPE_PNG,                                 imagePath, Units.toEMU(400), Units.toEMU(300));
                } catch (Exception e) {
                    log.error("添加图片失败: {}", imagePath, e);
                }
            } else {
                log.warn("图片文件不存在: {}", imagePath);
            }
        }

        @Override
        public void visit(Document document) {
            visitChildren(document);
        }
    }

    /**
     * 示例使用
     */
    public static void main(String[] args) {
        String markdownFilePath = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\bai-ci-zan-071.md"; // 你的 Markdown 文件路径
        String docxFilePath = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\bai-ci-zan-071.docx";    // 输出 Docx 文件路径

        MarkdownToWordConverter02 converter = new MarkdownToWordConverter02();
        try {
            converter.convert(markdownFilePath, docxFilePath);
        } catch (IOException e) {
            log.error("转换过程发生错误", e);
        }
    }
}
