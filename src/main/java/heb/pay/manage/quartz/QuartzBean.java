package heb.pay.manage.quartz;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

public class QuartzBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4420465448025878207L;
	
	private String jobName;
	private String jobGroup;
	private String JobClass;
	private String methodName;
	private Object[] params;
	private String model = "0";//0无阻塞 1阻塞
	private String cronExpression;

	public QuartzBean(String jobName, String jobGroup) {
		super();
		this.jobName = jobName;
		this.jobGroup = jobGroup;
	}

	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getJobGroup() {
		return jobGroup;
	}
	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}
	public String getJobClass() {
		return JobClass;
	}
	public void setJobClass(String jobClass) {
		JobClass = jobClass;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public Object[] getParams() {
		return params;
	}
	public void setParams(Object[] params) {
		this.params = params;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getCronExpression() {
		return cronExpression;
	}
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}
	
	

}
