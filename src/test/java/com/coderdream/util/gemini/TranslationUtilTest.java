package com.coderdream.util.gemini;

import cn.hutool.core.io.FileUtil;
import com.coderdream.util.CdFileUtil;
import com.coderdream.util.CommonUtil;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class TranslationUtilTest {


  @Test
  void translate() {
    String text = "翻译成简体中文：" + "hello";
    String translate = TranslationUtil.translate(text);
    log.info("translate: {}", translate);
  }

  //

  @Test
  void translate_02() {
    String text = "帮我实现如下功能：输入是是6组英文词汇，6行为1组；第1行英文单词或词组，第2行是英文释义，第1行和第2行不要处理，按原始文本返回，第3行是对第1行的中文简明翻译，翻译结果尽量少于20个字符串，第4行是对第2行的中文翻译，第5行是用第1行进行英文造句，第6行时对第5行进行中文翻译；请根据规则补齐空行，按文本文件格式返回给我，不要任何标记，移除空行，后面的5组也是按一样方式处理；以下是6组词汇：cephalopod\nthe group of animals to which the octopus belongs\npublicity stunt\nsomething a company might do to grab your attention and promote its products\na common ancestor\na distant relative from which two different species evolved\ncomparable to\nsimilar to\nvertebrates\nanimals that have a spine\nprotean\n(adjective) adaptable and changeable";
    String translate = TranslationUtil.translate(text);
    log.info("translate: {}", translate);
  }

  @Test
  void genPhonetics_03() {
    String fileName = "D:\\0000\\EnBook001\\商务职场英语口语900句\\dialog_single.txt";
    TranslationUtil.genPhonetics(fileName);
//    log.info("translate: {}", translate);
  }


  @Test
  void genDescription() {
    String folderName = "180906";
    String folderPath = CommonUtil.getFullPath(folderName);
    String fileName = folderPath + folderName + "_中英双语对话脚本.txt";
    TranslationUtil.genDescription(fileName);
//    log.info("translate: {}", translate);
  }

}
