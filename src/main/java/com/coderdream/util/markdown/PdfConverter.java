package com.coderdream.util.markdown;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.vladsch.flexmark.ext.toc.TocExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.pdf.converter.PdfConverterExtension;
import com.vladsch.flexmark.profile.pegdown.Extensions;
import com.vladsch.flexmark.profile.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;

public class PdfConverter {

  static final MutableDataHolder OPTIONS = PegdownOptionsAdapter.flexmarkOptions(
    Extensions.ALL & ~(Extensions.ANCHORLINKS | Extensions.EXTANCHORLINKS_WRAP),
    TocExtension.create()).toMutable();
  static final Parser PARSER = Parser.builder(OPTIONS).build();
  static final HtmlRenderer RENDERER = HtmlRenderer.builder(OPTIONS).build();

  public static void main(String[] args) throws Exception {
//        String path = Objects.requireNonNull(PdfConverter.class.getClassLoader()
//          .getResource("D:\\java_output\\HYZhongHeiTi-197.ttf")).getPath();

    File path2 = new File("D:\\java_output\\HYZhongHeiTi-197.ttf");
    String path = "file:" + path2.getAbsolutePath();

    // Markdown文件的路径
    String markdownFilePath = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\bai-ci-zan-001.md";
    // 生成的PDF文件的路径
    String pdfFilePath = "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\bai-ci-zan-001.pdf";

    try {
      // 1. 读取Markdown文件
      String markdown = new String(
        Files.readAllBytes(Paths.get(markdownFilePath)));

//            // 2. 使用flexmark-java将Markdown转换为HTML
//            MutableDataSet options = new MutableDataSet();
//            Parser parser = Parser.builder(options).build();
//            HtmlRenderer renderer = HtmlRenderer.builder(options).build();
//            String html = renderer.render(parser.parse(markdown));
//
//            // 3. 使用openhtmltopdf将HTML转换为PDF
//            try (FileOutputStream os = new FileOutputStream(pdfFilePath)) {
//                PdfRendererBuilder builder = new PdfRendererBuilder();
//                builder.useFastMode();
//                builder.withHtmlContent(html, null);
//                builder.toStream(os);
//                builder.run();
//            }

      Node document = PARSER.parse(markdown);
      String html = RENDERER.render(document);
      System.out.println(html);
      html = "<!DOCTYPE html><html><head>\n" +
        "</head><body>" + html + "\n" +
        "</body></html>";
      String css = "@font-face {\n" +
        "  font-family: 'cnFont';\n" +
        "  src: url('file:" + path + "');\n" +
        "  font-weight: normal;\n" +
        "  font-style: normal;\n" +
        "}\n" +
        "* {\n" +
        "    font-family: 'cnFont';\n" +
        "}\n";
      html = PdfConverterExtension.embedCss(html, css);
//        OPTIONS.set(PdfConverterExtension.PROTECTION_POLICY, new StandardProtectionPolicy("123", "123", new AccessPermission()));
//            PdfConverterExtension.exportToPdf("flexmark-java-landscape.pdf", html, "", OPTIONS);
      PdfConverterExtension.exportToPdf(pdfFilePath, html, "", OPTIONS);
    } catch (IOException e) {
      e.printStackTrace();
    }

//        String markdown = "" +
//                "# Heading\n\n" +
//                "=======\n" +
//                "\n" +
//                "*** ** * ** ***\n" +
//                "\n" +
//                "paragraph text lazy continuation\n" +
//                "\n" +
//                "* list itemblock quote lazy continuation\n" +
//                "\n" +
//                "\\~\\~\\~info with uneven indent with uneven indent indented code \\~\\~\\~\n" +
//                "\n" +
//                "       with uneven indent\n" +
//                "          with uneven indent\n" +
//                "    indented code\n" +
//                "\n" +
//                "1. numbered item 1\n" +
//                "2. numbered item 2\n" +
//                "3. numbered item 3\n" +
//                "   * bullet item 1\n" +
//                "   * bullet item 2\n" +
//                "   * bullet item 3\n" +
//                "     1. numbered sub-item 1\n" +
//                "     2. numbered sub-item 2\n" +
//                "     3. numbered sub-item 3\n" +
//                "\n" +
//                "   \\~\\~\\~info with uneven indent with uneven indent indented code \\~\\~\\~\n" +
//                "```java \n" +
//                "   System.out.println(\"test print\");\n" +
//                "``` \n\n" +
//                "## 中文测试\n\n" +
//                "| 字符 | 说明 | \n" +
//                "| --- | --- | \n" +
//                "| var 变量名称    | 申明变量，弱类型 |";

  }
}
