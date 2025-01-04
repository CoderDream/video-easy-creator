package com.coderdream.util.itext5;

import com.coderdream.util.itext5.util.PathUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 使用 FreeMarker 渲染 HTML 模板，并使用 iText 和 XML Worker 将 HTML 文件转换为 PDF 文档。
 */
@Slf4j
public class JavaToPdfHtmlFreeMarker {

    private static final String DEST = "target/HelloWorld_CN_HTML_FREEMARKER.pdf"; // 输出 PDF 文件路径
    private static final String HTML = "template_freemarker.html"; // HTML 模板文件路径
    private static final String FONT = "D:\\java_output\\fonts\\simhei.ttf"; // 字体文件路径
    private static Configuration freemarkerCfg = null; // FreeMarker 配置

    static {
        freemarkerCfg = new Configuration();
        // freemarker的模板目录
        try {
            freemarkerCfg.setDirectoryForTemplateLoading(new File(PathUtil.getCurrentPath()));
        } catch (IOException e) {
            log.error("设置 FreeMarker 模板目录失败:", e);
        }
        freemarkerCfg.setDefaultEncoding("UTF-8"); // 设置默认编码为 UTF-8
    }

    /**
     * 主方法，使用 FreeMarker 渲染 HTML 并转换为 PDF。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        long startTime = System.nanoTime(); // 记录开始时间
        log.info("开始生成 PDF 文件...");
        try {
            generatePdfFromHtmlWithFreeMarker();
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
    private static void generatePdfFromHtmlWithFreeMarker() {
        FileOutputStream fos = null;
        Document document = null;
        try {
             fos = new FileOutputStream(DEST);
            document = new Document();
             PdfWriter writer = PdfWriter.getInstance(document, fos);
            document.open();

            XMLWorkerFontProvider fontImp = new XMLWorkerFontProvider(
                    XMLWorkerFontProvider.DONTLOOKFORFONTS);
            fontImp.register(FONT);

            Map<String, Object> data = new HashMap<>();
            data.put("name", "鲁家宁");
            String content = freeMarkerRender(data, HTML);

            try(ByteArrayInputStream bais =  new ByteArrayInputStream(content.getBytes())) {
                XMLWorkerHelper.getInstance().parseXHtml(writer, document,
                       bais, null, StandardCharsets.UTF_8, fontImp);
            }


        } catch (DocumentException | IOException e) {
             log.error("生成 PDF 文档时发生异常:", e);
        } finally {
             if (document != null) {
                document.close();
            }
            if (fos != null) {
                try{
                    fos.close();
                 }catch (IOException e){
                   log.error("关闭FileOutputStream资源时发生异常:",e);
                }
            }
        }
    }


    /**
     * 使用 FreeMarker 渲染 HTML 模板。
     *
     * @param data    模板数据
     * @param htmlTmp 模板文件名
     * @return 渲染后的 HTML 字符串
     */
    public static String freeMarkerRender(Map<String, Object> data, String htmlTmp) {
         Writer out = new StringWriter();
         try{
             Template template = freemarkerCfg.getTemplate(htmlTmp); // 获取模板
              template.process(data, out); // 合并数据模型和模板
              out.flush();
            return out.toString();
         } catch (Exception e) {
              log.error("FreeMarker 渲染 HTML 时发生错误:", e);
              return null;
         } finally {
            try {
               out.close();
           } catch (IOException ex) {
                log.error("关闭StringWriter资源时发生异常:", ex);
           }
        }
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
