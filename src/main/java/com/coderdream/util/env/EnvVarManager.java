package com.coderdream.util.env;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnvVarManager {

    /**
     * 读取指定的环境变量的值。
     *
     * @param variableName 环境变量的名称
     * @return 环境变量的值，如果不存在则返回 null
     */
    public static String readEnvVar(String variableName) {
        return System.getenv(variableName);
    }

   /**
     * 设置环境变量（永久生效，仅限 macOS/Linux）。
     *
     * @param variableName  环境变量的名称
     * @param variableValue 环境变量的值
     * @return true 如果设置成功, false 如果设置失败
     */
    public static boolean setEnvVar(String variableName, String variableValue) {
         // Windows 下不支持持久化设置环境变量
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            System.err.println("Windows 下不支持持久化设置环境变量。");
            return false;
        }

        String shellConfigFile = getShellConfigFile();
        if (shellConfigFile == null) {
            System.err.println("无法确定 shell 配置文件。");
            return false;
        }

        Path configFilePath = Paths.get(shellConfigFile);
        try {
            List<String> lines = Files.readAllLines(configFilePath);
            List<String> updatedLines = new ArrayList<>();
            boolean found = false;
            Pattern pattern = Pattern.compile("^\\s*export\\s+" + Pattern.quote(variableName) + "\\s*=.*");

            for (String line : lines) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    // 找到匹配的行，进行替换
                    updatedLines.add("export " + variableName + "=\"" + variableValue + "\"");
                    found = true;
                } else {
                    updatedLines.add(line);
                }
            }

            // 如果没有找到匹配的行，则添加新行
            if (!found) {
                updatedLines.add("export " + variableName + "=\"" + variableValue + "\"");
            }

            // 将更新后的内容写回文件
            Files.write(configFilePath, updatedLines);

            // 立即生效 (在新的 shell 会话中生效)
            // System.setProperty(variableName, variableValue); // 移除这一行！
            reloadShellConfig(shellConfigFile);

            return true;

        } catch (IOException e) {
            System.err.println("写入 shell 配置文件时出错: " + e.getMessage());
            return false;
        }
    }
    /**
     * 获取 shell 配置文件的路径。
     *
     * @return shell 配置文件的路径，如果无法确定则返回 null
     */
    private static String getShellConfigFile() {
        String shell = System.getenv("SHELL");
        if (shell == null) {
            return null;
        }

        String homeDir = System.getProperty("user.home");
        if (shell.endsWith("/zsh")) {
            return homeDir + "/.zshrc";
        } else if (shell.endsWith("/bash")) {
            // 尝试 .bash_profile，如果不存在则尝试 .bashrc
            File bashProfile = new File(homeDir + "/.bash_profile");
            if (bashProfile.exists()) {
                return bashProfile.getAbsolutePath();
            } else {
                return homeDir + "/.bashrc";
            }
        } else {
            // 其他 shell，可能需要根据实际情况进行处理
            System.err.println("不支持的 shell: " + shell);
            return null;
        }
    }

    /**
     * 重新加载 shell 配置文件，使更改在新的 shell 会话中生效。
     */
    private static void reloadShellConfig(String configFile) {
        try {
            List<String> commandList;

            // 根据操作系统选择不同的命令前缀
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                // Windows, 不需要source
                return;
            } else {
                // macOS 或 Linux 系统
                // 使用 bash -ic 可以强制启动交互式shell, 确保配置文件被加载
                commandList = Arrays.asList("bash", "-ic", "source " + configFile);
            }
            ProcessBuilder processBuilder = new ProcessBuilder(commandList);
            // processBuilder.inheritIO(); // 不要使用 inheritIO()，可能会干扰 source 命令
            Process process = processBuilder.start();
            process.waitFor();  // 等待子进程完成
        } catch (IOException | InterruptedException e) {
            System.err.println("重新加载 shell 配置文件时出错: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        // 读取环境变量
        String existingVar = readEnvVar("PATH");
        System.out.println("PATH: " + existingVar);

        // 设置环境变量
        boolean success = setEnvVar("MY_NEW_VAR", "MyNewValue");
        if (success) {
            System.out.println("MY_NEW_VAR 已设置。");
            // 在新的 shell 会话中验证
            try {
                Process p;
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    p = new ProcessBuilder("cmd.exe", "/c", "echo %MY_NEW_VAR%").start();
                } else {
                    p = new ProcessBuilder("bash", "-c", "echo $MY_NEW_VAR").start();
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("从新的shell读取到的变量值: " + line);
                }
                p.waitFor(); // 等待进程执行完毕

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        } else {
            System.err.println("MY_NEW_VAR 设置失败。");
        }


        // 再次读取，验证是否设置成功 (在当前进程中)
        System.out.println("MY_NEW_VAR (当前进程): " + readEnvVar("MY_NEW_VAR")); // 应该仍然是 null
    }
}