package com.coderdream.util.txt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.coderdream.entity.DialogSingleEntity;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;


/**
 * TextSingleUtil 单元测试类
 */
public class TextSingleUtilTest {


  /**
   * 文本文件的路径
   */
  private static final String filePath = "D:\\0000\\EnBook001\\商务职场英语口语900句";

  /**
   * 文本文件的文件名
   */
  private static final String fileName = "商务职场英语口语900句V1_ch02_v1.txt";


  /**
   * 测试 extractHosts 方法
   */
  @Test
  void testExtractHosts() throws IOException {
    // 调用 extractHosts 方法，生成 host.txt
    TextSingleUtil.extractHosts(filePath, fileName);

    // 验证 host.txt 是否存在
    Path hostFilePath = Paths.get("D:\\0000\\EnBook001\\商务职场英语口语900句",
      "host.txt");
    assertTrue(Files.exists(hostFilePath), "host.txt 文件应该存在");

    // 验证 host.txt 内容不为空
    List<String> lines = Files.readAllLines(hostFilePath);
    assertTrue(!lines.isEmpty(), "host.txt 文件内容不应该为空");

    // 验证提取结果是否去重
    Set<String> expectedHosts = Files.readAllLines(
        Paths.get("D:\\0000\\EnBook001\\商务职场英语口语900句",
          "商务职场英语口语900句V1_ch02_v1.txt")).stream()
      .filter(line -> !line.trim().isEmpty()) //过滤空行
      .map(line -> {
        int colonIndex = findFirstColonIndex(line);
        if (colonIndex != -1) {
          return line.substring(0, colonIndex).trim();
        }
        return null;
      })
      .filter(Objects::nonNull)
      .collect(Collectors.toSet());

    assertEquals(expectedHosts.size(), lines.size(),
      "提取的结果应该去重，并且数量正确");
    assertTrue(lines.containsAll(expectedHosts),
      "提取结果应该包含所有预期的host");
  }

  /**
   * 测试 parseDialogs 和 writeDialogsToFile 方法
   *
   * @throws IOException
   */
  @Test
  void testParseAndWriteDialogs() throws IOException {
    // 解析对话信息
    List<DialogSingleEntity> dialogs = TextSingleUtil.parseDialogs(filePath, fileName);
    assertNotNull(dialogs, "对话列表不应该为空");
    assertTrue(dialogs.size() > 0, "对话列表应该包含对话内容");

    //写入对话信息到文件
    TextSingleUtil.writeDialogsToFile(dialogs, filePath);

    // 验证 dialog.txt 文件是否存在
    Path dialogFilePath = Paths.get(
      "D:\\0000\\EnBook001\\商务职场英语口语900句", "dialog.txt");
    assertTrue(Files.exists(dialogFilePath), "dialog.txt 文件应该存在");

    // 验证文件内容不为空
    List<String> lines = Files.readAllLines(dialogFilePath);
    assertTrue(!lines.isEmpty(), "dialog.txt 文件内容不应该为空");

    // 可选：验证提取结果是否符合预期（根据实际情况添加）
    for (DialogSingleEntity dialog : dialogs) {
      assertNotNull(dialog.getHostEn(), "hostEn 不能为空");
      assertNotNull(dialog.getHostCn(), "hostCn 不能为空");
      assertNotNull(dialog.getHostEn(), "hostEn 不能为空");
      assertNotNull(dialog.getContentEn(), "contentEn 不能为空");
      assertNotNull(dialog.getContentCn(), "contentCn 不能为空");
      assertNotNull(dialog.getContentEn(), "contentEn 不能为空");
      assertNotNull(dialog.getContentCn(), "contentCn 不能为空");
      System.out.println("Host A En: " + dialog.getHostEn());
      System.out.println("Host A Cn: " + dialog.getHostCn());
      System.out.println("Content A En: " + dialog.getContentEn());
      System.out.println("Content A Cn: " + dialog.getContentCn());
      System.out.println("Host B En: " + dialog.getHostEn());
      System.out.println("Host B Cn: " + dialog.getHostCn());
      System.out.println("Content B En: " + dialog.getContentEn());
      System.out.println("Content B Cn: " + dialog.getContentCn());
      System.out.println("-----------------------------");
    }
  }

  /**
   * 测试 parseDialogs 和 writeDialogsToFile 方法
   *
   * @throws IOException
   */
  @Test
  void testParseAndWriteDialogs_02() throws IOException {
    // 解析对话信息
    List<DialogSingleEntity> dialogs = TextSingleUtil.parseDialogs(filePath, fileName);
    assertNotNull(dialogs, "对话列表不应该为空");
    assertTrue(dialogs.size() > 0, "对话列表应该包含对话内容");

    //写入对话信息到文件
    TextSingleUtil.writeDialogsToFile(dialogs, fileName);

    // 验证 dialog.txt 文件是否存在
    Path dialogFilePath = Paths.get(
      "D:\\0000\\EnBook001\\商务职场英语口语900句", "dialog.txt");
    assertTrue(Files.exists(dialogFilePath), "dialog.txt 文件应该存在");

    // 验证文件内容不为空
    List<String> lines = Files.readAllLines(dialogFilePath);
    assertTrue(!lines.isEmpty(), "dialog.txt 文件内容不应该为空");

    // 可选：验证提取结果是否符合预期（根据实际情况添加）
    for (DialogSingleEntity dialog : dialogs) {
      assertNotNull(dialog.getHostEn(), "hostEn 不能为空");
      assertNotNull(dialog.getHostCn(), "hostCn 不能为空");
      assertNotNull(dialog.getHostEn(), "hostEn 不能为空");
      assertNotNull(dialog.getContentEn(), "contentEn 不能为空");
      assertNotNull(dialog.getContentCn(), "contentCn 不能为空");
      assertNotNull(dialog.getContentEn(), "contentEn 不能为空");
      assertNotNull(dialog.getContentCn(), "contentCn 不能为空");
      System.out.println("Host A En: " + dialog.getHostEn());
      System.out.println("Host A Cn: " + dialog.getHostCn());
      System.out.println("Content A En: " + dialog.getContentEn());
      System.out.println("Content A Cn: " + dialog.getContentCn());
      System.out.println("Host B En: " + dialog.getHostEn());
      System.out.println("Host B Cn: " + dialog.getHostCn());
      System.out.println("Content B En: " + dialog.getContentEn());
      System.out.println("Content B Cn: " + dialog.getContentCn());
      System.out.println("-----------------------------");
    }
  }

  /**
   * 测试 extractSimpleSentences 方法
   *
   * @throws IOException
   */
  @Test
  void testWriteSentenceToFile() throws IOException {
    TextSingleUtil.writeSentenceToFile(filePath, fileName);
    // 验证 dialog_single.txt 文件是否存在
    Path dialogSingleFilePath = Paths.get(
      "D:\\0000\\EnBook001\\商务职场英语口语900句", "dialog_single.txt");
    assertTrue(Files.exists(dialogSingleFilePath),
      "dialog_single.txt 文件应该存在");

    // 验证文件内容不为空
    List<String> lines = Files.readAllLines(dialogSingleFilePath);
    assertTrue(!lines.isEmpty(), "dialog_single.txt 文件内容不应该为空");
  }

  /**
   * 查找字符串中第一个中文或英文冒号的索引位置
   *
   * @param line
   * @return
   */
  private int findFirstColonIndex(String line) {
    int chineseColonIndex = line.indexOf("：");
    int englishColonIndex = line.indexOf(":");
    if (chineseColonIndex == -1) {
      return englishColonIndex;
    }
    if (englishColonIndex == -1) {
      return chineseColonIndex;
    }
    return Math.min(chineseColonIndex, englishColonIndex);
  }
}