package heb.pay.service.impl;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import heb.pay.dao.PaymentDAO;
import heb.pay.service.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {

	@Autowired
	private PaymentDAO paymentDAO;

	@Override
	public int insertPaymentRecord(Map<String, Object> map) {
		return paymentDAO.insertPaymentRecord(map);
	}

	@Override
	public int updateOrderTrxNO(Map<String,String> map) {
		return paymentDAO.updateOrderTrxNO(map);
	}
	
	
}
