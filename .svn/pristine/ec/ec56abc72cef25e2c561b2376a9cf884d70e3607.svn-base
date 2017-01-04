package heb.pay.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import heb.pay.dao.JFPayDAO;
import heb.pay.entity.BaseUnit;
import heb.pay.entity.JFBankInfo;
import heb.pay.entity.PayerType;
import heb.pay.service.JFPayService;

@Service
public class JFPayServiceImpl implements JFPayService {
	
	@Autowired
	private JFPayDAO jfPayDao;

	@Override
	public List<BaseUnit> getBaseUnit(String user_no) {
		return jfPayDao.getBaseUnit(user_no);
	}

	@Override
	public List<PayerType> getPayerType(String productName) {
		return jfPayDao.getPayerType(productName);
	}

	@Override
	public int insertJfPayorder(String orderNo, int cantonid, int unitid,
			String payerName, int payertypeid,String busiTypeCode, double amt, String curDate) {
		return jfPayDao.insertJfPayorder(orderNo, cantonid, unitid, payerName, payertypeid,busiTypeCode, amt, curDate);
	}

	@Override
	public double getLimitAmt(int payertypeid, int unitid) {
		return jfPayDao.getLimitAmt(payertypeid, unitid);
	}

	@Override
	public int updateJFOrderBankVSState(int unitid, String merchant_order_no,int status,int bankid,String bankOrderNo) {
		return jfPayDao.updateJFOrderBankVSState(unitid,merchant_order_no,status,bankid,bankOrderNo);
	}

	@Override
	public List<JFBankInfo> getJFBankInfo(String pay_way_name) {
		return jfPayDao.getJFBankInfo(pay_way_name);
	}

	@Override
	public int updateJFOrderStatus(String orderId,String jfStatus) {
		return jfPayDao.updateJFOrderStatus(orderId,jfStatus);
	}

	
}
