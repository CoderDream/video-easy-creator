package com.coderdream.util.hexo;

import com.coderdream.util.cd.CdConstants;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.coderdream.util.proxy.OperatingSystem;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CmdUtil3 {

    public static void main(String[] args) {

        // 执行多条命令，先删除index.lock 文件，再进行后续操作
        String filePath = OperatingSystem.getHexoFolder() + ".deploy_git" + File.separator + ".git" + File.separator + "index.lock";
        File fileLock = new File(filePath);
        if (fileLock.exists()) {
            boolean delete = fileLock.delete();
            if (delete) {
                System.out.println("文件已删除");
            }
        }

        String path = CdConstants.RESOURCES_BASE_PATH + "\\cmd\\cmd.bat";
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("文件不存在");
        }

        Runtime run = Runtime.getRuntime();
        Process process = null;
        BufferedReader reader = null;
        try {
            // 使用 cmd /c 执行命令，命令执行完后关闭 cmd 窗口
            process = run.exec("cmd.exe /c start " + file.getAbsolutePath());
            InputStream in = process.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 确保资源被正确释放
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
        System.out.println("Main method finished");
    }
}
