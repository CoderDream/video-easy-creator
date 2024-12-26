package com.coderdream.util.cmd;

import lombok.extern.slf4j.Slf4j;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *  命令行工具类
 */
@Slf4j
public class CommandUtil1 {


    /**
     * 执行命令行命令并返回输出结果
     *
     * @param command 要执行的命令行命令
     * @return 命令输出结果，如果执行失败则返回null
     */
    public static String executeCommand(String command) {
        log.info("开始执行命令: {}", command);
        StringBuilder result = new StringBuilder();
        Process process = null;
        BufferedReader input = null;
        try {
            Runtime rt = Runtime.getRuntime();
            // 执行命令行命令
            process = rt.exec(command);
            // 获取命令输出的输入流，并指定字符编码为GBK，以正确处理中文输出
            input = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
            String line;
            // 逐行读取命令输出并追加到结果中
            while ((line = input.readLine()) != null) {
                result.append(line).append(System.lineSeparator()); // 添加换行符，保证输出格式清晰
            }
            // 等待命令执行完成，并获取退出码
            int exitVal = process.waitFor();
            if (exitVal == 0) {
                 log.info("命令执行成功，退出码：{}", exitVal);
                return result.toString();
            } else {
                log.error("命令执行失败，退出码：{}", exitVal);
                 // 如果执行失败，需要读取错误流中的内容
                 BufferedReader errorInput = new BufferedReader(new InputStreamReader(process.getErrorStream(), "GBK"));
                 StringBuilder errorMsg = new StringBuilder();
                 String errorLine;
                 while ((errorLine = errorInput.readLine()) != null) {
                     errorMsg.append(errorLine).append(System.lineSeparator());
                 }
                log.error("错误信息: {}", errorMsg);
                return null;
            }


        } catch (IOException | InterruptedException e) {
            log.error("执行命令 {} 时发生异常: ",command, e);
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
     * 关闭输入流
     * @param input 需要关闭的输入流
     */
    private static void closeStream(BufferedReader input) {
        if(input != null) {
            try {
                input.close();
            } catch (IOException e) {
               log.error("关闭输入流失败",e);
            }
        }
    }

    public static void main(String[] args) {
          // 执行ping命令和dir命令
//        String command = "cmd /c ping www.baidu.com && dir";
//        String result = CommandUtil1.executeCommand(command);
//        if (result != null) {
//            System.out.println("命令执行结果:");
//            System.out.println(result);
//        } else {
//            System.out.println("命令执行失败!");
//        }


        // 执行其他exe程序
        String command2 = "cmd /c cd D:/04_GitHub/hexo-project/Hexo-BlueLake-Blog/ && hexo g && hexo d" ;// CdConstants.RESOURCES_BASE_PATH + "\\cmd\\cmd.bat";
        String result2 = CommandUtil1.executeCommand(command2);
        if (result2 != null) {
            System.out.println("命令执行结果:");
            System.out.println(result2);
        } else {
            System.out.println("命令执行失败!");
        }

    }
}
