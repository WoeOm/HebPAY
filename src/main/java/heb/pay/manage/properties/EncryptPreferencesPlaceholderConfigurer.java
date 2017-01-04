package heb.pay.manage.properties;

import heb.pay.util.AESUtils;

import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PreferencesPlaceholderConfigurer;

public class EncryptPreferencesPlaceholderConfigurer extends PreferencesPlaceholderConfigurer{

	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess,Properties props) throws BeansException {
		String password = props.getProperty("jdbc.password"); 
        if (password != null) { 
                props.setProperty("jdbc.password", AESUtils.Dncode(password)); 
        } 
        String username = props.getProperty("jdbc.username"); 
        if (username != null) { 
            props.setProperty("jdbc.username", AESUtils.Dncode(username)); 
        }
        String driverClassName = props.getProperty("jdbc.driverClassName"); 
        if (password != null) { 
                props.setProperty("jdbc.driverClassName", AESUtils.Dncode(driverClassName)); 
        } 
        String url = props.getProperty("jdbc.url"); 
        if (url != null) { 
                props.setProperty("jdbc.url", AESUtils.Dncode(url)); 
        } 
		super.processProperties(beanFactoryToProcess, props);
	}
	
}
