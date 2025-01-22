package com.coderdream.util.sentence;

import cn.hutool.core.util.StrUtil;
import com.coderdream.entity.DialogSingleEntity;
import com.coderdream.util.cd.CdStringUtil;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 脚本解析工具类，用于将字符串列表解析为 ScriptSingleEntity 列表
 */
@Slf4j
public class ScriptParser {


  /**
   * 将字符串列表解析为 ScriptSingleEntity 列表
   *
   * @param lines 待解析的字符串列表
   * @return 解析后的 ScriptSingleEntity 列表，如果解析失败，则返回 null
   */
  public static List<DialogSingleEntity> parseScript(List<String> lines) {
    LocalDateTime startTime = LocalDateTime.now();
    try {
      // 校验1：是否为偶数行
      if (lines.size() % 2 != 0) {
        log.error("脚本行数不是偶数行，无法解析。行数: {}", lines.size());
        return null;
      }

      List<DialogSingleEntity> dialogList = new ArrayList<>();
      for (int i = 0; i < lines.size(); i++) {
        String line = lines.get(i);
        if (StrUtil.isEmpty(line)) {
          continue;
        }
        // 校验2：是否存在不含中英文冒号的行或者有多个中英文冒号
        if (!CdStringUtil.hasOneColon(line)) {
          log.error(
            "第{}行格式错误，必须有且仅有一个冒号，且冒号必须为中英文冒号: {}",
            i + 1, line);
          return null;
        }
        //
        DialogSingleEntity dialogEntity = DialogParser.parseLine(line);
        if (dialogEntity != null) {
          dialogList.add(dialogEntity);
        } else {
          log.error("解析行 {} 失败，内容: {}", i + 1, line);
          return null;
        }
//        log.debug("解析行 {}，host: {}, content: {}", i + 1, host, content);
      }
      log.info("脚本解析成功。解析行数：{}", lines.size());
      return dialogList;
    } finally {
      LocalDateTime endTime = LocalDateTime.now();
      Duration duration = Duration.between(startTime, endTime);
      long seconds = duration.getSeconds();
      long millis = duration.toMillis() % 1000;
      log.info("方法耗时: {} 分 {} 秒 {} 毫秒", seconds / 60, seconds % 60,
        millis);
    }
  }
}
