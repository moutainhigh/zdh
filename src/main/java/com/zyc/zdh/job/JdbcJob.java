package com.zyc.zdh.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zyc.zdh.dao.QuartzJobMapper;
import com.zyc.zdh.dao.ZdhHaInfoMapper;
import com.zyc.zdh.entity.*;
import com.zyc.zdh.quartz.QuartzManager2;
import com.zyc.zdh.service.EtlTaskService;
import com.zyc.zdh.service.ZdhLogsService;
import com.zyc.zdh.service.impl.DataSourcesServiceImpl;
import com.zyc.zdh.util.DBUtil;
import com.zyc.zdh.util.DateUtil;
import com.zyc.zdh.util.HttpUtil;
import com.zyc.zdh.util.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class JdbcJob extends JobCommon {


    public static void run(QuartzJobInfo quartzJobInfo) {

        ZdhLogsService zdhLogsService = (ZdhLogsService) SpringContext.getBean("zdhLogsServiceImpl");

        logger.info("开始执行[JDBC] JOB");
        //debugInfo(quartzJobInfo);

        //last_status 表示 finish,etl,error
        //finish 表示成功,etl 表示正在处理,error 表示失败
        if (quartzJobInfo.getLast_status() != null && quartzJobInfo.getLast_status().equals("etl")) {
            logger.info("[JDBC] JOB ,当前任务正在处理中");
            insertLog(quartzJobInfo.getJob_id(),"info",
                    "[JDBC] JOB ,当前任务正在处理中",zdhLogsService);
            return;
        }

        //error 状态  next_time 减一天
        if (quartzJobInfo.getLast_status() != null && quartzJobInfo.getLast_status().equals("error")) {
            logger.info("[JDBC] JOB ,上次任务处理失败,将重新执行,执行时间减一天");
            insertLog(quartzJobInfo.getJob_id(),"info",
                    "[JDBC] JOB ,上次任务处理失败,将重新执行,执行时间减一天",zdhLogsService);
            Date next = quartzJobInfo.getNext_time();
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

    private static Boolean runCommand(QuartzJobInfo quartzJobInfo, ZdhLogsService zdhLogsService) {
        try {
            DBUtil dbUtil = new DBUtil();
            Boolean exe_status = true;
            /// /连接jdbc
            String params = quartzJobInfo.getParams().trim();
            if (params.equals("")) {
                exe_status = false;
                logger.info("参数不可为空,必须包含特定参数,zdh.jdbc.url,zdh.jdbc.driver,zdh.jdbc.username,zdh.jdbc.password");
                insertLog(quartzJobInfo.getJob_id(),"error",
                        "参数不可为空,必须包含特定参数,zdh.jdbc.url,zdh.jdbc.driver,zdh.jdbc.username,zdh.jdbc.password",zdhLogsService);

                return exe_status;
            }
            String url = JSON.parseObject(params).getString("zdh.jdbc.url");
            String driver = JSON.parseObject(params).getString("zdh.jdbc.driver");
            String username = JSON.parseObject(params).getString("zdh.jdbc.username");
            String password = JSON.parseObject(params).getString("zdh.jdbc.password");

            if (url == null || url.equals("") || driver == null || driver.equals("") || username == null || username.equals("") || password == null || password.equals("")) {
                exe_status = false;
                logger.info("[JDBC] JOB ,参数不可为空");
                //插入日志
                insertLog(quartzJobInfo.getJob_id(),"error",
                        "参数不可为空,必须包含特定参数,zdh.jdbc.url,zdh.jdbc.driver,zdh.jdbc.username,zdh.jdbc.password",zdhLogsService);

                return exe_status;
            }

            String command = quartzJobInfo.getCommand();


            if (command.equals("")) {
                exe_status = true;
            }


            if (quartzJobInfo.getLast_time() == null) {
                //第一次执行,下次执行时间为起始时间+1
                if (quartzJobInfo.getStart_time() == null) {
                    logger.info("[JDBC] JOB ,开始日期为空设置当前日期为开始日期");
                    quartzJobInfo.setStart_time(new Date());
                }
                quartzJobInfo.setNext_time(quartzJobInfo.getStart_time());
            }
            String date_nodash = DateUtil.formatNodash(quartzJobInfo.getNext_time());
            String date = DateUtil.format(quartzJobInfo.getNext_time());
            String new_command = command.replace("zdh.date.nodash", date_nodash)
                    .replace("zdh.date", date);
            List<String> results = dbUtil.R(driver, url, username, password, new_command, null);
            if (results.size() >= 1) {
                exe_status = true;
            }
            return exe_status;
        } catch (Exception ex) {
            //插入日志
            insertLog(quartzJobInfo.getJob_id(),"error",
                    "[调度平台]:"+ex.getMessage(),zdhLogsService);
            logger.error(ex.getMessage());
            return false;
        }

    }

    /**
     * 执行时间序列
     *
     * @param quartzJobInfo
     */
    public static void runTimeSeq(QuartzJobInfo quartzJobInfo) {


        QuartzManager2 quartzManager2 = (QuartzManager2) SpringContext.getBean("quartzManager2");
        QuartzJobMapper quartzJobMapper = (QuartzJobMapper) SpringContext.getBean("quartzJobMapper");
        EtlTaskService etlTaskService = (EtlTaskService) SpringContext.getBean("etlTaskServiceImpl");
        DataSourcesServiceImpl dataSourcesServiceImpl = (DataSourcesServiceImpl) SpringContext.getBean("dataSourcesServiceImpl");
        ZdhLogsService zdhLogsService = (ZdhLogsService) SpringContext.getBean("zdhLogsServiceImpl");
        ZdhHaInfoMapper zdhHaInfoMapper=(ZdhHaInfoMapper) SpringContext.getBean("zdhHaInfoMapper");
        //finish成功状态 判断next_time 是否超过结束日期,超过，删除任务,更新状态
        if (quartzJobInfo.getNext_time() != null && quartzJobInfo.getNext_time().after(quartzJobInfo.getEnd_time())) {
            logger.info("[JDBC] JOB ,下次时间超过结束时间,任务结束");
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
            //插入日志
            insertLog(quartzJobInfo.getJob_id(),"info",
                    "[JDBC] JOB ,结束调度任务",zdhLogsService);
            return;
        }


        //判断次数
        quartzJobInfo.setCount(quartzJobInfo.getCount() + 1);
        isCount("JDBC", quartzManager2, quartzJobInfo);

        Boolean exe_status = runCommand(quartzJobInfo, zdhLogsService);

        String params = quartzJobInfo.getParams().trim();
        String url =getZdhUrl(zdhHaInfoMapper);
        if (!params.equals("")) {
            String value = JSON.parseObject(params).getString("url");
            if (value != null && !value.equals("")) {
                url = value;
            }
            JSONObject json = JSON.parseObject(params);
            String date = DateUtil.format(quartzJobInfo.getNext_time());
            json.put("ETL_DATE", date);
            logger.info("[JDBC] JOB ,处理当前日期,传递参数ETL_DATE 为" + date);
            quartzJobInfo.setParams(json.toJSONString());
        }
        logger.info("[JDBC] JOB ,获取当前的[url]:" + url);

        if (exe_status) {
            //查询结果不为空则算成功
            //发送命令
            try {
                ZdhInfo zdhInfo = create_zhdInfo(quartzJobInfo, quartzJobMapper, etlTaskService, dataSourcesServiceImpl);
                HttpUtil.postJSON(url, JSON.toJSONString(zdhInfo));
                exe_status = true;
            } catch (Exception ex) {
                exe_status = false;
                //插入日志
                insertLog(quartzJobInfo.getJob_id(),"error",
                        "[JDBC] JOB ,发送任务到ETL处理失败:" + ex.getMessage(),zdhLogsService);
            }

        }

        if (exe_status) {
            System.out.println("[JDBC] JOB ,执行命令成功");
            //如果执行成功则+ 一天
            //next_time <= end_time 继续执行,否则 修改任务状态,删除quartz 中的任务
            //next_time +1天
            Date last_time = quartzJobInfo.getNext_time();
            quartzJobInfo.setLast_time(last_time);
            Date next_time = DateUtil.add(last_time, 1);
            quartzJobInfo.setNext_time(next_time);

            if (quartzJobInfo.getEnd_time() == null) {
                logger.info("[JDBC] JOB ,结束日期为空设置当前日期为结束日期");
                quartzJobInfo.setEnd_time(new Date());
            }

            if (next_time.after(quartzJobInfo.getEnd_time())) {
                logger.info("[JDBC] JOB ,下次时间超过结束时间,任务结束");
                quartzJobInfo.setStatus("finish");
                //删除quartz 任务
                quartzManager2.deleteTask(quartzJobInfo, "finish");
            }
        }
        //更新任务信息
        debugInfo(quartzJobInfo);
        //如果执行失败 next_time 时间不变,last_time 不变
        quartzJobMapper.updateByPrimaryKey(quartzJobInfo);


    }


    /**
     * 执行一次任务
     *
     * @param quartzJobInfo
     */
    private static void runOnce(QuartzJobInfo quartzJobInfo) {

        logger.info("[JDBC] JOB,任务模式为[ONCE]");

        QuartzManager2 quartzManager2 = (QuartzManager2) SpringContext.getBean("quartzManager2");
        QuartzJobMapper quartzJobMapper = (QuartzJobMapper) SpringContext.getBean("quartzJobMapper");
        EtlTaskService etlTaskService = (EtlTaskService) SpringContext.getBean("etlTaskServiceImpl");
        DataSourcesServiceImpl dataSourcesServiceImpl = (DataSourcesServiceImpl) SpringContext.getBean("dataSourcesServiceImpl");
        ZdhLogsService zdhLogsService = (ZdhLogsService) SpringContext.getBean("zdhLogsServiceImpl");
        ZdhHaInfoMapper zdhHaInfoMapper=(ZdhHaInfoMapper) SpringContext.getBean("zdhHaInfoMapper");
        //判断次数
        quartzJobInfo.setCount(quartzJobInfo.getCount() + 1);
        isCount("JDBC", quartzManager2, quartzJobInfo);


        if (quartzJobInfo.getPlan_count().trim().equals("-1")) {
            logger.info("[JDBC] JOB ,当前任务未设置执行次数限制");
        }
        Boolean exe_status = runCommand(quartzJobInfo, zdhLogsService);
        //拼接任务信息发送请求


        String params = quartzJobInfo.getParams().trim();
        String url =getZdhUrl(zdhHaInfoMapper);
        if (!params.equals("")) {
            String value = JSON.parseObject(params).getString("url");
            if (value != null && !value.equals("")) {
                url = value;
            }
            JSONObject json = JSON.parseObject(params);
            String date = DateUtil.format(quartzJobInfo.getNext_time());
            json.put("ETL_DATE", date);
            logger.info("[JDBC] JOB ,处理当前日期,传递参数ETL_DATE 为" + date);
            quartzJobInfo.setParams(json.toJSONString());

        }
        logger.info("[JDBC] JOB ,获取当前的[url]:" + url);

        ZdhInfo zdhInfo = create_zhdInfo(quartzJobInfo, quartzJobMapper, etlTaskService, dataSourcesServiceImpl);
        try {
            if (exe_status == true) {
                logger.info("[JDBC] JOB ,开始发送ETL处理请求");
                HttpUtil.postJSON(url, JSON.toJSONString(zdhInfo));
                //插入日志
                insertLog(quartzJobInfo.getJob_id(),"debug",JSON.toJSONString(zdhInfo),zdhLogsService);
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
            System.out.println("[JDBC] JOB ,执行命令成功");
            //如果执行成功则+ 一天
            //next_time <= end_time 继续执行,否则 修改任务状态,删除quartz 中的任务
            //next_time 还为当前天
            Date last_time = quartzJobInfo.getNext_time();
            quartzJobInfo.setLast_time(last_time);
            Date next_time = last_time;
            quartzJobInfo.setNext_time(next_time);

            if (quartzJobInfo.getEnd_time() == null) {
                logger.info("[JDBC] JOB ,结束日期为空设置当前日期为结束日期");
                quartzJobInfo.setEnd_time(new Date());
            }

            //更新任务信息
            debugInfo(quartzJobInfo);
            System.out.println("===================================");
            quartzJobInfo.setStatus("finish");
            //delete 里面包含更新
            quartzManager2.deleteTask(quartzJobInfo, "finish");
            //插入日志
            insertLog(quartzJobInfo.getJob_id(),"info","[JDBC] JOB ,结束调度任务",zdhLogsService);
        } else {
            //如果执行失败 next_time 时间不变,last_time 不变
            quartzJobMapper.updateByPrimaryKey(quartzJobInfo);
        }


    }


    /**
     * 执行重复任务
     *
     * @param quartzJobInfo
     */
    private static void runRepeat(QuartzJobInfo quartzJobInfo) {

        logger.info("[JDBC] JOB,任务模式为[重复执行模式]");

        QuartzManager2 quartzManager2 = (QuartzManager2) SpringContext.getBean("quartzManager2");
        QuartzJobMapper quartzJobMapper = (QuartzJobMapper) SpringContext.getBean("quartzJobMapper");
        EtlTaskService etlTaskService = (EtlTaskService) SpringContext.getBean("etlTaskServiceImpl");
        DataSourcesServiceImpl dataSourcesServiceImpl = (DataSourcesServiceImpl) SpringContext.getBean("dataSourcesServiceImpl");
        ZdhLogsService zdhLogsService = (ZdhLogsService) SpringContext.getBean("zdhLogsServiceImpl");
        ZdhHaInfoMapper zdhHaInfoMapper=(ZdhHaInfoMapper) SpringContext.getBean("zdhHaInfoMapper");

        if (quartzJobInfo.getCount() == Long.parseLong(quartzJobInfo.getPlan_count().trim())) {
            System.out.println("===================================");
            quartzJobInfo.setStatus("finish");
            //delete 里面包含更新
            quartzManager2.deleteTask(quartzJobInfo, "finish");
            //插入日志
            insertLog(quartzJobInfo.getJob_id(),"info","[JDBC] JOB ,结束调度任务",zdhLogsService);
            return;
        }

        Boolean is_count = true;
        //判断次数
        quartzJobInfo.setCount(quartzJobInfo.getCount() + 1);
        isCount("JDBC", quartzManager2, quartzJobInfo);

        if (quartzJobInfo.getPlan_count().trim().equals("-1")) {
            logger.info("[JDBC] JOB ,当前任务未设置执行次数限制,此任务将一直执行");
        }
        Boolean exe_status = false;
        exe_status = runCommand(quartzJobInfo, zdhLogsService);

        //拼接任务信息发送请求

        String params = quartzJobInfo.getParams().trim();
        String url =getZdhUrl(zdhHaInfoMapper);
        if (!params.equals("")) {
            String value = JSON.parseObject(params).getString("url");
            if (value != null && !value.equals("")) {
                url = value;
            }
            JSONObject json = JSON.parseObject(params);
            String date = DateUtil.format(quartzJobInfo.getNext_time());
            json.put("ETL_DATE", date);
            logger.info("[JDBC] JOB ,处理当前日期,传递参数ETL_DATE 为" + date);
            quartzJobInfo.setParams(json.toJSONString());

        }
        logger.info("[JDBC] JOB ,获取当前的[url]:" + url);

        ZdhInfo zdhInfo = create_zhdInfo(quartzJobInfo, quartzJobMapper, etlTaskService, dataSourcesServiceImpl);
        try {
            if (exe_status == true) {
                logger.info("[JDBC] JOB ,开始发送ETL处理请求");
                HttpUtil.postJSON(url, JSON.toJSONString(zdhInfo));
                //插入日志
                insertLog(zdhInfo.getQuartzJobInfo().getJob_id(),"debug","[调度平台]:" +JSON.toJSONString(zdhInfo),zdhLogsService);
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
            System.out.println("[JDBC] JOB ,执行命令成功");
            //如果执行成功则+ 一天
            //next_time <= end_time 继续执行,否则 修改任务状态,删除quartz 中的任务
            //next_time 还为起始时间
            Date last_time = quartzJobInfo.getNext_time();
            quartzJobInfo.setLast_time(last_time);
            Date next_time = last_time;
            quartzJobInfo.setNext_time(next_time);

            if (quartzJobInfo.getEnd_time() == null) {
                logger.info("[JDBC] JOB ,结束日期为空设置当前日期为结束日期");
                quartzJobInfo.setEnd_time(new Date());
            }
        }
        //更新任务信息
        debugInfo(quartzJobInfo);
        //如果执行失败 next_time 时间不变,last_time 不变
        quartzJobMapper.updateByPrimaryKey(quartzJobInfo);


    }

}
