package com.coderdream.util.itext5.flyingsaucer;

import com.coderdream.util.itext5.util.PathUtil;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

/**
 * 使用 Velocity 渲染 HTML 模板，并使用 Flying Saucer 将 HTML 文件转换为 PDF 文档。
 */
@Slf4j
public class JavaToPdfHtmlVelocity2 {

    private static final String DEST = "target/HelloWorld_CN_HTML_VELOCITY_FS2.pdf"; // 输出 PDF 文件路径
    private static final String HTML = "template_velocity_fs.html"; // HTML 模板文件路径
    private static final String FONT = "D:\\java_output\\fonts\\simhei.ttf"; // 字体文件路径
    private static final String LOGO_PATH = "file://" + PathUtil.getCurrentPath() + "/logo.png"; // Logo 图片路径
    private static final VelocityEngine ve = new VelocityEngine(); // Velocity 引擎

    static {
        // 配置 Velocity 引擎
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.init();
    }

    /**
     * 主方法，使用 Velocity 渲染 HTML 并转换为 PDF。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        long startTime = System.nanoTime(); // 记录开始时间
        log.info("开始生成 PDF 文件...");
        try {
            generatePdfFromHtmlWithVelocity();
            long endTime = System.nanoTime(); // 记录结束时间
            String duration = formatDuration(startTime, endTime); // 计算方法耗时
            log.info("PDF 文件生成完成， 耗时：{}", duration);
        } catch (Exception e) {
            log.error("生成 PDF 文件时发生错误:", e);
        }
    }

    /**
     *  生成 PDF 文档的核心方法，内部处理异常。
     */
    private static void generatePdfFromHtmlWithVelocity() {
        FileOutputStream fileStream = null;
        try {
            Map<String,Object> data = new HashMap<>();
            data.put("name","鲁家宁&lt;"); // 添加要渲染的数据
            String content = velocityRender(data,HTML);
            fileStream = new FileOutputStream(DEST);
            createPdf(content, fileStream);
        } catch (IOException | DocumentException | com.lowagie.text.DocumentException e) {
            log.error("生成 PDF 文档时发生异常:",e);
        } finally {
            if (fileStream != null){
                try {
                    fileStream.close();
                } catch (IOException e){
                    log.error("关闭 FileOutputStream 资源时发生异常:",e);
                }
            }
        }
    }

    /**
     * 使用 Velocity 渲染 HTML 模板。
     *
     * @param data    模板数据
     * @param htmlTmp 模板文件名
     * @return 渲染后的 HTML 字符串
     */
    public static String velocityRender(Map<String, Object> data, String htmlTmp) {
        StringWriter sw = new StringWriter();
        try {
            Template t = ve.getTemplate(htmlTmp); // 获取 Velocity 模板
            VelocityContext ctx = new VelocityContext(data); // 创建 Velocity 上下文
            t.merge(ctx, sw); // 合并数据和模板
            return sw.toString();
        } catch (Exception e) {
            log.error("Velocity 渲染 HTML 时发生错误:",e);
            return null;
        } finally {
            try {
                sw.close();
            } catch (IOException ex) {
                log.error("关闭 StringWriter 资源时发生异常:",ex);
            }
        }
    }


    /**
     * 根据 HTML 内容和输出文件流 生成 PDF 文件
     *
     * @param content    HTML 内容
     * @param fileStream PDF 输出文件流
     * @throws IOException                    IO 异常
     * @throws DocumentException              iText 文档异常
     * @throws com.lowagie.text.DocumentException Flying Saucer 文档异常
     */
    public static void createPdf(String content, FileOutputStream fileStream)
      throws IOException, DocumentException, com.lowagie.text.DocumentException {
        ITextRenderer render = new ITextRenderer();
        ITextFontResolver fontResolver = render.getFontResolver();
        fontResolver.addFont(FONT, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        // 解析html生成pdf
        render.setDocumentFromString(content);
        //解决图片相对路径的问题
        render.getSharedContext().setBaseURL(LOGO_PATH);
        render.layout();
        render.createPDF(fileStream);
    }

    /**
     * 计算并格式化方法耗时.
     *
     * @param startTime 开始时间（纳秒）
     * @param endTime   结束时间（纳秒）
     * @return 格式化后的耗时字符串 (HH:mm:ss.SSS)
     */
    private static String formatDuration(long startTime, long endTime) {
        long durationNano = endTime - startTime;
        long durationMillis = TimeUnit.NANOSECONDS.toMillis(durationNano);
        long hours = TimeUnit.MILLISECONDS.toHours(durationMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis) % 60;
        long millis = durationMillis % 1000;

        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis);
    }
}
