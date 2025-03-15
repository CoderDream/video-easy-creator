//package com.coderdream.util.word;
//
//import com.coderdream.util.cd.CdFileUtil;
//import com.deepoove.poi.XWPFTemplate;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.HashMap;
//
///**
// * poi-tl Word模板引擎
// *
// * @author dafeng
// * @date 2024/12/20 13:41
// */
//public class PoiTlUtil {
//
//    private static final String  folderPath =
//      CdFileUtil.getResourceRealPath() + File.separatorChar + "templates";
//
//    private static final String TEMP_PATH = folderPath  + File.separatorChar + "words.docx";
//    private static final String OUT_PATH = "D:\\output\\output_001.docx";
//
//    public static void main(String[] args) throws Exception {
//        XWPFTemplate template = XWPFTemplate.compile(TEMP_PATH).render(genData());
//        template.writeAndClose(Files.newOutputStream(Paths.get(OUT_PATH)));
//    }
//
//    public static void process() throws Exception {
//        XWPFTemplate template = XWPFTemplate.compile(TEMP_PATH).render(genData());
//        template.writeAndClose(Files.newOutputStream(Paths.get(OUT_PATH)));
//    }
//
//    private static Object genData() {
//        return new HashMap<String, Object>() {{
//            // 文本
//            put("title", "Hi, poi-tl Word模板引擎");
//        }};
//    }
//
//
//}
