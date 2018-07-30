package com.common.quartz;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class Tragger {
	public static void main(String[] args) {

		trigger();

	}

	private static void trigger() {
		try {

			// 通过schedulerFactory获取一个调度器
			Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
			// 创建jobDetail实例，绑定Job实现类
			JobDetail jobDetail = JobBuilder.newJob(MyJob.class)// 指明job的名称，所在组的名称，以及绑定job类
					.withIdentity("testJob_1", "group_1")// 定义name/group
					.build();
			// ------------------------------------------------------------
			/*
			 * 使用simpleTrigger规则
			 */
			// Trigger trigger = TriggerBuilder.newTrigger().
			// withIdentity("trigger_1", "group_1").
			// startNow().
			// withSchedule(
			// SimpleScheduleBuilder
			// .simpleSchedule()
			// .withIntervalInSeconds(10) // 时间间隔
			// .withRepeatCount(5) // 重复次数(将执行6次)
			// ).build();
			/*
			 * 使用cornTrigger规则 每天10点42分
			 */
			Trigger trigger = TriggerBuilder.newTrigger()// 定义name/group
					.withIdentity("cornTrigger", "triggerGroup")// 一旦加入scheduler，立即生效
					.startNow()// 一旦加入scheduler，立即生效
					.withSchedule(CronScheduleBuilder.cronSchedule("*/1 * * * * ?"))//
					.build();
			// Cron表达式
			// 1. Seconds
			// 2. Minutes
			// 3. Hours
			// 4. Day-of-Month
			// 5. Month
			// 6. Day-of-Week
			// 7. Year (可选字段)
			// (1)*：表示匹配该域的任意值，假如在Minutes域使用*, 即表示每分钟都会触发事件。
			//
			// (2)?:只能用在DayofMonth和DayofWeek两个域。它也匹配域的任意值，但实际不会。因为DayofMonth和 DayofWeek会相互影响。例如想在每月的20日触发调度，不管20日到底是星期几，则只能使用如下写法： 13 13 15 20 * ?, 其中最后一位只能用？，而不能使用*，如果使用*表示不管星期几都会触发，实际上并不是这样。
			//
			// (3)-:表示范围，例如在Minutes域使用5-20，表示从5分到20分钟每分钟触发一次
			//
			// (4)/：表示起始时间开始触发，然后每隔固定时间触发一次，例如在Minutes域使用5/20,则意味着5分钟触发一次，而25，45等分别触发一次.
			//
			// (5),:表示列出枚举值值。例如：在Minutes域使用5,20，则意味着在5和20分每分钟触发一次。
			//
			// (6)L:表示最后，只能出现在DayofWeek和DayofMonth域，如果在DayofWeek域使用5L,意味着在最后的一个星期四触发。
			//
			// (7)W: 表示有效工作日(周一到周五),只能出现在DayofMonth域，系统将在离指定日期的最近的有效工作日触发事件。例如：在 DayofMonth使用5W，如果5日是星期六，则将在最近的工作日：星期五，即4日触发。如果5日是星期天，则在6日(周一)触发；如果5日在星期一 到星期五中的一天，则就在5日触发。另外一点，W的最近寻找不会跨过月份
			//
			// (8)LW:这两个字符可以连用，表示在某个月最后一个工作日，即最后一个星期五。
			//
			// (9)#:用于确定每个月第几个星期几，只能出现在DayofMonth域。例如在4#2，表示某月的第二个星期三。
			// 每隔5秒执行一次：*/5 * * * * ?
			// ------------------------------------------------------------
			// 把作业和触发器注册到任务调度中
			scheduler.scheduleJob(jobDetail, trigger);
			// 启动调度
			scheduler.start();

		} catch (SchedulerException e) {
			e.printStackTrace();
		}

	}
}
