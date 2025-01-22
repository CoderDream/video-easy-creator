package com.coderdream.util.txt;

import com.coderdream.entity.DialogDualEntity;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;


/**
 * TextUtil2 单元测试类
 */
class TextUtil2Test {

    /**
     *  测试 extractHosts 方法
     */
    @Test
    void testExtractHosts() throws IOException {
        // 调用 extractHosts 方法，生成 host.txt
        TextUtil2.extractHosts();

        // 验证 host.txt 是否存在
        Path hostFilePath = Paths.get("D:\\0000\\EnBook001\\900", "host.txt");
        assertTrue(Files.exists(hostFilePath), "host.txt 文件应该存在");

        // 验证 host.txt 内容不为空
        List<String> lines = Files.readAllLines(hostFilePath);
        assertTrue(!lines.isEmpty(), "host.txt 文件内容不应该为空");

        // 验证提取结果是否去重
        Set<String> expectedHosts = Files.readAllLines(Paths.get("D:\\0000\\EnBook001\\900", "900V1_ch02_v1.txt")).stream()
                 .filter(line->!line.trim().isEmpty()) //过滤空行
                 .map(line -> {
                   int colonIndex = findFirstColonIndex(line);
                   if(colonIndex !=-1){
                     return line.substring(0,colonIndex).trim();
                   }
                    return null;
                 })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());


        assertEquals(expectedHosts.size(), lines.size(), "提取的结果应该去重，并且数量正确");
        assertTrue(lines.containsAll(expectedHosts), "提取结果应该包含所有预期的host");
    }

    /**
     * 测试 parseDialogs 和 writeDialogsToFile 方法
     * @throws IOException
     */
    @Test
    void testParseAndWriteDialogs() throws IOException {
        // 解析对话信息
        List<DialogDualEntity> dialogs = TextUtil2.parseDialogs();
        assertNotNull(dialogs, "对话列表不应该为空");
        assertTrue(dialogs.size() > 0, "对话列表应该包含对话内容");

        //写入对话信息到文件
        TextUtil2.writeDialogsToFile(dialogs);

        // 验证 dialog.txt 文件是否存在
        Path dialogFilePath = Paths.get("D:\\0000\\EnBook001\\900", "dialog.txt");
        assertTrue(Files.exists(dialogFilePath), "dialog.txt 文件应该存在");

        // 验证文件内容不为空
        List<String> lines = Files.readAllLines(dialogFilePath);
        assertTrue(!lines.isEmpty(), "dialog.txt 文件内容不应该为空");


         // 可选：验证提取结果是否符合预期（根据实际情况添加）
        for (DialogDualEntity dialog : dialogs) {
             assertNotNull(dialog.getHostAEn(), "hostAEn 不能为空");
             assertNotNull(dialog.getHostACn(), "hostACn 不能为空");
             assertNotNull(dialog.getHostBEn(), "hostBEn 不能为空");
           assertNotNull(dialog.getContentAEn(), "contentAEn 不能为空");
            assertNotNull(dialog.getContentACn(), "contentACn 不能为空");
           assertNotNull(dialog.getContentBEn(), "contentBEn 不能为空");
            assertNotNull(dialog.getContentBCn(), "contentBCn 不能为空");
           System.out.println("Host A En: " + dialog.getHostAEn());
           System.out.println("Host A Cn: " + dialog.getHostACn());
           System.out.println("Content A En: " + dialog.getContentAEn());
           System.out.println("Content A Cn: " + dialog.getContentACn());
           System.out.println("Host B En: " + dialog.getHostBEn());
           System.out.println("Host B Cn: " + dialog.getHostBCn());
           System.out.println("Content B En: " + dialog.getContentBEn());
           System.out.println("Content B Cn: " + dialog.getContentBCn());
           System.out.println("-----------------------------");
        }
    }

    /**
     *  查找字符串中第一个中文或英文冒号的索引位置
     * @param line
     * @return
     */
    private int findFirstColonIndex(String line) {
        int chineseColonIndex = line.indexOf("：");
        int englishColonIndex = line.indexOf(":");
        if(chineseColonIndex == -1){
            return englishColonIndex;
        }
        if(englishColonIndex == -1){
            return chineseColonIndex;
        }
        return Math.min(chineseColonIndex,englishColonIndex);
    }
}
