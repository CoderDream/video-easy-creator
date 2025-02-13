package com.coderdream.util.env;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

public class EnvVarWriter {
    public static void main(String[] args) {
        try {
            // 创建 ProcessBuilder 实例
            ProcessBuilder processBuilder = new ProcessBuilder("printenv");

            // 修改子进程的环境变量
            Map<String, String> env = processBuilder.environment();
            env.put("MY_TEMP_VAR", "HelloWorld");
            env.put("GEMINI_API_KEY", "HelloWorld");

            // 启动子进程
            Process process = processBuilder.start();

            // 读取子进程输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(System.getenv("GEMINI_API_KEY"));
    }
}
