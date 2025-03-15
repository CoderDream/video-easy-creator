package com.coderdream.util.markdown.demo04;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.apache.poi.util.Units;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineProperties;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyle;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTInd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STStyleType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Markdown转换为Word文档的工具类
 */
@Slf4j
public class MarkdownToWordConverter {

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

        // 读取Markdown文件内容并移除YAML元数据
        String markdownContent;
        Path markdownPath = Paths.get(markdownFilePath);
        try (BufferedReader reader = Files.newBufferedReader(markdownPath)) {
            StringBuilder contentBuilder = new StringBuilder();
            String line;
            boolean inYaml = false;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equals("---")) {
                    inYaml = !inYaml;
                    continue;
                }
                if (!inYaml) {
                    contentBuilder.append(line).append("\n");
                }
            }
            markdownContent = contentBuilder.toString();
        }

        // 解析Markdown
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdownContent);

        // 创建Word文档并设置全局样式
        try (XWPFDocument doc = new XWPFDocument();
             FileOutputStream out = new FileOutputStream(wordFilePath)) {

            // 设置默认样式，去掉首行缩进
            setDefaultStyle(doc);

            // 渲染Markdown到Word
            WordRenderer renderer = new WordRenderer(doc, markdownPath.getParent());
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
     * 设置文档默认样式，去掉首行缩进
     */
    private void setDefaultStyle(XWPFDocument doc) {
        XWPFStyles styles = doc.createStyles();
        CTStyle style = CTStyle.Factory.newInstance();
        style.setType(STStyleType.PARAGRAPH);
        style.setStyleId("Normal");
        style.setDefault(true);

        // 设置缩进属性
        CTInd indent = style.addNewPPr().addNewInd();
        indent.setFirstLine(0); // 首行缩进为0
        indent.setLeft(0);      // 左缩进为0（除非特别指定，如引用块）

        styles.addStyle(new XWPFStyle(style));
    }

    /**
     * 自定义Word渲染器
     */
    private static class WordRenderer extends AbstractVisitor implements NodeRenderer {
        private final XWPFDocument document;
        private XWPFParagraph currentParagraph;
        private final Path basePath; // Markdown文件所在目录
        private static final Pattern HEADING_PATTERN = Pattern.compile("^\\d{2}\\s+\\w+\\s+\\[.*\\]$");

        public WordRenderer(XWPFDocument document, Path basePath) {
            this.document = document;
            this.basePath = basePath;
        }

        @Override
        public void render(Node node) {
            node.accept(this);
        }

        @Override
        public Set<Class<? extends Node>> getNodeTypes() {
            return Set.of(
                    org.commonmark.node.Document.class,
                    Heading.class,
                    Paragraph.class,
                    Text.class,
                    StrongEmphasis.class,
                    BlockQuote.class,
                    Image.class,
                    SoftLineBreak.class
            );
        }

        @Override
        public void visit(Heading heading) {
            ensureParagraph();
            // 确保四级标题使用 Heading4 样式
            currentParagraph.setStyle("Heading" + Math.min(heading.getLevel(), 4)); // 限制最大为 Heading4
            currentParagraph.setIndentationFirstLine(0); // 确保无首行缩进
            visitChildren(heading);
            currentParagraph = null; // 重置段落
        }

        @Override
        public void visit(Paragraph paragraph) {
            ensureParagraph();
            currentParagraph.setIndentationFirstLine(0); // 确保无首行缩进
            visitChildren(paragraph);
            currentParagraph = null; // 重置段落
        }

        @Override
        public void visit(BlockQuote blockQuote) {
            ensureParagraph();
            currentParagraph.setIndentationLeft(500); // 引用整体缩进
            currentParagraph.setIndentationFirstLine(0); // 确保无首行缩进
            visitChildren(blockQuote);
            currentParagraph = null; // 重置段落
        }

        @Override
        public void visit(Text text) {
            ensureParagraph();
            String literal = text.getLiteral().trim();
            if (literal.isEmpty()) {
                return; // 跳过空行
            }

            // 检查是否是纯文本标题（例如 "01 paraphernalia ..."）
            if (HEADING_PATTERN.matcher(literal).matches() && text.getParent() instanceof Paragraph) {
                currentParagraph.setStyle("Heading4"); // 设置为四级标题
                currentParagraph.setIndentationFirstLine(0);
            }

            XWPFRun run = currentParagraph.createRun();
            if (literal.contains("\n")) {
                String[] lines = literal.split("\n");
                for (int i = 0; i < lines.length; i++) {
                    if (!lines[i].trim().isEmpty()) {
                        run.setText(lines[i]);
                        if (i < lines.length - 1) {
                            run.addBreak(); // 添加换行
                        }
                    }
                }
            } else {
                run.setText(literal);
            }
        }

        @Override
        public void visit(StrongEmphasis strongEmphasis) {
            ensureParagraph();
            Node child = strongEmphasis.getFirstChild();
            while (child != null) {
                if (child instanceof Text) {
                    XWPFRun run = currentParagraph.createRun();
                    run.setBold(true);
                    String literal = ((Text) child).getLiteral();
                    if (literal.contains("\n")) {
                        String[] lines = literal.split("\n");
                        for (int i = 0; i < lines.length; i++) {
                            if (!lines[i].trim().isEmpty()) {
                                run.setText(lines[i]);
                                if (i < lines.length - 1) {
                                    run.addBreak(); // 加粗文本中的换行
                                }
                            }
                        }
                    } else {
                        run.setText(literal);
                    }
                }
                child = child.getNext();
            }
        }

        @Override
        public void visit(Image image) {
            ensureParagraph();
            currentParagraph.setIndentationFirstLine(0); // 确保无首行缩进
            String relativePath = image.getDestination();
            Path imagePath = basePath.resolve(relativePath).normalize();
            File imageFile = imagePath.toFile();

            if (imageFile.exists()) {
                try (FileInputStream fis = new FileInputStream(imageFile)) {
                    XWPFRun run = currentParagraph.createRun();
                    byte[] bytes = fis.readAllBytes();
                    try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
                        XWPFPicture picture = run.addPicture(bais, XWPFDocument.PICTURE_TYPE_PNG,
                                imageFile.getName(), Units.toEMU(400), Units.toEMU(300));
                        // 添加2像素黑色边框
                        org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties spPr =
                                picture.getCTPicture().getSpPr();
                        CTLineProperties ln = spPr.isSetLn() ? spPr.getLn() : spPr.addNewLn();
                        ln.addNewSolidFill().addNewSrgbClr().setVal(new byte[]{(byte) 0, (byte) 0, (byte) 0}); // 黑色
                        ln.setW(Units.toEMU(2)); // 2像素边框
                    }
                } catch (Exception e) {
                    log.error("添加图片失败: {}", imagePath, e);
                }
            } else {
                log.warn("图片文件不存在: {}", imagePath);
            }
            currentParagraph = null; // 重置段落
        }

        @Override
        public void visit(SoftLineBreak softLineBreak) {
            ensureParagraph();
            currentParagraph.createRun().addBreak(); // 处理软换行
        }

        @Override
        public void visit(org.commonmark.node.Document document) {
            visitChildren(document);
        }

        /**
         * 确保当前段落存在，如果不存在则创建新段落
         */
        private void ensureParagraph() {
            if (currentParagraph == null) {
                currentParagraph = document.createParagraph();
                currentParagraph.setIndentationFirstLine(0); // 默认去掉首行缩进
            }
        }
    }

    /**
     * 示例使用
     */
    public static void main(String[] args) {
        String markdownFilePath = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\bai-ci-zan-071.md"; // 你的 Markdown 文件路径
        String docxFilePath = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\bai-ci-zan-071.docx";    // 输出 Docx 文件路径

        MarkdownToWordConverter converter = new MarkdownToWordConverter();
        try {
            converter.convert(markdownFilePath, docxFilePath);
        } catch (IOException e) {
            log.error("转换过程发生错误", e);
        }
    }
}
