package heb.pay.manage.activemq;

import heb.pay.entity.MerchantInfo;
import heb.pay.manage.quartz.QuartzBean;
import heb.pay.manage.quartz.QuartzManage;
import heb.pay.service.PayOpService;
import heb.pay.service.PayService;
import heb.pay.util.HttpClientUtil;
import heb.pay.util.JSONUtils;
import heb.pay.util.PageUtils;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;

public class QueueReciver implements MessageListener{
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private QueueSender queueSender;
	@Autowired
	private PayService payService;
	@Autowired
	private PayOpService payOpService;
	@Autowired
	private QuartzManage quartzManage;

	@Override
	public void onMessage(Message message) {
		logger.info("##进入JMS接收……");
		try {
			//1 接收执收单位推送数据
			String text = ((TextMessage)message).getText();
			Map<String,Object> map =  JSONUtils.jsonToMap(text);
			//获取执收单位推送后台地址
			String notifyURL = payService.getNotifyURLByOrderNum(map.get("payKey").toString(),map.get("payerNum").toString());
			logger.info("##执收单位服务推送地址："+notifyURL);
			try {
				//2 推送并等待结果
				String result = HttpClientUtil.doPost(notifyURL, map, "UTF-8");
				//3 如果result不符合要求即不为"SUCCESS"，异步定时再次推送
				if(!result.equals("SUCCESS")){
					logger.info("##【无回应】再次推送……");
					sendDataRepeat(text,map,notifyURL);
				}
			} catch (Exception e) {
				//3 如果发生异常，异步定时再次推送
				logger.info("##【异常】再次推送……");
				sendDataRepeat(text,map,notifyURL);
			}
			message.acknowledge();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void sendDataRepeat(String text,Map<String,Object> map,String notifyURL){
		logger.info("##开始推送……");
		List<MerchantInfo> list = payService.getMerchantInfo(map.get("payKey").toString());
		Map<String,Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("user_no", list.get(0).getUser_no());
		paramsMap.put("payerNum", map.get("payerNum").toString());
		int count = payService.getCount(paramsMap);
		int maxTimes = 0;
		int curTimes = 0;
		if(count>0){
			maxTimes = payService.getMaxTimes(list.get(0).getUser_no(),map.get("payerNum").toString());
			curTimes = payService.getCurTimes(list.get(0).getUser_no(),map.get("payerNum").toString());
		}
		maxTimes = (maxTimes==0?5:maxTimes);
		curTimes = (curTimes==0?1:curTimes);
		if(curTimes<maxTimes){
			int ret = payService.insertNotifyRecord(curTimes+1,maxTimes,notifyURL,map.get("payerNum").toString(),list.get(0).getUser_no(),"100");
			if(ret>0){
				Date now = new Date();
				Calendar c = Calendar.getInstance();
				c.setTime(now);
				SimpleDateFormat format = new SimpleDateFormat("s m H d M ? yyyy");
				//计算时间差值
				if(curTimes==1){
					c.add(Calendar.SECOND, 30);	
				}
				if(curTimes==2){
					c.add(Calendar.SECOND, 5*60);	
				}
				if(curTimes==3){
					c.add(Calendar.SECOND, 1*60*60);	
				}
				if(curTimes==4){
					c.add(Calendar.SECOND, 6*60*60);	
				}
				if(curTimes==5){
					c.add(Calendar.SECOND, 12*60*60);	
				}
				String jobGroup = "jobGroup_" + PageUtils.getUUID();
				String jobName = "jobName_" + PageUtils.getUUID();
				QuartzBean qurtBean = new QuartzBean(jobName,jobGroup);
				qurtBean.setJobClass("heb.pay.manage.activemq.QueueSender");
				qurtBean.setMethodName("send");
				qurtBean.setParams(new Object[]{text});
				String cronExpression = format.format(c.getTime());
				qurtBean.setCronExpression(cronExpression);
				try {
					quartzManage.addJob(qurtBean);
				} catch (SchedulerException e) {
					e.printStackTrace();
				}
			}
			
		}else{
			return;
		}
		
	}
	
	public static void main(String[] args) {
		Calendar c = Calendar.getInstance();
		Date now = new Date();
		c.setTime(now);
		SimpleDateFormat format = new SimpleDateFormat("s m H d M ? yyyy");
		String cronExpression = format.format(c.getTime());
		System.out.println(cronExpression);
	}
}
