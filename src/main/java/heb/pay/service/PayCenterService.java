package heb.pay.service;

import java.util.List;
import java.util.Map;

import heb.pay.entity.BaseUnit;
import heb.pay.entity.MerchantInfo;
import heb.pay.entity.MerchantPayConfig;
import heb.pay.entity.MerchantPayInfo;
import heb.pay.entity.PayWay;
import heb.pay.entity.PayerType;
import heb.pay.entity.PaymentOrder;


public interface PayCenterService {

	/**
	 * 根据单位标示返回秘钥
	 * @param payKey 单位标示
	 * @return 秘钥
	 */
	public MerchantPayConfig getSecretKeyByPaykey(String payKey);
	/**
	 * 根据订单号判断订单是否存在
	 * @param orderNum
	 * @return
	 */
	public List<PaymentOrder> getPaymentOrderByOrderNum(String orderNum,String merchantNo,String payTypeCode,String busiTypeCode,double amt);
	/**
	 * 根据单位传递信息，插入订单表
	 * @param unitPayInfo
	 * @return 插入结果
	 */
	public int insertPaymentOrder(PaymentOrder paymentOrder);
	/**
	 * 根据单位标示获取单位信息
	 * @param payKey 单位标示
	 * @return 单位信息
	 */
	public List<MerchantInfo> getMerchantInfo(String payKey);
	/**
	 * 根据payKey获取当前商户支持的银行列表
	 * @param payKey
	 * @return
	 */
	public List<PayWay> getPayWayList(String payKey);
	/**
	 * 查询缴费结果列表
	 * @param payerNum 订单号
	 * @param startDate 开始日期
	 * @param endDate 结束日期
	 * @param pageIndex 页码
	 * @return
	 */
	public List<PaymentOrder> getOrderSingleByOrderNum(Map<String,Object> map);
	/**
	 * 查询订单总行数
	 * @param payerNum 订单号
	 * @param payKey 商户标示
	 * @param startDate 开始日期
	 * @param endDate 结束日期
	 * @param pageIndex 页码
	 * @return
	 */
	public int getOrderCount(Map<String,Object> map);
	/**
	 * 根据返回信息更新订单表
	 * @param map
	 * @return
	 */
	public int updateOrderByInfo(Map<String, Object> map);
	/**
	 * 获取returnUrl
	 * @param payerNum
	 * @return
	 */
	public String getReturnURLByOrderNum(String merchantNO,String merOrderNO);
	/**
	 * 获取notifyURL
	 * @param payerNum
	 * @return
	 */
	public String getNotifyURLByOrderNum(String payKey,String orderNO);
	/**
	 * 根据支付方式编号和支付类型编号获取银行信息
	 * @param payWayCode
	 * @param payTpyeCode
	 * @return
	 */
	public List<PayWay> getPayWayByCode(String payWayCode,String payTypeCode);

	/**
	 * 验证商户是否存在或者可用
	 * @param payKey
	 * @return
	 */
	public List<MerchantInfo> checkMerchant(String payKey);
	/**
	 * 获取商户支付信息配置信息
	 * @param merchant_no
	 * @param payWayCode
	 * @return
	 */
	public MerchantPayInfo getMerchantPayInfo(String merchant_no,String payWayCode);

	/**
	 * 根据BANKID和POSTID获取商户配置信息
	 * @param postId
	 * @param bankId
	 * @return
	 */
	public List<MerchantPayInfo> getMerPayInfoByPostid(String postId,String bankId);
	/**
	 * 根据orderId获取订单信息
	 * @param orderId
	 * @return
	 */
	public List<PaymentOrder> getPayOrderByOrderId(String orderId);
	/**
	 * 获取商户支付配置信息
	 * @param merchantNO
	 * @param payProductCode
	 * @return
	 */
	public List<MerchantPayConfig> getMerPayConfig(String merchantNO,String payProductCode);
	/**
	 * 根据订单号更新订单状态
	 * @param orderId
	 * @param statusDB
	 * @return
	 */
	public int updateOrderByOrderId(String orderId, String statusDB);
	/**
	 * 根据订单号更新记录表状态
	 * @param orderId
	 * @param statusDB
	 * @return
	 */
	public int updateRecordByOrderId(String orderId, String statusDB);
	/**
	 * 获取单位信息
	 * @param merchantNO
	 * @return
	 */
	public List<BaseUnit> getBaseUnit(String merchantNO);
	/**
	 * 获取缴费类型信息
	 * @param payerTypeCode
	 * @return
	 */
	public List<PayerType> getPayerType(String payerTypeCode);
	/**
	 * 获取最大重复次数
	 * @param user_no
	 * @param orderNum
	 * @return
	 */
	public int getMaxTimes(String user_no, String orderNum);
	/**
	 * 获取当前重复次数
	 * @param user_no
	 * @param orderNum
	 * @return
	 */
	public int getCurTimes(String user_no, String orderNum);
	/**
	 * 插入通知记录表
	 * @param curTimes
	 * @param maxTimes
	 * @param notifyURL
	 * @param orderNum
	 * @param user_no
	 * @return
	 */
	public int insertNotifyRecord(int curTimes, int maxTimes, String notifyURL,String orderNum, String user_no,String status);
	/**
	 * 获取通知次数行数
	 * @param user_no
	 * @param string
	 * @return
	 */
	public int getCount(Map<String,Object> paramsMap);
	/**
	 * 获取返回页面地址
	 * @param orderId
	 * @return
	 */
	public String getReturnUrlByOrderId(String orderId);
	/**
	 * 获取返回后台地址
	 * @param orderId
	 * @return
	 */
	public String getNotifyUrlByOrderId(String orderId);

}
