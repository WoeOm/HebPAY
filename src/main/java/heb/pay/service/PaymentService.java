package heb.pay.service;

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
	
}
