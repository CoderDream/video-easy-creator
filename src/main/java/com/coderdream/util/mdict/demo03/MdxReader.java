package com.coderdream.util.mdict.demo03;//package com.coderdream.freeapps.util.mdict.demo03;
//
////import io.github.zhanliang.mdict.mdx.MdxEntry;
////import io.github.zhanliang.mdict.mdx.MdxFile;
//
//import java.io.File;
//import java.io.IOException;
//
//public class MdxReader {
//    public static void main(String[] args) {
//        // 设置 MDX 文件路径
//        String mdxFilePath = "path/to/your/dictionary.mdx";
//
//        try {
//            // 加载 MDX 文件
//            MdxFile mdxFile = new MdxFile(new File(mdxFilePath));
//
//            // 遍历字典条目
//            System.out.println("Reading MDX entries...");
//            for (MdxEntry entry : mdxFile) {
//                String word = entry.getKey();  // 获取单词
//                String definition = entry.getDefinition();  // 获取释义
//
//                // 输出
//                System.out.println("Word: " + word);
//                System.out.println("Definition: " + definition);
//                System.out.println("--------------------------------------------------");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.out.println("Error while reading the MDX file.");
//        }
//    }
//}
