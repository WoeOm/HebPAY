package heb.pay.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import org.apache.log4j.Logger;

import CCBSign.RSASig;

public class CheckBankDataUtils {
	
	private static Logger logger = Logger.getLogger(CheckBankDataUtils.class.getName());
	
	public static boolean checkCCBData(LinkedHashMap<String,String> map,String publicKey){
		if(map.isEmpty()){
			return false;
		}
		Set<String> set = map.keySet();
		Iterator<String> keys = set.iterator();
		String originalStr = "";
		String signStr = "";
		while(keys.hasNext()){
			String key = keys.next();
			if(key.equals("SIGN")){
				signStr = map.get(key);
				continue;
			}
			originalStr += key + "=" + map.get(key) + "&";
		}
		originalStr = originalStr.substring(0, originalStr.length()-1);
		RSASig rsa=new RSASig();
		logger.info("银行参数串："+originalStr);
		rsa.setPublicKey(publicKey);
		return rsa.verifySigature(signStr, originalStr);
	}
}
