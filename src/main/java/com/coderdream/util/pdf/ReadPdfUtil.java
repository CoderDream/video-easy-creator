package com.coderdream.util.pdf;

//import com.itextpdf.io.source.RandomAccessFileOrArray;
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.PdfReader;
//import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
//import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
//import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.coderdream.util.CommonUtil;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.file.PdfFileFinder;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author CoderDream
 */
public class ReadPdfUtil {

  public static void main(String[] args) {

//    String fileName = "D:\\14_LearnEnglish\\6MinuteEnglish\\2022\\221110\\221110_controlling_the_weather.pdf";
//    fileName = "D:\\14_LearnEnglish\\6MinuteEnglish\\2022\\220630\\220630_science.pdf";
//
//    fileName = "D:\\14_LearnEnglish\\6MinuteEnglish\\2023\\231207\\231207_invasive_species_why_not_eat_them.pdf";
//        String string = readPdfByPage(fileName);
//        String[] arr = string.split("\n");
//
//        System.out.println(arr);

//        PdfReader pr = null;
//        try {
//            pr = new PdfReader(fileName);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        PdfDocument pd = new PdfDocument(pr);
//        LocationTextExtractionStrategy strategy = new LocationTextExtractionStrategy();
//        PdfCanvasProcessor parser = new PdfCanvasProcessor(strategy);
//        parser.processPageContent(pd.getPage(1));
//        String text = strategy.getResultantText();
//        System.out.println(text);
    String folderName = "170209";
    String folderPath = CommonUtil.getFullPath(folderName);
    String fileName = PdfFileFinder.findPdfFileName(folderName);
    String fileNameWithPath = folderPath + File.separator + fileName;
//    List<String> stringList = getStringList(folderPath, fileName);
//    for (String str : stringList) {
//      System.out.println(str);
//    }

    ReadPdfUtil.genScriptTxt(folderName, fileName);
  }

  public static final String STR_ONE = "6 Minute English ©";

  public static final String STR_TWO = "bbclearningenglish.com Page";

  public static void genScriptTxt(String folderName) {
    String fileName = PdfFileFinder.findPdfFileName(folderName);
    genScriptTxt(folderName, fileName);
  }

  public static void genScriptTxt(String folderName, String fileName) {
    String folderPath = CommonUtil.getFullPath(folderName);
    List<String> stringList = getStringList(folderPath, fileName);
    // 文本末尾补空行
    if (CollectionUtil.isNotEmpty(stringList) && StrUtil.isNotEmpty(
      stringList.get(stringList.size() - 1))) {
      stringList.add("");
    }

    String srcFileNameCn =
      CommonUtil.getFullPath(folderName) + fileName.substring(0, 6) + "_script"
        + ".txt";
    if (CdFileUtil.isFileEmpty(srcFileNameCn)) {
      CdFileUtil.writeToFile(srcFileNameCn, stringList);
    }
  }

  public static List<String> getStringList(String folderName, String fileName) {
    String fileNameWithPath = folderName + fileName;
    String string = readPdfByPage(fileNameWithPath);
    String[] arr = string.split("\n");
    // 写入原始文本到文件
    writeToRawTextFile(folderName, fileName, arr);

    List<String> stringList = new ArrayList<>();
    int length = arr.length;
    int flagIdx = 0;
    for (int i = 0; i < length; i++) {
      String str = arr[i];
      // 找到后开始新增，过滤掉6行，包括 自己及前2行和后3行
      if (str.contains(STR_ONE)) {

        if (flagIdx == 0) {
          for (int j = 0; j < i - 2; j++) {
            stringList.add(arr[j]);
          }
          i += 4;
          flagIdx = i;
        } else {
          System.out.println("flagIdx: " + flagIdx);
          for (int j = flagIdx; j < i - 2; j++) {
            stringList.add(arr[j]);
          }

          i += 4;
          flagIdx = i;
        }
      }
    }

    System.out.println(stringList);

    return stringList;
  }

  // © British Broadcasting Corporation 2017
  //6 Minute English

  public static final String STR_THREE = "©";

  public static final String STR_FOUR = "6 Minute English";

  private static void writeToRawTextFile(String folderName, String fileName,
    String[] arr) {
    List<String> rawStringList = new ArrayList<>();
    for (String str : arr) {
      if (!str.startsWith(STR_ONE) && !str.startsWith(STR_TWO)
        && !str.startsWith(STR_THREE) && !str.startsWith(STR_FOUR)) {
        rawStringList.add(str);
      }
    }
    String rawFileNameWithPath =
      folderName + fileName.substring(0, 6) + "_pdf_raw" + ".txt";
    if (CdFileUtil.isFileEmpty(rawFileNameWithPath)) {
      CdFileUtil.writeToFile(rawFileNameWithPath, rawStringList);
    }
  }

  /**
   * 用来读取pdf文件
   *
   * @param fileName
   * @return
   * @throws IOException
   */
  public static String readPdfByPage(String fileName) {
    String result = "";
    File file = new File(fileName);
    FileInputStream in = null;
    try {
      in = new FileInputStream(fileName);
      // 新建一个PDF解析器对象
      PdfReader reader = new PdfReader(fileName);
      reader.setAppendable(true);
      // 对PDF文件进行解析，获取PDF文档页码
      int size = reader.getNumberOfPages();
      for (int i = 1; i < size + 1; i++) {
        //一页页读取PDF文本
        String pageStr = PdfTextExtractor.getTextFromPage(reader, i);
        System.out.println(pageStr);
        result += pageStr + "\n";// + "PDF解析第"+ (i)+ "页\n";
//                i++;
      }
      reader.close();
    } catch (Exception e) {
      System.out.println(
        "读取PDF文件" + file.getAbsolutePath() + "生失败！" + e);
      e.printStackTrace();
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e1) {
        }
      }
    }
//        System.out.println(result);
    return result;
  }
}
