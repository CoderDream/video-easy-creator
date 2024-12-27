package com.coderdream.util.bbc;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * WebCrawler1 类
 * 用于爬取 BBC Learning English 网页内容，下载 PDF、脚本，提取脚本和词汇内容。
 */
@Slf4j
public class WebCrawler1 {

    // 定义代理 IP 和端口
    private static final String PROXY_HOST = "127.0.0.1";
    private static final int PROXY_PORT = 7890;

    /**
     * 爬取指定 URL 的网页内容，提取 PDF 链接、脚本链接，保存脚本和词汇内容。
     *
     * @param url 要爬取的网页 URL
     */
    public static void crawl(String url) {
        try {
            // 1. 使用 Jsoup 获取网页文档
            Document doc = fetchDocumentWithProxy(url);
            if (doc == null) {
                 log.error("Failed to fetch document for URL: {}", url);
                return;
            }

            // 2. 下载 PDF 文件
            downloadFile(doc, "Download PDF", "downloaded.pdf");
            // 3. 下载脚本文件
            downloadFile(doc, "transcript", "transcript.pdf");

             // 4. 提取脚本内容并保存
            String scriptContent = extractContent(doc, "Note: This is not a word-for-word transcript.", "Latest 6 Minute English");
            if(scriptContent!=null) {
                 saveContentToFile(scriptContent, "script.txt");
            }

            // 5. 提取词汇内容并保存
            String vocabularyContent = extractContent(doc, "Vocabulary", "TRANSCRIPT");
            if(vocabularyContent != null) {
                 saveContentToFile(vocabularyContent, "voc.txt");
            }

        } catch (IOException e) {
             log.error("Error during crawling process for URL: {}", url, e);
            System.err.println("Error during crawling process: " + e.getMessage());
        }
    }


    /**
     * 使用 Jsoup 通过代理获取网页文档。
     *
     * @param url 要获取的网页 URL
     * @return 获取到的 Document 对象，失败返回 null
     */
    private static Document fetchDocumentWithProxy(String url) {
        try {
             Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_HOST, PROXY_PORT));
             Connection connection = Jsoup.connect(url).proxy(proxy);
             return connection.get();
        } catch (IOException e) {
             log.error("Failed to fetch document with proxy for URL: {}", url, e);
            return null;
        }
    }

    /**
     * 下载指定链接的文件。
     *
     * @param doc        Jsoup Document 对象
     * @param linkText   链接文本，用于查找对应的链接
     * @param outputName 输出文件名称
     * @throws IOException 如果下载或保存文件时发生错误
     */
    private static void downloadFile(Document doc, String linkText, String outputName) throws IOException {
        Elements links = doc.select("a:contains(" + linkText + ")"); // 使用 contains 查找包含指定文本的元素
        if (!links.isEmpty()) {
            String fileUrl = links.first().absUrl("href"); // 获取绝对 URL
            if(fileUrl != null && !fileUrl.isEmpty()){
               try (InputStream in = Jsoup.connect(fileUrl).ignoreContentType(true).execute().bodyStream()) {
                 Path outputPath = Paths.get(outputName);
                 Files.copy(in, outputPath, StandardCopyOption.REPLACE_EXISTING); // 下载文件并覆盖
                   log.info("Downloaded file from {} to {}", fileUrl, outputPath);
               }
           } else{
                 log.warn("File url is empty: {}",fileUrl);
            }
         } else{
             log.warn("Link with text '{}' not found.", linkText);
        }
    }

   /**
     * 提取网页中指定开始和结束标签之间的内容。
     *
     * @param doc       Jsoup Document 对象
     * @param startText 开始文本
     * @param endText   结束文本
     * @return 提取到的文本内容，如果没找到或发生错误则返回 null
     */
    private static String extractContent(Document doc, String startText, String endText) {
        String content = null;
        try {
           String html = doc.html();
           int startIndex = html.indexOf(startText);
           if (startIndex == -1) {
               log.warn("Start text '{}' not found.", startText);
              return null;
           }

            int endIndex = html.indexOf(endText, startIndex);
            if (endIndex == -1) {
                log.warn("End text '{}' not found.", endText);
                return null;
            }
             content = html.substring(startIndex, endIndex);
             log.info("Successfully extracted content between '{}' and '{}'", startText, endText);

         } catch (Exception e) {
            log.error("Error while extracting content:", e);
        }
        return content;
    }


    /**
     * 将字符串内容保存到文件。
     *
     * @param content    要保存的字符串内容
     * @param outputName 输出文件名称
     */
    private static void saveContentToFile(String content, String outputName) {
         Path outputPath = Paths.get(outputName);
          try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
             writer.write(content);
               log.info("Saved content to: {}", outputPath);
         } catch (IOException e) {
             log.error("Failed to save content to file: {}", outputPath, e);
            System.err.println("Failed to save content to file: " + e.getMessage());
        }
    }

    /**
     * 主方法，用于测试爬虫功能。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        String url = "https://www.bbc.co.uk/learningenglish/english/features/6-minute-english_2024/ep-241226";
        crawl(url);
    }
}
