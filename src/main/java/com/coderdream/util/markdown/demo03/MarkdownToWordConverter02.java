package com.coderdream.util.markdown.demo03;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

/**
 * Markdown 转 Word 文档工具类
 */
@Slf4j
public class MarkdownToWordConverter02 {

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
            createWordDocument(markdownContent, wordPath);
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
     * @param wordPath        输出 Word 文件路径
     * @throws IOException 文件操作异常
     */
    private static void createWordDocument(String markdownContent, String wordPath) throws IOException {
        log.info("开始创建 Word 文档: {}", wordPath);

        try (XWPFDocument document = new XWPFDocument();
             FileOutputStream out = new FileOutputStream(wordPath)) {

            // 正则表达式匹配标题和图片
            Pattern headingPattern = Pattern.compile("^(#+)\\s+(.+)$");
            Pattern imagePattern = Pattern.compile("!\\[([^\\]]*)\\]\\(([^\\)]+)\\)");

            String[] lines = markdownContent.split("\n");
            for (String line : lines) {
                Matcher headingMatcher = headingPattern.matcher(line);
                Matcher imageMatcher = imagePattern.matcher(line);

                if (headingMatcher.find()) {
                    // 处理标题
                    int level = headingMatcher.group(1).length(); // 标题级别
                    String text = headingMatcher.group(2);
                    addHeading(document, text, level);
                } else if (imageMatcher.find()) {
                    // 处理图片
                    String altText = imageMatcher.group(1);
                    String imagePath = imageMatcher.group(2);
                    addImage(document, imagePath, altText);
                } else {
                    // 处理普通文本
                    addParagraph(document, line);
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
     * 添加普通段落到 Word 文档
     *
     * @param document Word 文档对象
     * @param text     段落文本
     */
    private static void addParagraph(XWPFDocument document, String text) {
      if (text.trim().isEmpty()) {
        return; // 忽略空行
      }
        log.debug("添加段落: {}", text);
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(text);
    }

    /**
     * 添加图片到 Word 文档
     *
     * @param document  Word 文档对象
     * @param imagePath 本地图片路径
     * @param altText   图片替代文本
     * @throws IOException 文件操作异常
     */
    private static void addImage(XWPFDocument document, String imagePath, String altText) throws IOException {
        log.info("添加图片: {}，替代文本: {}", imagePath, altText);
        try (FileInputStream fis = new FileInputStream(imagePath)) {
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
//        String markdownPath = "C:/path/to/your/input.md"; // 替换为实际 Markdown 文件路径
//        String wordPath = "C:/path/to/your/output.docx"; // 替换为实际输出 Word 文件路径

        String markdownPath = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\bai-ci-zan-071.md"; // 你的 Markdown 文件路径
        String wordPath = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\bai-ci-zan-071.docx";    // 输出 Docx 文件路径

        convertMarkdownToWord(markdownPath, wordPath);
    }
}
