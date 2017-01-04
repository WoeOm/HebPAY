package heb.pay.manage.quartz;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import heb.pay.service.AutoScanService;
import heb.pay.service.NotifyService;
import heb.pay.util.BeanUtils;
import heb.pay.util.HttpClientUtil;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class AutoScanOrder {
	
	@Value("${ccbJob}")
	private String jobName;
	
	@Value(value = "${ccbGroup}")
	private String groupName;

	@Autowired     
	private QuartzManage quartzManage;
	
	@Autowired
	private NotifyService notifyService;
	
	@Autowired
	private AutoScanService autoScanService;
	
	//@PostConstruct
	public void scan(){
		int result = autoScanService.getAutoJob(jobName, groupName);
		if(result <= 0){
			QuartzBean quarteBean = new QuartzBean(jobName, groupName);
			quarteBean.setJobClass("heb.pay.manage.quartz.AutoScanOrder");
			quarteBean.setMethodName("autoScan");
			quarteBean.setCronExpression("0 0/1 * * * ?");
			try {
				quartzManage.addJob(quarteBean);
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void autoScan(){
		List<String> paymentOrders = new ArrayList<String>();
		
		AutoScanService service = (AutoScanService) BeanUtils.getInstance().getBean("autoScanServiceImpl");
		
		paymentOrders = service.getAutoScan();
		
		for(int i = 0;i<paymentOrders.size();i++){
			String orderId = paymentOrders.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("requestXml", getXML(orderId));
			try {
				String result = HttpClientUtil.doPost("http://localhost:8888/", map, "UTF-8");
				String status = StringUtils.substringBetween(result, "<ORDER_STATUS>", "</ORDER_STATUS>");
				if(status != null){
					LinkedHashMap<String,String> link = new LinkedHashMap<String, String>();
					link.put("ORDERID", orderId);
					link.put("SUCCESS", status);
					
					notifyService.notice(link, 1);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String getXML(String orderId){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version='1.0' encoding='GB2312' standalone='yes' ?>");
		buffer.append("<TX>");
		buffer.append("<REQUEST_SN>"+new SimpleDateFormat("yyyyMMddhhmmss").format(new Date())+"</REQUEST_SN>");
		buffer.append("<CUST_ID>105130193990227</CUST_ID>");
		buffer.append("<USER_ID>00021802-6-aaa</USER_ID>");
		buffer.append("<PASSWORD>qwert12345</PASSWORD>");
		buffer.append("<TX_CODE>5W1002</TX_CODE>");
		buffer.append("CN");
		buffer.append("<TX_INFO>");
		buffer.append("<START></START>");
		buffer.append("<STARTHOUR></STARTHOUR>");
		buffer.append("<STARTMIN></STARTMIN>");
		buffer.append("<END></END>");
		buffer.append("<ENDHOUR></ENDHOUR>");
		buffer.append("<ENDMIN></ENDMIN>");
		buffer.append("<KIND>0</KIND>");//0:未结流水,1:已结流水
		buffer.append("<ORDER>"+orderId+"</ORDER>");//订单号
		buffer.append("<ACCOUNT></ACCOUNT>");//
		buffer.append("<DEXCEL>1</DEXCEL>");
		buffer.append("<MONEY></MONEY>");
		buffer.append("<NORDERBY>1</NORDERBY>");//1:交易日期,2:订单号
		buffer.append("<PAGE>1</PAGE>");
		buffer.append("<POS_CODE></POS_CODE>");
		buffer.append("<STATUS>3</STATUS>");
		buffer.append("</TX_INFO>");
		buffer.append("</TX>");
		return buffer.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(getXML("20161227221830"));
	}
	
}
