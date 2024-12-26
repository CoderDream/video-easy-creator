package com.coderdream.util.video;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class GPUMetrics {

    /**
     * 获取 NVIDIA GPU 核心数（CUDA 核心数）
     *
     * @return GPU 核心数
     */
    public static int getGpuCoreCount() {
        try {
            // 执行 nvidia-smi 命令
            ProcessBuilder processBuilder = new ProcessBuilder("nvidia-smi", "--query-gpu=count", "--format=csv,noheader,nounits");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            if (line != null) {
                return Integer.parseInt(line.trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0; // 如果无法获取 GPU 信息，返回 0
    }

    public static void main(String[] args) {
        int gpuCoreCount = getGpuCoreCount();
        System.out.println("GPU 核心数: " + gpuCoreCount);
    }
}
