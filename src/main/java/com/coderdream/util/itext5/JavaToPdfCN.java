package com.coderdream.util.itext5;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


/**
 * 使用 iText 生成包含中文的 Hello World PDF 文档。
 */
@Slf4j
public class JavaToPdfCN {

    private static final String DEST = "target/HelloWorld_CN.pdf"; // 输出 PDF 文件路径
    private static final String FONT = "D:\\java_output\\fonts\\SourceHanSansCN-Bold.ttf"; // 字体文件路径

    /**
     * 主方法，生成一个包含中文的 "hello world" PDF 文档。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        long startTime = System.nanoTime();  // 记录开始时间
        log.info("开始生成 PDF 文件...");
        try {
            generateChineseHelloWorldPdf();
            long endTime = System.nanoTime(); // 记录结束时间
            String duration = formatDuration(startTime, endTime); // 计算方法耗时
            log.info("PDF 文件生成完成， 耗时：{}", duration);

        } catch (Exception e) {
            log.error("生成 PDF 文件时发生错误:", e);
        }
    }

      /**
     * 生成 PDF 文档的核心方法, 内部捕获并处理异常。
     */
    private static void generateChineseHelloWorldPdf() {
        FileOutputStream fos = null;
        Document document = null;
        try {
            fos = new FileOutputStream(DEST);
             document = new Document();
            PdfWriter.getInstance(document, fos);
            document.open();
            // 添加中文字体
            BaseFont baseFont = BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font chineseFont = new Font(baseFont, 12);
            document.add(new Paragraph("hello world,我是鲁家宁", chineseFont));

        } catch (DocumentException | IOException e) {
            log.error("生成PDF文档时发生异常:", e);
        } finally {
             if (document != null) {
               document.close();
             }
            if (fos != null){
               try {
                  fos.close();
               }catch (IOException e){
                   log.error("关闭FileOutputStream资源时发生异常:", e);
              }
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