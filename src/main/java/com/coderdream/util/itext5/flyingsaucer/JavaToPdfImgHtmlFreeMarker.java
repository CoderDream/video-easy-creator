package com.coderdream.util.itext5.flyingsaucer;

import com.coderdream.util.itext5.util.PathUtil;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfException;
import org.jpedal.fonts.FontMappings;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * 使用 FreeMarker 渲染 HTML 模板，并使用 Flying Saucer 将 HTML 文件转换为 PDF，最后将 PDF 转换为 PNG 图片。
 */
@Slf4j
public class JavaToPdfImgHtmlFreeMarker {

  private static final String DEST = "target/HelloWorld_CN_HTML_FREEMARKER_FS_IMG.png"; // 输出 PNG 图片文件路径
  private static final String HTML = "template_freemarker_fs.html"; // HTML 模板文件路径
  private static final String FONT = "D:\\java_output\\fonts\\simhei.ttf"; // 字体文件路径
  private static final String LOGO_PATH = "file://" + PathUtil.getCurrentPath() + "/logo.png"; // Logo 图片路径
  private static final String IMG_EXT = "png"; // 输出图片格式

  private static final Configuration freemarkerCfg; // FreeMarker 配置

  static {
    freemarkerCfg = new Configuration();
    // freemarker的模板目录
    try {
      freemarkerCfg.setDirectoryForTemplateLoading(
        new File(PathUtil.getCurrentPath()));
    } catch (IOException e) {
      log.error("设置 FreeMarker 模板目录失败：", e);
    }
    freemarkerCfg.setDefaultEncoding("UTF-8"); // 设置默认编码为 UTF-8
  }

  /**
   * 主方法，生成一个包含中文的 "hello world" PNG 图片。
   *
   * @param args 命令行参数
   */
  public static void main(String[] args) {
    long startTime = System.nanoTime();
    log.info("开始生成 PNG 图片...");
    try {
      generatePngFromHtmlWithFreeMarker();
      long endTime = System.nanoTime();
      String duration = formatDuration(startTime, endTime);
      log.info("PNG 图片生成完成， 耗时：{}", duration);
    } catch (Exception e) {
      log.error("生成 PNG 图片时发生错误:", e);
    }
  }

  /**
   *  生成 PNG 图片的核心方法，内部处理异常。
   */
  private static void generatePngFromHtmlWithFreeMarker()  {
    ByteArrayOutputStream pdfStream = null;
    ByteArrayOutputStream imgSteam = null;
    FileOutputStream fileStream = null;
    try {
      Map<String, Object> data = new HashMap<>();
      data.put("name", "鲁家宁");
      String content = freeMarkerRender(data, HTML);
      pdfStream = createPdf(content);
      imgSteam = pdfToImg(pdfStream.toByteArray(), 2, 1, IMG_EXT);
      if (imgSteam == null){
        log.error("生成图片数据流失败");
        return;
      }

      fileStream = new FileOutputStream(DEST);
      fileStream.write(imgSteam.toByteArray());

    }  catch (IOException | DocumentException | com.lowagie.text.DocumentException e) {
      log.error("生成PNG图片时发生错误：", e);
    } finally {
      if (fileStream != null){
        try {
          fileStream.close();
        } catch (IOException e){
          log.error("关闭 FileOutputStream 资源时发生异常:",e);
        }
      }
      if (pdfStream != null){
        try{
          pdfStream.close();
        } catch (IOException e){
          log.error("关闭 ByteArrayOutputStream 资源时发生异常:",e);
        }
      }
      if(imgSteam != null){
        try{
          imgSteam.close();
        }catch (IOException e){
          log.error("关闭 ByteArrayOutputStream 资源时发生异常:",e);
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
  public static String freeMarkerRender(Map<String, Object> data,
    String htmlTmp) {
    Writer out = new StringWriter();
    try {
      // 获取模板,并设置编码方式
      Template template = freemarkerCfg.getTemplate(htmlTmp);
      // 合并数据模型与模板
      template.process(data, out);
      out.flush();
      return out.toString();
    } catch (Exception e) {
      log.error("FreeMarker 渲染 HTML 时发生错误:",e);
      return null;
    }  finally {
      try {
        out.close();
      } catch (IOException e) {
        log.error("关闭 StringWriter 资源时发生异常:", e);
      }
    }

  }


  /**
   * 根据模板生成pdf文件流
   */
  public static ByteArrayOutputStream createPdf(String content)
    throws IOException, DocumentException, com.lowagie.text.DocumentException {
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    ITextRenderer render = new ITextRenderer();
    ITextFontResolver fontResolver = render.getFontResolver();
    fontResolver.addFont(FONT, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
    // 解析html生成pdf
    render.setDocumentFromString(content);
    //解决图片相对路径的问题
    render.getSharedContext().setBaseURL(LOGO_PATH);
    render.layout();
    render.createPDF(outStream);
    return outStream;
  }


  /**
   * 根据pdf二进制文件 生成图片文件
   *
   * @param bytes      pdf二进制
   * @param scaling    清晰度
   * @param pageNum    页数
   * @param formatName 图片格式
   */
  public static ByteArrayOutputStream pdfToImg(byte[] bytes, float scaling,
    int pageNum, String formatName) {
    //推荐的方法打开PdfDecoder
    PdfDecoder pdfDecoder = new PdfDecoder(true);
    FontMappings.setFontReplacements();
    //修改图片的清晰度
    pdfDecoder.scaling = scaling;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      //打开pdf文件，生成PdfDecoder对象
      pdfDecoder.openPdfArray(bytes); //bytes is byte[] array with PDF
      //获取第pageNum页的pdf
      BufferedImage img = pdfDecoder.getPageAsImage(pageNum);
      ImageIO.write(img, formatName, out);

    } catch (PdfException | IOException e) {
      log.error("PDF 转图片发生错误:",e);
      return null;
    } finally {
      pdfDecoder.closePdfFile();
    }

    return out;
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
