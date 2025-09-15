package com.coderdream.util.process.bbc;

import com.coderdream.entity.WordInfo;
import com.coderdream.util.CommonUtil;
import com.coderdream.util.cd.CdExcelUtil;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.excel.MakeExcel;
import java.io.File;
import java.util.List;

/**
 * @author CoderDream
 */
public class AdvancedWordUtil {

    public static void main(String[] args) {
        String folderName = "181122";
        AdvancedWordUtil.genAdvancedWordTable(folderName, "D");
    }

    public static File genAdvancedWordTable(String folderName, String templateType) {
        List<WordInfo> wordInfoList = getAdvancedWordList(folderName);
        String folderPath =
            CdFileUtil.getResourceRealPath() + File.separatorChar + "data" + File.separatorChar + "dict";
        String templateFileName = folderPath + File.separator + "高级词汇表.xlsx";

        switch (templateType) {
            case "A":
                templateFileName = folderPath + File.separator + "高级词汇表 - 联想.xlsx";
                break;
            case "B":
                templateFileName = folderPath + File.separator + "高级词汇表 - 曲面屏.xlsx";
                break;
            case "C":
                templateFileName = folderPath + File.separator + "高级词汇表 - 戴尔.xlsx";
                break;
            case "D":
                templateFileName = folderPath + File.separator + "高级词汇表 - 三星.xlsx";
                break;
            default:
                templateFileName = folderPath + File.separator + "高级词汇表.xlsx";
                break;
        }
        //

        // WordInfo

        // 方案1 一下子全部放到内存里面 并填充
        String excelFileName = CommonUtil.getFullPathFileName(folderName, folderName, "_高级词汇表.xlsx");
        String sheetName = "四六级及以上";

        MakeExcel.fillWordEntityList(templateFileName, excelFileName, sheetName, wordInfoList);

        return new File(excelFileName);
    }

    /**
     * @return
     */
    public static List<WordInfo> getAdvancedWordList(String folderName) {
        String fileName = folderName + "_完整词汇表";
        String filePath = CommonUtil.getFullPathFileName(folderName, fileName, ".xlsx");
        List<WordInfo> wordEntityList = CdExcelUtil.genWordInfoList(filePath, "四六级及以上");

        return wordEntityList;
    }

}
