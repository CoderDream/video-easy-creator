package com.coderdream.util.env;

import java.io.IOException;

public class PersistWindowsEnv {
    public static void main(String[] args) {
        String varName = "MY_GLOBAL_VAR";
        String varValue = "PersistentValue";

        try {
            // 修改用户环境变量（当前用户生效）
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c",
                "REG ADD HKEY_CURRENT_USER\\Environment /v " + varName + " /t REG_SZ /d " + varValue + " /f");
            Process process = pb.start();
            process.waitFor();

            System.out.println("环境变量 " + varName + " 已添加到 HKEY_CURRENT_USER");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
