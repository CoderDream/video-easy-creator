package com.coderdream.util.resource;

import java.io.File;
import java.net.URISyntaxException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResourcesSourcePathUtil {

  /**
   * 获取 src/main/resources 目录在源代码中的绝对路径（未编译）。
   *
   * @return resources 目录的绝对路径字符串，如果获取失败则返回 null
   */
  public static String getResourcesSourceAbsolutePath() {
    try {
      // 1. 获取当前类文件的 URL
      File thisClassFile = new File(
        ResourcesSourcePathUtil.class.getProtectionDomain().getCodeSource()
          .getLocation().toURI());

      // 2.  回溯到项目根目录
      File projectRoot = thisClassFile
        .getParentFile() // target
        .getParentFile() //  video-easy-creator 或则 build
        ;

      // 3. 构建 src/main/resources 目录的路径
      File resourcesDir = new File(projectRoot, "src/main/resources");

      if (resourcesDir.exists() && resourcesDir.isDirectory()) {
        return resourcesDir.getAbsolutePath();
      } else {
        log.error("未找到 src/main/resources 目录");
        return null;
      }
    } catch (URISyntaxException e) {
      log.error("获取 resources 目录路径时发生异常: {}", e.getMessage(), e);
      return null;
    }
  }

  public static void main(String[] args) {
    String resourcesPath = ResourcesSourcePathUtil.getResourcesSourceAbsolutePath();
    if (resourcesPath != null) {
      log.info("src/main/resources 目录的绝对路径（源代码）: {}", resourcesPath);
    } else {
      log.info("无法获取 resources 目录路径。");
    }
  }
}
