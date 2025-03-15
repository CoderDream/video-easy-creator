package com.coderdream.util.markdown.demo03;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.commons.lang3.time.StopWatch;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Markdown 转 Word 文档工具类
 */
@Slf4j
public class MarkdownToWordConverter {

    /**
     * 主方法：将 Markdown 文件转换为 Word 文档
     *
     * @param markdownPath Markdown 文件路径
     * @param wordPath     输出 Word 文件路径
     */
    public static void convertMarkdownToWord(String markdownPath, String wordPath) {
        StopWatch stopWatch = StopWatch.createStarted(); // 开始计时
        log.info("开始转换 Markdown 文件: {} 到 Word 文件: {}", markdownPath, wordPath);

        try {
            String markdownContent = readMarkdownFile(markdownPath);
            // 获取 Markdown 文件所在目录，用于解析相对路径
            String markdownDir = Paths.get(markdownPath).getParent().toString();
            createWordDocument(markdownContent, markdownDir, wordPath);
            stopWatch.stop();
            log.info("转换完成，耗时: {} 时 {} 分 {} 秒 {} 毫秒",
                    stopWatch.getTime() / 3600000,       // 时
                    (stopWatch.getTime() % 3600000) / 60000, // 分
                    (stopWatch.getTime() % 60000) / 1000,    // 秒
                    stopWatch.getTime() % 1000);         // 毫秒
        } catch (Exception e) {
            log.error("转换过程中发生错误: {}", e.getMessage(), e);
            throw new RuntimeException("Markdown 转 Word 失败", e);
        }
    }

    /**
     * 读取 Markdown 文件内容
     *
     * @param markdownPath Markdown 文件路径
     * @return Markdown 文件内容字符串
     * @throws IOException 文件读取异常
     */
    private static String readMarkdownFile(String markdownPath) throws IOException {
        log.info("开始读取 Markdown 文件: {}", markdownPath);
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(markdownPath))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            log.debug("Markdown 文件内容读取成功，长度: {}", content.length());
            return content.toString();
        }
    }

    /**
     * 创建 Word 文档并写入 Markdown 解析后的内容
     *
     * @param markdownContent Markdown 内容
     * @param markdownDir     Markdown 文件所在目录，用于解析图片相对路径
     * @param wordPath        输出 Word 文件路径
     * @throws IOException 文件操作异常
     */
    private static void createWordDocument(String markdownContent, String markdownDir, String wordPath) throws IOException {
        log.info("开始创建 Word 文档: {}", wordPath);

        try (XWPFDocument document = new XWPFDocument();
             FileOutputStream out = new FileOutputStream(wordPath)) {

            // 正则表达式匹配标题、图片、引用和加粗
            Pattern headingPattern = Pattern.compile("^(#+)\\s+(.+)$");
            Pattern imagePattern = Pattern.compile("!\\[([^\\]]*)\\]\\(([^\\)]+)\\)");
            Pattern quotePattern = Pattern.compile("^>\\s*(.+)$");
            Pattern boldPattern = Pattern.compile("\\*\\*(.+?)\\*\\*");

            String[] lines = markdownContent.split("\n");
            for (String line : lines) {
                Matcher headingMatcher = headingPattern.matcher(line);
                Matcher imageMatcher = imagePattern.matcher(line);
                Matcher quoteMatcher = quotePattern.matcher(line);

                if (headingMatcher.find()) {
                    // 处理标题
                    int level = headingMatcher.group(1).length();
                    String text = headingMatcher.group(2);
                    addHeading(document, text, level);
                } else if (imageMatcher.find()) {
                    // 处理图片
                    String altText = imageMatcher.group(1);
                    String relativeImagePath = imageMatcher.group(2);
                    String absoluteImagePath = Paths.get(markdownDir, relativeImagePath).toString();
                    addImage(document, absoluteImagePath, altText);
                } else if (quoteMatcher.find()) {
                    // 处理引用
                    String quoteText = quoteMatcher.group(1);
                    addQuote(document, quoteText);
                } else {
                    // 处理普通文本（包括加粗）
                    addParagraphWithBold(document, line, boldPattern);
                }
            }

            // 将文档写入文件
            document.write(out);
            log.info("Word 文档写入成功: {}", wordPath);
        }
    }

    /**
     * 添加标题到 Word 文档
     *
     * @param document Word 文档对象
     * @param text     标题文本
     * @param level    标题级别 (1-6)
     */
    private static void addHeading(XWPFDocument document, String text, int level) {
        log.debug("添加标题: {}，级别: {}", text, level);
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setStyle("Heading" + Math.min(level, 6)); // POI 支持 Heading1 到 Heading6
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(true);
        run.setFontSize(12 + (6 - level) * 2); // 动态调整字体大小
    }

    /**
     * 添加普通段落并处理加粗到 Word 文档
     *
     * @param document    Word 文档对象
     * @param text        段落文本
     * @param boldPattern 加粗的正则表达式
     */
    private static void addParagraphWithBold(XWPFDocument document, String text, Pattern boldPattern) {
        if (text.trim().isEmpty()) {
            return; // 忽略空行
        }
        log.debug("添加段落并解析加粗: {}", text);

        XWPFParagraph paragraph = document.createParagraph();
        Matcher boldMatcher = boldPattern.matcher(text);
        int lastEnd = 0;

        while (boldMatcher.find()) {
            // 添加加粗前的普通文本
            if (boldMatcher.start() > lastEnd) {
                String beforeText = text.substring(lastEnd, boldMatcher.start());
                XWPFRun run = paragraph.createRun();
                run.setText(beforeText);
            }

            // 添加加粗文本
            String boldText = boldMatcher.group(1);
            XWPFRun boldRun = paragraph.createRun();
            boldRun.setText(boldText);
            boldRun.setBold(true);

            lastEnd = boldMatcher.end();
        }

        // 添加剩余的普通文本
        if (lastEnd < text.length()) {
            String remainingText = text.substring(lastEnd);
            XWPFRun run = paragraph.createRun();
            run.setText(remainingText);
        }
    }

    /**
     * 添加引用到 Word 文档
     *
     * @param document Word 文档对象
     * @param text     引用文本
     */
    private static void addQuote(XWPFDocument document, String text) {
        log.debug("添加引用: {}", text);
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setIndentationLeft(Units.toEMU(0.5 * 72)); // 缩进 0.5 英寸
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setItalic(true); // 引用文本设置为斜体
    }

    /**
     * 添加图片到 Word 文档
     *
     * @param document  Word 文档对象
     * @param imagePath 图片绝对路径
     * @param altText   图片替代文本
     * @throws IOException 文件操作异常
     */
    private static void addImage(XWPFDocument document, String imagePath, String altText) throws IOException {
        log.info("添加图片: {}，替代文本: {}", imagePath, altText);
        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            log.error("图片文件不存在: {}", imagePath);
            throw new IOException("图片文件不存在: " + imagePath);
        }

        try (FileInputStream fis = new FileInputStream(imageFile)) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();

            // 获取图片扩展名并确定格式
            String extension = imagePath.substring(imagePath.lastIndexOf(".") + 1).toLowerCase();
            int format;
            switch (extension) {
                case "png":
                    format = XWPFDocument.PICTURE_TYPE_PNG;
                    break;
                case "jpg":
                case "jpeg":
                    format = XWPFDocument.PICTURE_TYPE_JPEG;
                    break;
                default:
                    log.warn("不支持的图片格式: {}", extension);
                    return;
            }

            // 添加图片到文档
            byte[] imageBytes = fis.readAllBytes();
            run.addPicture(new ByteArrayInputStream(imageBytes), format, imagePath, Units.toEMU(400), Units.toEMU(300));
            run.setText(" [" + altText + "]");
        } catch (Exception e) {
            log.error("添加图片失败: {}，错误: {}", imagePath, e.getMessage(), e);
            throw new IOException("无法添加图片: " + imagePath, e);
        }
    }

    /**
     * 主函数，测试 Markdown 转 Word 功能
     */
    public static void main(String[] args) {
        String markdownPath = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\bai-ci-zan-071.md"; // 你的 Markdown 文件路径
        String wordPath = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\bai-ci-zan-071.docx";    // 输出 Docx 文件路径
        convertMarkdownToWord(markdownPath, wordPath);
    }
}
