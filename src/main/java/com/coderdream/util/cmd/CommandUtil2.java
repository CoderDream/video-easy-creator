package com.coderdream.util.cmd;

import java.io.File;
import lombok.extern.slf4j.Slf4j;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * 命令行工具类
 */
@Slf4j
public class CommandUtil2 {


  /**
   * 执行多条命令行命令并返回总的输出结果
   *
   * @param commands 要执行的命令行命令列表
   * @return 命令输出结果，如果执行失败则返回null
   */
  public static String executeCommands(List<String> commands) {
    StringBuilder totalResult = new StringBuilder();
    Instant startTime = Instant.now();
    for (String command : commands) {
      String result = executeCommand(command);
      if (result == null) { // 如果其中一条命令执行失败，则返回null
        Instant endTime = Instant.now();
        String elapsedTime = formatElapsedTime(
          Duration.between(startTime, endTime));
        log.error("执行命令失败, 耗时: {}", elapsedTime);
        return null;
      }
      totalResult.append(result);
    }
    Instant endTime = Instant.now();
    String elapsedTime = formatElapsedTime(
      Duration.between(startTime, endTime));
    log.info("所有命令执行成功, 耗时: {}", elapsedTime);
    return totalResult.toString();
  }

  /**
   * 执行单条命令行命令并返回输出结果
   *
   * @param command 要执行的命令行命令
   * @return 命令输出结果，如果执行失败则返回null
   */
  public static String executeCommand(String command) {
    log.info("开始执行命令: {}", command);
    Instant startTime = Instant.now(); // 记录命令开始执行的时间
    StringBuilder result = new StringBuilder();
    Process process = null;
    BufferedReader input = null;
    try {
      Runtime rt = Runtime.getRuntime();
      // 执行命令行命令
      process = rt.exec(command);
      // 获取命令输出的输入流，并指定字符编码为GBK，以正确处理中文输出
      input = new BufferedReader(
        new InputStreamReader(process.getInputStream(), "GBK"));
      String line;
      // 逐行读取命令输出并追加到结果中
      while ((line = input.readLine()) != null) {
        result.append(line).append(System.lineSeparator()); // 添加换行符，保证输出格式清晰
      }
      // 等待命令执行完成，并获取退出码
      int exitVal = process.waitFor();
      if (exitVal == 0) {
        Instant endTime = Instant.now();// 记录命令结束执行的时间
        String elapsedTime = formatElapsedTime(
          Duration.between(startTime, endTime));
        log.info("命令执行成功，退出码：{}，耗时：{}", exitVal, elapsedTime);
        return result.toString();
      } else {
        Instant endTime = Instant.now(); // 记录命令结束执行的时间
        String elapsedTime = formatElapsedTime(
          Duration.between(startTime, endTime));
        log.error("命令执行失败，退出码：{}，耗时：{}", exitVal, elapsedTime);
        // 如果执行失败，需要读取错误流中的内容
        BufferedReader errorInput = new BufferedReader(
          new InputStreamReader(process.getErrorStream(), "GBK"));
        StringBuilder errorMsg = new StringBuilder();
        String errorLine;
        while ((errorLine = errorInput.readLine()) != null) {
          errorMsg.append(errorLine).append(System.lineSeparator());
        }
        log.error("错误信息: {}", errorMsg);
        return null;
      }
    } catch (IOException | InterruptedException e) {
      Instant endTime = Instant.now();// 记录命令结束执行的时间
      String elapsedTime = formatElapsedTime(
        Duration.between(startTime, endTime));
      log.error("执行命令 {} 时发生异常，耗时: {}, ", command, elapsedTime, e);
      return null;
    } finally {
      // 确保资源正确释放
      closeStream(input);
      if (process != null) {
        process.destroy();  // 销毁进程
      }
    }
  }


  /**
   * 格式化耗时
   *
   * @param duration 耗时时间
   * @return 格式化后的时间字符串
   */
  private static String formatElapsedTime(Duration duration) {
    long seconds = duration.getSeconds();
    long hours = seconds / 3600;
    long minutes = (seconds % 3600) / 60;
    long secs = seconds % 60;
    return String.format("%02d:%02d:%02d", hours, minutes, secs);

  }

  /**
   * 关闭输入流
   *
   * @param input 需要关闭的输入流
   */
  private static void closeStream(BufferedReader input) {
    if (input != null) {
      try {
        input.close();
      } catch (IOException e) {
        log.error("关闭输入流失败", e);
      }
    }
  }

  public static void main(String[] args) {
    Instant startTime = Instant.now(); // 记录命令开始执行的时间
    // 执行多条命令，先删除index.lock 文件，再进行后续操作
    String filePath = "D:/04_GitHub/hexo-project/Hexo-BlueLake-Blog/.deploy_git/.git/index.lock";
    File file = new File(filePath);
    if (file.exists()) {
      boolean delete = file.delete();
      if (delete) {
        System.out.println("文件已删除");
      }
    }

    List<String> commandList = Arrays.asList(
      "cmd /c cd D:/04_GitHub/hexo-project/Hexo-BlueLake-Blog/ && hexo version",
      "cmd /c cd D:/04_GitHub/hexo-project/Hexo-BlueLake-Blog/ && hexo help"
//      ,
//      "cmd /c cd D:/04_GitHub/hexo-project/Hexo-BlueLake-Blog/ && hexo g",
//      "cmd /c cd D:/04_GitHub/hexo-project/Hexo-BlueLake-Blog/ && hexo d"
    );
//    String result = CommandUtil2.executeCommands(commandList);
//    if (result != null) {
//      System.out.println("多条命令执行结果:");
//      System.out.println(result);
//    } else {
//      System.out.println("多条命令执行失败!");
//    }

    String command3 = "cmd /c cd D:/04_GitHub/hexo-project/Hexo-BlueLake-Blog/ && hexo d";
    String result3 = CommandUtil2.executeCommand(command3);
    if (result3 != null) {
      System.out.println("命令执行结果:");
      System.out.println(result3);
    } else {
      System.out.println("命令执行失败!");
    }

    Instant endTime = Instant.now();// 记录命令结束执行的时间
    String elapsedTime = formatElapsedTime(
      Duration.between(startTime, endTime));
    log.info("命令执行成功，耗时：{}", elapsedTime);
  }
}
