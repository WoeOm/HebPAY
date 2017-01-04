package heb.pay.util;

import java.util.HashMap;
import java.util.Map;

public class BankConfigUtils {

	public static Map<String,String> bankConfig = new HashMap<String, String>();
	
	static{
		bankConfig.put("CCB-BANK", "https://ibsbjstar.ccb.com.cn/CCBIS/");
	}
}
