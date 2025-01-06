package com.coderdream.util;

import cn.hutool.core.io.FileUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;

/**
 * Java按一行一行进行文件的读取或写入 https://blog.csdn.net/yuanhaiwn/article/details/83090540
 *
 * @author CoderDream
 */
@Slf4j
public class CdFileUtil {

    /**
     * 读取resources文件夹下13500文件夹中的1-3500.txt文件并返回内容列表
     *
     * @return 文件内容的列表
     */
    public static List<String> readFileContent(String resourcePath) {
        // 获取资源的URL
//        String resourcePath = "classpath:13500/" + filename;
        try {
            // 使用HuTool的ResourceUtil获取资源路径
            // 指定要下载的文件
            File file = ResourceUtils.getFile(resourcePath);
            // 定义UTF-16 Little Endian编码
            Charset utf16Le = StandardCharsets.UTF_16LE;
            // 读取文件内容到列表
//            return FileUtil.readLines(file, "UTF-8");
//            List<String> lines = FileUtil.readLines(file, utf16Le);
            List<String> lines = FileUtil.readLines(file, StandardCharsets.UTF_8);
            // 移除每行首尾空格，并过滤掉空行
            return lines.stream()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toList());
        } catch (Exception e) {
//            e.printStackTrace();
            log.error("读取文件失败: {}", e.getMessage());
            // 抛出运行时异常或进行其他错误处理
            throw new RuntimeException("读取文件失败", e);
        }
    }

    /**
     * 读取resources文件夹下13500文件夹中的1-3500.txt文件并返回内容列表
     *
     * @return 文件内容的列表
     */
    public static List<String> readFileContentWithCharset(String resourcePath, Charset charset) {
        // 获取资源的URL
//        String resourcePath = "classpath:13500/" + filename;
        try {
            // 使用HuTool的ResourceUtil获取资源路径
            // 指定要下载的文件
            File file = ResourceUtils.getFile(resourcePath);
            // 定义UTF-16 Little Endian编码
//            Charset utf16Le = StandardCharsets.UTF_16LE;
            // 读取文件内容到列表
//            return FileUtil.readLines(file, "UTF-8");
            List<String> lines = FileUtil.readLines(file, charset);
            // 移除每行首尾空格，并过滤掉空行
            return lines.stream()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toList());
        } catch (Exception e) {
//            e.printStackTrace();
            log.error("读取文件失败: {}", e.getMessage());
            // 抛出运行时异常或进行其他错误处理
            throw new RuntimeException("读取文件失败", e);
        }
    }

    public static void writeToFile(String fileName, String content) {
        try {
//            String[] arrs = {
//                    "zhangsan,23,福建",
//                    "lisi,30,上海",
//                    "wangwu,43,北京",
//                    "laolin,21,重庆",
//                    "ximenqing,67,贵州"
//            };
            String[] contentList = content.split(" ");
            //写入中文字符时解决中文乱码问题
            FileOutputStream fos = null;

//            fos = new FileOutputStream(new File("E:/phsftp/evdokey/evdokey_201103221556.txt"));

            fos = new FileOutputStream(fileName);

            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);
            //简写如下：
            //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
            //        new FileOutputStream(new File("E:/phsftp/evdokey/evdokey_201103221556.txt")), "UTF-8"));

            for (String arr : contentList) {
                bw.write(arr + "\t\n");
            }

            //注意关闭的先后顺序，先打开的后关闭，后打开的先关闭
            bw.close();
            osw.close();
            fos.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    public static boolean writeToFile(String fileName, List<String> contentList) {
//        FileOutputStream fos = null;
//        OutputStreamWriter osw = null;
//        BufferedWriter bw = null;
//        try {
//            //写入中文字符时解决中文乱码问题
//            fos = new FileOutputStream(fileName);
//
//            osw = new OutputStreamWriter(fos, "UTF-8");
//            bw = new BufferedWriter(osw);
//            int size = contentList.size();
//            for (int i = 0; i < size; i++) {
//                String str = contentList.get(i);
//                //  str = "\uFEFF" + str; BOM格式，剪映不认识，Subindex 合并字幕时要打开
//                // 如果不是最后一行，就加上回车换行
//                if (i != size - 1) {
//                    if (str != null) {
//                        str = str.trim().replaceAll("  ", " ") + "\r\n";
//                    }
//                }
//
//                if (str != null) {
//                    bw.write(str);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        } finally {
//            //注意关闭的先后顺序，先打开的后关闭，后打开的先关闭
//            if (bw != null) {
//                try {
//                    bw.close();
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//            if (osw != null) {
//                try {
//                    osw.close();
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//            if (fos != null) {
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//
//            return true;
//        }
//    }

    /**
     * 将内容写入指定的文件，并处理异常。
     *
     * @param fileName 文件名
     * @param contentList 要写入文件的内容列表
     * @return 如果写入成功返回 true，否则返回 false
     */
    public static boolean writeToFile(String fileName, List<String> contentList) {
        // 使用 try-with-resources 自动关闭资源
        try (FileOutputStream fos = new FileOutputStream(fileName);
          OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
          BufferedWriter bw = new BufferedWriter(osw)) {

            int size = contentList.size();  // 获取内容列表的大小
            log.info("开始写入文件: {}", fileName);  // 记录开始写入文件的日志

            // 遍历所有内容并写入文件
            for (int i = 0; i < size; i++) {
                String str = contentList.get(i);

                // 如果内容不是最后一行，添加换行符
                if (i != size - 1 && str != null) {
                    str = str.trim().replaceAll("  ", " ") + "\r\n";
                }

                // 如果内容不为空，则写入
                if (str != null) {
                    bw.write(str);
                }
            }

            log.info("文件写入成功: {}", fileName);  // 记录成功写入文件的日志
            return true;  // 写入成功返回true

        } catch (IOException e) {
            // 捕获IOException并打印堆栈信息，返回false表示写入失败
            log.error("写入文件失败: {}，错误信息: {}", fileName, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 返回 D:\04_GitHub\java-architect-util\free-apps\src\main\resources //
     * https://blog.csdn.net/qq_38319289/article/details/115236819 // SpringBoot获取resources文件路径 // File directory = new
     * File("src/main/resources"); // String reportPath = directory.getCanonicalPath(); // String resource =reportPath +
     * "/static/template/resultTemplate.docx";
     *
     * @return 资源文件夹路径
     */
    public static String getResourceRealPath() {
        File directory = new File("src/main/resources");
        String reportPath = "";
        try {
            reportPath = directory.getCanonicalPath();
        } catch (Exception e) {
            log.error("获取资源文件夹路径失败: {}", e.getMessage());
        }

        return reportPath;
    }


    /**
     * 在文件名后添加指定的字符串，保留文件路径和扩展名
     *
     * @param filePath 原始文件路径
     * @param part     要添加的字符串
     * @return 修改后的文件路径
     */
    public static String addPostfixToFileName(String filePath, String part) {
        // 将文件路径字符串转换为Path对象
        Path path = Paths.get(filePath);

        // 获取文件名
        Path fileName = path.getFileName();
        if (fileName == null) {
            return filePath;  // 如果没有文件名，则直接返回原始路径
        }

        String fileNameStr = fileName.toString();
        // 分割文件名和扩展名
        int dotIndex = fileNameStr.lastIndexOf('.');
        String baseName, extension = "";

        if (dotIndex > 0) {
            baseName = fileNameStr.substring(0, dotIndex);
            extension = fileNameStr.substring(dotIndex);
        } else {
            baseName = fileNameStr; // 没有扩展名
        }

        // 构建新的文件名
        String newFileName = baseName + part + extension;

        // 获取文件所在的目录
        Path parent = path.getParent();

        // 构建新的文件路径，如果parent为空，则保持原路径不变
        Path newPath = (parent != null) ? parent.resolve(newFileName) : Paths.get(newFileName);

        return newPath.toString();
    }

    public static void main(String[] args) {
//        String filePath = "D:\\0000\\EnBook001\\商务职场英语口语900句\\商务职场英语口语900句V1_ch0201.txt";
        String filePath = "商务职场英语口语900句V1_ch0201.txt";
        String newFilePath = addPostfixToFileName(filePath, "_part01");
        System.out.println("原始文件路径: " + filePath);
        System.out.println("修改后的文件路径: " + newFilePath);

        String filePath2 = "D:/0000/EnBook001/商务职场英语口语900句/商务职场英语口语900句V1_ch0201.txt";
        String newFilePath2 = addPostfixToFileName(filePath2, "_part01");
        System.out.println("原始文件路径2: " + filePath2);
        System.out.println("修改后的文件路径2: " + newFilePath2);

    }
}
