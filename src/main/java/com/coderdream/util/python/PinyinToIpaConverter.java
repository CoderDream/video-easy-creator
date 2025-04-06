package com.coderdream.util.python;

import lombok.extern.slf4j.Slf4j;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

@Slf4j
public class PinyinToIpaConverter {

    /**
     * 将带声调数字的拼音转换为 IPA 音标
     *
     * @param pinyin 带声调数字的拼音，例如 "pang1"
     * @return 对应的 IPA 音标，如果转换失败则返回 null
     */
    public static String convertPinyinToIpa(String pinyin) {
        log.info("Converting pinyin: {}", pinyin);
        String ipa = null;
        try {
            // 创建 ProcessBuilder 对象，用于执行外部命令 "pinyin-to-ipa-cli"
            //  并设置 PYTHONIOENCODING 环境变量为 UTF-8
            ProcessBuilder processBuilder = new ProcessBuilder("pinyin-to-ipa-cli", pinyin);
            Map<String, String> environment = processBuilder.environment();
            environment.put("PYTHONIOENCODING", "UTF-8"); // 设置环境变量

            Process process = processBuilder.start(); // 启动进程

            // 创建 BufferedReader 对象，用于读取进程的输入流（即 "pinyin-to-ipa-cli" 命令的输出）
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8")); // 指定字符集为 UTF-8
            StringBuilder output = new StringBuilder(); // 创建 StringBuilder 对象，用于存储进程的输出
            String line; // 用于存储读取的每一行输出
            while ((line = reader.readLine()) != null) { // 循环读取进程的输出，直到读取完毕
                output.append(line); // 将读取的每一行添加到 StringBuilder 对象中
            }

            int exitCode = process.waitFor(); // 等待进程执行完毕，并获取进程的退出代码
            log.debug("pinyin-to-ipa-cli exited with code: {}", exitCode); // 记录进程的退出代码

            if (exitCode == 0) { // 如果进程的退出代码为 0，表示命令执行成功
                ipa = output.toString().trim(); // 获取进程的输出，并去除首尾空格，作为 IPA 音标
                log.info("IPA conversion successful: {} -> {}", pinyin, ipa); // 记录成功的转换
            } else { // 如果进程的退出代码不为 0，表示命令执行失败
                log.error("pinyin-to-ipa-cli failed with exit code: {}", exitCode); // 记录失败的退出代码

                // 创建 BufferedReader 对象，用于读取进程的错误流（即 "pinyin-to-ipa-cli" 命令的错误输出）
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8")); // 指定字符集为 UTF-8
                StringBuilder errorOutput = new StringBuilder(); // 创建 StringBuilder 对象，用于存储进程的错误输出
                String errorLine; // 用于存储读取的每一行错误输出
                while ((errorLine = errorReader.readLine()) != null) { // 循环读取进程的错误输出，直到读取完毕
                    errorOutput.append(errorLine); // 将读取的每一行添加到 StringBuilder 对象中
                }
                log.error("Error output from pinyin-to-ipa-cli: {}", errorOutput.toString()); // 记录进程的错误输出
            }

        } catch (IOException | InterruptedException e) { // 捕获 IO 异常和中断异常
            log.error("Error during pinyin to IPA conversion: ", e); // 记录转换过程中的错误
        }
        return ipa; // 返回 IPA 音标，如果转换失败则返回 null
    }


    public static void main(String[] args) {
        String pinyin = "pang1"; // 定义要转换的拼音
        String ipa = PinyinToIpaConverter.convertPinyinToIpa(pinyin); // 调用 convertPinyinToIpa 方法进行转换
        if (ipa != null) { // 如果转换成功
            System.out.println("Pinyin: " + pinyin + ", IPA: " + ipa); // 打印拼音和对应的 IPA 音标
        } else { // 如果转换失败
            System.out.println("Failed to convert pinyin: " + pinyin); // 打印转换失败的消息
        }
    }
}
