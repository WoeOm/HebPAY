package heb.pay.manage.quartz;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

public class QuartzManage {    
    	    
	@Autowired     
	private SchedulerFactoryBean schedulerFactoryBean;

	public boolean addJob(QuartzBean qurtBean) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();    
		TriggerKey triggerKey = TriggerKey.triggerKey(qurtBean.getJobName(), qurtBean.getJobGroup());    
		CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);  
		
		if (trigger == null) {
			Class<? extends Job> clazz = qurtBean.getModel().equals("1") ? QuartzJobFactory.class : QuartzJobFactoryDisCurrentExecution.class;    
			JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(qurtBean.getJobName(), qurtBean.getJobGroup()).build();     
			jobDetail.getJobDataMap().put("qurtBean", qurtBean);    
			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(qurtBean.getCronExpression());    
			trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();     
			scheduler.scheduleJob(jobDetail, trigger);       
		} else { 
			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(qurtBean.getCronExpression());         
			trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();        
			scheduler.rescheduleJob(triggerKey, trigger);       
		}
		return true;    
	}       
   
	public boolean deleteJob(QuartzBean qurtBean){    
		Scheduler scheduler = schedulerFactoryBean.getScheduler();    
		JobKey jobKey = JobKey.jobKey(qurtBean.getJobName(), qurtBean.getJobGroup());    
		try{    
			scheduler.deleteJob(jobKey);
			return true;    
		} catch (SchedulerException e) {			    
		}    
		return false;    
	}    

	@SuppressWarnings("unused")
	private boolean validCronExpression(String cron){
		return CronExpression.isValidExpression(cron);
	}

}