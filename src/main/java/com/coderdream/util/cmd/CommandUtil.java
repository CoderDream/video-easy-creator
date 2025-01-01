package com.coderdream.util.cmd;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;


/**
 * 命令执行工具类，用于执行 Windows 控制台命令
 */
@Slf4j
public class CommandUtil {


  /**
   * 执行指定的 Windows 控制台命令，并将执行过程中的输出显示在控制台中，并记录执行时间
   *
   * @param command 要执行的命令字符串
   */
  public static void executeCommand(String command) {
    LocalDateTime startTime = LocalDateTime.now(); // 记录开始时间

    try (BufferedReader reader = getCommandReader(command)) {
      log.info("开始执行命令: {}", command);
      String line;
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
      }
      log.info("命令执行完成: {}", command);
      Duration duration = Duration.between(startTime,
        LocalDateTime.now());// 计算执行时长
      String formattedTime = formatDuration(duration);// 格式化时长为时分秒
      log.info("命令 {} 执行耗时：{}", command, formattedTime); // 记录执行耗时

    } catch (IOException e) {
      log.error("执行命令时发生IO异常: {}", e.getMessage(), e);
    }
  }

  /**
   * 执行命令并获取 BufferedReader
   *
   * @param command 要执行的命令字符串
   * @return BufferedReader
   */
  private static BufferedReader getCommandReader(String command)
    throws IOException {
    ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c",
      command); // 创建 ProcessBuilder
    builder.redirectErrorStream(true); // 将错误输出重定向到标准输出
    Process process = builder.start(); // 启动进程

    log.debug("进程已启动，正在获取输出流");

    // 获取控制台的字符编码
    Charset consoleCharset = getConsoleCharset();

    return new BufferedReader(
      new InputStreamReader(process.getInputStream(), consoleCharset));
    // 使用控制台的字符编码创建 BufferedReader
  }

  /**
   * 获取控制台字符编码
   *
   * @return Charset
   */
  private static Charset getConsoleCharset() {
    //尝试获取系统属性 "sun.stdout.encoding"
    String consoleEncoding = System.getProperty("sun.stdout.encoding");
    if (consoleEncoding != null && !consoleEncoding.isEmpty()) {
      try {
        return Charset.forName(consoleEncoding);
      } catch (Exception e) {
        log.warn("无法识别的字符编码 {} ，使用默认的UTF-8", consoleEncoding, e);
      }
    }

    //如果获取不到，则使用默认的 UTF-8
//    log.warn("无法获取控制台字符编码，使用默认的UTF-8");
    return StandardCharsets.UTF_8;
  }


  /**
   * 格式化时长为时分秒
   *
   * @param duration 时长
   * @return 格式化后的时长字符串  HH:mm:ss
   */
  private static String formatDuration(Duration duration) {
    long seconds = duration.getSeconds();
    long absSeconds = Math.abs(seconds);
    return String.format("%02d:%02d:%02d",
      absSeconds / 3600, (absSeconds % 3600) / 60, absSeconds % 60);
  }

  public static void main(String[] args) {
    String command1 = "dir"; // 列出当前目录
    log.info("执行命令: {}", command1);
    executeCommand(command1);

    String command2 = "ipconfig"; // 查看IP配置
    log.info("执行命令: {}", command2);
    executeCommand(command2);

    String command3 = "cd test"; // 一个错误的命令
    log.info("执行命令: {}", command3);
    executeCommand(command3);
    String command4 = "chcp";
    log.info("执行命令: {}", command4);
    executeCommand(command4);
  }
}
