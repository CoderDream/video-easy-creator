//package com.coderdream.util.file;
//
//import cn.hutool.core.io.FileUtil;
//import cn.hutool.core.io.IORuntimeException;
//import cn.hutool.core.util.StrUtil;
//import java.io.File;
//import lombok.extern.slf4j.Slf4j;
//
///**
// * 文件重命名工具类
// */
//@Slf4j
//public class FileRenameUtil02 {
//
//    /**
//     * 私有构造函数，防止实例化工具类
//     */
//    private FileRenameUtil02() {
//        throw new IllegalStateException("Utility class");
//    }
//
//    /**
//     * 重命名文件。
//     * 如果新文件路径与旧文件路径的目录不同，则执行移动并重命名。
//     * 默认不覆盖已存在的目标文件。
//     *
//     * @param oldFilePath 旧文件的完整路径 (e.g., "C:/videos/old_movie.mp4")
//     * @param newFilePath 新文件的完整路径 (e.g., "C:/movies/new_movie_name.mp4")
//     * @return 如果重命名（或移动并重命名）成功，返回 true；否则返回 false。
//     */
//    public static boolean renameFile(String oldFilePath, String newFilePath) {
//        // 1. 输入参数校验
//        if (StrUtil.isBlank(oldFilePath)) {
//            log.error("旧文件路径不能为空或空白。");
//            return false;
//        }
//        if (StrUtil.isBlank(newFilePath)) {
//            log.error("新文件路径不能为空或空白。");
//            return false;
//        }
//
//        File oldFile = FileUtil.file(oldFilePath); // 使用 HuTool 的 file 方法更健壮
//        File newFile = FileUtil.file(newFilePath);
//
//        // 2. 检查旧文件是否存在且是文件
//        if (!FileUtil.isFile(oldFile)) { // isFile 会检查存在性和是否为文件
//             if (!oldFile.exists()) {
//                 log.error("重命名失败：旧文件不存在 '{}'", oldFilePath);
//             } else {
//                 log.error("重命名失败：旧路径不是一个文件 '{}'", oldFilePath);
//             }
//            return false;
//        }
//
//        // 3. 检查新文件路径的父目录是否存在，如果不存在则尝试创建
//        File parentDir = newFile.getParentFile();
//        if (parentDir != null && !parentDir.exists()) {
//            if (FileUtil.mkdir(parentDir) == null) {
//                log.error("重命名失败：无法创建新文件所在的目录 '{}'", parentDir.getAbsolutePath());
//                return false;
//            }
//            log.info("成功创建新文件目录: {}", parentDir.getAbsolutePath());
//        }
//
//
//        // 4. 执行重命名（移动）操作，默认不覆盖
//        try {
//            // FileUtil.move 方法能很好地处理同目录重命名和跨目录移动并重命名
//            // 第三个参数 isOverride 设置为 false
//            File resultFile = FileUtil.move(oldFile, newFile, false);
//
//            // 5. 验证结果
//            if (resultFile != null && resultFile.exists()) {
//                 // 再次确认旧文件是否已不存在
//                 if (!oldFile.exists()) {
//                    log.info("文件重命名/移动成功: '{}' -> '{}'", oldFilePath, newFilePath);
//                    return true;
//                 } else {
//                     // 这种情况理论上不应该发生，但以防万一
//                     log.warn("文件可能已复制到 '{}'，但旧文件 '{}' 仍然存在。", newFilePath, oldFilePath);
//                     return false; // 或者根据业务逻辑判断是否算成功
//                 }
//            } else {
//                 // 如果 resultFile 为 null 或 !resultFile.exists()
//                 // 检查是否是因为目标文件已存在导致失败 (因为 isOverride=false)
//                 if (newFile.exists()) {
//                     log.warn("重命名失败：目标文件 '{}' 已存在，且未设置覆盖。", newFilePath);
//                 } else {
//                     log.error("重命名失败：未知原因导致目标文件 '{}' 未能生成。", newFilePath);
//                 }
//                return false;
//            }
//        } catch (IORuntimeException e) {
//            // IORuntimeException 是 HuTool 对 IOException 的封装
//            log.error("文件重命名/移动操作时发生 IO 异常: '{}' -> '{}'", oldFilePath, newFilePath, e);
//            return false;
//        } catch (Exception e) {
//            // 捕获其他可能的运行时异常
//            log.error("文件重命名/移动操作时发生未知异常: '{}' -> '{}'", oldFilePath, newFilePath, e);
//            return false;
//        }
//    }
//
//    /**
//     * 重命名文件，允许覆盖已存在的目标文件。
//     *
//     * @param oldFilePath 旧文件的完整路径
//     * @param newFilePath 新文件的完整路径
//     * @return 如果重命名（或移动并重命名）成功，返回 true；否则返回 false。
//     */
//    public static boolean renameFileOverride(String oldFilePath, String newFilePath) {
//       // 基本逻辑同上，仅在调用 move 时将 isOverride 设为 true
//        if (StrUtil.isBlank(oldFilePath) || StrUtil.isBlank(newFilePath)) {
//             log.error("旧文件路径或新文件路径不能为空或空白。");
//             return false;
//        }
//
//        File oldFile = FileUtil.file(oldFilePath);
//        File newFile = FileUtil.file(newFilePath);
//
//        if (!FileUtil.isFile(oldFile)) {
//            if (!oldFile.exists()) {
//                 log.error("重命名(覆盖)失败：旧文件不存在 '{}'", oldFilePath);
//             } else {
//                 log.error("重命名(覆盖)失败：旧路径不是一个文件 '{}'", oldFilePath);
//             }
//            return false;
//        }
//
//        File parentDir = newFile.getParentFile();
//        if (parentDir != null && !parentDir.exists()) {
//             if (FileUtil.mkdir(parentDir) == null) {
//                 log.error("重命名(覆盖)失败：无法创建新文件所在的目录 '{}'", parentDir.getAbsolutePath());
//                 return false;
//             }
//            log.info("成功创建新文件目录: {}", parentDir.getAbsolutePath());
//        }
//
//        try {
//             // isOverride 设置为 true
//             File resultFile = FileUtil.move(oldFile, newFile, true);
//
//             if (resultFile != null && resultFile.exists()) {
//                 if (!oldFile.exists()) {
//                    log.info("文件重命名/移动并覆盖成功: '{}' -> '{}'", oldFilePath, newFilePath);
//                    return true;
//                 } else {
//                     log.warn("文件已覆盖到 '{}'，但旧文件 '{}' 仍然存在。", newFilePath, oldFilePath);
//                     return false;
//                 }
//             } else {
//                 log.error("重命名(覆盖)失败：未知原因导致目标文件 '{}' 未能生成。", newFilePath);
//                 return false;
//             }
//         } catch (IORuntimeException e) {
//             log.error("文件重命名/移动(覆盖)操作时发生 IO 异常: '{}' -> '{}'", oldFilePath, newFilePath, e);
//             return false;
//         } catch (Exception e) {
//             log.error("文件重命名/移动(覆盖)操作时发生未知异常: '{}' -> '{}'", oldFilePath, newFilePath, e);
//             return false;
//         }
//    }
//
//
//    // --- 示例 main 方法 ---
//    public static void main(String[] args) {
//        String testDir = "D:\\0000\\0003_PressBriefings\\20250408";
//        String oldMp4FileName = testDir + File.separator + "20250408.png";
//        String newMp4FileName = testDir + File.separator + "20250408_raw.png"; // 移动到子目录并重命名
//        String existingFileName =testDir + File.separator + "20250408_raw.png"; // testDir + "existing_file.txt";
//
//        // 准备测试环境
////        FileUtil.del(testDir); // 先清理旧的测试目录
//        FileUtil.mkdir(testDir);
//        try {
//            FileUtil.touch(oldMp4FileName);
//            FileUtil.touch(existingFileName);
//            log.info("测试文件创建完毕.");
//        } catch (Exception e) {
//            log.error("创建测试文件失败", e);
//            return;
//        }
//
//        // 测试场景1：成功重命名（并移动）
//        log.info("--- 测试场景1：成功重命名（并移动） ---");
//        boolean success = renameFile(oldMp4FileName, newMp4FileName);
//        log.info("测试场景1结果: {}", success ? "成功" : "失败");
//        log.info("检查新文件是否存在: {}", FileUtil.exist(newMp4FileName));
//        log.info("检查旧文件是否还存在: {}", FileUtil.exist(oldMp4FileName));
//
//        // 测试场景2：旧文件不存在
//        log.info("--- 测试场景2：旧文件不存在 ---");
//        success = renameFile(testDir + "non_existent_file.avi", testDir + "some_new_name.avi");
//        log.info("测试场景2结果: {}", success ? "成功" : "失败");
//
//        // 测试场景3：新文件已存在（不覆盖）
//        log.info("--- 测试场景3：新文件已存在（不覆盖） ---");
//        String tempOldFile = testDir + "temp_old.txt"; // 需要一个新的旧文件
//        FileUtil.touch(tempOldFile);
//        success = renameFile(tempOldFile, existingFileName); // 尝试重命名为已存在的 existing_file.txt
//        log.info("测试场景3结果: {}", success ? "成功" : "失败");
//        log.info("检查新文件（目标）是否存在: {}", FileUtil.exist(existingFileName));
//        log.info("检查临时旧文件是否还存在: {}", FileUtil.exist(tempOldFile)); // 应该还存在
//
//        // 测试场景4：新文件已存在（允许覆盖）
//        log.info("--- 测试场景4：新文件已存在（允许覆盖） ---");
//        // 使用上一步的 tempOldFile 和 existingFileName
//        success = renameFileOverride(tempOldFile, existingFileName); // 允许覆盖
//        log.info("测试场景4结果: {}", success ? "成功" : "失败");
//        log.info("检查新文件（目标）是否存在: {}", FileUtil.exist(existingFileName)); // 应该存在（内容已被覆盖）
//        log.info("检查临时旧文件是否还存在: {}", FileUtil.exist(tempOldFile)); // 应该不存在了
//
//        // 测试场景5：路径为空
//        log.info("--- 测试场景5：路径为空 ---");
//        success = renameFile("", "newpath.txt");
//        log.info("测试场景5结果 (空旧路径): {}", success ? "成功" : "失败");
//        success = renameFile("oldpath.txt", null);
//        log.info("测试场景5结果 (空新路径): {}", success ? "成功" : "失败");
//
//
//        // 清理测试环境
//        FileUtil.del(testDir);
//        log.info("测试目录 {} 已清理。", testDir);
//    }
//}
