package com.coderdream.util.hexo;

import com.coderdream.util.cd.CdConstants;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CmdUtil {

  /**
   * 执行指定路径的命令，输出日志，并计算命令执行耗时。
   *
   * @param commandFilePath 命令文件的绝对路径。
   */
  public static void executeCommand(String commandFilePath) {
    File commandFile = new File(commandFilePath);
    if (!commandFile.exists()) {
      log.error("命令文件不存在：{}", commandFilePath);
      return; // 文件不存在，直接返回
    }

    long startTime = System.currentTimeMillis();
    ProcessBuilder processBuilder = new ProcessBuilder();
    // 使用 ProcessBuilder 构建命令，避免字符串拼接错误
    // 注意，对于 .bat 文件，直接执行即可，无需 cmd.exe /k start
    List<String> commandList = Arrays.asList(commandFile.getAbsolutePath());

    processBuilder.command(commandList);
    // 合并错误输出和标准输出
    processBuilder.redirectErrorStream(true);


    Process process = null;
    BufferedReader reader = null;
    try {
      log.info("开始执行命令：{}", String.join(" ", commandList));

      process = processBuilder.start();
      reader = new BufferedReader(
          new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
      String line;
      while ((line = reader.readLine()) != null) {
        log.info("命令输出：{}", line);
      }

      int exitCode = process.waitFor();
      long endTime = System.currentTimeMillis();
      long elapsedTime = endTime - startTime;

      if (exitCode == 0) {
        log.info("命令执行成功，耗时：{}ms，退出码：{}", elapsedTime, exitCode);
      } else {
        log.error("命令执行失败，退出码：{}，耗时：{}ms", exitCode, elapsedTime);
      }
    } catch (IOException e) {
      log.error("IO 异常：执行命令时发生错误", e);
    } catch (InterruptedException e) {
      log.error("线程中断异常：命令执行被中断", e);
      Thread.currentThread().interrupt(); // 恢复中断状态
    } finally {
      // 关闭资源
      if(reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          log.error("关闭 BufferedReader 发生异常", e);
        }
      }
      if(process != null) {
        process.destroy();
      }
    }
  }

  public static void main(String[] args) {
    String path = CdConstants.RESOURCES_BASE_PATH + "\\cmd\\cmd.bat";
    executeCommand(path);
  }
}
