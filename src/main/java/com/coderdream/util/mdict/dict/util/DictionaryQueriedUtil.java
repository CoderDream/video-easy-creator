package com.coderdream.util.mdict.dict.util;

import com.coderdream.util.mdict.dict.model.CompressedRecord;
import com.coderdream.util.mdict.dict.model.Dictionary;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * 词典查询类
 */
@Slf4j
public class DictionaryQueriedUtil {

  private static final String DICT_FOLDER_PATH = "D:\\java_output\\dict\\";

  /**
   * 默认词典查询方法，使用“牛津高阶8简体.mdx”词典
   *
   * @param query 查询的词条
   * @return 查询结果
   */
  public static String query(String query) {
    return query("牛津高阶8简体.mdx", query);
  }

  /**
   * 指定词典查询方法
   *
   * @param dictName 词典名称
   * @param query    查询的词条
   * @return 查询结果
   */
  public static String query(String dictName, String query) {
    Instant startTime = Instant.now(); // 记录方法开始时间
    String filePath = DICT_FOLDER_PATH + dictName;
    Dictionary dict = null;

    try (FileInputStream fins = new FileInputStream(filePath)) {
      MdxFileParser parser = new MdxFileParser();
      dict = parser.parse(fins);
    } catch (FileNotFoundException e) {
      log.error("词典文件未找到: {}", filePath, e);
      return "词典文件未找到！";
    } catch (Exception e) {
      log.error("解析词典文件失败: {}", filePath, e);
      return "解析词典文件失败！";
    }

    String result;
    if (dict == null) {
      result = "词典不存在！";
      log.warn("词典为空: {}", dictName);
    } else {
      List<String> keys = dict.getOriKeys();
      HashMap<Long, CompressedRecord> recordsMap = dict.getRecords();

      // 定位到词条或者最相近的词条
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

      // 确定要显示的词，拿到偏移量
      String item = keys.get(start);
      Long wordOffset = dict.getOffsets().get(item);
      if (wordOffset == null) {
        log.warn("未找到词条 {} 的偏移量", item);
        return "未找到该词条！";
      }

      // 根据偏移量定位到块
      long pre = 0;
      Set<Long> offSets = dict.getRecords().keySet();
      for (Long offSet : offSets) {
        if (wordOffset < offSet) {
          break;
        } else if (wordOffset >= offSet) {
          pre = offSet;
        }
      }

      // 拿出记录块，从里面解压出对应的词条
      long position = wordOffset - pre;
      CompressedRecord record = recordsMap.get(pre);
      if (record == null) {
        log.warn("未找到偏移量 {} 对应的记录块", pre);
        return "未找到该词条！";
      }
      result = record.getString(position);
    }
    Instant finishTime = Instant.now(); // 记录方法结束时间
    long timeElapsed = Duration.between(startTime, finishTime)
      .toMillis(); // 计算方法耗时
//    log.info("查询词条 {} 耗时： {} ms, 结果：{}", query, timeElapsed, result);

    log.info("查询词条 {} 耗时： {} ms", query, timeElapsed);

    return result;
  }

  public static void main(String[] args) {

    String word = "a realistic possibility";
    word = "realistic"; // DictionaryQueriedUtil.query("realistic")
    List<String> dictNames = Arrays.asList(
      "Cambridge English-Chinese (Simplified) Dictionary.mdx"//, // OK
      //   "COCA Frequency 60000.mdx", // NOT OK
//      "Collins COBUILD (CN).mdx",// NOT OK
//      "Collins Cobuild Audio.mdx",// NOT OK
//      "oald.mdx",// NOT OK
//      "oaldpe.mdx",// NOT OK
//      "牛津高阶英汉双解词典（第10版）V3.mdx",// NOT OK
//      "牛津高阶8简体.mdx"

    );
    for (String dictName : dictNames) {
      log.info("查询词条 {} 在词典 {} 中的结果：", word, dictName);
      System.out.println(DictionaryQueriedUtil.query(dictName, word));
    }
  }
}
