package com.zyc.zspringboot.job;

import com.zyc.zspringboot.dao.QuartzJobMapper;
import com.zyc.zspringboot.entity.QuartzJobInfo;
import com.zyc.zspringboot.quartz.QuartzManager2;
import com.zyc.zspringboot.util.DateUtil;
import com.zyc.zspringboot.util.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class ShellJob {

    private static Logger logger = LoggerFactory.getLogger(ShellJob.class);

    public static void run(QuartzJobInfo quartzJobInfo){

        if(quartzJobInfo.getJob_model()==JobModel.TIME_SEQ.getValue()){

            //执行命令

            Boolean exe_status=true;

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
                QuartzJobMapper quartzJobMapper=(QuartzJobMapper) SpringContext.getBean("quartzJobMapper");
                if(next_time.after(quartzJobInfo.getEnd_time())){
                    logger.info("下次时间超过结束时间,任务结束");
                    quartzJobInfo.setStatus("finish");
                    //删除quartz 任务
                    QuartzManager2 quartzManager2=(QuartzManager2) SpringContext.getBean("quartzManager2");
                    quartzManager2.deleteTask(quartzJobInfo);
                }
                //更新任务信息
                quartzJobMapper.updateByPrimaryKey(quartzJobInfo);
            }else{
                //如果执行失败 next_time 时间不变,last_time 不变


            }





        }




    }
}
