package com.coderdream.util.epub;

import com.coderdream.util.cd.CdStringUtil;
import com.coderdream.util.markdown.MarkdownToTxtConverter;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import com.vladsch.flexmark.parser.Parser;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.extern.slf4j.Slf4j;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@Slf4j
public class EpubToMarkdownUtil02 {

  public static void main(String[] args) throws IOException {
    String folderPath = "D:\\ebooks\\";
    String pureFileName = "孙子兵法";
    pureFileName = "Gradle 实战";
    folderPath = "D:\\ebooks\\";
    folderPath = "D:\\ebooks\\demo\\";
    folderPath = "C:\\Users\\CoderDream\\Documents\\000_口语_epub\\";

    try {
      // 使用Paths类获取Path对象
      Path path = Paths.get(folderPath);

      // 获取所有以.epub结尾的文件名
      List<Path> epubFiles = Files.list(path)
        .filter(Files::isRegularFile)
        .filter(p -> p.toString().endsWith(".epub"))
        .collect(Collectors.toList());

      // 输出文件名
      for (Path epubFile : epubFiles) {
        String fileName = epubFile.getFileName().toString();
        System.out.println("File Name: " + fileName);

        // 使用Paths类获取Path对象 [nl.siegmann.epublib.epub.ResourcesLoader]: EOF in header
        Path path2 = Paths.get(fileName);

        // 获取文件名（不包含后缀）
        int lastDotIndex = path2.getFileName().toString()
          .lastIndexOf(".");
        if (lastDotIndex >= 0) {
          String fileNameWithoutExtension = path2.getFileName()
            .toString().substring(0, lastDotIndex);
          System.out.println("File Name Without Extension: "
            + fileNameWithoutExtension);
          EpubToMarkdownUtil02.process(folderPath,
            fileNameWithoutExtension);
        } else {
          System.out.println(
            "File Name Without Extension: " + fileName);
          EpubToMarkdownUtil02.process(folderPath, fileName);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static String processEpubToZip(String fileName) {
    File epubFile = new File(fileName);
    String folderPath = epubFile.getParent();
    String pureFileName = epubFile.getName()
      .substring(0, epubFile.getName().lastIndexOf("."));

    // 调用之前的工具类方法进行处理（假设EpubToMarkdownUtil类的process方法实现了核心逻辑）
    String zipFileName = null;
    try {
      zipFileName = EpubToMarkdownUtil02.process(folderPath, pureFileName);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // 根据处理后的文件名构建生成的zip文件对象并返回
    return zipFileName;
  }

  public static String process(String folderPath, String pureFileName)
    throws IOException {

    String fileName = folderPath + File.separator + pureFileName + ".epub";
    String mdFileName = folderPath + File.separator + pureFileName + ".md";
    String txtFileName = folderPath + File.separator + pureFileName + ".txt";
    String zipFileName =
      folderPath + File.separator + pureFileName + ".zip";

    // 加载 EPUB 文件
    File epubFile = new File(fileName);  // 请替换为你的 EPUB 文件路径
    if (!epubFile.exists()) {
      throw new FileNotFoundException("EPUB 文件不存在: " + fileName);
    }
    FileInputStream epubFileInputStream = new FileInputStream(epubFile);
    Book book = (new EpubReader()).readEpub(epubFileInputStream);

    // 获取所有的 HTML 内容
    List<Resource> htmlResources = book.getContents();

    // 获取所有的图片资源并转换为 List
    List<Resource> images = new ArrayList<>(
      book.getResources().getAll());  // 转换为 List

    // 创建一个 StringBuilder 用来保存 Markdown 内容
    StringBuilder markdownContent = new StringBuilder();

    // 先删除images文件夹下的所有文件
    String imageDir = folderPath + File.separator + "images";
    deleteDirectory(new File(imageDir));

    // 遍历 HTML 内容并转换为 Markdown
    for (Resource resource : htmlResources) {
      String htmlContent = new String(resource.getData(),
        StandardCharsets.UTF_8);

      // 添加图片前缀
      htmlContent = addImagesPrefix(htmlContent);

      // 清理 HTML 标签，提取纯文本或转为 Markdown 格式
      String cleanedContent = cleanHtmlContent(htmlContent);

      // 使用文件名作为章节标题
      String chapterTitle = getChapterTitle(htmlContent);
      log.info("正在处理章节标题：{}", chapterTitle);
      if (chapterTitle.equals("Cover")) {
        markdownContent.append("## ").append("封面").append("\n\n");
        // 处理封面图片
        Resource coverImage = getCoverImage(book);
        if (coverImage != null) {
          saveImage(coverImage, folderPath + File.separator + "images");
          markdownContent.append("![Cover Image](").append("/images/")
            .append(coverImage.getHref())
            .append(")\n\n");
        }
      } else {
//                markdownContent.append("## ").append(chapterTitle).append("\n\n");
      }

      markdownContent.append(cleanedContent);
      markdownContent.append("\n\n");
    }

    // 处理图片资源并保存到本地
    for (Resource image : images) {
      if (image.getMediaType().toString().startsWith("image")) {
        saveImage(image, folderPath + File.separator + "images");
//                markdownContent.append("![Image](").append("images/").append(image.getHref()).append(")\n\n");
      }
    }

    // 替换 Markdown 中的图片链接
    markdownContent = new StringBuilder(
      CdStringUtil.replaceImagesLinks(markdownContent.toString()));
    // 移除images标签括号后的第一个的斜线
    markdownContent = new StringBuilder(
      CdStringUtil.replaceImagesFirstLinks(markdownContent.toString()));

    // 替换 Markdown 中的 {#XXXX} 格式的子串
    markdownContent = replaceAnchorString(markdownContent);

    // 写入 Markdown 文件
    try (BufferedWriter writer = new BufferedWriter(
      new FileWriter(mdFileName))) {
      writer.write(markdownContent.toString());
    }

    System.out.println("Markdown 文件已生成：" + mdFileName); //
    log.info("Markdown 文件已生成：{}", mdFileName);

    // 创建txt文件
    MarkdownToTxtConverter.convert(mdFileName, txtFileName);

    // 创建 ZIP 文件
    try (FileOutputStream fos = new FileOutputStream(zipFileName);
      ZipOutputStream zos = new ZipOutputStream(fos)) {

      // 添加 Markdown 文件到 ZIP 文件
      addToZipFile(mdFileName, zos);

      // 添加 images 文件夹到 ZIP 文件
      addToZipFolder(folderPath + File.separator + "images", zos);
    }

    log.info("ZIP 文件已生成：{}", zipFileName);

    return zipFileName;
  }

  private static void addToZipFolder(String folderName, ZipOutputStream zos)
    throws IOException {
    File folder = new File(folderName);
    System.out.println("folderName: " + folderName);
    File[] files = folder.listFiles();
    if (files != null) {
      for (File file : files) {
        if (file.isDirectory()) {
          addToZipFolder(file.getAbsolutePath(), zos);
        } else {
          String zipEntryName =
            "images/" + file.getName(); // 修改路径，放在 images 文件夹中
          addToZipFile(file.getAbsolutePath(), zipEntryName, zos);
        }
      }
    }
  }

  private static void addToZipFile(String fileName, String zipEntryName,
    ZipOutputStream zos) throws IOException {
    File file = new File(fileName);
    System.out.println("Adding: " + file.getName());
    FileInputStream fis = new FileInputStream(file);
    ZipEntry zipEntry = new ZipEntry(zipEntryName); // 使用传入的 zipEntryName
    zos.putNextEntry(zipEntry);

    byte[] bytes = new byte[1024];
    int length;
    while ((length = fis.read(bytes)) >= 0) {
      zos.write(bytes, 0, length);
    }

    fis.close();
  }

  public static void deleteDirectory(File dir) {
    if (dir.isDirectory()) {
      File[] children = dir.listFiles();
      if (children != null) {
        for (File child : children) {
          deleteDirectory(child);
        }
      }
    }
    // The directory is now empty so now it can be deleted
    dir.delete();
  }

  public static String addImagesPrefix(String html) {
    Document document = Jsoup.parse(html);
    Elements imgTags = document.select("img");

    for (Element imgTag : imgTags) {
      String src = imgTag.attr("src");
      System.out.println("src: " + src);
      src = addImagesPrefix2(src);
      imgTag.attr("src", src);
    }

    return document.html();
  }

  public static String addImagesPrefix2(String originalString) {
    if (originalString.contains("..")) {
      return originalString.replace("..", "/images/");
    } else {
      return "/images/" + originalString;
    }
  }

  public static StringBuilder replaceAnchorString(
    StringBuilder originalString) {
    String regex = "\\{#.*?\\}";
    String replacement = "";
    return new StringBuilder(
      originalString.toString().replaceAll(regex, replacement));
  }

//    public static StringBuilder replaceImagesLinks(
//        StringBuilder originalString) {
//        String replacementString = "(/images/images/";
//        String newString = "(/images/";
//
//        //      String tempStr = originalString.toString()
//        //            .replace(newString, replacementString);
//        // 第一次替换
//        String tempStr = originalString.toString()
//            .replace(replacementString, newString);
//
//        replacementString = "(/images/images//";
//        newString = "(/images/";
//        // 第二次替换
//        tempStr = originalString.toString()
//            .replace(replacementString, newString);
//        return new StringBuilder(tempStr);
//    }

  public static String cleanHtmlContent(String htmlContent) {
    Parser parser = Parser.builder().build();
    FlexmarkHtmlConverter converter = FlexmarkHtmlConverter.builder()
      .build();
    return converter.convert(htmlContent);
  }

  public static void saveImage(Resource image, String imageDir)
    throws IOException {
    InputStream inputStream = new ByteArrayInputStream(image.getData());
    File imageFile = new File(imageDir, image.getHref());
    imageFile.getParentFile().mkdirs();

    try (FileOutputStream fos = new FileOutputStream(imageFile)) {
      byte[] buffer = new byte[1024];
      int length;
      while ((length = inputStream.read(buffer)) > 0) {
        fos.write(buffer, 0, length);
      }
    }

    System.out.println("图片已保存: " + imageFile.getAbsolutePath());
  }

  public static String getChapterTitle(String htmlContent) {
    Document document = Jsoup.parse(htmlContent);
    Element titleElement = document.select("title").first();
    String title = "";
    if (titleElement != null) {
      title = titleElement.text();
      System.out.println("Title: " + title);
    } else {
      System.out.println("Title not found.");
    }
    return title;
  }

  public static Resource getCoverImage(Book book) {
    Resource coverImage = null;
    for (Resource resource : book.getResources().getAll()) {
      if (resource.getMediaType().toString().startsWith("image")
        && resource.getHref()
        .equals(book.getCoverImage().getHref())) {
        coverImage = resource;
        break;
      }
    }
    return coverImage;
  }

  private static void addToZipFile(String fileName, ZipOutputStream zos)
    throws IOException {
    File file = new File(fileName);
    System.out.println("Adding: " + file.getName());
    FileInputStream fis = new FileInputStream(file);
    ZipEntry zipEntry = new ZipEntry(file.getName());
    zos.putNextEntry(zipEntry);

    byte[] bytes = new byte[1024];
    int length;
    while ((length = fis.read(bytes)) >= 0) {
      zos.write(bytes, 0, length);
    }

    fis.close();
  }
}
