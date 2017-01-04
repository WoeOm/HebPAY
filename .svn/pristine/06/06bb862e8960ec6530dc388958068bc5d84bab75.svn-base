package heb.pay.service;

import java.util.Date;
import java.util.Map;


public interface PayOpService {
	
	/**
	 * 验证数据包括：商户是否存在，缴费类型是否存在，金额是否超出限额
	 * @param userNO
	 * @param payerTypecode
	 * @param amt
	 * @return
	 */
	public Map<String,Object> checkBaseInfo(String payKey,String payerTypecode,double amt);

	/**
	 * 根据执收单位传入参数信息生成缴费平台订单信息
	 * @param user_no 商户号即单位编号
	 * @param orderNo 订单号
	 * @param payerName 缴款人
	 * @param productName 缴费类型编号
	 * @param amt 金额
	 * @param curDate 订单时间
	 * @return
	 */
	public int insertJFPayOrder(String user_no, String orderNo, String payerName,String productName,String busiTypeCode, double amtDate,Date curDate);
	/**
	 * 更新缴费平台订单表状态
	 * @param user_no 单位编号
	 * @param merchant_order_no 订单编号
	 * @param status 状态
	 * @param bankid 银行id
	 * @return
	 */
	public int updateJFPayorderState(String user_no, String merchant_order_no,int status,int bankid,String bankOrderNo);
	/**
	 * 获取银行id
	 * @param pay_way_name
	 * @return
	 */
	public int getJFBankInfo(String pay_way_name);
	/**
	 * 更新订单状态
	 * @param orderId
	 * @param jfStatus
	 */
	public int updateJFOrderStatus(String orderId,String jfStatus);

}
