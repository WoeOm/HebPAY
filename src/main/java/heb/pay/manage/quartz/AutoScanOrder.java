package heb.pay.manage.quartz;

import heb.pay.service.AutoScanService;
import heb.pay.service.PaymentService;
import heb.pay.util.BankConfigUtils;
import heb.pay.util.BeanUtils;
import heb.pay.util.CheckBankDataUtils;
import heb.pay.util.HttpClientUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.abc.pay.client.JSON;
import com.abc.pay.client.ebus.QueryOrderRequest;
import com.csii.payment.client.entity.RequestParameterObject;
import com.csii.payment.client.entity.SignParameterObject;
import com.csii.payment.client.http.HttpUtil;

public class AutoScanOrder {
	
	@Value("${bankJob}")
	private String jobName;
	@Value(value = "${bankGroup}")
	private String groupName;

	@Autowired     
	private QuartzManage quartzManage;
	@Autowired
	private PaymentService paymentService;
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
	
	@SuppressWarnings("unchecked")
	public void autoScan(){
		List<Map<String,Object>> paymentOrders = new ArrayList<Map<String,Object>>();
		AutoScanService service = (AutoScanService) BeanUtils.getInstance().getBean("autoScanServiceImpl");
		paymentOrders = service.getAutoScan();
		for(int i = 0;i<paymentOrders.size();i++){
			String orderId = paymentOrders.get(i).get("BANK_ORDER_NO").toString();
			String payWayCode = paymentOrders.get(i).get("PAY_WAY_CODE").toString();
			String merchantNo = paymentOrders.get(i).get("MERCHANT_NO").toString();//商户编号
			Date TransDateTime = (Date)paymentOrders.get(i).get("ORDER_TIME");
			String amt = paymentOrders.get(i).get("ORDER_AMOUT").toString();
			
			if(payWayCode.equals("CCB-BANK")){//建行BBC_BANK
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("requestXml", getXML(orderId,payWayCode));
				try {
					String result = HttpClientUtil.doPost("http://localhost:8888/", map, "UTF-8");
					String status = StringUtils.substringBetween(result, "<ORDER_STATUS>", "</ORDER_STATUS>");
					if(status != null && !status.equals("")){
						LinkedHashMap<String,String> link = new LinkedHashMap<String, String>();
						link.put("ORDERID", orderId);
						link.put("SUCCESS", status);
						paymentService.notice(link, 1,"ccb");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else if(payWayCode.equals("BOC-BANK")){//中行BOC-BANK
				Map<String, Object> map = new HashMap<String, Object>();
				StringBuilder plainTextBuilder = new StringBuilder();
				plainTextBuilder.append(merchantNo).append(":").append(orderId);
				String plainText = plainTextBuilder.toString();
				byte[] plainTextByte=null;
				try {
					plainTextByte = plainText.getBytes("UTF-8");
				
					String signData = CheckBankDataUtils.bocEncode(plainTextByte);
					map.put("merchantNo", merchantNo);
					map.put("orderNo", orderId);
					map.put("signData", signData);
					String result = HttpClientUtil.doPost(BankConfigUtils.bankConfig.get("BOC-BANK-SCAN"), map, "UTF-8");
					String status = StringUtils.substringBetween(result, "<tranStauts>", "</tranStauts>");
					if(status != null && !status.equals("")){
						LinkedHashMap<String,String> link = new LinkedHashMap<String, String>();
						link.put("orderNo", orderId);
						link.put("orderStatus", status);
						paymentService.notice(link, 1,"boc");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else if(payWayCode.equals("ABC-BANK")){//农行
				try {
					QueryOrderRequest queryRequest = new QueryOrderRequest();
					queryRequest.queryRequest.put("PayTypeID", "ImmediatePay");    //设定交易类型
					queryRequest.queryRequest.put("OrderNo",orderId);    //设定订单编号 （必要信息）
					queryRequest.queryRequest.put("QueryDetail", "false");//设定查询方式
					JSON json = queryRequest.postRequest();
					String returnCode = json.GetKeyValue("ReturnCode");
					//String ErrorMessage = json.GetKeyValue("ErrorMessage");
					LinkedHashMap<String,String> link = new LinkedHashMap<String, String>();
					if (returnCode.equals("0000")){
						link.put("orderNo", orderId);
						link.put("orderStatus", "1");
					}else{
						link.put("orderNo", orderId);
						link.put("orderStatus", "0");
					}
					paymentService.notice(link, 1,"abc");
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}else if(payWayCode.equals("CEB-BANK")){//农行
				String transName = "IQSR";
				String merchantId = merchantNo;
				String originalorderId = orderId;
				String originalTransDateTime = new SimpleDateFormat("yyyyMMddHHmmss").format(TransDateTime);
				String originalTransAmt = amt;
				StringBuilder originalStr = new StringBuilder();
				originalStr.append("transId="+transName+"~|~");
				originalStr.append("merchantId="+merchantId+"~|~");
				originalStr.append("originalorderId="+originalorderId+"~|~");
				originalStr.append("originalTransDateTime="+originalTransDateTime+"~|~");
				originalStr.append("originalTransAmt="+originalTransAmt);
				
				String plain = originalStr.toString();
				
				String questUrl = BankConfigUtils.bankConfig.get("CEB-BANK-SCAN");
				SignParameterObject signParam = new SignParameterObject();
				signParam.setMerchantId(merchantId);//商户号
				signParam.setPlain(plain);//明文
				signParam.setCharset("GBK");//明文使用的字符集
				signParam.setType(0);//0-普通报文
				signParam.setAlgorithm("MD5withRSA");//签名算法
				
				LinkedHashMap<String,String> link = new LinkedHashMap<String, String>();

				try {
					String signature = CheckBankDataUtils.cebEncode(signParam);
					RequestParameterObject requestParameterObject = new RequestParameterObject();
					requestParameterObject.setRequestURL(questUrl);
					requestParameterObject.setRequestData("TransName="+transName+"&Plain="+plain+"&Signature="+signature);
					requestParameterObject.setRequestCharset("GBK");
					byte[] result = HttpUtil.sendHost(requestParameterObject);
					String status = StringUtils.substringBetween(new String(result,"GBK"), "transStatus=", "~|~");
					if(status != null && !status.equals("")){
						link.put("orderNo", orderId);
						link.put("orderStatus", status);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				paymentService.notice(link, 1,"ceb");
			}
		}
	}
	
	public static String getXML(String orderId,String payWayCode){
		StringBuffer buffer = new StringBuffer();
	 if(payWayCode.equals("CCB-BANK")){
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
	 }
	 
		return buffer.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(getXML("20161227221830",""));
	}
	
}
