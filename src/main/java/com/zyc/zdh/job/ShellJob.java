package com.zyc.zdh.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zyc.zdh.dao.QuartzJobMapper;
import com.zyc.zdh.entity.QuartzJobInfo;
import com.zyc.zdh.entity.ZdhInfo;
import com.zyc.zdh.entity.ZdhLogs;
import com.zyc.zdh.quartz.QuartzManager2;
import com.zyc.zdh.service.EtlTaskService;
import com.zyc.zdh.service.ZdhLogsService;
import com.zyc.zdh.service.impl.DataSourcesServiceImpl;
import com.zyc.zdh.util.CommandUtils;
import com.zyc.zdh.util.DateUtil;
import com.zyc.zdh.util.HttpUtil;
import com.zyc.zdh.util.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ShellJob extends JobCommon {

    public static void run(QuartzJobInfo quartzJobInfo) {

        logger.info("开始执行[SHELL] JOB");
        //debugInfo(quartzJobInfo);

        //last_status 表示 finish,etl,error
        //finish 表示成功,etl 表示正在处理,error 表示失败
        if (quartzJobInfo.getLast_status() != null && quartzJobInfo.getLast_status().equals("etl")) {
            logger.info("[SHELL] JOB ,当前任务正在处理中");
            return;
        }

        //error 状态  next_time 减一天
        if (quartzJobInfo.getLast_status() != null && quartzJobInfo.getLast_status().equals("error")) {
            logger.info("[SHELL] JOB ,上次任务处理失败,将重新执行");
            Date next = quartzJobInfo.getNext_time();
            logger.info("执行时间减一天");
            quartzJobInfo.setNext_time(DateUtil.add(next, -1));
        }

        //finish成功状态 判断next_time 是否超过结束日期,超过，删除任务,更新状态--具体操作 见每个单独的逻辑

        if (quartzJobInfo.getJob_model().equals(JobModel.TIME_SEQ.getValue())) {
            runTimeSeq(quartzJobInfo);
        } else if (quartzJobInfo.getJob_model().equals(JobModel.ONCE.getValue())) {
            runOnce(quartzJobInfo);
        } else if (quartzJobInfo.getJob_model().equals(JobModel.REPEAT.getValue())) {
            runRepeat(quartzJobInfo);
        }
    }


    private static Boolean shellCommand(QuartzJobInfo quartzJobInfo) {
        Boolean exe_status = true;
        //执行命令
        try {
            //当前只支持检查文件是否存在 if [ ! -f "/data/filename" ];then echo "文件不存在"; else echo "true"; fi
            //日期替换zdh.date => yyyy-MM-dd 模式
            //日期替换zdh.date.nodash=> yyyyMMdd 模式
            logger.info("目前支持日期参数2种模式:zdh.date => yyyy-MM-dd ,zdh.date.nodash=> yyyyMMdd ");
            if (!quartzJobInfo.getCommand().equals("")) {
                if (quartzJobInfo.getLast_time() == null) {
                    //第一次执行,下次执行时间为起始时间+1
                    if (quartzJobInfo.getStart_time() == null) {
                        logger.info("[SHELL] JOB ,开始日期为空设置当前日期为开始日期");
                        quartzJobInfo.setStart_time(new Date());
                    }
                    quartzJobInfo.setNext_time(quartzJobInfo.getStart_time());
                }
                String date_nodash = DateUtil.formatNodash(quartzJobInfo.getNext_time());
                String date = DateUtil.format(quartzJobInfo.getNext_time());
                logger.info("[SHELL] JOB ,COMMAND:" + quartzJobInfo.getCommand());
                String result = "fail";
                if (quartzJobInfo.getCommand().trim().equals("")) {
                    result = "success";
                } else {
                    result = CommandUtils.exeCommand(quartzJobInfo.getCommand().
                            replace("zdh.date.nodash", date_nodash).
                            replace("zdh.date", date));
                }
                logger.info("[SHELL] JOB ,执行结果:" + result.trim());
                if (!result.trim().contains("success")) {
                    throw new Exception("文件不存在");
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            exe_status = false;
        }
        return exe_status;
    }


    /**
     * 执行时间序列任务,会根据配置的起始时间和结束时间 按天执行
     *
     * @param quartzJobInfo
     */
    private static void runTimeSeq(QuartzJobInfo quartzJobInfo) {
        logger.info("[SHELL] JOB,任务模式为[时间序列]");

        QuartzManager2 quartzManager2 = (QuartzManager2) SpringContext.getBean("quartzManager2");
        QuartzJobMapper quartzJobMapper = (QuartzJobMapper) SpringContext.getBean("quartzJobMapper");
        EtlTaskService etlTaskService = (EtlTaskService) SpringContext.getBean("etlTaskServiceImpl");
        DataSourcesServiceImpl dataSourcesServiceImpl = (DataSourcesServiceImpl) SpringContext.getBean("dataSourcesServiceImpl");
        ZdhLogsService zdhLogsService = (ZdhLogsService) SpringContext.getBean("zdhLogsServiceImpl");

        //finish成功状态 判断next_time 是否超过结束日期,超过，删除任务,更新状态
        if (quartzJobInfo.getNext_time() != null && quartzJobInfo.getNext_time().after(quartzJobInfo.getEnd_time())) {
            logger.info("[SHELL] JOB ,下次时间超过结束时间,任务结束");
            quartzJobInfo.setStatus("finish");
            //删除quartz 任务
            quartzManager2.deleteTask(quartzJobInfo, "finish");

            return;
        }

        //finish成功状态 判断次数是否超出,超过，删除任务,更新状态
        if (quartzJobInfo.getCount() == Long.parseLong(quartzJobInfo.getPlan_count().trim())) {
            System.out.println("===================================");
            quartzJobInfo.setStatus("finish");
            //delete 里面包含更新
            quartzManager2.deleteTask(quartzJobInfo, "finish");
            ZdhLogs zdhLogs = new ZdhLogs();
            zdhLogs.setMsg("[JDBC] JOB ,结束调度任务");
            zdhLogs.setJob_id(quartzJobInfo.getJob_id());
            zdhLogs.setLog_time(new Timestamp(new Date().getTime()));
            zdhLogsService.insert(zdhLogs);

            return;
        }


        Boolean is_count = true;
        //判断次数
        quartzJobInfo.setCount(quartzJobInfo.getCount() + 1);
        isCount("SHELL", quartzManager2, quartzJobInfo);

        if (quartzJobInfo.getPlan_count().trim().equals("-1")) {
            logger.info("[SHELL] JOB ,当前任务未设置执行次数限制");
        }
        Boolean exe_status = false;
        exe_status = shellCommand(quartzJobInfo);

        //拼接任务信息发送请求


        String params = quartzJobInfo.getParams().trim();
        String url = "http://127.0.0.1:60001/api/v1/zdh";
        if (!params.equals("")) {
            String value = JSON.parseObject(params).getString("url");
            if (value != null && !value.equals("")) {
                url = value;
            }
            JSONObject json = JSON.parseObject(params);
            String date = DateUtil.format(quartzJobInfo.getNext_time());
            json.put("ETL_DATE", date);
            logger.info("[SHELL] JOB ,处理当前日期,传递参数ETL_DATE 为" + date);
            quartzJobInfo.setParams(json.toJSONString());

        }
        logger.info("[SHELL] JOB ,获取当前的[url]:" + url);

        ZdhInfo zdhInfo = create_zhdInfo(quartzJobInfo, quartzJobMapper, etlTaskService, dataSourcesServiceImpl);
        try {
            if (exe_status == true) {
                logger.info("[SHELL] JOB ,开始发送ETL处理请求");
                HttpUtil.postJSON(url, JSON.toJSONString(zdhInfo));
                ZdhLogs zdhLogs = new ZdhLogs();
                zdhLogs.setJob_id(zdhInfo.getQuartzJobInfo().getJob_id());
                Timestamp lon_time = new Timestamp(new Date().getTime());
                zdhLogs.setLog_time(lon_time);
                zdhLogs.setMsg("[调度平台]:" + JSON.toJSONString(zdhInfo));
                zdhLogsService.insert(zdhLogs);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            exe_status = false;
        }

        if (quartzJobInfo.getLast_time() == null) {
            //第一次执行,下次执行时间为起始时间+1
            quartzJobInfo.setNext_time(quartzJobInfo.getStart_time());
        }

        if (exe_status) {
            System.out.println("[SHELL] JOB ,执行命令成功");
            //如果执行成功则+ 一天
            //next_time <= end_time 继续执行,否则 修改任务状态,删除quartz 中的任务
            //next_time +1天
            Date last_time = quartzJobInfo.getNext_time();
            quartzJobInfo.setLast_time(last_time);
            Date next_time = DateUtil.add(last_time, 1);
            quartzJobInfo.setNext_time(next_time);
            quartzJobInfo.setLast_status("etl");

            if (quartzJobInfo.getEnd_time() == null) {
                logger.info("[SHELL] JOB ,结束日期为空设置当前日期为结束日期");
                quartzJobInfo.setEnd_time(new Date());
            }

            //更新任务信息
            debugInfo(quartzJobInfo);
        }
        quartzJobMapper.updateByPrimaryKey(quartzJobInfo);

    }


    /**
     * 执行一次任务
     *
     * @param quartzJobInfo
     */
    private static void runOnce(QuartzJobInfo quartzJobInfo) {

        logger.info("[SHELL] JOB,任务模式为[ONCE]");
        Boolean is_count = true;
        //判断次数
        quartzJobInfo.setCount(quartzJobInfo.getCount() + 1);
        if (!quartzJobInfo.getPlan_count().trim().equals("") && !quartzJobInfo.getPlan_count().trim().equals("-1")) {
            //任务有次数限制,满足添加说明这是最后一次任务
            System.out.println(quartzJobInfo.getCount() + "================" + quartzJobInfo.getPlan_count().trim());
            if (quartzJobInfo.getCount() > Long.parseLong(quartzJobInfo.getPlan_count().trim())) {
                logger.info("[SHELL] JOB 检测到任务次数超过限制,直接返回结束");
                return;
            }
            if (quartzJobInfo.getCount() == Long.parseLong(quartzJobInfo.getPlan_count().trim())) {
                logger.info("[SHELL] JOB ,当前执行的任务是最后一次任务,设置调度任务的状态为[finish]");
                quartzJobInfo.setStatus("finish");
            }

        }

        if (quartzJobInfo.getPlan_count().trim().equals("-1")) {
            logger.info("[SHELL] JOB ,当前任务未设置执行次数限制");
        }
        Boolean exe_status = false;
        exe_status = shellCommand(quartzJobInfo);
        //拼接任务信息发送请求

        QuartzManager2 quartzManager2 = (QuartzManager2) SpringContext.getBean("quartzManager2");
        QuartzJobMapper quartzJobMapper = (QuartzJobMapper) SpringContext.getBean("quartzJobMapper");
        EtlTaskService etlTaskService = (EtlTaskService) SpringContext.getBean("etlTaskServiceImpl");
        DataSourcesServiceImpl dataSourcesServiceImpl = (DataSourcesServiceImpl) SpringContext.getBean("dataSourcesServiceImpl");
        ZdhLogsService zdhLogsService = (ZdhLogsService) SpringContext.getBean("zdhLogsServiceImpl");

        String params = quartzJobInfo.getParams().trim();
        String url = "http://127.0.0.1:60001/api/v1/zdh";
        if (!params.equals("")) {
            String value = JSON.parseObject(params).getString("url");
            if (value != null && !value.equals("")) {
                url = value;
            }
            JSONObject json = JSON.parseObject(params);
            String date = DateUtil.format(quartzJobInfo.getNext_time());
            json.put("ETL_DATE", date);
            logger.info("[SHELL] JOB ,处理当前日期,传递参数ETL_DATE 为" + date);
            quartzJobInfo.setParams(json.toJSONString());

        }
        logger.info("[SHELL] JOB ,获取当前的[url]:" + url);

        ZdhInfo zdhInfo = create_zhdInfo(quartzJobInfo, quartzJobMapper, etlTaskService, dataSourcesServiceImpl);
        try {
            if (exe_status == true) {
                logger.info("[SHELL] JOB ,开始发送ETL处理请求");
                HttpUtil.postJSON(url, JSON.toJSONString(zdhInfo));
                ZdhLogs zdhLogs = new ZdhLogs();
                zdhLogs.setJob_id(zdhInfo.getQuartzJobInfo().getJob_id());
                Timestamp lon_time = new Timestamp(new Date().getTime());
                zdhLogs.setLog_time(lon_time);
                zdhLogs.setMsg("[调度平台]:" + JSON.toJSONString(zdhInfo));
                zdhLogsService.insert(zdhLogs);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            exe_status = false;
        }

        if (quartzJobInfo.getLast_time() == null) {
            //第一次执行,下次执行时间为起始时间+1
            quartzJobInfo.setNext_time(quartzJobInfo.getStart_time());
        }

        if (exe_status) {
            System.out.println("[SHELL] JOB ,执行命令成功");
            //如果执行成功则+ 一天
            //next_time <= end_time 继续执行,否则 修改任务状态,删除quartz 中的任务
            //next_time 还为当前天
            Date last_time = quartzJobInfo.getNext_time();
            quartzJobInfo.setLast_time(last_time);
            Date next_time = last_time;
            quartzJobInfo.setNext_time(next_time);
            quartzJobInfo.setLast_status("etl");

            if (quartzJobInfo.getEnd_time() == null) {
                logger.info("[SHELL] JOB ,结束日期为空设置当前日期为结束日期");
                quartzJobInfo.setEnd_time(new Date());
            }

            //更新任务信息
            debugInfo(quartzJobInfo);
            System.out.println("===================================");
            quartzJobInfo.setStatus("finish");
            //delete 里面包含更新
            quartzManager2.deleteTask(quartzJobInfo, "finish");
            ZdhLogs zdhLogs = new ZdhLogs();
            zdhLogs.setMsg("[JDBC] JOB ,结束调度任务");
            zdhLogs.setJob_id(quartzJobInfo.getJob_id());
            zdhLogs.setLog_time(new Timestamp(new Date().getTime()));
            zdhLogsService.insert(zdhLogs);
        } else {
            //如果执行失败 next_time 时间不变,last_time 不变
            quartzJobMapper.updateByPrimaryKey(quartzJobInfo);
        }


    }


    /**
     * 重复执行
     *
     * @param quartzJobInfo
     */
    private static void runRepeat(QuartzJobInfo quartzJobInfo) {

        logger.info("[SHELL] JOB,任务模式为[重复执行模式]");

        QuartzManager2 quartzManager2 = (QuartzManager2) SpringContext.getBean("quartzManager2");
        QuartzJobMapper quartzJobMapper = (QuartzJobMapper) SpringContext.getBean("quartzJobMapper");
        EtlTaskService etlTaskService = (EtlTaskService) SpringContext.getBean("etlTaskServiceImpl");
        DataSourcesServiceImpl dataSourcesServiceImpl = (DataSourcesServiceImpl) SpringContext.getBean("dataSourcesServiceImpl");
        ZdhLogsService zdhLogsService = (ZdhLogsService) SpringContext.getBean("zdhLogsServiceImpl");

        if (quartzJobInfo.getCount() == Long.parseLong(quartzJobInfo.getPlan_count().trim())) {
            System.out.println("===================================");
            quartzJobInfo.setStatus("finish");
            //delete 里面包含更新
            quartzManager2.deleteTask(quartzJobInfo, "finish");
            ZdhLogs zdhLogs = new ZdhLogs();
            zdhLogs.setMsg("[JDBC] JOB ,结束调度任务");
            zdhLogs.setJob_id(quartzJobInfo.getJob_id());
            zdhLogs.setLog_time(new Timestamp(new Date().getTime()));
            zdhLogsService.insert(zdhLogs);

            return;
        }

        Boolean is_count = true;
        //判断次数
        quartzJobInfo.setCount(quartzJobInfo.getCount() + 1);

        isCount("SHELL", quartzManager2, quartzJobInfo);

        if (quartzJobInfo.getPlan_count().trim().equals("-1")) {
            logger.info("[SHELL] JOB ,当前任务未设置执行次数限制,此任务将一直执行");
        }
        Boolean exe_status = false;
        exe_status = shellCommand(quartzJobInfo);

        //拼接任务信息发送请求


        String params = quartzJobInfo.getParams().trim();
        String url = "http://127.0.0.1:60001/api/v1/zdh";
        if (!params.equals("")) {
            String value = JSON.parseObject(params).getString("url");
            if (value != null && !value.equals("")) {
                url = value;
            }
            JSONObject json = JSON.parseObject(params);
            String date = DateUtil.format(quartzJobInfo.getNext_time());
            json.put("ETL_DATE", date);
            logger.info("[SHELL] JOB ,处理当前日期,传递参数ETL_DATE 为" + date);
            quartzJobInfo.setParams(json.toJSONString());

        }
        logger.info("[SHELL] JOB ,获取当前的[url]:" + url);

        ZdhInfo zdhInfo = create_zhdInfo(quartzJobInfo, quartzJobMapper, etlTaskService, dataSourcesServiceImpl);
        try {
            if (exe_status == true) {
                logger.info("[SHELL] JOB ,开始发送ETL处理请求");
                HttpUtil.postJSON(url, JSON.toJSONString(zdhInfo));
                ZdhLogs zdhLogs = new ZdhLogs();
                zdhLogs.setJob_id(zdhInfo.getQuartzJobInfo().getJob_id());
                Timestamp lon_time = new Timestamp(new Date().getTime());
                zdhLogs.setLog_time(lon_time);
                zdhLogs.setMsg("[调度平台]:" + JSON.toJSONString(zdhInfo));
                zdhLogsService.insert(zdhLogs);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            exe_status = false;
        }

        if (quartzJobInfo.getLast_time() == null) {
            //第一次执行,下次执行时间为起始时间+1
            quartzJobInfo.setNext_time(quartzJobInfo.getStart_time());
        }

        if (exe_status) {
            System.out.println("[SHELL] JOB ,执行命令成功");
            //如果执行成功则+ 一天
            //next_time <= end_time 继续执行,否则 修改任务状态,删除quartz 中的任务
            //next_time 还为起始时间
            Date last_time = quartzJobInfo.getNext_time();
            quartzJobInfo.setLast_time(last_time);
            Date next_time = last_time;
            quartzJobInfo.setNext_time(next_time);
            quartzJobInfo.setLast_status("etl");

            if (quartzJobInfo.getEnd_time() == null) {
                logger.info("[SHELL] JOB ,结束日期为空设置当前日期为结束日期");
                quartzJobInfo.setEnd_time(new Date());
            }
        }
        //更新任务信息
        debugInfo(quartzJobInfo);

        //如果执行失败 next_time 时间不变,last_time 不变
        quartzJobMapper.updateByPrimaryKey(quartzJobInfo);


    }

    public static String run(String[] command) throws IOException {
        Scanner input = null;
        String result = "";
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
            try {
                //等待命令执行完成
                process.waitFor(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            InputStream is = process.getInputStream();
            input = new Scanner(is);
            while (input.hasNextLine()) {
                result += input.nextLine() + "\n";
            }
            //加上命令本身，打印出来
        } finally {
            if (input != null) {
                input.close();
            }
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }

}
