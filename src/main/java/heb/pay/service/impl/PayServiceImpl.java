package heb.pay.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import heb.pay.dao.PayDAO;
import heb.pay.entity.BaseUnit;
import heb.pay.entity.MerchantInfo;
import heb.pay.entity.MerchantPayConfig;
import heb.pay.entity.MerchantPayInfo;
import heb.pay.entity.PayWay;
import heb.pay.entity.PayerType;
import heb.pay.entity.PaymentOrder;
import heb.pay.service.PayService;

@Service
public class PayServiceImpl implements PayService {

	@Autowired
	private PayDAO payDAO;
	
	@Override
	public String getSecretKey(String payKey) {
		return payDAO.getSecretKey(payKey);
	}

	@Override
	public List<PaymentOrder> getPaymentOrderByOrderNum(String orderNum,String merchantNo,String payTypeCode,String busiTypeCode,double amt) {
		
		return payDAO.getPaymentOrderByOrderNum(orderNum,merchantNo,payTypeCode,busiTypeCode,amt);
	}

	@Override
	public int insertPaymentOrder(PaymentOrder paymentOrder) {
		return payDAO.insertPaymentOrder(paymentOrder);
	}

	@Override
	public List<MerchantInfo> getMerchantInfo(String payKey) {
		return payDAO.getMerchantInfo(payKey);
	}

	@Override
	public List<PayWay> getPayWayList(String payKey) {
		return payDAO.getPayWayList(payKey);
	}

	@Override
	public List<PaymentOrder> getOrderSingleByOrderNum(Map<String,Object> map) {
		return payDAO.getOrderSingleByOrderNum(map);
	}

	@Override
	public int getOrderCount(Map<String,Object> map) {
		return payDAO.getOrderCount(map);
	}

	@Override
	public int updateOrderByInfo(Map<String, Object> map) {
		return payDAO.updateOrderByInfo(map);
	}

	@Override
	public String getReturnURLByOrderNum(String merchantNO,String merOrderNO) {
		return payDAO.getReturnURLByOrderNum(merchantNO,merOrderNO);
	}

	@Override
	public String getNotifyURLByOrderNum(String payKey,String orderNO) {
		
		return payDAO.getNotifyURLByOrderNum(payKey,orderNO);
	}

	@Override
	public List<PayWay> getPayWayByCode(String payWayCode,String payTypeCode) {
		
		return payDAO.getPayWayByCode(payWayCode,payTypeCode);
	}

	@Override
	public List<MerchantInfo> checkMerchant(String payKey) {
		
		return payDAO.checkMerchant(payKey);
	}

	@Override
	public List<MerchantPayInfo> getMerchantPayInfo(String merchant_no,
			String payWayCode) {
		
		return payDAO.getMerchantPayInfo(merchant_no, payWayCode);
	}

	@Override
	public String getSecretKeyByPaykey(String payKey) {
		
		return payDAO.getSecretKey(payKey);
	}

	@Override
	public List<MerchantPayInfo> getMerPayInfoByPostid(String postId,
			String bankId) {
		
		return payDAO.getMerPayInfoByPostid(postId, bankId);
	}

	@Override
	public List<PaymentOrder> getPayOrderByOrderId(String orderId) {
		
		return payDAO.getPayOrderByOrderId(orderId);
	}

	@Override
	public List<MerchantPayConfig> getMerPayConfig(String merchantNO,
			String payProductCode) {
		
		return payDAO.getMerPayConfig(merchantNO, payProductCode);
	}

	@Override
	public int updateOrderByOrderId(String orderId, String statusDB) {
		
		return payDAO.updateOrderByOrderId(orderId, statusDB);
	}

	@Override
	public int updateRecordByOrderId(String orderId, String statusDB) {
		
		return payDAO.updateRecordByOrderId(orderId, statusDB);
	}

	@Override
	public List<BaseUnit> getBaseUnit(String merchantNO) {
		
		return payDAO.getBaseUnit(merchantNO);
	}

	@Override
	public List<PayerType> getPayerType(String payerTypeCode) {
		
		return payDAO.getPayerType(payerTypeCode);
	}

	@Override
	public int getMaxTimes(String user_no, String orderNum) {
		return payDAO.getMaxTimes(user_no, orderNum);
	}

	@Override
	public int getCurTimes(String user_no, String orderNum) {
		return payDAO.getCurTimes(user_no, orderNum);
	}

	@Override
	public int insertNotifyRecord(int curTimes, int maxTimes, String notifyURL,String orderNum, String user_no,String status) {
		return payDAO.insertNotifyRecord(curTimes, maxTimes, notifyURL, orderNum, user_no,status);
	}

	@Override
	public int getCount(Map<String,Object> paramsMap) {
		// TODO Auto-generated method stub
		return payDAO.getCount(paramsMap);
	}
	
	
	
}
