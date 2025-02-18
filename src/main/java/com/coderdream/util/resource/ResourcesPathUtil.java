package com.coderdream.util.resource;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

/**
 * 获取 src/main/resources 目录绝对路径的工具类 (使用 ResourceLoader)
 */
@Slf4j
public class ResourcesPathUtil {

    /**
     * 获取 src/main/resources 目录的绝对路径 (跨平台)。
     *
     * @return resources 目录的绝对路径字符串，如果获取失败则返回 null
     */
    public static String getResourcesAbsolutePath() {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("classpath:"); // 获取 classpath 根目录

        try {
            if (resource.exists()) {
                File file = resource.getFile();  //这里用getFile()，是因为classpath: 对应的一定是文件系统路径
                return file.getAbsolutePath();
            } else {
                // resources 目录不存在
                log.error("resources 目录不存在");
                return null;
            }
        } catch (IOException e) {
            // 处理异常，例如记录日志
            log.error("获取 resources 目录路径时发生异常: {}", e.getMessage(), e);
            return null;
        }
    }
     /**
      * 测试
      * @param args
      */
    public static void main(String[] args) {
        String resourcesPath = getResourcesAbsolutePath();
        if (resourcesPath != null) {
            log.info("src/main/resources 目录的绝对路径: {}", resourcesPath);
        } else {
            log.info("无法获取 resources 目录路径。");
        }
    }
}
