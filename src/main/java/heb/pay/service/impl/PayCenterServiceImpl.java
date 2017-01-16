package heb.pay.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import heb.pay.dao.PayCenterDAO;
import heb.pay.entity.BaseUnit;
import heb.pay.entity.MerchantInfo;
import heb.pay.entity.MerchantPayConfig;
import heb.pay.entity.MerchantPayInfo;
import heb.pay.entity.PayWay;
import heb.pay.entity.PayerType;
import heb.pay.entity.PaymentOrder;
import heb.pay.service.PayCenterService;

@Service
public class PayCenterServiceImpl implements PayCenterService {

	@Autowired
	private PayCenterDAO payCenterDAO;
	
	@Override
	public MerchantPayConfig getSecretKeyByPaykey(String payKey) {
		return payCenterDAO.getSecretKeyByPaykey(payKey);
	}

	@Override
	public List<PaymentOrder> getPaymentOrderByOrderNum(String orderNum,String merchantNo,String payTypeCode,String busiTypeCode,double amt) {
		
		return payCenterDAO.getPaymentOrderByOrderNum(orderNum,merchantNo,payTypeCode,busiTypeCode,amt);
	}

	@Override
	public int insertPaymentOrder(PaymentOrder paymentOrder) {
		return payCenterDAO.insertPaymentOrder(paymentOrder);
	}

	@Override
	public List<MerchantInfo> getMerchantInfo(String payKey) {
		return payCenterDAO.getMerchantInfo(payKey);
	}

	@Override
	public List<PayWay> getPayWayList(String payKey) {
		return payCenterDAO.getPayWayList(payKey);
	}

	@Override
	public List<PaymentOrder> getOrderSingleByOrderNum(Map<String,Object> map) {
		return payCenterDAO.getOrderSingleByOrderNum(map);
	}

	@Override
	public int getOrderCount(Map<String,Object> map) {
		return payCenterDAO.getOrderCount(map);
	}

	@Override
	public int updateOrderByInfo(Map<String, Object> map) {
		return payCenterDAO.updateOrderByInfo(map);
	}

	@Override
	public String getReturnURLByOrderNum(String merchantNO,String merOrderNO) {
		return payCenterDAO.getReturnURLByOrderNum(merchantNO,merOrderNO);
	}

	@Override
	public String getNotifyURLByOrderNum(String payKey,String orderNO) {
		
		return payCenterDAO.getNotifyURLByOrderNum(payKey,orderNO);
	}

	@Override
	public List<PayWay> getPayWayByCode(String payWayCode,String payTypeCode) {
		
		return payCenterDAO.getPayWayByCode(payWayCode,payTypeCode);
	}

	@Override
	public List<MerchantInfo> checkMerchant(String payKey) {
		
		return payCenterDAO.checkMerchant(payKey);
	}

	@Override
	public MerchantPayInfo getMerchantPayInfo(String merchant_no,
			String payWayCode) {
		
		return payCenterDAO.getMerchantPayInfo(merchant_no, payWayCode);
	}

	@Override
	public List<MerchantPayInfo> getMerPayInfoByPostid(String postId,
			String bankId) {
		
		return payCenterDAO.getMerPayInfoByPostid(postId, bankId);
	}

	@Override
	public List<PaymentOrder> getPayOrderByOrderId(String orderId) {
		
		return payCenterDAO.getPayOrderByOrderId(orderId);
	}

	@Override
	public List<MerchantPayConfig> getMerPayConfig(String merchantNO,
			String payProductCode) {
		
		return payCenterDAO.getMerPayConfig(merchantNO, payProductCode);
	}

	@Override
	public int updateOrderByOrderId(String orderId, String statusDB) {
		
		return payCenterDAO.updateOrderByOrderId(orderId, statusDB);
	}

	@Override
	public int updateRecordByOrderId(String orderId, String statusDB) {
		
		return payCenterDAO.updateRecordByOrderId(orderId, statusDB);
	}

	@Override
	public List<BaseUnit> getBaseUnit(String merchantNO) {
		
		return payCenterDAO.getBaseUnit(merchantNO);
	}

	@Override
	public List<PayerType> getPayerType(String payerTypeCode) {
		
		return payCenterDAO.getPayerType(payerTypeCode);
	}

	@Override
	public int getMaxTimes(String user_no, String orderNum) {
		return payCenterDAO.getMaxTimes(user_no, orderNum);
	}

	@Override
	public int getCurTimes(String user_no, String orderNum) {
		return payCenterDAO.getCurTimes(user_no, orderNum);
	}

	@Override
	public int insertNotifyRecord(int curTimes, int maxTimes, String notifyURL,String orderNum, String user_no,String status) {
		return payCenterDAO.insertNotifyRecord(curTimes, maxTimes, notifyURL, orderNum, user_no,status);
	}

	@Override
	public int getCount(Map<String,Object> paramsMap) {
		return payCenterDAO.getCount(paramsMap);
	}

	@Override
	public String getReturnUrlByOrderId(String orderId) {
		return payCenterDAO.getReturnUrlByOrderId(orderId);
	}

	@Override
	public String getNotifyUrlByOrderId(String orderId) {
		return payCenterDAO.getNotifyUrlByOrderId(orderId);
	}
	
	
}
