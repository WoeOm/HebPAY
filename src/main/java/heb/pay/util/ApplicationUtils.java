package heb.pay.util;

import java.util.Properties;


public class ApplicationUtils {

	private static Properties applicationSettings; 
	
	public static Properties getApplicationSettings(){
		if(applicationSettings == null){
			applicationSettings =  (Properties) BeanUtils.getInstance().getBean("applicationProperties");
		}
		return applicationSettings;
	}
	
}