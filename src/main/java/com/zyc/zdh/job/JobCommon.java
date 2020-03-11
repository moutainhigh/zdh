package com.zyc.zdh.job;

import com.alibaba.fastjson.JSON;
import com.zyc.zdh.dao.QuartzJobMapper;
import com.zyc.zdh.dao.ZdhHaInfoMapper;
import com.zyc.zdh.entity.*;
import com.zyc.zdh.quartz.QuartzManager2;
import com.zyc.zdh.service.EtlTaskService;
import com.zyc.zdh.service.ZdhLogsService;
import com.zyc.zdh.service.impl.DataSourcesServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class JobCommon {

    public static Logger logger= LoggerFactory.getLogger(JobCommon.class) ;


    /**
     *
     * @param job SHELL,JDBC,FTP,CLASS
     * @param quartzManager2
     * @param quartzJobInfo
     */
    public static void isCount(String job,QuartzManager2 quartzManager2, QuartzJobInfo quartzJobInfo){
        if (!quartzJobInfo.getPlan_count().trim().equals("") && !quartzJobInfo.getPlan_count().trim().equals("-1")) {
            //任务有次数限制,满足添加说明这是最后一次任务
            System.out.println(quartzJobInfo.getCount() + "================" + quartzJobInfo.getPlan_count().trim());
            if (quartzJobInfo.getCount() > Long.parseLong(quartzJobInfo.getPlan_count().trim())) {
                logger.info("["+job+"] JOB 检测到任务次数超过限制,删除任务并直接返回结束");
                quartzManager2.deleteTask(quartzJobInfo, "finish");
                return;
            }
            if (quartzJobInfo.getCount() == Long.parseLong(quartzJobInfo.getPlan_count().trim())) {
                logger.info("["+job+"] JOB ,当前执行的任务是最后一次任务,设置调度任务的状态为[finish]");
                quartzJobInfo.setStatus("finish");
            }

        }
    }

    public static void debugInfo(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (int i = 0, len = fields.length; i < len; i++) {
            // 对于每个属性，获取属性名
            String varName = fields[i].getName();
            try {
                // 获取原来的访问控制权限
                boolean accessFlag = fields[i].isAccessible();
                // 修改访问控制权限
                fields[i].setAccessible(true);
                // 获取在对象f中属性fields[i]对应的对象中的变量
                Object o;
                try {
                    o = fields[i].get(obj);
                    logger.info("传入的对象中包含一个如下的变量：" + varName + " = " + o);
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // 恢复访问控制权限
                fields[i].setAccessible(accessFlag);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        }
    }


    public static ZdhInfo create_zhdInfo(QuartzJobInfo quartzJobInfo, QuartzJobMapper quartzJobMapper,
                                          EtlTaskService etlTaskService, DataSourcesServiceImpl dataSourcesServiceImpl) {

        //获取调度任务信息
        QuartzJobInfo dti = quartzJobMapper.selectByPrimaryKey(quartzJobInfo.getJob_id());

        String etl_task_id = dti.getEtl_task_id();
        //获取etl 任务信息
        EtlTaskInfo etlTaskInfo = etlTaskService.selectById(etl_task_id);

        Map<String, Object> map = (Map<String, Object>) JSON.parseObject(dti.getParams());
        //此处做参数匹配转换
        if (map != null) {
            logger.info("自定义参数不为空,开始替换:" + dti.getParams());
            //System.out.println("自定义参数不为空,开始替换:" + dti.getParams());
            map.forEach((k, v) -> {
                logger.info("key:" + k + ",value:" + v);
                String filter = etlTaskInfo.getData_sources_filter_input();
                etlTaskInfo.setData_sources_filter_input(filter.replace(k, v.toString()));
                String clear = etlTaskInfo.getData_sources_clear_output();
                etlTaskInfo.setData_sources_clear_output(clear.replace(k, v.toString()));
            });


        }

        //获取数据源信息
        String data_sources_choose_input = etlTaskInfo.getData_sources_choose_input();
        String data_sources_choose_output = etlTaskInfo.getData_sources_choose_output();
        DataSourcesInfo dataSourcesInfoInput = dataSourcesServiceImpl.selectById(data_sources_choose_input);
        DataSourcesInfo dataSourcesInfoOutput = null;
        if (!data_sources_choose_input.equals(data_sources_choose_output)) {
            dataSourcesInfoOutput = dataSourcesServiceImpl.selectById(data_sources_choose_output);
        } else {
            dataSourcesInfoOutput = dataSourcesInfoInput;
        }

        ZdhInfo zdhInfo = new ZdhInfo();
        zdhInfo.setZdhInfo(dataSourcesInfoInput, etlTaskInfo, dataSourcesInfoOutput, dti);

        return zdhInfo;


    }

    /**
     * 获取后台url
     * @param zdhHaInfoMapper
     * @return
     */
    public static String getZdhUrl(ZdhHaInfoMapper zdhHaInfoMapper){
        String url = "http://127.0.0.1:60001/api/v1/zdh";
        List<ZdhHaInfo> zdhHaInfoList= zdhHaInfoMapper.selectByStatus("enabled");
        if(zdhHaInfoList!=null && zdhHaInfoList.size()==1){
            url=zdhHaInfoList.get(0).getZdh_url();
        }
        return url;
    }


    /**
     * 插入日志
     * @param job_id
     * @param level
     * @param msg
     * @param zdhLogsService
     */
    public static void insertLog(String job_id,String level,String msg,ZdhLogsService zdhLogsService){
        ZdhLogs zdhLogs = new ZdhLogs();
        zdhLogs.setJob_id(job_id);
        Timestamp lon_time = new Timestamp(new Date().getTime());
        zdhLogs.setLog_time(lon_time);
        zdhLogs.setMsg(msg);
        zdhLogs.setLevel(level);
        zdhLogsService.insert(zdhLogs);
    }
}
