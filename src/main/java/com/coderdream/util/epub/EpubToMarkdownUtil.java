package com.coderdream.util.epub;

// --- 保持原有的 import ---
import com.coderdream.util.cd.CdStringUtil;
import com.coderdream.util.markdown.MarkdownToTxtConverter;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import com.vladsch.flexmark.parser.Parser;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects; // 添加 Objects 用于比较
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import cn.hutool.core.util.StrUtil; // 引入 Hutool StrUtil
import lombok.extern.slf4j.Slf4j;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
// --- import 结束 ---

@Slf4j
public class EpubToMarkdownUtil {

    /**
     * 主程序入口，扫描指定文件夹下的所有 .epub 文件并进行处理。
     * @param args 命令行参数（未使用）
     */
    public static void main(String[] args) {
        // --- 配置区 ---
        // 请将此路径修改为你实际的 EPUB 文件所在的文件夹路径
        String folderPath = "C:\\Users\\CoderDream\\Documents\\000_口语_epub\\002";
        // 例如:
        // String folderPath = "D:\\my_ebooks\\";
        // String folderPath = "/home/user/epubs/";
        // --- 配置区结束 ---

        Path path = Paths.get(folderPath);

        // 检查文件夹是否存在且是目录
        if (!Files.isDirectory(path)) {
            log.error("指定的文件夹路径不存在或不是一个有效的目录: {}", folderPath);
            System.err.println("错误：指定的文件夹路径无效: " + folderPath);
            return; // 文件夹无效，退出程序
        }

        log.info("开始扫描文件夹: {}", folderPath);
        List<Path> epubFiles = null;
        try (var stream = Files.list(path)) { // 使用 try-with-resources 确保流关闭
            epubFiles = stream
                    .filter(Files::isRegularFile) // 只处理普通文件
                    .filter(p -> p.getFileName().toString().toLowerCase().endsWith(".epub")) // 不区分大小写匹配 .epub 后缀
                    .collect(Collectors.toList()); // 收集为列表
        } catch (IOException e) {
            // 处理列出目录内容时可能发生的 IO 异常
            log.error("扫描文件夹 '{}' 时发生 IO 错误: {}", folderPath, e.getMessage(), e);
            System.err.println("错误：无法扫描文件夹 " + folderPath + "，程序将终止。");
            return; // 无法扫描，退出程序
        }

        // 检查是否找到文件
        if (epubFiles.isEmpty()) {
            log.info("在文件夹 '{}' 中未找到任何 .epub 文件。", folderPath);
            return;
        }

        log.info("找到 {} 个 .epub 文件，开始处理...", epubFiles.size());
        int successCount = 0;
        int failureCount = 0;

        // 遍历并处理每个 EPUB 文件
        for (Path epubFilePath : epubFiles) {
            String fileName = epubFilePath.getFileName().toString();
            log.info("==================================================");
            log.info("准备处理文件: {}", fileName);

            // 获取文件名（不包含后缀）
            String pureFileName;
            int lastDotIndex = fileName.lastIndexOf(".");
            // 确保点不是文件名的第一个字符
            if (lastDotIndex > 0) {
                pureFileName = fileName.substring(0, lastDotIndex);
            } else {
                pureFileName = fileName; // 如果文件名没有点（不太可能）
            }
            log.debug("提取到的纯文件名: {}", pureFileName);

            // 调用核心处理方法
            String resultZipFile = EpubToMarkdownUtil.process(folderPath, pureFileName);

            // 根据返回值判断处理结果
            if (resultZipFile != null) {
                log.info("成功处理文件 '{}'，输出 ZIP: {}", fileName, resultZipFile);
                successCount++;
            } else {
                // process 方法内部已经记录了详细错误日志
                log.error("处理文件 '{}' 失败，已跳过。", fileName);
                failureCount++;
            }
            log.info("==================================================");
        }

        // 输出处理总结
        log.info("所有文件处理完毕。成功: {}, 失败: {}", successCount, failureCount);
    }


    /**
     * 处理单个 EPUB 文件，将其转换为 Markdown、TXT，并打包成 ZIP。
     * 如果在处理过程中发生任何异常，将记录错误日志并返回 null。
     *
     * @param folderPath EPUB 文件所在的文件夹路径
     * @param pureFileName EPUB 文件名（不含扩展名）
     * @return 成功则返回生成的 ZIP 文件绝对路径，失败则返回 null
     */
    public static String process(String folderPath, String pureFileName) {
        // --- 文件路径定义 ---
        String epubFilePath = Paths.get(folderPath, pureFileName + ".epub").toString();
        String mdFilePath = Paths.get(folderPath, pureFileName + ".md").toString();
        String txtFilePath = Paths.get(folderPath, pureFileName + ".txt").toString();
        String zipFilePath = Paths.get(folderPath, pureFileName + ".zip").toString();
        String imageDirPath = Paths.get(folderPath, "images").toString(); // 图片目录路径

        File epubFile = new File(epubFilePath);

        // 1. 检查 EPUB 文件是否存在
        if (!epubFile.exists() || !epubFile.isFile()) {
            log.error("EPUB 文件不存在或不是一个有效文件: {}", epubFilePath);
            return null; // 文件无效，返回 null
        }

        Book book;
        // 2. 读取和解析 EPUB 文件 (捕获所有可能的异常)
        try (FileInputStream epubFileInputStream = new FileInputStream(epubFile)) {
            // ... 读取 ...
            book = (new EpubReader()).readEpub(epubFileInputStream);
            // ... log ...

            // *** 在这里添加检查 ***
            if (book == null) { // 正常情况下 readEpub 不会返回 null，除非有更严重问题
                log.error("EPUB 文件 '{}' 读取后返回了 null Book 对象。", epubFile.getName());
                return null;
            }
            // 检查核心内容是否为空，这可能是加载出错的迹象
            if (book.getContents().isEmpty()) {
                log.warn("EPUB 文件 '{}' 读取后目录内容为空 (book.getContents() is empty)。可能加载过程中出错，请检查 epublib 的错误日志。", epubFile.getName());
                // 根据你的需求，决定是否将此视为失败
                 return null;
            }
            // 检查资源是否为空
            if (book.getResources().getAll().isEmpty()) { // 注意是 getAll()
                log.warn("EPUB 文件 '{}' 读取后资源列表为空 (book.getResources().getAll() is empty)。可能加载过程中出错，请检查 epublib 的错误日志。", epubFile.getName());
                 return null;
            }
            log.info("开始读取 EPUB 文件: {}", epubFile.getAbsolutePath());
            book = (new EpubReader()).readEpub(epubFileInputStream);
            log.info("成功读取并解析 EPUB: {}", epubFile.getName());
        } catch (Exception e) { // 捕获 IOException 和 RuntimeException
//            log.error("读取或解析 EPUB 文件 '{}' 时出错 (例如 'EOF in header', 'Wrong Local header signature' 等): {}",
//                    epubFile.getAbsolutePath(), e.getMessage(), e);
//            return null; // 读取或解析失败，返回 null
//        }
            log.error(
              ">>> 进入了 process 方法的 EPUB 读取 catch 块! <<<"); // <== 添加这行
            log.error("读取或解析 EPUB 文件 '{}' 时出错 (...): {}",
              epubFile.getAbsolutePath(), e.getMessage(), e);
            return null; // 返回 null
        }
        // --- EPUB 读取成功，开始后续处理 ---
        try {
            // 获取内容和资源
            List<Resource> htmlResources = book.getContents();
            List<Resource> images = new ArrayList<>(book.getResources().getAll());
            StringBuilder markdownContent = new StringBuilder();
            Resource coverImageResource = book.getCoverImage(); // 获取封面资源
            String coverHref = (coverImageResource != null) ? coverImageResource.getHref() : null;


            // 3. 清理和创建图片目录
            File imageDirFile = new File(imageDirPath);
            if (imageDirFile.exists()) {
                log.info("准备删除旧的图片目录: {}", imageDirPath);
                if (!deleteDirectoryRecursive(imageDirFile)) {
                     log.warn("未能完全删除旧的图片目录: {}", imageDirPath);
                     // 如果需要，可以在这里 return null
                }
            }
            // 尝试创建目录，如果创建失败且目录仍不存在
            if (!imageDirFile.mkdirs() && !imageDirFile.isDirectory()) {
                 log.error("无法创建图片目录: {}", imageDirPath);
                 return null; // 无法创建，处理失败
            }
            log.info("图片目录已准备好: {}", imageDirPath);


            // 4. 处理 HTML 内容和封面
            log.info("开始处理 HTML 内容...");
            for (Resource resource : htmlResources) {
                String resourceHref = resource.getHref();
                log.debug("处理 HTML 资源: {}", resourceHref);
                try {
                    String htmlContent = new String(resource.getData(), StandardCharsets.UTF_8);
                    // 添加图片路径前缀 (确保 addImagesPrefix 健壮)
                    htmlContent = addImagesPrefix(htmlContent);
                    // HTML 转 Markdown (确保 cleanHtmlContent 健壮)
                    String cleanedContent = cleanHtmlContent(htmlContent);
                    // 获取章节标题 (确保 getChapterTitle 健壮)
                    String chapterTitle = getChapterTitle(htmlContent);
                    log.info("处理章节: {}", chapterTitle);

                    boolean isCoverProcessed = false;
                    // 处理封面逻辑 (优先使用 Href 判断)
                    if (coverHref != null && Objects.equals(resourceHref, coverHref)) {
                        log.info("检测到封面章节 (根据资源 Href)");
                        markdownContent.append("## ").append("封面").append("\n\n");
                        try {
                            String savedImageName = saveImage(coverImageResource, imageDirPath);
                            if (savedImageName != null) {
                                markdownContent.append("![封面图片](images/").append(savedImageName).append(")\n\n");
                            }
                        } catch (IOException e) {
                            log.warn("保存封面图片失败 '{}': {}", coverHref, e.getMessage());
                        }
                        isCoverProcessed = true; // 标记封面已处理
                    }

                    // 如果不是通过 Href 判定的封面，或者没有封面 Href，再尝试用 Title 判断（作为备选）
                    if (!isCoverProcessed && chapterTitle.equalsIgnoreCase("Cover")) {
                        log.info("检测到封面章节 (根据 Title)");
                        markdownContent.append("## ").append("封面").append("\n\n");
                        // 尝试保存封面资源（如果存在）
                        if (coverImageResource != null) {
                            try {
                                String savedImageName = saveImage(coverImageResource, imageDirPath);
                                if (savedImageName != null) {
                                    markdownContent.append("![封面图片](images/").append(savedImageName).append(")\n\n");
                                }
                            } catch (IOException e) {
                                log.warn("尝试根据 Title 保存封面图片失败 '{}': {}", coverHref, e.getMessage());
                            }
                        }
                    }

                    // 添加处理后的 Markdown 内容 (即使是封面章节，也可能包含其他文本)
                    markdownContent.append(cleanedContent).append("\n\n");

                } catch (Exception e) {
                     // 捕获处理单个 HTML 资源时的意外错误
                     log.error("处理 HTML 资源 '{}' 时出错: {}", resourceHref, e.getMessage(), e);
                     log.warn("跳过处理失败的 HTML 资源: {}", resourceHref);
                     // 可以选择在这里 return null 使整个文件处理失败
                     // return null;
                }
            }
            log.info("HTML 内容处理完成。");


            // 5. 保存内联图片 (排除封面，因为已在上面处理)
            log.info("开始保存内联图片...");
            for (Resource image : images) {
                String imageHref = image.getHref();
                if (image.getMediaType().toString().startsWith("image") &&
                    (coverHref == null || !Objects.equals(imageHref, coverHref))) {
                    try {
                        saveImage(image, imageDirPath);
                    } catch (IOException e) {
                        log.warn("保存内联图片 '{}' 失败: {}", imageHref, e.getMessage());
                        // 可选: return null;
                    } catch (Exception e) {
                         log.error("保存内联图片 '{}' 时发生意外错误: {}", imageHref, e.getMessage(), e);
                         // 可选: return null;
                    }
                }
            }
            log.info("内联图片处理完成。");


            // 6. 后处理 Markdown 内容 (确保这些工具方法健壮)
            log.info("开始后处理 Markdown 内容...");
            String finalMarkdown = markdownContent.toString();
            try {
                finalMarkdown = CdStringUtil.replaceImagesLinks(finalMarkdown);
                finalMarkdown = CdStringUtil.replaceImagesFirstLinks(finalMarkdown);
                finalMarkdown = replaceAnchorString(finalMarkdown); // 使用返回 String 的版本
            } catch (Exception e) {
                 log.error("后处理 Markdown 字符串时出错: {}", e.getMessage(), e);
                 // 根据需要决定是否 return null
            }
             log.info("Markdown 内容后处理完成。");


            // 7. 写入 Markdown 文件
            log.info("准备写入 Markdown 文件: {}", mdFilePath);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(mdFilePath, StandardCharsets.UTF_8))) {
                writer.write(finalMarkdown);
            } catch (IOException e) {
                log.error("写入 Markdown 文件 '{}' 时失败: {}", mdFilePath, e.getMessage(), e);
                return null; // 写入失败，返回 null
            }
            log.info("Markdown 文件已生成: {}", mdFilePath);


            // 8. 转换为 TXT 文件
            try {
                log.info("准备转换 Markdown 到 TXT 文件: {}", txtFilePath);
                MarkdownToTxtConverter.convert(mdFilePath, txtFilePath);
                log.info("TXT 文件已生成: {}", txtFilePath);
            } catch (Exception e) { // 捕获 convert 可能抛出的所有异常
                log.error("转换 Markdown '{}' 到 TXT 文件 '{}' 时失败: {}", mdFilePath, txtFilePath, e.getMessage(), e);
                // TXT 转换失败通常不应阻塞 ZIP 生成，可以选择继续或返回 null
                // return null;
                log.warn("TXT 文件转换失败，将继续进行 ZIP 打包（不包含 TXT 文件）。");
                 // 如果 TXT 转换失败，确保后续 ZIP 不会尝试添加它
                 txtFilePath = null; // 设置为 null，或使用标志位
            }


            // 9. 创建 ZIP 文件
            log.info("准备创建 ZIP 文件: {}", zipFilePath);
            try (FileOutputStream fos = new FileOutputStream(zipFilePath);
                 ZipOutputStream zos = new ZipOutputStream(fos)) {

                // 添加 Markdown 文件
                if (new File(mdFilePath).exists()) {
                    addToZipFile(mdFilePath, zos);
                } else {
                    log.warn("Markdown 文件 '{}' 不存在，无法添加到 ZIP。", mdFilePath);
                }

                // 添加 TXT 文件 (如果转换成功且文件存在)
                if (txtFilePath != null && new File(txtFilePath).exists()) {
                     addToZipFile(txtFilePath, zos);
                } else if (txtFilePath != null) { // txtFilePath 不为 null 但文件不存在
                    log.warn("TXT 文件 '{}' 不存在（可能转换失败），无法添加到 ZIP。", txtFilePath);
                }

                // 添加 images 文件夹 (如果存在且是目录)
                if (imageDirFile.exists() && imageDirFile.isDirectory()) {
                    addToZipFolder(imageDirPath, "images", zos);
                } else {
                     log.info("图片目录 '{}' 不存在或不是目录，不添加到 ZIP 文件。", imageDirPath);
                }

            } catch (IOException e) {
                log.error("创建或写入 ZIP 文件 '{}' 时失败: {}", zipFilePath, e.getMessage(), e);
                // 删除可能已部分创建的 ZIP 文件
                File partialZip = new File(zipFilePath);
                if (partialZip.exists()) {
                    partialZip.delete();
                }
                return null; // ZIP 操作失败，返回 null
            }
            log.info("ZIP 文件已成功生成: {}", zipFilePath);

            // 10. 所有关键步骤成功完成
            return zipFilePath; // 返回生成的 ZIP 文件路径

        } catch (Exception e) {
            // 捕获在成功读取 EPUB 之后、进行内容处理或文件操作时发生的 *意外* 运行时错误
            log.error("处理 EPUB 文件 '{}' 的内容或执行后续步骤时发生意外错误: {}",
                      epubFile.getName(), e.getMessage(), e);
            return null; // 发生意外错误，返回 null
        }
    }

    /**
     * 递归删除目录及其内容。
     * @param dir 要删除的目录 File 对象
     * @return 如果成功删除返回 true，否则返回 false
     */
    public static boolean deleteDirectoryRecursive(File dir) {
        if (dir == null || !dir.exists()) {
            return true; // 目录不存在视为删除成功
        }
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            if (children != null) {
                for (File child : children) {
                    boolean success = deleteDirectoryRecursive(child);
                    if (!success) {
                        log.warn("未能删除文件或子目录: {}", child.getAbsolutePath());
                        // 根据策略决定是否立即返回 false
                        // return false;
                    }
                }
            }
        }
        // 目录为空或是文件，执行删除
        boolean deleted = dir.delete();
        if (!deleted) {
             log.warn("无法删除: {}", dir.getAbsolutePath());
        }
        return deleted;
    }


    /**
     * 保存图片资源到指定目录。
     * @param image 图片资源
     * @param imageDir 目标目录
     * @return 保存后的文件名 (不含路径)，如果失败则返回 null
     * @throws IOException 如果发生写入错误
     */
    public static String saveImage(Resource image, String imageDir) throws IOException {
        if (image == null || image.getData() == null || image.getHref() == null) {
            log.warn("尝试保存的图片资源无效 (null, data is null, or href is null)");
            return null;
        }
        // 处理 Href 中的路径，只取文件名，并进行基本的文件名清理
        String href = image.getHref().replace("\\", "/");
        String imageName = href.substring(href.lastIndexOf('/') + 1);
        // 移除 URL 参数（如果存在）
        int queryIndex = imageName.indexOf('?');
        if (queryIndex != -1) {
            imageName = imageName.substring(0, queryIndex);
        }
        // 简单的非法字符替换（可能需要更完善的策略）
        imageName = imageName.replaceAll("[\\\\/:*?\"<>|]", "_");

        if (StrUtil.isBlank(imageName)) {
             log.warn("无法从 href '{}' 中提取或清理出有效的文件名，跳过保存", href);
             return null;
        }

        File imageFile = new File(imageDir, imageName);
        // 确保父目录存在 (应该在 process 开始时已创建)
        // imageFile.getParentFile().mkdirs(); // 可以省略如果确信目录已创建

        log.info("准备保存图片: {} 到 {}", imageName, imageFile.getAbsolutePath());
        try (InputStream inputStream = new ByteArrayInputStream(image.getData());
             FileOutputStream fos = new FileOutputStream(imageFile)) {
            byte[] buffer = new byte[8192]; // 增大缓冲区
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        } // try-with-resources 会自动关闭流
        log.debug("图片已保存: {}", imageFile.getAbsolutePath());
        return imageName; // 返回保存的文件名
    }


    /**
     * 为 HTML 中的图片 src 添加 'images/' 前缀（如果需要）。
     * @param html HTML 内容字符串
     * @return 修改后的 HTML 内容字符串
     */
    public static String addImagesPrefix(String html) {
        if (StrUtil.isBlank(html)) return "";
        try {
            Document document = Jsoup.parse(html);
            Elements imgTags = document.select("img[src]"); // 只选择带 src 属性的 img

            for (Element imgTag : imgTags) {
                String src = imgTag.attr("src");
                String updatedSrc = addImagesPrefix2(src); // 调用路径处理逻辑
                if (!Objects.equals(src, updatedSrc)) {
                    log.debug("更新图片路径: '{}' -> '{}'", src, updatedSrc);
                    imgTag.attr("src", updatedSrc);
                }
            }
            return document.html();
        } catch (Exception e) {
             log.error("Jsoup 处理 HTML 添加图片前缀时出错: {}", e.getMessage());
             return html; // 出错时返回原始 HTML
        }
    }

    /**
     * 规范化图片路径并确保以 'images/' 开头。
     * @param originalPath 原始路径字符串
     * @return 处理后的路径字符串
     */
    public static String addImagesPrefix2(String originalPath) {
        if (StrUtil.isBlank(originalPath)) return "";
        try {
            // 尝试解码 URL 编码的路径 (例如 %20)
            String decodedPath = java.net.URLDecoder.decode(originalPath, StandardCharsets.UTF_8);
            // 规范化路径，处理 ../ ./ 并统一 /
            String normalizedPath = Paths.get(decodedPath).normalize().toString().replace("\\", "/");

            // 移除协议和主机部分（如果存在）
            if (normalizedPath.matches("^[a-zA-Z]+://.*")) {
                 normalizedPath = Paths.get(new java.net.URI(normalizedPath).getPath()).toString().replace("\\", "/");
            }

            // 移除开头的 /
            if (normalizedPath.startsWith("/")) {
                normalizedPath = normalizedPath.substring(1);
            }
            // 移除开头的 ../ (循环处理)
            while (normalizedPath.startsWith("../")) {
                 normalizedPath = normalizedPath.substring(3);
                 // 再次移除可能出现的 /
                 if (normalizedPath.startsWith("/")) {
                      normalizedPath = normalizedPath.substring(1);
                 }
            }

            // 如果路径已经是 images/ 开头，直接返回
            if (normalizedPath.startsWith("images/")) {
                return normalizedPath;
            } else {
                // 否则，添加 images/ 前缀
                return "images/" + normalizedPath;
            }
        } catch (Exception e) {
             log.warn("处理图片路径 '{}' 时出错: {}", originalPath, e.getMessage());
             // 出错时返回一个基本的 images/ 前缀 + 原始路径，或根据需要返回空
             return "images/" + originalPath.substring(originalPath.lastIndexOf('/') + 1); // 尝试只取文件名
        }
    }

    /**
     * 移除字符串中符合 {#...} 格式的锚点标记。
     * @param originalString 原始字符串
     * @return 移除锚点后的字符串
     */
    public static String replaceAnchorString(String originalString) {
        if (originalString == null) return "";
        String regex = "\\{#.*?\\}"; // 匹配 {#...}
        try {
            return originalString.replaceAll(regex, "");
        } catch (Exception e) {
             log.error("移除锚点标记时出错: {}", e.getMessage());
             return originalString; // 出错时返回原始字符串
        }
    }

    /**
     * 使用 Flexmark 将 HTML 内容转换为 Markdown。
     * @param htmlContent HTML 字符串
     * @return Markdown 字符串，如果转换失败则返回空字符串
     */
    public static String cleanHtmlContent(String htmlContent) {
        if (StrUtil.isBlank(htmlContent)) return "";
        try {
             Parser parser = Parser.builder().build();
             FlexmarkHtmlConverter converter = FlexmarkHtmlConverter.builder().build();
             return converter.convert(htmlContent);
        } catch (Exception e) {
             log.error("Flexmark HTML 转换 Markdown 出错: {}", e.getMessage(), e);
             return ""; // 返回空字符串避免影响后续流程
        }
    }

    /**
     * 从 HTML 内容中提取 <title> 标签的文本。
     * @param htmlContent HTML 字符串
     * @return 标题文本，如果找不到或出错则返回 "未知章节"
     */
    public static String getChapterTitle(String htmlContent) {
        if (StrUtil.isBlank(htmlContent)) return "未知章节";
        try {
             Document document = Jsoup.parse(htmlContent);
             Element titleElement = document.selectFirst("title");
             if (titleElement != null) {
                 String title = titleElement.text();
                 return StrUtil.isNotBlank(title) ? title : "无标题章节";
             }
        } catch (Exception e) {
             log.error("Jsoup 解析 HTML 提取标题时出错: {}", e.getMessage());
        }
        return "未知章节"; // 如果没有 title 标签或解析出错
    }

    /**
     * 获取 EPUB 书籍的封面图片资源。
     * @param book EPUB Book 对象
     * @return 封面图片 Resource 对象，如果不存在则返回 null
     */
    public static Resource getCoverImage(Book book) {
        if (book == null) return null;
        try {
             return book.getCoverImage(); // 直接调用 epublib 的方法
        } catch (Exception e) {
             log.error("获取封面图片资源时出错: {}", e.getMessage());
             return null;
        }
    }


    /**
     * 将指定文件夹的内容（包括子文件夹）添加到 ZIP 输出流中。
     * @param folderPath 要添加的文件夹的绝对路径
     * @param basePathInZip 在 ZIP 文件中，此文件夹内容的基础相对路径 (例如 "images")
     * @param zos ZIP 输出流
     * @throws IOException 如果发生 IO 错误
     */
    private static void addToZipFolder(String folderPath, String basePathInZip, ZipOutputStream zos)
            throws IOException {
        File folder = new File(folderPath);
        log.debug("添加到 ZIP 文件夹: '{}' as '{}'", folderPath, basePathInZip);
        File[] files = folder.listFiles(); // 获取文件夹内容
        if (files != null) {
            for (File file : files) {
                // 构建在 ZIP 文件内部的完整条目名称
                String entryName = basePathInZip + "/" + file.getName();
                if (file.isDirectory()) {
                    // 递归添加子文件夹
                    addToZipFolder(file.getAbsolutePath(), entryName, zos);
                } else {
                    // 添加文件
                    addToZipFile(file.getAbsolutePath(), entryName, zos);
                }
            }
        } else {
             // listFiles() 返回 null 的情况（例如 IO 错误或权限问题）
             log.warn("无法列出文件夹内容或文件夹为空: {}", folderPath);
        }
    }

    /**
     * 将单个文件添加到 ZIP 输出流中，使用指定的条目名称。
     * @param filePath 要添加的文件的绝对路径
     * @param entryNameInZip 在 ZIP 文件中此文件的完整路径和名称
     * @param zos ZIP 输出流
     * @throws IOException 如果发生 IO 错误
     */
    private static void addToZipFile(String filePath, String entryNameInZip, ZipOutputStream zos)
            throws IOException {
        File file = new File(filePath);
        // 检查文件是否存在且是文件
        if (!file.exists() || !file.isFile()) {
             log.warn("尝试添加到 ZIP 的文件不存在或不是文件，跳过: {}", filePath);
             return;
        }
        log.info("添加到 ZIP: '{}' as '{}'", file.getName(), entryNameInZip);
        try (FileInputStream fis = new FileInputStream(file)) { // 使用 try-with-resources
            ZipEntry zipEntry = new ZipEntry(entryNameInZip); // 创建 ZIP 条目
            zos.putNextEntry(zipEntry); // 开始写入新条目

            byte[] buffer = new byte[8192]; // 缓冲区
            int length;
            // 从文件读取数据并写入 ZIP 输出流
            while ((length = fis.read(buffer)) >= 0) {
                zos.write(buffer, 0, length);
            }
            zos.closeEntry(); // 完成当前条目的写入
        } // fis 会在此自动关闭
    }

    /**
     * 将单个文件添加到 ZIP 输出流中，使用文件名作为 ZIP 内的条目名称（放在根目录）。
     * @param filePath 要添加的文件的绝对路径
     * @param zos ZIP 输出流
     * @throws IOException 如果发生 IO 错误
     */
    private static void addToZipFile(String filePath, ZipOutputStream zos) throws IOException {
         File file = new File(filePath);
         // 使用文件名作为 ZIP 中的条目名
         addToZipFile(filePath, file.getName(), zos);
    }
}
