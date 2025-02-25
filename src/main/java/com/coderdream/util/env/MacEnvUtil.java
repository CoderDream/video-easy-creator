package com.coderdream.util.env;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MacEnvUtil {

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
     * 1. 打开环境变量文件
     * 2. 设置/更新变量
     * 3. 使更改生效 (source)
     *
     * @param variableName  环境变量的名称
     * @param variableValue 环境变量的值
     * @return true 如果设置成功, false 如果设置失败
     */
    public static boolean setEnvVar(String variableName, String variableValue) {
        // Windows 下不支持持久化设置环境变量
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            System.err.println("Windows 下不支持通过此方法持久化设置环境变量。");
            return false;
        }

        String shellConfigFile = getShellConfigFile();
        if (shellConfigFile == null) {
            System.err.println("无法确定 shell 配置文件。");
            return false;
        }

        // 1. & 2. 写入配置文件 (打开、查找/替换、添加、保存)
        if (!writeToConfigFile(shellConfigFile, variableName, variableValue)) {
            return false; // 写入失败
        }

        // 3. 使更改生效 (source)
        reloadShellConfig(shellConfigFile);

        return true;
    }

    /**
     * 将环境变量写入 shell 配置文件。
     *
     * @param configFile    配置文件路径
     * @param variableName  变量名
     * @param variableValue 变量值
     * @return true if successful, false otherwise
     */
    private static boolean writeToConfigFile(String configFile, String variableName, String variableValue) {
        Path configFilePath = Paths.get(configFile);
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
                // 使用 bash -ic 强制启动交互式shell, 确保配置文件被加载
                commandList = Arrays.asList("bash", "-ic", "source " + configFile);
            }
            ProcessBuilder processBuilder = new ProcessBuilder(commandList);
            // processBuilder.inheritIO(); // 不要使用 inheritIO()，可能会干扰 source 命令
            Process process = processBuilder.start();
            process.waitFor(); // 等待子进程完成

        } catch (IOException | InterruptedException e) {
            System.err.println("重新加载 shell 配置文件时出错: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        // 示例用法
        String varName = "SPEECH_KEY_EASTASIA";
        String varValue = "XXX";

        // 设置环境变量
        if (MacEnvUtil.setEnvVar(varName, varValue)) {
            System.out.println(varName + " 已成功设置。");
        } else {
            System.err.println(varName + " 设置失败。");
        }

        // 读取环境变量
        String value = MacEnvUtil.readEnvVar(varName);
        System.out.println(varName + " (当前进程): " + value);

        // 在新的 shell 会话中验证
        try {
            Process p;
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                p = new ProcessBuilder("cmd.exe", "/c", "echo %" + varName + "%").start();
            } else {
                p = new ProcessBuilder("bash", "-c", "echo $" + varName).start();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("从新的 shell 读取到的变量值: " + line);
            }
            p.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}