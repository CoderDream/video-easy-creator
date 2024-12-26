package com.coderdream.util.hexo;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CmdUtil4 {

    /**
     * 执行 Hexo 项目的构建和部署命令，并统计每个步骤的执行时间
     *
     * @param args 运行参数，未使用
     */
    public static void main(String[] args) {
        // Hexo 项目的路径
        String hexoProjectPath = "D:/04_GitHub/hexo-project/Hexo-BlueLake-Blog/";

        // 步骤 1: 切换到 Hexo 项目的目录
        String[] command1 = new String[]{
            "cmd.exe", "/c", "cd " + hexoProjectPath
        };

        // 步骤 2: 查看文件夹中的文件列表
        String[] command2 = new String[]{
            "cmd.exe", "/c", "dir"
        };

        // 步骤 3: 执行 Hexo 构建命令
        String[] command3 = new String[]{
            "cmd.exe", "/c", "hexo g"
        };

        // 步骤 4: 执行 Hexo 部署命令
        String[] command4 = new String[]{
            "cmd.exe", "/c", "hexo d"
        };

        // 使用 ProcessBuilder 来执行命令
        ProcessBuilder processBuilder = new ProcessBuilder();

        try {
            // 步骤 1: 切换到 Hexo 项目的目录并执行
            executeCommand(processBuilder, command1, "切换到 Hexo 项目目录");

            // 步骤 2: 执行 dir 命令，查看文件夹内容
            executeCommand(processBuilder, command2, "查看文件夹内容");

            // 步骤 3: 执行 Hexo 构建命令
            executeCommand(processBuilder, command3, "执行 Hexo 构建命令");

            // 步骤 4: 执行 Hexo 部署命令
            executeCommand(processBuilder, command4, "执行 Hexo 部署命令");

        } catch (IOException | InterruptedException e) {
            // 记录异常
            log.error("命令执行时发生错误: {}", e.getMessage(), e);
        }
    }

    /**
     * 执行命令并记录详细日志
     *
     * @param processBuilder 执行命令的 ProcessBuilder 对象
     * @param command 命令的数组
     * @param stepDescription 步骤的描述，用于日志中
     * @throws IOException 执行命令时抛出的异常
     * @throws InterruptedException 等待进程时的异常
     */
    private static void executeCommand(ProcessBuilder processBuilder, String[] command, String stepDescription)
            throws IOException, InterruptedException {
        // 记录开始时间
        long startTime = System.currentTimeMillis();
        log.info("{} 开始执行...", stepDescription);

        // 设置命令并执行
        processBuilder.command(command);
        Process process = processBuilder.start();

        // 获取命令行输出流并打印
        InputStream in = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null) {
            log.debug("{} 输出: {}", stepDescription, line);  // 打印命令输出
        }
        in.close();

        // 获取错误流输出并打印
        InputStream errorStream = process.getErrorStream();
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
        while ((line = errorReader.readLine()) != null) {
            log.error("{} 错误输出: {}", stepDescription, line);  // 打印错误输出
        }
        errorStream.close();

        // 等待进程执行完毕
        int exitCode = process.waitFor();

        // 记录结束时间并计算耗时
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        String formattedTime = formatDuration(duration);  // 格式化耗时为时分秒

        if (exitCode == 0) {
            log.info("{} 执行成功，执行时间: {}", stepDescription, formattedTime);
        } else {
            log.error("{} 执行失败，退出码: {}，执行时间: {}", stepDescription, exitCode, formattedTime);
        }
    }

    /**
     * 格式化毫秒为时分秒格式
     *
     * @param duration 耗时（毫秒）
     * @return 格式化后的时间（时分秒）
     */
    private static String formatDuration(long duration) {
        long hours = duration / (1000 * 60 * 60);
        long minutes = (duration % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (duration % (1000 * 60)) / 1000;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
