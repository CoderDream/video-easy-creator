//package com.coderdream;
//
//import com.deepoove.poi.XWPFTemplate;
//import com.deepoove.poi.config.Configure;
//import com.deepoove.poi.plugin.markdown.MarkdownRenderData;
//import com.deepoove.poi.plugin.markdown.MarkdownRenderPolicy;
//import com.deepoove.poi.plugin.markdown.MarkdownStyle;
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import org.apache.commons.io.FileUtils;
//import org.commonmark.Extension;
//import org.commonmark.ext.gfm.tables.TablesExtension;
//import org.commonmark.node.AbstractVisitor;
//import org.commonmark.node.BulletList;
//import org.commonmark.node.Link;
//import org.commonmark.node.ListItem;
//import org.commonmark.node.Node;
//import org.commonmark.node.Paragraph;
//import org.commonmark.node.Text;
//import org.commonmark.parser.Parser;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//public class DocsifyTest {
//
//    String docsifyFolder = "./";
//
//    @BeforeEach
//    public void init() throws IOException {
////        Path path = Paths.get("docsify");
////        Files.createDirectories(path);
////
////        FileUtils.cleanDirectory(new File("docsify"));
//
////        Collection<File> listFiles = FileUtils.listFiles(new File(docsifyFolder), new String[] { "png" }, true);
////        for (File src : listFiles) {
////            FileUtils.copyFile(src, new File("docsify/" + src.getName()));
////
////        }
//    }
//
////    @Test
//    public void testConvertAllInOne() throws Exception {
//        StringBuilder all = new StringBuilder();
//
//        String toc = new String(Files.readAllBytes(Paths.get(docsifyFolder, "_sidebar.md")));
//        List<Extension> extensions = Arrays.asList(TablesExtension.create());
//        Parser parser = Parser.builder().extensions(extensions).build();
//        Node node = parser.parse(toc);
//        node.accept(new AbstractVisitor() {
//            @Override
//            public void visit(BulletList listBlock) {
//                Node node = listBlock.getFirstChild();
//                while (null != node) {
//                    if (node instanceof ListItem) {
//                        Node itemNode = node.getFirstChild();
//                        while (null != itemNode) {
//                            Paragraph p = (Paragraph) itemNode;
//                            Node toc = p.getFirstChild();
//                            Link t = (Link) toc;
//                            String destination = t.getDestination();
//                            String title = ((Text) t.getFirstChild()).getLiteral();
//                            all.append("\n## " + title + "\n");
//                            try {
//                                all.append(new String((destination.endsWith("md") || destination.endsWith("MD"))
//                                        ? Files.readAllBytes(Paths.get(docsifyFolder, destination))
//                                        : Files.readAllBytes(Paths.get(docsifyFolder, destination, "README.md"))));
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            itemNode = itemNode.getNext();
//                        }
//                    }
//                    node = node.getNext();
//                }
//            }
//        });
//
//        System.out.println(all);
//        Path file = Files.createFile(Paths.get("docsify/all.md"));
//        Files.write(file, all.toString().getBytes());
//
//    }
//
////    @Test
//    public void testMultiMarkdown() throws Exception {
//        testConvertAllInOne();
//        MarkdownRenderData code = new MarkdownRenderData();
//        byte[] bytes = Files.readAllBytes(Paths.get("docsify/all.md"));
//        String mkdn = new String(bytes);
//        code.setMarkdown(mkdn);
//
//        MarkdownStyle style = MarkdownStyle.newStyle();
//
//        style.setImagesDir(docsifyFolder);
//        style.setShowHeaderNumber(true);
//        code.setStyle(style);
//
//        Map<String, Object> data = new HashMap<>();
//        data.put("md", code);
//
//        Configure config = Configure.builder().bind("md", new MarkdownRenderPolicy()).build();
//        XWPFTemplate.compile("src/test/resources/markdown/markdown_template.docx", config)
//                .render(data)
//                .writeToFile("out_markdown.docx");
//    }
//
//    @Test
//    public void testSingleMarkdown() throws Exception {
//        MarkdownRenderData code = new MarkdownRenderData();
////        byte[] bytes = Files.readAllBytes(Paths.get(docsifyFolder + "/README.md"));
//        // D:\04_GitHub\hexo-project\Hexo-BlueLake-Blog\source\_posts\wechat-2017-04-06.md
//        byte[] bytes = Files.readAllBytes(Paths.get("D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\wechat-2017-04-06.md"));
//        String mkdn = new String(bytes);
//        code.setMarkdown(mkdn);
//
//        MarkdownStyle style = MarkdownStyle.newStyle();
////        style.setImagesDir(docsifyFolder);
//        style.setImagesDir("D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts");
////        style.setShowHeaderNumber(true);
//        style.setShowHeaderNumber(false);
//        code.setStyle(style);
//
//        Map<String, Object> data = new HashMap<>();
//        data.put("md", code);
//
//        Configure config = Configure.builder().bind("md", new MarkdownRenderPolicy()).build();
//        XWPFTemplate.compile("src/test/resources/markdown/markdown_template.docx", config)
//                .render(data)
//                .writeToFile("D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\wechat-2017-04-06.docx");
//    }
//
//    @Test
//    public void testSingleMarkdown_02() throws Exception {
//        MarkdownRenderData code = new MarkdownRenderData();
////        byte[] bytes = Files.readAllBytes(Paths.get(docsifyFolder + "/README.md"));
//        // D:\04_GitHub\hexo-project\Hexo-BlueLake-Blog\source\_posts\wechat-2017-04-06.md
//        byte[] bytes = Files.readAllBytes(Paths.get("D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\bai-ci-zan-108.md"));
//        String mkdn = new String(bytes);
//        code.setMarkdown(mkdn);
//
//        MarkdownStyle style = MarkdownStyle.newStyle();
////        style.setImagesDir(docsifyFolder);
//        style.setImagesDir("D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts");
////        style.setShowHeaderNumber(true);
//        style.setShowHeaderNumber(false);
//        code.setStyle(style);
//
//        Map<String, Object> data = new HashMap<>();
//        data.put("md", code);
//
//        Configure config = Configure.builder().bind("md", new MarkdownRenderPolicy()).build();
//        XWPFTemplate.compile("src/test/resources/markdown/markdown_template.docx", config)
//            .render(data)
//            .writeToFile("D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog\\source\\_posts\\bai-ci-zan-108.docx");
//    }
//
//}
