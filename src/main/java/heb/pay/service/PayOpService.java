package heb.pay.service;

import heb.pay.entity.PaymentOrder;

import java.util.LinkedHashMap;
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
	 * 验证支付平台订单是否存在
	 * @param payerNum
	 * @param userNo
	 * @param payerTypeCode
	 * @param busiTypeCode
	 * @param amt
	 * @return
	 */
	public Map<String,Object> checkOrderExist(String orderNo,String userNo,String payerTypeCode,String busiTypeCode,double amt);
	
	/**
	 * 银行返回页面通知验证银行数据
	 * @param params
	 * @return
	 */
	public Map<String, Object> checkBankData(LinkedHashMap<String, String> params,String bankName);
	
	/**
	 * 插入支付平台记录表
	 * @param payWayCode
	 * @param payTpyeCode
	 * @param paymentOrder
	 * @return
	 */
	public Map<String,Object> insertZFOrderRecord(String payWayCode,String payTpyeCode,String returnUrl,String notifyUrl,PaymentOrder paymentOrder);

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
	public int insertPayOrder(PaymentOrder paymentOrder,String payerName);
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
	/**
	 * 更新订单表状态，支付+缴费
	 * @param orderId
	 * @param statusDB
	 * @return
	 */
	public int updateOrderStatus(String orderId, String statusDB);
	
	/**
	 * 根据订单号获取订单信息
	 * @param orderId
	 * @return
	 */
	public Map<String,Object> getPayOrderByOrderId(String orderId);

}
