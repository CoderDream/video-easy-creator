package com.coderdream.util.cd;

import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.coderdream.entity.AbbrevComplete;
import com.coderdream.entity.WordEntity;
import com.coderdream.entity.WordInfo;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * @author CoderDream
 */
@Slf4j
public class CdExcelUtil {

    public static void main(String[] args) {

//        for (int i = 0; i < 100; i++) {
//            Random r = new Random();
//            int i1 = r.nextInt(10); // 生成[0,3]区间的整数
//            System.out.print(i1 + " ");
//        }
//        String folderPath = "D:\\99_自媒体创业\\diandian";
//        List<String> allFileNames = CdFileUtils.getAllFileNames(folderPath);
//        if (CollectionUtils.isNotEmpty(allFileNames)) {
//            for (String fileName : allFileNames) {
//                log.info(fileName);
//                List<TopList> topLists = CdExcelUtils.genTopList(fileName);
//                if (CollectionUtils.isNotEmpty(topLists)) {
//                    for (TopList topList : topLists) {
//                        log.info(topList.toString());
//                    }
//                }
//            }
//        }
//        log.info("size: " + genTotalTopList().size());

//        List<String> appIdList = CdExcelUtils.genTotalTopAppIdList();
//        if (CollectionUtils.isNotEmpty(appIdList)) {
//            for (String appId : appIdList) {
//                log.info(appId);
//            }
//        }
//        m1();

//        Map<String, Set<String>> map = new LinkedHashMap<>();
//        Set<String> items1 = null;
//        Set<String> items2 = null;
//        Set<String> items3 = null;
//        Set<String> items4 = null;
//        Set<String> items5 = null;
//        Set<String> items6 = null;
//        Set<String> items7 = null;
//        Set<String> items8 = null;
//        Set<String> items9 = null;
//        String key = "";
//        String str = "";
//        List<Medicine> medicines = genMedicineList();
//        String[] strings;
//        for (Medicine medicine : medicines) {
////            System.out.println(medicine);
//
//            key = "别名";
//            items1 = map.get(key);
//            if (items1 == null) {
//                items1 = new LinkedHashSet<>();
//            }
//            str = medicine.get别名();
//            str = str.replace(" | ", "#");
//            strings = str.split("#");
//            items1.addAll(Arrays.asList(strings));
//            map.put(key, items1);
//
//            key = "作用机制";
//            items2 = map.get(key);
//            if (items2 == null) {
//                items2 = new LinkedHashSet<>();
//            }
//            str = medicine.get作用机制();
//            str = str.replace(" | ", "#");
//            strings = str.split("#");
//            items2.addAll(Arrays.asList(strings));
//            map.put(key, items2);
//
//            key = "药物类型";
//            items3 = map.get(key);
//            if (items3 == null) {
//                items3 = new LinkedHashSet<>();
//            }
//            str = medicine.get药物类型();
//            str = str.replace(" | ", "#");
//            strings = str.split("#");
//            items3.addAll(Arrays.asList(strings));
//            map.put(key, items3);
//
//            key = "在研适应症_疾病名";
//            items4 = map.get(key);
//            if (items4 == null) {
//                items4 = new LinkedHashSet<>();
//            }
//            str = medicine.get在研适应症_疾病名();
//            if (StrUtil.isNotEmpty(str)) {
//                str = str.replace(" | ", "#");
//                strings = str.split("#");
//                items4.addAll(Arrays.asList(strings));
//                map.put(key, items4);
//            }
//
//            key = "在研机构";
//            items5 = map.get(key);
//            if (items5 == null) {
//                items5 = new LinkedHashSet<>();
//            }
//            str = medicine.get在研机构();
//            str = str.replace(" | ", "#");
//            strings = str.split("#");
//            items5.addAll(Arrays.asList(strings));
//            map.put(key, items5);
//
//            key = "药物获批国家和地区";
//            items6 = map.get(key);
//            if (items6 == null) {
//                items6 = new LinkedHashSet<>();
//            }
//            str = medicine.get药物获批国家和地区();
//            if (StrUtil.isNotEmpty(str)) {
//                str = str.replace(" | ", "#");
//                strings = str.split("#");
//                items6.addAll(Arrays.asList(strings));
//                map.put(key, items6);
//            }
//
//            key = "治疗领域";
//            items7 = map.get(key);
//            if (items7 == null) {
//                items7 = new LinkedHashSet<>();
//            }
//            str = medicine.get治疗领域();
//            if (StrUtil.isNotEmpty(str)) {
//                str = str.replace(" | ", "#");
//                strings = str.split("#");
//                items7.addAll(Arrays.asList(strings));
//                map.put(key, items7);
//            }
//
//            key = "特殊审评";
//            items8 = map.get(key);
//            if (items8 == null) {
//                items8 = new LinkedHashSet<>();
//            }
//            str = medicine.get特殊审评();
//            if (StrUtil.isNotEmpty(str)) {
//                str = str.replace(" | ", "#");
//                strings = str.split("#");
//                items8.addAll(Arrays.asList(strings));
//                map.put(key, items8);
//            }
//
//            key = "靶点_基因名";
//            items9 = map.get(key);
//            if (items9 == null) {
//                items9 = new LinkedHashSet<>();
//            }
//            str = medicine.get靶点_基因名();
//            if (StrUtil.isNotEmpty(str)) {
//                str = str.replace(" x ", "#");
//                strings = str.split("#");
//                items9.addAll(Arrays.asList(strings));
//                map.put(key, items9);
//            }
//        }
//
//        System.out.println(map.size());
//
//        for (String keyStr : map.keySet()) {
//            Set<String> strings1 = map.get(keyStr);
//            Set<String> linkSet = new TreeSet<>(Comparator.naturalOrder());
//            linkSet.addAll(strings1);
//            System.out.println("### " + keyStr);
//            for (String s : linkSet) {
//                System.out.println("\t" + s);
//            }
//            System.out.println();
//        }

//        genStockInfoList(null);
    }


    //

    /**
     * @return 单词列表
     */
    public static List<WordEntity> genWordEntityList(String filePath, String sheetName) {
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
        List<WordEntity> recommendAppList = reader.readAll(WordEntity.class);
        for (WordEntity wordEntity : recommendAppList) {
            wordEntity.setWord(wordEntity.getWord().toLowerCase());
            wordEntity.setComment(wordEntity.getComment().replaceAll("\n", ";"));
        }
        reader.close();

        return recommendAppList;
    }


    public static List<WordInfo> genWordInfoList(String filePath, String sheetName) {
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
        List<WordInfo> recommendAppList = reader.readAll(WordInfo.class);
        for (WordInfo wordInfo : recommendAppList) {
            wordInfo.setWord(wordInfo.getWord().toLowerCase());
            wordInfo.setCn(wordInfo.getCn().replaceAll("\n", ";"));
            wordInfo.setLevelStr(wordInfo.getLevelStr());
        }
        reader.close();

        return recommendAppList;
    }


    public static List<AbbrevComplete> genAbbrevCompleteList(String filePath) {
        ExcelReader reader = ExcelUtil.getReader(FileUtil.file(filePath), "Sheet1");
        reader.addHeaderAlias("abbrev", "abbrev");
        reader.addHeaderAlias("complete", "complete");
        List<AbbrevComplete> abbrevCompleteList = reader.readAll(AbbrevComplete.class);
        for (AbbrevComplete abbrevComplete : abbrevCompleteList) {
            abbrevComplete.setAbbrev(abbrevComplete.getAbbrev().toLowerCase());
            abbrevComplete.setComplete(abbrevComplete.getComplete().toLowerCase());
        }

        reader.close();

        return abbrevCompleteList;
    }

}

