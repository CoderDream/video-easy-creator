package com.coderdream.util.hexo;

import com.coderdream.util.cd.CdConstants;
import java.io.File;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CmdUtil5 {

  public static void main(String[] args) {

    // 执行多条命令，先删除index.lock 文件，再进行后续操作
    String filePath = "D:/04_GitHub/hexo-project/Hexo-BlueLake-Blog/.deploy_git/.git/index.lock";
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
    try {
      //run.exec("cmd /k shutdown -s -t 3600");
      Process process = run.exec("cmd.exe /k start " + file.getAbsolutePath());
      InputStream in = process.getInputStream();
      while (in.read() != -1) {
        System.out.println(in.read());
      }
      in.close();
      process.waitFor();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
