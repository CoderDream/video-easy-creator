package com.coderdream.util.markdown;

import com.coderdream.util.cd.CdStringUtil;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MarkdownToTxtConverter {

  /**
   * 将 Markdown 文件转换为 TXT 文件。
   *
   * @param markdownFilePath 输入的 Markdown 文件路径
   * @param txtFilePath      输出的 TXT 文件路径
   * @throws IOException 如果发生 I/O 错误
   */
  public static void convert(String markdownFilePath, String txtFilePath)
    throws IOException {
    // 读取 Markdown 文件内容
    String markdownContent = readMarkdownFile(markdownFilePath);

    // 将 Markdown 转换为纯文本
    String plainText = convertMarkdownToPlainText(markdownContent);

    // 去掉
    plainText = CdStringUtil.replaceHtmlTag(plainText);


    // 去掉无用的行（引用）
    plainText = CdStringUtil.removeUseLessLines(plainText);



    // 将纯文本写入 TXT 文件
    writePlainTextFile(plainText, txtFilePath);
  }

  /**
   * 读取Markdown文件的内容
   *
   * @param markdownFilePath Markdown文件的路径
   * @return Markdown文件内容
   * @throws IOException IO异常
   */
  private static String readMarkdownFile(String markdownFilePath)
    throws IOException {
    StringBuilder sb = new StringBuilder();
    try (BufferedReader br = new BufferedReader(
      new FileReader(markdownFilePath))) {
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line).append(System.lineSeparator());
      }
      return sb.toString();
    }
  }

  /**
   * 将 Markdown 转换为纯文本。
   *
   * @param markdownContent 输入的 Markdown 内容
   * @return 转换后的纯文本
   */
  private static String convertMarkdownToPlainText(String markdownContent) {
    // 使用 flexmark 解析 Markdown
    MutableDataSet options = new MutableDataSet();
    options.set(Parser.EXTENSIONS,
      Collections.singletonList(StrikethroughExtension.create()));

    Parser parser = Parser.builder(options).build();
    Node document = parser.parse(markdownContent);

    HtmlRenderer renderer = HtmlRenderer.builder(options).build();
    String html = renderer.render(document);
    // 使用正则表达式去除 HTML 标签，并解码 HTML 实体
    return decodeHtmlEntities(removeHtmlTags(html));
  }

  /**
   * 使用正则表达式去除 HTML 标签和脚注标记。
   *
   * @param html HTML字符串
   * @return 去除HTML标签和脚注标记后的字符串
   */
  private static String removeHtmlTags(String html) {
    String replacedHtml = html;
    // 去除 HTML 标签
    replacedHtml = replacedHtml.replaceAll("<[^>]*>", "");
    // 去除脚注标记（如 <sup>[数字]</sup>）
    replacedHtml = replacedHtml.replaceAll("<sup>\\[\\d+\\]</sup>", "");
    return replacedHtml;
  }

  /**
   * 解码 HTML 实体
   *
   * @param html HTML字符串
   * @return 解码后的字符串
   */
  private static String decodeHtmlEntities(String html) {
    String decodedHtml = html;
    decodedHtml = decodedHtml.replaceAll(" ", " ");
    decodedHtml = decodedHtml.replaceAll("<", "<");
    decodedHtml = decodedHtml.replaceAll(">", ">");
    decodedHtml = decodedHtml.replaceAll("&", "&");
    decodedHtml = decodedHtml.replaceAll("&quot;", "\"");
    decodedHtml = decodedHtml.replaceAll("'", "'");
    return decodedHtml;
  }


  /**
   * 将纯文本写入 TXT 文件。
   *
   * @param plainText   要写入的纯文本内容
   * @param txtFilePath 输出的 TXT 文件路径
   * @throws IOException 如果发生 I/O 错误
   */
  private static void writePlainTextFile(String plainText, String txtFilePath)
    throws IOException {
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(txtFilePath))) {
      bw.write(plainText);
    }
  }

  public static void main(String[] args) {
    try {
      String folder = "D:\\0000\\01_BookStore\\0001_LuoXiang\\刑法学讲义\\";
      String fileName = "刑法学讲义";
      String markdownFilePath =
        folder + fileName + ".md"; // 替换为你的 markdown 文件路径
      String txtFilePath = folder + fileName + ".txt"; // 替换为你想要的 txt 文件路径
      MarkdownToTxtConverter.convert(markdownFilePath, txtFilePath);
      log.info("Markdown file converted to txt file successfully!");
    } catch (IOException e) {
      log.error("Error converting markdown to txt: {}", e.getMessage(), e);
    }
  }
}
