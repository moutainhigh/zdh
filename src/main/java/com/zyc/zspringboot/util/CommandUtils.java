package com.zyc.zspringboot.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

/**
 * 执行系统命令工具类
 * 
 * @author Storm
 *
 */
public class CommandUtils {

    private static final String DEFAULT_CHARSET = "GBK";

    /**
     * 执行指定命令
     * 
     * @param command 命令
     * @return 命令执行完成返回结果
     * @throws IOException 失败时抛出异常，由调用者捕获处理
     */
    public static String exeCommand(String command) throws IOException {
        try (
                ByteArrayOutputStream out = new ByteArrayOutputStream();
        ) {
            int exitCode = exeCommand(command, out);
            if (exitCode == 0) {
                System.out.println("命令运行成功!");
            } else {
                System.out.println("命令运行失败!");
            }
            return out.toString(DEFAULT_CHARSET);
        }
    }

    /**
     * 执行指定命令，输出结果到指定输出流中
     * 
     * @param command 命令
     * @param out 执行结果输出流
     * @return 执行结果状态码：执行成功返回0
     * @throws ExecuteException 失败时抛出异常，由调用者捕获处理
     * @throws IOException 失败时抛出异常，由调用者捕获处理
     */
    public static int exeCommand(String command, OutputStream out) throws ExecuteException, IOException {
        CommandLine commandLine = CommandLine.parse(command);
        PumpStreamHandler pumpStreamHandler = null;
        if (null == out) {
            pumpStreamHandler = new PumpStreamHandler();
        } else {
            pumpStreamHandler = new PumpStreamHandler(out);
        }

        // 设置超时时间为10秒
        ExecuteWatchdog watchdog = new ExecuteWatchdog(10000);

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(pumpStreamHandler);
        executor.setWatchdog(watchdog);

        return executor.execute(commandLine);
    }

    public static void main(String[] args) {
        try {
            String result = exeCommand("ipconfig /all");
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}