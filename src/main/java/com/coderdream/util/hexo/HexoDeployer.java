package com.coderdream.util.hexo;

import lombok.extern.slf4j.Slf4j;
import java.io.IOException;

@Slf4j  // 使用 Lombok 的 @Slf4j 注解来记录日志
public class HexoDeployer {

    /**
     * 执行 Hexo 命令生成和部署博客。
     *
     * @param folderPath Hexo 项目的文件夹路径（例如 "D:\\04_GitHub\\hexo-project\\Hexo-BlueLake-Blog"）
     * @throws IOException 如果执行命令过程中发生 I/O 错误
     * @throws InterruptedException 如果当前线程在等待命令执行时被中断
     */
    public static void executeHexoCommands(String folderPath) throws IOException, InterruptedException {
        // 进入指定的文件夹
        log.info("正在进入文件夹: {}", folderPath);
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(new java.io.File(folderPath)); // 设置工作目录为 Hexo 项目的文件夹

        // 拼接第一个命令：hexo g（生成静态文件）
        StringBuilder command1 = new StringBuilder();
        command1.append("dir");  // 第一条命令：生成静态文件
//        command1.append("hexo g");  // 第一条命令：生成静态文件
//        command1.append("D:\\03_Dev\\nvm_1_1_10\\v20.11.0\\node_modules\\hexo-cli").append(" g");  // 第一条命令：生成静态文件
        log.info("准备执行命令: {}", command1.toString()); // 记录命令执行日志

        // 执行第一个命令
        Process process1 = processBuilder.command(command1.toString().split(" ")).start();

        int exitCode1 = process1.waitFor();  // 等待命令执行完毕

        if (exitCode1 == 0) {
            log.info("命令 'hexo g' 执行成功！");
        } else {
            log.error("命令 'hexo g' 执行失败，退出代码: {}", exitCode1);
            return;  // 如果第一条命令失败，终止执行后续命令
        }

        // 拼接第二个命令：hexo d（部署博客）
        StringBuilder command2 = new StringBuilder();
        command2.append("hexo d");  // 第二条命令：部署博客
        log.info("准备执行命令: {}", command2.toString()); // 记录命令执行日志

        // 执行第二个命令
        Process process2 = processBuilder.command(command2.toString().split(" ")).start();
        int exitCode2 = process2.waitFor();  // 等待命令执行完毕

        if (exitCode2 == 0) {
            log.info("命令 'hexo d' 执行成功！");
        } else {
            log.error("命令 'hexo d' 执行失败，退出代码: {}", exitCode2);
        }
    }

    public static void main(String[] args) {
        try {
            // 传入 Hexo 项目的路径并执行命令
            String folderPath = "D:/04_GitHub/hexo-project/Hexo-BlueLake-Blog/";
            executeHexoCommands(folderPath);
        } catch (IOException | InterruptedException e) {
            log.error("执行命令时发生异常: {}", e.getMessage(), e);
        }
    }
}
