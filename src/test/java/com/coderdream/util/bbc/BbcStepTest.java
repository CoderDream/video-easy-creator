package com.coderdream.util.bbc;

import cn.hutool.core.io.FileUtil;
import com.coderdream.util.cd.CdFileUtil;
import com.coderdream.util.translate.TranslateUtil;
import java.io.File;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

//@SpringBootTest
//@J
@Slf4j
public class BbcStepTest {

//    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(BbcStepTest.class);

    private List<String> NUMBER_LIST;

    @BeforeEach
    void init() {
        String folderPath =
            CdFileUtil.getResourceRealPath() + File.separatorChar + "data" + File.separatorChar + "bbc"
                + File.separatorChar;

        NUMBER_LIST = FileUtil.readLines(folderPath + File.separator + "todo.txt", "UTF-8");
//        list = new ArrayList<>(Arrays.asList("test1", "test2"));
    }


    //    @Before
//    public void setup() {
//
//
//        String folderPath =
//            CdFileUtils.getResourceRealPath() + File.separatorChar + "data" + File.separatorChar + "bbc"
//                + File.separatorChar;
//
//    }
//    @Test
//    public void testStep000() {
//        for (String num : NUMBER_LIST) {
//            String folderName = "" + num;
//            System.out.println(folderName);
////            log.error("error "+ folderName);
//////            ProcessRawTxtUtil.processRawTxtSrt(folderName);
////        ProcessScriptUtil.process(folderName);
////        TranslateUtil.process(folderName);
////        TranslateUtil.mergeScriptContent(folderName);
//        }
//    }


    /**
     * 第一步：生成voc（英文版词汇）和对话脚本
     */
    @Test
    @Order(1)
    public void testStep00() {
        // D:\04_GitHub\video-easy-creator\src\main\resources\data\bbc\todo.txt
        for (String num : NUMBER_LIST) {
            String folderName = "" + num;
//            ProcessRawTxtUtil.processRawTxtSrt(folderName);
            // 生成 script_dialog.txt 和 voc.txt
//            ProcessScriptUtil.process(folderName);
            // 生成 script_dialog_cn.txt 和 voc_cn.txt
//            TranslateUtil.process(folderName);
            TranslateUtil.mergeScriptContent(folderName);
        }
    }

    /**
     * 第一步：字幕初剪
     */
    @Test
    @Order(2)
    public void testStep01() {
        for (String num : NUMBER_LIST) {
            String folderName = "" + num;
            GenSrtUtil.processScriptDialog(folderName);
        }
    }
//
//    /**
//     * 第四步：翻译核心词汇表，生成 voc_cn.txt 文件
//     */
//    @Test
//    @Order(4)
//    public void testStep04() {
//        for (String num : NUMBER_LIST) {
//            String folderName = "" + num;
//            DictUtils.processVoc(folderName);
//        }
//    }
//
//    /**
//     *  第五步：生成ppt和待填充《核心词汇表》的文件
//     */
//    @Test
//    @Order(5)
//    public void testStep05() {
//        int i = 0;
//        String fileName = "script_raw";
//        List<String> stringList = TranslateUtil.translateTitle(NUMBER_LIST, fileName);
//        for (String num : NUMBER_LIST) {
//            String folderName = "" + num;
//            GenSixMinutePptx.genPpt(folderName, stringList.get(i));
//            i++;
//        }
//    }
//
//    /**
//     * 第三步：生成完整词汇表
//     */
//    @Test
//    @Order(8)
//    public void testStep08() {
//        for (String num : NUMBER_LIST) {
//            WordCountUtil wordCountUtil;
////            String folderName = "" + num;
////            log.info("num:{}", num);
////            WordCountUtil.genVocTable(folderName);
//        }
//    }

    /**
     * 第三步：生成核心词汇表
     */
//    @Test
//    public void testStep09() {
//        for (String num : NUMBER_LIST) {
//            String folderName = "" + num;
//            CoreWordUtil.genCoreWordTable(folderName);
//        }
//    }

//    /**
//     * 第三步：生成高级词汇表
//     */
//    @Test
//    public void testStep10() {
//        for (String num : NUMBER_LIST) {
//            String folderName = "" + num;
//            AdvancedWordUtil.genCoreWordTable(folderName);
//        }
//    }

}
