package heb.pay.manage.activemq;

import heb.pay.entity.MerchantInfo;
import heb.pay.manage.quartz.QuartzBean;
import heb.pay.manage.quartz.QuartzManage;
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
	private QuartzManage quartzManage;

	@Override
	public void onMessage(Message message) {
		try {
			System.out.println("已进入通知方法QueueReciver....");
			String text = ((TextMessage)message).getText();
			Map<String,Object> map =  JSONUtils.jsonToMap(text);
				
			String notifyURL = payService.getNotifyURLByOrderNum(map.get("payKey").toString(),map.get("payerNum").toString());
			try {
				String result = HttpClientUtil.doPost(notifyURL, map, "UTF-8");
				//如果result不符合要求，异步定时再次推送(notifyResult)
				System.out.println("result:"+result);
				if(!result.equals("SUCCESS")){
					sendDataRepeat(text,map,notifyURL);
				}
				
			} catch (Exception e) {
				//XX 异步定时再次推送(notifyResult)
				System.out.println("异常开启");
				sendDataRepeat(text,map,notifyURL);
			}
			message.acknowledge();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void sendDataRepeat(String text,Map<String,Object> map,String notifyURL){
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
				logger.info(cronExpression + ":正在向执收单位推送结果数据....");
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
}
