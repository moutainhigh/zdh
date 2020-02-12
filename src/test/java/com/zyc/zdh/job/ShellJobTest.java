package com.zyc.zdh.job;

import com.zyc.zdh.util.CommandUtils;
import org.junit.Test;

public class ShellJobTest {
    @Test
    public void run() throws Exception {

        String[] a={"/bin/sh","-c","[ -f C:/Users/zhaoyachao/Desktop/hive-site.xml ] && echo yes || echo no"};
        String[] b={"c:/Windows/System32/cmd.exe", "ping www.baidu.com"};

        String c="if exist d:/postal_fund_app.jar (echo a)";
        String d="dir c:/Users/zhaoyachao/Desktop/monitor.sh";
        System.out.println(CommandUtils.exeCommand("dir"));


    }

}