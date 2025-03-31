package com.coderdream.util.cmd;

import com.coderdream.util.cd.CdTimeUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * 命令执行工具类，用于执行 Windows 控制台命令
 */
@Slf4j
public class CommandUtil02 {

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
            Duration duration = Duration.between(startTime, LocalDateTime.now());// 计算执行时长
            String formattedTime = CdTimeUtil.formatDuration(duration.toMillis());;// 格式化时长为时分秒
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
   * @throws IOException
   */
    private static BufferedReader getCommandReader(String command) throws IOException {
        ProcessBuilder builder;
        List<String> commandList;

        // 根据操作系统选择不同的命令前缀
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            // Windows 系统
            commandList = Arrays.asList("cmd.exe", "/c", command);
        } else {
            // macOS 或 Linux 系统
            // 使用 bash -c 可以执行更复杂的命令，包括管道、重定向等
            commandList = Arrays.asList("bash", "-c", command);
        }

        builder = new ProcessBuilder(commandList);
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
       // 优先尝试获取系统属性指定的控制台编码
       String charsetName = System.getProperty("sun.stdout.encoding");
       if (charsetName != null) {
           try {
               return Charset.forName(charsetName);
           } catch (IllegalArgumentException e) {
               log.warn("指定的控制台编码无效: {}", charsetName, e);
           }
       }

        charsetName = System.getProperty("native.encoding");
       if (charsetName != null) {
           try {
               return Charset.forName(charsetName);
           } catch (IllegalArgumentException e) {
               log.warn("指定的控制台编码无效: {}", charsetName, e);
           }
       }

       // 如果系统属性未指定，则使用默认字符集
       log.info("使用默认字符集");
       return Charset.defaultCharset();
   }



    public static void main(String[] args) {
        // Windows specific commands
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            String command1 = "dir"; // 列出当前目录
            log.info("执行命令: {}", command1);
            executeCommand(command1);

            String command2 = "ipconfig"; // 查看IP配置
            log.info("执行命令: {}", command2);
            executeCommand(command2);

            String command3 = "cd test"; // cd 命令在 Windows 和 *nix 系统中的行为略有不同，但都有效
            log.info("执行命令: {}", command3);
            executeCommand(command3);

            String command4 = "chcp";
            log.info("执行命令: {}", command4);
            executeCommand(command4);
        } else { // macOS and Linux
//            String command1 = "ls -l"; // 列出当前目录
//            log.info("执行命令: {}", command1);
//            executeCommand(command1);
//
//            String command2 = "ifconfig"; // 查看IP配置 (或 ip addr)
//            log.info("执行命令: {}", command2);
//            executeCommand(command2);
//
//            String command3 = "cd test"; // cd 命令
//            log.info("执行命令: {}", command3);
//            executeCommand(command3);
//
//            String command4 = "locale charmap"; // 类似于 chcp, 查看字符编码
//            log.info("执行命令: {}", command4);
//            executeCommand(command4);

          String command5 = "python3 -m aeneas.tools.execute_task /Users/coderdream/Documents/EnBook002/一輩子夠用的英語口語大全集-EP-10-情緒/一輩子夠用的英語口語大全集-EP-10-情緒.MP3 /Volumes/System/0000/EnBook002/Chapter010/Chapter010_total_cht.txt \"task_language=eng|os_task_file_format=srt|is_text_type=plain\" /Users/coderdream/Documents/EnBook002/一輩子夠用的英語口語大全集-EP-10-情緒/一輩子夠用的英語口語大全集-EP-10-情緒.srt";
          log.info("执行命令: {}", command5);
          executeCommand(command5);

        }
    }
}
