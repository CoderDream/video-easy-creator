package com.coderdream.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class BatchProcessUtilTest {

  @Test
  void batchProcess() {
    long startTime = System.currentTimeMillis(); // 记录开始时间
    String fileName = "CampingInvitation_02";
    fileName = "CampingInvitation_cht_03";
    File file = BatchProcessUtil.batchProcess(fileName);
//    assertNotNull(file);
//    log.info("file: {}", file);
    long endTime = System.currentTimeMillis(); // 记录视频生成结束时间
    long durationMillis = endTime - startTime; // 计算耗时（毫秒）
    log.info("视频创建成功，耗时: {}",
      CdTimeUtil.formatDuration(durationMillis));
  }
}
