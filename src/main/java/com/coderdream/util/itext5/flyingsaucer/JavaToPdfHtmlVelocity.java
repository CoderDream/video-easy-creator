package com.coderdream.util.itext5.flyingsaucer;


import com.coderdream.util.itext5.util.PathUtil;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

/**
 * Created by lujianing on 2017/5/7.
 */
public class JavaToPdfHtmlVelocity {

  private static final String DEST = "target/HelloWorld_CN_HTML_VELOCITY_FS.pdf";
  private static final String HTML = "template_velocity_fs.html";
  private static final String FONT = "simhei.ttf";
  private static final String LOGO_PATH =
    "file://" + PathUtil.getCurrentPath() + "/logo.png";

  private static VelocityEngine ve = new VelocityEngine();


  static {
    ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
    ve.setProperty("classpath.resource.loader.class",
      ClasspathResourceLoader.class.getName());

    ve.init();
  }

  public static void main(String[] args)
    throws IOException, DocumentException, com.lowagie.text.DocumentException {
    Map<String, Object> data = new HashMap();
    data.put("name", "鲁家宁&lt;");
    String content = JavaToPdfHtmlVelocity.velocityRender(data, HTML);
    JavaToPdfHtmlVelocity.createPdf(content, DEST);
  }

  /**
   * freemarker渲染html
   */
  public static String velocityRender(Map<String, Object> data,
    String htmlTmp) {

    Template t = ve.getTemplate(htmlTmp);
    VelocityContext ctx = new VelocityContext(data);

    StringWriter sw = new StringWriter();

    t.merge(ctx, sw);

    return sw.toString();
  }

  public static void createPdf(String content, String dest)
    throws IOException, DocumentException, com.lowagie.text.DocumentException {
    ITextRenderer render = new ITextRenderer();
    ITextFontResolver fontResolver = render.getFontResolver();
    fontResolver.addFont(FONT, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
    // 解析html生成pdf
    render.setDocumentFromString(content);
    //解决图片相对路径的问题
    render.getSharedContext().setBaseURL(LOGO_PATH);
    render.layout();
    render.createPDF(new FileOutputStream(dest));
  }
}
