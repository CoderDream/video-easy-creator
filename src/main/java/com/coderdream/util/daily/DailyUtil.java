package com.coderdream.util.daily;

import com.coderdream.util.CommonUtil;
import com.coderdream.util.gemini.TranslationUtil;
import com.coderdream.util.wechat.MarkdownFileGenerator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DailyUtil {

   public static void process(String folderName, String title ) {
//     String folderName = "250102";
     String folderPath = CommonUtil.getFullPath(folderName);
     String fileName = folderPath + folderName + "_中英双语对话脚本.txt";
     TranslationUtil.genDescription(fileName);

//     String folderName = "123456";
//     String title = "【BBC六分钟英语】哪些人会购买高端相机？";
     MarkdownFileGenerator.genWechatArticle(folderName, title);

  }
}
