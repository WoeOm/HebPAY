package heb.pay.manage.activemq;

import heb.pay.util.ApplicationUtils;
import heb.pay.util.BeanUtils;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.log4j.Logger;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class QueueSender {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	private JmsTemplate jmsQueueTemplate;

	public JmsTemplate getJmsQueueTemplate() {
		return jmsQueueTemplate;
	}
	
	public void setJmsQueueTemplate(JmsTemplate jmsQueueTemplate) {
		this.jmsQueueTemplate = jmsQueueTemplate;
	}

	public void send(final String message) {
		logger.info("##进入JMS发送……");
		String destination = ApplicationUtils.getApplicationSettings().getProperty("activemq.destination","heb_pay_destination");
		if(jmsQueueTemplate == null){
			jmsQueueTemplate = (JmsTemplate) BeanUtils.getInstance().getBean("jmsQueueTemplate");
		}
		jmsQueueTemplate.send(destination, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				return jmsQueueTemplate.getMessageConverter().toMessage(message, session);
			}
		});
	}
}
