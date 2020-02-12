package com.zyc.zdh.job;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.zyc.zdh.dao.QuartzJobMapper;
import com.zyc.zdh.entity.QuartzJobInfo;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;


@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class MyJobBean extends QuartzJobBean implements Serializable {

	private static Logger logger = LoggerFactory.getLogger(MyJobBean.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -8509585011462529939L;

	public static final String TASK_ID = "task_id";
	
	@Autowired
	private QuartzJobMapper quartzJobMapper;

	public QuartzJobMapper getQuartzJobMapper() {
		return quartzJobMapper;
	}

	public void setQuartzJobMapper(QuartzJobMapper quartzJobMapper) {
		this.quartzJobMapper = quartzJobMapper;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		try {
			JobDataMap jobDataMap = context.getTrigger().getJobDataMap();
			System.out.println(jobDataMap.get(TASK_ID)
					+ "----执行-----"
					+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
							.format(new Date()));
			String taskId = jobDataMap.getString(TASK_ID);
			if (taskId == null || taskId.trim().equals("")) {
				throw new Exception("任务id为空");
			}
			// 记录当前时间更新任务最后执行时间
			Date currentTime = new Date();

			QuartzJobMapper quartzJobMapper2 = this.quartzJobMapper;
			QuartzJobInfo quartzJobInfo = new QuartzJobInfo();
			quartzJobInfo = quartzJobMapper2.selectByPrimaryKey(taskId);

			if(quartzJobInfo.getJob_type().equals("SHELL")){
				ShellJob.run(quartzJobInfo);
			}



//			Date lastTime = dmTaskInfoTb.getLastUpdateTime();
//			dmTaskInfoTb.setLastUpdateTime(currentTime);
//			// 任务执行次数计算
//			String cou = dmTaskInfoTb.getTaskCount();
//			Long count = 0L;
//			if (cou == null || cou.equals("")
//					|| Long.valueOf(cou) == Long.MAX_VALUE) {
//				count = 1L;
//			} else {
//				count = Long.valueOf(cou) + 1;
//			}
//			dmTaskInfoTb.setTaskCount(count.toString());
//			// 更新任务到数据库
//			dmTaskInfoTb.setTaskStatus("runing");
//			int result = dmTaskInfoTbMapper.updateByPrimaryKey(dmTaskInfoTb);
			//Thread.sleep(1000);
//			logger.info("{}", result);

			//获取任务信息 判断 执行那个



			// 获取规则服务
			// DimCheckedTablesInfoTbService ruleInfoService =
			// (DimCheckedTablesInfoTbService) SpringContextService
			// .getBean("dimCheckedTablesInfoTbService");
			//
			// @SuppressWarnings("rawtypes")
			// IDataNormProcessor dataNormProcessor = new ProcessorFactory()
			// .getgetProcessor(taskInfoTb.getTaskTablename());
			// dataNormProcessor.getDataNormContext().setTableName(
			// taskInfoTb.getTaskTablename());// tablename
			// dataNormProcessor.getDataNormContext().setDimNormDataList(// 规则
			// ruleInfoService.getNormInfoByTableName(taskInfoTb
			// .getTaskTablename()));
			// dataNormProcessor.getDataNormContext().setDataList(// 数据
			// new ScanData().getScanData(taskInfoTb.getTaskTablename(),
			// lastTime, currentTime));
			// dataNormProcessor.alarmDetection();// 告警
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

}