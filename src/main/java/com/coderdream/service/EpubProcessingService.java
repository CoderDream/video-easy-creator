package com.coderdream.service;

import com.coderdream.util.epub.EpubToMarkdownUtil;
import java.io.File;
import java.io.IOException;
import org.springframework.stereotype.Service;

@Service
public class EpubProcessingService {

    public String processEpubToZip(File epubFile) throws IOException {
        String folderPath = epubFile.getParent();
        String pureFileName = epubFile.getName().substring(0, epubFile.getName().lastIndexOf("."));

        // 调用之前的工具类方法进行处理（假设EpubToMarkdownUtil类的process方法实现了核心逻辑）
        String zipFileName = EpubToMarkdownUtil.process(folderPath, pureFileName);

        // 根据处理后的文件名构建生成的zip文件对象并返回
        return zipFileName;
    }
}
