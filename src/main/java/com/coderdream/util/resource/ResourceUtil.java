package com.coderdream.util.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class ResourceUtil {

    @Autowired
    private ResourceLoader resourceLoader;

    /**
     * 获取 resources 目录下的文件内容（作为字符串）。
     *
     * @param resourcePath 相对于 resources 目录的路径，例如 "config/data.txt"
     * @return 文件内容字符串，如果文件不存在或读取失败，返回 null
     */
    public String getResourceAsString(String resourcePath) {
        try {
            Resource resource = resourceLoader.getResource("classpath:" + resourcePath);
            if (resource.exists()) {
                // 推荐使用 FileCopyUtils (Spring 提供) 读取资源内容
                return FileCopyUtils.copyToString(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            } else {
                return null; // 或抛出异常，根据您的需求
            }
        } catch (IOException e) {
            // 处理异常，例如记录日志
            log.error("Error reading resource: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
      * 获取 resources 目录下的文件（作为 File 对象）
      * @param resourcePath
      * @return
      */
    public File getResourceAsFile(String resourcePath) {
        try {
            Resource resource = resourceLoader.getResource("classpath:" + resourcePath);
            if (resource.exists()) {
                return resource.getFile(); // 获取 File 对象
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

     /**
      * 获取 resources目录下的文件（作为 InputStream）
      * @param resourcePath
      * @return
      */
    public  InputStream getResourceAsStream(String resourcePath) {
        try{
            Resource resource = resourceLoader.getResource("classpath:" + resourcePath);
            if (resource.exists()) {
                return resource.getInputStream(); // 获取 InputStream
            } else {
                return null;
            }
        }catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
