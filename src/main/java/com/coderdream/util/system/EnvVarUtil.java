package com.coderdream.util.system;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

public class EnvVarUtil {

    /**
     * 读取指定环境变量的值
     *
     * @param key 环境变量名称
     * @return 环境变量值，如果不存在则返回 null
     */
    public static String readEnv(String key) {
        return System.getenv(key);
    }

    /**
     * 写入环境变量（仅对子进程生效）
     *
     * @param key   环境变量名称
     * @param value 环境变量值
     * @return 子进程输出内容
     */
    public static String writeEnv(String key, String value) {
        try {
            // 创建 ProcessBuilder 实例
            ProcessBuilder processBuilder = new ProcessBuilder("printenv");

            // 修改子进程的环境变量
            Map<String, String> env = processBuilder.environment();
            env.put(key, value);

            // 启动子进程
            Process process = processBuilder.start();

            // 读取子进程输出
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append(System.lineSeparator());
                }
            }

            return output.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 打印所有环境变量（调试用）
     */
    public static void printAllEnv() {
        System.getenv().forEach((key, value) -> System.out.println(key + ": " + value));
    }

    public static void main(String[] args) {
        // 示例：读取环境变量
        String pathValue = EnvVarUtil.readEnv("PATH");
        System.out.println("PATH: " + pathValue);

        // 示例：写入环境变量
        String output = EnvVarUtil.writeEnv("MY_TEMP_VAR", "HelloWorld");
        System.out.println("子进程输出: \n" + output);

        // 示例：打印所有环境变量
        System.out.println("\n所有环境变量:");
        EnvVarUtil.printAllEnv();
    }
}
