package com.coderdream.util.daily;

import com.coderdream.util.gemini.TranslationUtil;
import com.coderdream.util.wechat.MarkdownFileGenerator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DailyUtil {

   public static void process(String folderName, String title ) {
     TranslationUtil.genDescription(folderName);

//     String folderName = "123456";
//     String title = "【BBC六分钟英语】哪些人会购买高端相机？";
     MarkdownFileGenerator.genWechatArticle(folderName, title);
  }
}
