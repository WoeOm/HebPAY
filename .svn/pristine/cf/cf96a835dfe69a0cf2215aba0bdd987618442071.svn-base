package heb.pay.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import heb.pay.entity.BaseUnit;
import heb.pay.entity.JFBankInfo;
import heb.pay.entity.MerchantInfo;
import heb.pay.entity.PayWay;
import heb.pay.entity.PayerType;
import heb.pay.service.JFPayService;
import heb.pay.service.PayOpService;
import heb.pay.service.PayService;

@Service
public class PayOpServiceImpl implements PayOpService {
	
	@Autowired
	private JFPayService jfPayService;
	@Autowired
	private PayService payService;

	@Override
	public Map<String,Object> checkBaseInfo(String payKey, String payerTypecode, double amt) {
		Map<String,Object> map = new HashMap<String, Object>();
		//获取支付平台单位信息
		List<MerchantInfo> merchantList = payService.checkMerchant(payKey);
		if(merchantList == null || merchantList.size()<=0){
			map.put("error", -1);
			return map;
		}
		map.put("merchantInfo", merchantList.get(0));
		String userNO = merchantList.get(0).getUser_no();
		//获取单位信息
		List<BaseUnit> unitList = jfPayService.getBaseUnit(userNO);
		if(unitList == null || unitList.size()<=0){
			map.put("error", -1);
			return map;
		}
		map.put("baseUnit",unitList.get(0));
		//获取缴费类型信息
		List<PayerType> payTypeList = jfPayService.getPayerType(payerTypecode);
		if(payTypeList == null || payTypeList.size()<=0){
			map.put("error", -1);
			return map;
		}
		//判断是否属于限额缴费类型
		if(payTypeList.get(0).getIslimitamt()>0){
			double limitAmt = jfPayService.getLimitAmt(payTypeList.get(0).getPayertypeid(),unitList.get(0).getUnitid());
			if(amt>limitAmt){
				map.put("error", -1);
				return map;
			}
		}
		map.put("payerType",payTypeList.get(0));
		//验证银行信息
		List<PayWay> payWayList = payService.getPayWayList(payKey);
		if(payWayList == null || payWayList.size()<=0){
			map.put("error", -1);
			return map;
		}
		map.put("payWayList",payWayList);
		return map;
	}
	
	@Override
	public int insertJFPayOrder(String user_no, String orderNo,
			String payerName, String productName,String busiTypeCode, double amt,Date curDate) {
		//获取单位信息
		List<BaseUnit> unitList = jfPayService.getBaseUnit(user_no);
		if(unitList == null || unitList.size()<=0){
			return -1;
		}
		//获取缴费类型信息
		List<PayerType> payTypeList = jfPayService.getPayerType(productName);
		if(payTypeList == null || payTypeList.size()<=0){
			return -2;
		}
		//判断是否属于限额缴费类型
		if(payTypeList.get(0).getIslimitamt()>0){
			double limitAmt = jfPayService.getLimitAmt(payTypeList.get(0).getPayertypeid(),unitList.get(0).getUnitid());
			if(amt>limitAmt){
				return -3;
			}
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMDDHHmmss");
		String orderDate = format.format(curDate);
		int ret = jfPayService.insertJfPayorder(orderNo,unitList.get(0).getCantonid(),unitList.get(0).getUnitid(),payerName,payTypeList.get(0).getPayertypeid(),busiTypeCode,amt,orderDate);
		if(ret>0){
			return 0;
		}else{
			return -4;
		}
	}

	@Override
	public int updateJFPayorderState(String user_no, String merchant_order_no,int status,int bankid,String bankOrderNo) {
		//获取单位信息
		List<BaseUnit> unitList = jfPayService.getBaseUnit(user_no);
		if(unitList == null || unitList.size()<=0){
			return -1;
		}
		int ret = jfPayService.updateJFOrderBankVSState(unitList.get(0).getUnitid(),merchant_order_no,status,bankid,bankOrderNo);
		if(ret<=0){
			return -2;
		}
		return 0;
	}

	@Override
	public int getJFBankInfo(String pay_way_name) {
		List<JFBankInfo> bankList = jfPayService.getJFBankInfo(pay_way_name);
		if(bankList==null || bankList.size()<=0){
			return 0;
		}
		return bankList.get(0).getBankClsId();
	}

	@Override
	public int updateJFOrderStatus(String orderId,String jfStatus) {
		
		return jfPayService.updateJFOrderStatus(orderId,jfStatus);
	}

	
}
