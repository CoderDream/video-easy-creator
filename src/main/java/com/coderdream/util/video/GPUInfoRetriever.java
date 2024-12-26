package com.coderdream.util.video;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GPUInfoRetriever {
    public static void main(String[] args) {
        try {
            // 执行nvidia - smi命令
            Process process = Runtime.getRuntime().exec("nvidia - smi");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine())!= null) {
                System.out.println(line);
            }
            reader.close();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
