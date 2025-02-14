package com.coderdream.util.env;

import java.io.IOException;

public class RefreshEnv {
    public static void main(String[] args) {
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c",
                "wmic process where name='explorer.exe' call terminate && start explorer.exe");
            pb.start().waitFor();
            System.out.println("已刷新 explorer.exe");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
