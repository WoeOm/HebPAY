package heb.pay.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class BankConfigUtils {
	
	private static Logger logger = Logger.getLogger(BankConfigUtils.class.getName());

	public static Map<String,String> bankConfig = new HashMap<String, String>();
	
	static{
		bankConfig.put("CCB-BANK", "https://ibsbjstar.ccb.com.cn/CCBIS/ccbMain?");
		bankConfig.put("CCB-BANK-SCAN", "https://101.231.206.170/PGWPortal/QueryOrderTrans.do");
		
		bankConfig.put("BOC-BANK", "https://101.231.206.170/PGWPortal/RecvOrder.do");
		bankConfig.put("BOC-BANK-SCAN", "https://101.231.206.170/PGWPortal/QueryOrderTrans.do");
		
		bankConfig.put("CEB-BANK", "https://111.205.51.141/per/preEpayLogin.do?_locale=zh_CN");
		bankConfig.put("CEB-BANK-SCAN", "https://111.205.51.141/per/QueryMerchantEpay.do");
		
		bankConfig.put("returnUrl","http://124.239.215.78:7001/paynotify/return/");
		bankConfig.put("notifyUrl","http://124.239.215.78:7001/paynotify/notify/");
	}
}
