package com.zyc.zspringboot.job;

import com.alibaba.fastjson.JSON;
import com.zyc.zspringboot.dao.QuartzJobMapper;
import com.zyc.zspringboot.entity.DataSourcesInfo;
import com.zyc.zspringboot.entity.EtlTaskInfo;
import com.zyc.zspringboot.entity.QuartzJobInfo;
import com.zyc.zspringboot.entity.ZdhInfo;
import com.zyc.zspringboot.quartz.QuartzManager2;
import com.zyc.zspringboot.service.EtlTaskService;
import com.zyc.zspringboot.service.impl.DataSourcesServiceImpl;
import com.zyc.zspringboot.util.DateUtil;
import com.zyc.zspringboot.util.HttpUtil;
import com.zyc.zspringboot.util.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;

public class ShellJob {

    private static Logger logger = LoggerFactory.getLogger(ShellJob.class);

    public static void run(QuartzJobInfo quartzJobInfo){

        if(quartzJobInfo.getJob_model().equals(JobModel.TIME_SEQ.getValue())){

            Boolean is_count=true;
            //判断次数
            quartzJobInfo.setCount(quartzJobInfo.getCount()+1);
            if(!quartzJobInfo.getPlan_count().trim().equals("") && !quartzJobInfo.getPlan_count().trim().equals("-1")){
                //任务有次数限制,满足添加说明这是最后一次任务
                System.out.println(quartzJobInfo.getCount()+"================"+quartzJobInfo.getPlan_count().trim());
                if(quartzJobInfo.getCount()>Long.parseLong(quartzJobInfo.getPlan_count().trim())){
                   return ;
                }
                if(quartzJobInfo.getCount()==Long.parseLong(quartzJobInfo.getPlan_count().trim())){
                    System.out.println("=====================================================");
                    quartzJobInfo.setStatus("finish");
                }

            }

            QuartzManager2 quartzManager2=(QuartzManager2) SpringContext.getBean("quartzManager2");
            //执行命令
            //拼接任务信息发送请求
            Boolean exe_status=true;
            QuartzJobMapper quartzJobMapper=(QuartzJobMapper)SpringContext.getBean("quartzJobMapper");
            EtlTaskService etlTaskService=(EtlTaskService)SpringContext.getBean("etlTaskServiceImpl");
            DataSourcesServiceImpl dataSourcesServiceImpl=(DataSourcesServiceImpl)SpringContext.getBean("dataSourcesServiceImpl");
            String url = "http://127.0.0.1:60001/api/v1/zdh";

            ZdhInfo zdhInfo=create_zhdInfo(quartzJobInfo,quartzJobMapper,etlTaskService,dataSourcesServiceImpl);
            try {
                HttpUtil.postJSON(url, JSON.toJSONString(zdhInfo));
            } catch (Exception e) {
                System.out.println(e.getMessage());
                exe_status=false;
            }



            if(exe_status){
                System.out.println("执行命令成功");
                //如果执行成功则+ 一天
                //next_time <= end_time 继续执行,否则 修改任务状态,删除quartz 中的任务
                //next_time +1天
                if(quartzJobInfo.getLast_time()==null){
                    //第一次执行,下次执行时间为起始时间+1
                    quartzJobInfo.setNext_time(quartzJobInfo.getStart_time());
                }
                Date last_time=quartzJobInfo.getNext_time();

                quartzJobInfo.setLast_time(last_time);
                Date next_time=DateUtil.add(last_time,1);
                quartzJobInfo.setNext_time(next_time);

                if(next_time.after(quartzJobInfo.getEnd_time())){
                    logger.info("下次时间超过结束时间,任务结束");
                    quartzJobInfo.setStatus("finish");
                    //删除quartz 任务

                    quartzManager2.deleteTask(quartzJobInfo);
                }
                //更新任务信息
                debugInfo(quartzJobInfo);
                if(quartzJobInfo.getCount()==Long.parseLong(quartzJobInfo.getPlan_count().trim())){
                    System.out.println("===================================");
                    quartzJobInfo.setStatus("finish");
                    //delete 里面包含更新
                    quartzManager2.deleteTask(quartzJobInfo);
                }else{
                    quartzJobMapper.updateByPrimaryKey(quartzJobInfo);
                }
            }else{
                //如果执行失败 next_time 时间不变,last_time 不变


            }





        }




    }

    private static void debugInfo(Object obj) {
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
                    System.err.println("传入的对象中包含一个如下的变量：" + varName + " = " + o);
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


    private static ZdhInfo create_zhdInfo(QuartzJobInfo quartzJobInfo,QuartzJobMapper quartzJobMapper,
                                   EtlTaskService etlTaskService,DataSourcesServiceImpl dataSourcesServiceImpl) {
        // dispatchTaskService.update(dispatchTaskInfo);


        //获取调度任务信息
        QuartzJobInfo dti = quartzJobMapper.selectByPrimaryKey(quartzJobInfo.getJob_id());

        String etl_task_id = dti.getEtl_task_id();
        //获取etl 任务信息
        EtlTaskInfo etlTaskInfo = etlTaskService.selectById(etl_task_id);

        Map<String, Object> map = (Map<String, Object>) JSON.parseObject(dti.getParams());
        //此处做参数匹配转换
        if (map != null) {
            System.out.println("自定义参数不为空,开始替换:" + dti.getParams());
            map.forEach((k, v) -> {
                System.out.println("key:" + k + ",value:" + v);
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
}
