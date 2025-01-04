package com.coderdream.util.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * 使用 iText 5.x 生成包含中文、图片和背景的 PDF 文档，并尝试使用 D:\\java_output\\fonts 目录下所有字体。
 * 解决重音符号（如ˈ）打印不出来的问题，并且使用两种字体，一种显示音标，一种显示中文
 */
@Slf4j
public class HtmlToPdfConverter {

    /**
     * 主方法，用于生成 PDF 文档。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        String htmlFilePath = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\bai-ci-zan-001.html";
        String outputPdfPath = "D:\\java_output\\bai-ci-zan-001.pdf";
        String doulosSILFontPath = "D:\\java_output\\fonts\\DoulosSIL-R.ttf";
        String simSunFontPath = "D:\\java_output\\fonts\\SourceHanSansCN-Bold.ttf";
        String imageBasePath = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\";

        try {
            convertHtmlToPdf(htmlFilePath, outputPdfPath, doulosSILFontPath, simSunFontPath, imageBasePath);
            log.info("PDF 文件已成功生成: {}", outputPdfPath);
        } catch (DocumentException | IOException e) {
            log.error("生成 PDF 文件时发生错误: ", e);
        }
    }

    /**
     * 将 HTML 文件转换为 PDF 文档，使用两种字体，并处理图片路径。
     *
     * @param htmlFilePath     输入的 HTML 文件路径
     * @param outputPdfPath    输出的 PDF 文件路径
     * @param doulosSILFontPath Doulos SIL 字体文件路径
     * @param simSunFontPath   SimSun 字体文件路径
     * @param imageBasePath    图片基础路径
     * @throws DocumentException iText 文档异常
     * @throws IOException      IO 异常
     */
    public static void convertHtmlToPdf(String htmlFilePath, String outputPdfPath, String doulosSILFontPath,
                                      String simSunFontPath, String imageBasePath)
            throws DocumentException, IOException {
        long startTime = System.nanoTime();
        log.info("开始生成 PDF 文件...");

        FileOutputStream fos = null;
        com.itextpdf.text.Document document = null;

        try {
            fos = new FileOutputStream(outputPdfPath);
              // 创建 iText 的 Document 对象时使用带参数的构造函数
            document = new com.itextpdf.text.Document(PageSize.A4, 36, 36, 36, 36);


            PdfWriter writer = PdfWriter.getInstance(document, fos);
            document.open();

            // 读取 HTML 文件
            String htmlContent = new String(Files.readAllBytes(Paths.get(htmlFilePath)), "UTF-8");

            // 使用 Jsoup 解析 HTML
            org.jsoup.nodes.Document doc = Jsoup.parse(htmlContent);

             //创建两种字体
            BaseFont doulosSILBaseFont = BaseFont.createFont(doulosSILFontPath,
                    BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font doulosSILFont = new Font(doulosSILBaseFont, 12);

            BaseFont simSunBaseFont = BaseFont.createFont(simSunFontPath,
                    BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font simSunFont = new Font(simSunBaseFont, 12);

              // 替换相对路径和处理图片
             Elements images = doc.select("img");
            for (Element image : images) {
                String src = image.attr("src");
                if (!src.startsWith("http") && !src.startsWith("https")) {
                    //  构建绝对路径
                    String absolutePath = Paths.get(imageBasePath, src).normalize().toString();
                    try{
                         Image img = Image.getInstance(absolutePath);
                        float widthPercentage = 80;
                        float pageWidth = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
                        float desiredWidth = pageWidth * (widthPercentage / 100f);
                         float scalePercentage = (desiredWidth / img.getWidth()) * 100f;
                        img.scalePercent(scalePercentage); //  将图片缩放到指定宽度
//                        img.setAlignment(org.it Element.ALIGN_CENTER); // 正确使用 iText 的 Element.ALIGN_CENTER
                      img.setAlignment(Image.ALIGN_CENTER); // 正确使用 iText 的 Image.ALIGN_CENTER
                        img.scaleToFit(document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin(), img.getHeight());
                        document.add(img);

                     } catch (Exception e){
                         log.error("图片加载失败：{}", absolutePath,e);
                         document.add(new Paragraph("图片加载失败: " + absolutePath, simSunFont));
                     }
                }
            }

             //使用html转换pdf
            Elements elements = Jsoup.parse(htmlContent).body().children();
            for (Element element:elements){
               if (element.tagName().equalsIgnoreCase("p")){
                    String text = element.text();
                    Paragraph p = new Paragraph();
                    String[] parts = text.split("(\\[.+?\\])");
                     if (parts.length > 0) {
                       p.add(new Chunk(parts[0], simSunFont));
                        log.debug("中文部分: {}",parts[0]);
                       }
                    String phoneticPart = "";
                     if (text.contains("[")&& text.contains("]")){
                           phoneticPart = text.substring(text.indexOf("["), text.indexOf("]") + 1);
                         p.add(new Chunk(phoneticPart, doulosSILFont));
                         log.debug("音标部分：{}",phoneticPart);
                    }
                     if (parts.length > 1) {
                          p.add(new Chunk(parts[1], simSunFont));
                        log.debug("剩余中文部分：{}",parts[1]);
                      }
                    document.add(p);

                } else{
                     document.add(new Paragraph(element.text(), simSunFont));
                }
            }


            long endTime = System.nanoTime();
            String duration = formatDuration(startTime, endTime);
            log.info("PDF 文件生成完成， 耗时：{}", duration);

        } finally {
            if (document != null) {
                document.close();
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    log.error("关闭FileOutputStream资源时发生异常：", e);
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

        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds,
                millis);
    }
}
