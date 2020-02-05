package com.zyc.zspringboot.quartz;

import javax.annotation.Resource;

import com.zyc.zspringboot.ZspringbootApplication;
import com.zyc.zspringboot.dao.QuartzJobMapper;
import com.zyc.zspringboot.entity.QuartzJobInfo;
import com.zyc.zspringboot.job.JobModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.zyc.zspringboot.entity.TaskInfo;

import java.util.Date;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ZspringbootApplication.class })
//@ActiveProfiles(resolver = ProfilesResolver.class)
@ActiveProfiles(value={"dev"})
public class QuartzTest {


	@Resource(name = "quartzManager2")
	private QuartzManager2 quartzManager2;

	@Autowired
	private QuartzJobMapper quartzJobMapper;


//	@Test
//	public void testQuartzCreate() {
//		TaskInfo createTaskInfo = quartzManager.createTaskInfo("任务7", "tb7",
//				"100s", 0, "测试任务");
//		quartzManager.addTaskInfo(createTaskInfo);
//		testQuartzRun(createTaskInfo);
//		while (true) {
//
//		}
//	}

	@Test
	public void testQuartz2Create() {

		QuartzJobInfo quartzJobInfo = quartzManager2.createQuartzJobInfo("SHELL",JobModel.TIME_SEQ.getValue(),
				new Date(),new Date(),"第一个调度任务","10s",5,"command","1");
				quartzManager2.addQuartzJobInfo(quartzJobInfo);

		quartzManager2.addTaskToQuartz(quartzJobInfo);
		while (true) {

		}
	}

	@Test
	public void testQuartz2Del() {
		QuartzJobInfo quartzJobInfo=new QuartzJobInfo();
		quartzJobInfo.setJob_id("674733379031138304");
		quartzJobInfo=quartzJobMapper.selectByPrimaryKey(quartzJobInfo);

		quartzManager2.deleteTask(quartzJobInfo);
		while (true) {

		}
	}

}
