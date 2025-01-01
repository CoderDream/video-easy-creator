package com.coderdream.util.mdict.dict.util;

import com.coderdream.util.mdict.dict.model.CompressedRecord;
import com.coderdream.util.mdict.dict.model.Dictionary;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class DictionaryQueried1 {

  //  private static HashMap<String, Dictionary> dicts = new HashMap<String, Dictionary>();
//
//  static {
//
//  }
  private static final String DICT_FOLDER_PATH = "D:\\java_output\\dict\\";

  public static String query(String query) {
    return query("牛津高阶8简体.mdx", query);
  }

  public static String query(String dictName, String query) {
    String filePath = DICT_FOLDER_PATH + dictName;
    // "C:\\Users\\CoderDream\\Downloads\\ABDM\\牛津高阶英汉双解词典（第10版）V3.mdx";
    //"E:\\BaiduPan\\0002_词典共享\\牛津高阶英汉双解词典第10版完美版\\oaldpe.mdx";
    //  "E:\\BaiduPan\\0002_词典共享\\剑桥在线英汉双解词典完美版\\cdepe.mdx";
    //  "E:\\BaiduPan\\好用的词典~\\牛津高阶8简体spx\\牛津高阶8简体.mdx";
    // "D:/MDictPC/doc/牛津高阶8简体.mdx"; 牛津高阶英汉双解词典（第10版）V3.mdx
    Dictionary dict = null;

    try {
      FileInputStream fins = new FileInputStream(filePath);
      MdxFileParser parser = new MdxFileParser();
      dict = parser.parse(fins);
//      dicts.put("牛津高阶8简体", dict);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    String result;
//    Dictionary dict = dicts.get(dictName);
    if (dict == null) {
      result = "词典不存在！";
    } else {
      List<String> keys = dict.getOriKeys();
      HashMap<Long, CompressedRecord> recordsMap = dict.getRecords();

      //定位到词条或者最相近的词条
      int start = 0, end = keys.size(), mid;
      while (end - start > 1) {
        mid = (start + end) / 2;
        if (end > start) {
          String midWord = keys.get(mid);
          int flag = query.compareTo(midWord);
          if (flag > 0) {
            start = mid;
          } else if (flag < 0) {
            end = mid;
          } else {
            start = mid;
            break;
          }
        } else {
          break;
        }
      }

      //确定要显示的词，拿到偏移量
      String item = keys.get(start);
      Long wordOffset = dict.getOffsets().get(item);

      //根据偏移量定位到块
      long pre = 0;
      Set<Long> offSets = dict.getRecords().keySet();
      for (Long offSet : offSets) {
        if (wordOffset < offSet) {
          break;
        } else if (wordOffset >= offSet) {
          pre = offSet;
          continue;
        }
      }

      //拿出记录块，从里面解压出对应的词条
      long position = wordOffset - pre;
      CompressedRecord record = recordsMap.get(pre);
      result = record.getString(position);
    }
    return result;
  }

  public static void main(String[] args) {
    String word = "a realistic possibility";
    String record = DictionaryQueried1.query(word);
    System.out.println(record);
  }
}
