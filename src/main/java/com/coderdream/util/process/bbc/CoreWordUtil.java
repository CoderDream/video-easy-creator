package com.coderdream.util.process.bbc;

import cn.hutool.core.io.FileUtil;
import com.coderdream.entity.CoreWordInfo;
import com.coderdream.util.CommonUtil;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.excel.MakeExcel;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author CoderDream
 */
public class CoreWordUtil {

    public static void main(String[] args) {
        String folderName = "230202";
        CoreWordUtil.genCoreWordTable(folderName);
    }

    public static void genCoreWordTable(String folderName) {
        String fileName = "voc_cn";
        List<CoreWordInfo> wordInfoList = process(folderName, fileName);
        String folderPath =
            CdFileUtil.getResourceRealPath() + File.separatorChar + "data" + File.separatorChar + "dict";
        String templateFileName = folderPath + File.separator + "核心词汇表.xlsx";

        // 方案1 一下子全部放到内存里面 并填充
        String excelFileName = CommonUtil.getFullPathFileName(folderName, folderName, "_核心词汇表.xlsx");
        String sheetName = "核心词汇表";

        MakeExcel.listFill(templateFileName, excelFileName, sheetName, wordInfoList);
    }

    public static List<CoreWordInfo> process(String folderName, String fileName) {
        List<CoreWordInfo> wordInfoList = new ArrayList<>();

        String filePath = CommonUtil.getFullPathFileName(folderName, fileName, ".txt");

        List<String> stringList = FileUtil.readLines(filePath, StandardCharsets.UTF_8);
        CoreWordInfo coreWordInfo;
        for (String string : stringList) {
            coreWordInfo = new CoreWordInfo();
            coreWordInfo.setContent(string);
            wordInfoList.add(coreWordInfo);
        }

        return wordInfoList;
    }


}
