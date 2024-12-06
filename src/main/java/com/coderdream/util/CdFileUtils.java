package com.coderdream.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.util.ResourceUtils;

/**
 * Java按一行一行进行文件的读取或写入 https://blog.csdn.net/yuanhaiwn/article/details/83090540
 *
 * @author CoderDream
 */
@Slf4j
public class CdFileUtils {

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
}
