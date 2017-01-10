package heb.pay.service;

import java.util.LinkedHashMap;

public interface NotifyService {

	/**
	 * 发送后台通知
	 * @param params 参数列表:POSID,BRANCHID,ORDERID,SUCCESS
	 * @param type 0需要验证参数，1不需要验证参数
	 * @return
	 */
	public void notice(LinkedHashMap<String, String> params, int type,String bankName);

	
}
