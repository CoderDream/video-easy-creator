package com.coderdream.util.excel;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;

import com.coderdream.entity.WordInfoEntity;
import com.coderdream.util.BaseUtils;
import com.coderdream.util.CommonUtil;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * @author CoderDream
 */
@Slf4j
public class CdExcelUtil {
    public static List<WordInfoEntity> genWordInfoEntityList(String filePath, String sheetName) {
//        String dateStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
//        String path = BaseUtils.getPath();
//        String fileName = File.separator + path + File.separator + dateStr + ".xlsx";
//        ExcelReader reader = ExcelUtil.getReader(FileUtil.file(fileName), "Sheet1");
        ExcelReader reader = ExcelUtil.getReader(FileUtil.file(filePath), sheetName);
        // 单词	英音	美音	释义	等级
        reader.addHeaderAlias("单词", "word");
        reader.addHeaderAlias("英音", "uk");
        reader.addHeaderAlias("美音", "us");
        reader.addHeaderAlias("释义", "comment");
        reader.addHeaderAlias("等级", "level");
        reader.addHeaderAlias("次数", "times");
        List<WordInfoEntity> recommendAppList = reader.readAll(WordInfoEntity.class);
        for (WordInfoEntity wordInfo : recommendAppList) {
            wordInfo.setWord(wordInfo.getWord().toLowerCase());
            wordInfo.setComment(wordInfo.getComment().replaceAll("\n", ";"));
            wordInfo.setLevelStr(wordInfo.getLevel());
        }
        reader.close();

        return recommendAppList;
    }

    /**
     * @return
     */
    public static List<WordInfoEntity> getAdvancedWordList(String folderName) {
        String fileName = folderName + "_完整词汇表";
        String filePath = CommonUtil.getFullPathFileName(folderName, fileName, ".xlsx");

      return CdExcelUtil.genWordInfoEntityList(filePath, "四六级及以上");
    }
}

