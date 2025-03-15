//package com.coderdream.util.markdown.demo05;
//
//import com.coderdream.util.cd.CdFileUtil;
//import com.deepoove.poi.XWPFTemplate;
//import com.deepoove.poi.config.Configure;
//import com.deepoove.poi.plugin.markdown.MarkdownRenderData;
//import com.deepoove.poi.plugin.markdown.MarkdownRenderPolicy;
//import com.deepoove.poi.plugin.markdown.MarkdownStyle;
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.HashMap;
//import java.util.Map;
//
//public class Markdown2WordUtil {
//
//  public static void singleMarkdown(String mdFileName, String docxFileName) {
//    MarkdownRenderData code = new MarkdownRenderData();
////        byte[] bytes = Files.readAllBytes(Paths.get(docsifyFolder + "/README.md"));
//    // D:\04_GitHub\hexo-project\Hexo-BlueLake-Blog\source\_posts\wechat-2017-04-06.md
//    byte[] bytes;
//    try {
//      bytes = Files.readAllBytes(Paths.get(mdFileName));
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//    String mkdn = new String(bytes);
//    code.setMarkdown(mkdn);
//
//    MarkdownStyle style = MarkdownStyle.newStyle();
////        style.setImagesDir(docsifyFolder);
//    File file = new File(mdFileName);
//    style.setImagesDir(file.getParent());
////        style.setShowHeaderNumber(true);
//    style.setShowHeaderNumber(false);
//    code.setStyle(style);
//
//    Map<String, Object> data = new HashMap<>();
//    data.put("md", code);
//
//    Configure config = Configure.builder()
//      .bind("md", new MarkdownRenderPolicy()).build();
//    try {
//      String folderPath =
//        CdFileUtil.getResourceRealPath() + File.separatorChar + "markdown";
//
//      String templateFileWithPath =
//        folderPath + File.separator + "markdown_template.docx";
//      XWPFTemplate.compile(templateFileWithPath,
//          config)
//        .render(data)
//        .writeToFile(
//          docxFileName);
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//}
