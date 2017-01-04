package heb.pay.manage.quartz;

import java.lang.reflect.Method;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class QuartzJobFactory implements Job {     
          
	@Override     
    public void execute(JobExecutionContext context) throws JobExecutionException {     
		Class<?> clazz = null;
		Object object = null;
		Method method = null;
		QuartzBean qurtBean = (QuartzBean)context.getMergedJobDataMap().get("qurtBean");     
		try {
			clazz = Class.forName(qurtBean.getJobClass());
			object = clazz.newInstance();
			if (object != null) {     
				int num = qurtBean.getParams()==null?0:qurtBean.getParams().length;
				if(num == 0){
					method = clazz.getMethod(qurtBean.getMethodName());
				}else{
					method = clazz.getMethod(qurtBean.getMethodName(),String.class);
				}
				if (method != null){
					method.invoke(object,qurtBean.getParams());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}             
    }     
	     
}