package heb.pay.service;

import java.util.LinkedHashMap;
import java.util.Map;


public interface PaymentService {

	public int insertPaymentRecord(Map<String, Object> map);
	/**
	 * 更新订单表trx_no
	 * @param payerNum
	 * @param trxUO
	 * @return
	 */
	public int updateOrderTrxNO(Map<String,String> map);
	/**
	 * 发送后台通知
	 * @param params 参数列表:POSID,BRANCHID,ORDERID,SUCCESS
	 * @param type 0需要验证参数，1不需要验证参数
	 * @return
	 */
	public void notice(LinkedHashMap<String, String> params, int type,String bankName);

	
}
