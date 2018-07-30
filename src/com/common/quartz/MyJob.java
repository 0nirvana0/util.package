package com.common.quartz;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

/**
 * 任务执行类  实现  Job接口 
 * 该接口只有一个方法 
 * @author LQ
 *
 */
public class MyJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// 通过上下文获取
		JobKey jobKey = context.getJobDetail().getKey();

		DateFormat df = new SimpleDateFormat("yyyy年MM月dd日  HH时mm分ss秒");
		System.out.println(jobKey + "在" + df.format(new Date()) + "时，输出了：Hello World!!!");

		// do more这里可以执行其他需要执行的任务

	}
}
