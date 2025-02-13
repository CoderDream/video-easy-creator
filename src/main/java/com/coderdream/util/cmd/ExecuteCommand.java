package com.coderdream.util.cmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class ExecuteCommand {

    public static void main(String[] args) {
        try {
            // 要执行的命令和参数 (macOS 示例)
            List<String> command = Arrays.asList("ls", "-l", "/Users/coderdream");
            // Windows 示例:
            // List<String> command = Arrays.asList("cmd", "/c", "dir");

            // 创建 ProcessBuilder
            ProcessBuilder processBuilder = new ProcessBuilder(command);

            // 可选: 设置工作目录
            // processBuilder.directory(new File("/path/to/working/directory"));

            // 可选: 重定向错误输出到标准输出
            processBuilder.redirectErrorStream(true);

            // 启动进程
            Process process = processBuilder.start();

            // 读取命令的输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // 等待进程完成
            int exitCode = process.waitFor();
            System.out.println("Exit Code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}