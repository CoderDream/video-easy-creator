package com.coderdream.util.gemini;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.coderdream.entity.VocInfo;
import com.coderdream.util.CdConstants;
import com.coderdream.util.CdFileUtil;
import com.coderdream.util.callapi.HttpUtil;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TranslationUtil {

  public static String URL =
    "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key="
      + CdConstants.GEMINI_API_KEY;

  public static String translate(String text) {
    // 使用 Hutool 的 JSONObject 构造 JSON 对象
    JSONObject jsonObject = new JSONObject();

    // 构造"contents"数组
    JSONObject content = new JSONObject();
    content.set("parts", new Object[]{
      new JSONObject().set("text", text)  // 将动态参数填入 "text"
    });

    // 将"contents"加入到最终的 JSON 对象中
    jsonObject.set("contents", new Object[]{content});

    // 打印输出生成的 JSONObject
    System.out.println(jsonObject.toStringPretty());

    String result = HttpUtil.httpHutoolPost(URL, jsonObject.toString(),
      CdConstants.PROXY_HOST,
      CdConstants.PROXY_PORT);
    log.info("{}", result);

    // 使用 Hutool 将 JSON 字符串解析为对象
    JSONObject resultObject = JSONUtil.parseObj(result);

    // 将 JSON 数据转换为 GeminiApiResponse 实体类
    GeminiApiResponse response = resultObject.toBean(GeminiApiResponse.class);

    // 打印结果验证
    System.out.println("模型版本号: " + response.getModelVersion());
    System.out.println("候选结果数量: " + response.getCandidates().size());
    System.out.println(
      "第一条内容: " + response.getCandidates().get(0).getContent().getParts()
        .get(0).getText());
    result = response.getCandidates().get(0).getContent().getParts().get(0)
      .getText();

    return result;
  }

  /**
   * 处理词汇信息，将其翻译后写入文件。
   *
   * @param vocInfoList 包含词汇信息的列表
   * @param fileName    文件名，用于存储翻译结果
   */
  public static void processVoc(List<VocInfo> vocInfoList, String fileName) {
    StringBuilder text = new StringBuilder(
      CdConstants.VOC_CN_PREFIX);  // 使用 StringBuilder 拼接字符串，避免多次创建字符串对象

    // 遍历 vocInfoList，拼接文本
    for (VocInfo vocInfo : vocInfoList) {
      text.append(vocInfo.getWord()).append("\n ");
      text.append(vocInfo.getWordExplainEn()).append("\n");
    }

    // 调用翻译方法并记录日志
    log.info("开始翻译文本内容，包含 {} 个词汇", vocInfoList.size());

    String result = translate(text.toString());  // 翻译文本

    // 记录翻译后的结果日志
    log.info("翻译完成，开始写入文件: {}", fileName);

    // 将翻译结果写入文件
    boolean writeSuccess = writeToFile(fileName, result);

    if (writeSuccess) {
      log.info("文件写入成功: {}", fileName);
    } else {
      log.error("文件写入失败: {}", fileName);
    }
  }

  public static boolean writeToFile(String newFileName, String result) {
    // 使用正则表达式直接处理换行符
    String temp = result.replaceAll("\n{2,}", "\n");  // 将连续的两个或更多的换行符替换为一个换行符
    temp = temp.replace("\n", "__");  // 替换单个换行符为 "__"

    // 使用 Arrays.asList 来避免不必要的 List.of
    List<String> list = Arrays.asList(temp.split("__"));

    // 写入文件
    boolean writeToFileResult = CdFileUtil.writeToFile(newFileName, list);
    log.info("写入文件结果: {}", writeToFileResult);
    return writeToFileResult;
  }
}
