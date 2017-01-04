package heb.pay.service;

import heb.pay.entity.BaseUnit;
import heb.pay.entity.JFBankInfo;
import heb.pay.entity.PayerType;
import java.util.List;

public interface JFPayService {

	/**
	 * 获取单位信息
	 * @param user_no 单位编码
	 * @return
	 */
	List<BaseUnit> getBaseUnit(String user_no);
	/**
	 * 获取缴费类型信息
	 * @param productName 缴费类型编码
	 * @return
	 */
	List<PayerType> getPayerType(String productName);
	/**
	 * 新增缴费平台订单
	 * @param orderNo 订单号
	 * @param cantonid 区划id
	 * @param unitid 单位id
	 * @param payerName 缴款人
	 * @param payertypeid 缴费类型id
	 * @param amt 金额
	 * @param curDate 创建时间
	 * @return
	 */
	int insertJfPayorder(String orderNo, int cantonid, int unitid,String payerName, int payertypeid,String busiTypeCode, double amt, String curDate);
	/**
	 * 获取限额金额
	 * @param payertypeid
	 * @param unitid
	 * @return
	 */
	double getLimitAmt(int payertypeid, int unitid);
	/**
	 * 更新缴费平台订单表
	 * @param unitid 单位id
	 * @param merchant_order_no 订单号
	 * @return
	 */
	int updateJFOrderBankVSState(int unitid, String merchant_order_no,int status,int bankid,String bankOrderNo);
	/**
	 * 获取银行信息
	 * @param pay_way_name 银行名称
	 * @return
	 */
	List<JFBankInfo> getJFBankInfo(String pay_way_name);
	/**
	 * 更新缴费订单状态
	 * @param orderId
	 * @param jfStatus
	 * @return
	 */
	int updateJFOrderStatus(String orderId,String jfStatus);

	
}
