package heb.pay.service.impl;

import heb.pay.entity.BaseUnit;
import heb.pay.entity.JFBankInfo;
import heb.pay.entity.MerchantInfo;
import heb.pay.entity.MerchantPayConfig;
import heb.pay.entity.MerchantPayInfo;
import heb.pay.entity.PayWay;
import heb.pay.entity.PayerType;
import heb.pay.entity.PaymentOrder;
import heb.pay.service.JFPayService;
import heb.pay.service.PayOpService;
import heb.pay.service.PayService;
import heb.pay.service.PaymentService;
import heb.pay.util.CheckBankDataUtils;
import heb.pay.util.PageUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abc.pay.client.TrxException;
import com.abc.pay.client.ebus.PaymentResult;

@Service
public class PayOpServiceImpl implements PayOpService {
	
	@Autowired
	private JFPayService jfPayService;
	@Autowired
	private PayService payService;
	@Autowired
	private PaymentService paymentService;
	

	@Override
	public Map<String,Object> checkBaseInfo(String payKey, String payerTypecode, double amt) {
		Map<String,Object> map = new HashMap<String, Object>();
		//获取支付平台单位信息
		List<MerchantInfo> merchantList = payService.checkMerchant(payKey);
		if(merchantList == null || merchantList.size()<=0){
			map.put("error", "no_user");
			return map;
		}
		map.put("merchantInfo", merchantList.get(0));
		String userNO = merchantList.get(0).getUser_no();
		//获取单位信息
		List<BaseUnit> unitList = jfPayService.getBaseUnit(userNO);
		if(unitList == null || unitList.size()<=0){
			map.put("error", "no_unit");
			return map;
		}
		map.put("baseUnit",unitList.get(0));
		//获取缴费类型信息
		List<PayerType> payTypeList = jfPayService.getPayerType(payerTypecode);
		if(payTypeList == null || payTypeList.size()<=0){
			map.put("error", "no_paytype");
			return map;
		}
		//判断是否属于限额缴费类型
		if(payTypeList.get(0).getIslimitamt()>0){
			double limitAmt = jfPayService.getLimitAmt(payTypeList.get(0).getPayertypeid(),unitList.get(0).getUnitid());
			if(amt>limitAmt){
				map.put("error", "out_pay");
				return map;
			}
		}
		map.put("payerType",payTypeList.get(0));
		//验证银行信息
		List<PayWay> payWayList = payService.getPayWayList(payKey);
		if(payWayList == null || payWayList.size()<=0){
			map.put("error", "no_pay");
			return map;
		}
		map.put("payWayList",payWayList);
		return map;
	}
	
	@Override
	public Map<String, Object> checkOrderExist(String orderNo, String userNo,
			String payerTypeCode, String busiTypeCode, double amt) {
		List<PaymentOrder> orderList = payService.getPaymentOrderByOrderNum(orderNo,userNo,payerTypeCode,busiTypeCode,amt);
		Map<String, Object> map = new HashMap<String, Object>();
		if(orderList != null && orderList.size()>0){
			if(orderList.get(0).getStatus().equals("SUCCESS")){
				map.put("error", "payed");
				return map;
			}else if(orderList.get(0).getStatus().equals("WAITING_PAYMENT")){
				map.put("error", "paying");
				return map;
			}else if(orderList.get(0).getStatus().equals("FAILED")){
				map.put("error", "pay_fail");
				return map;
			}
			Date curDate = new Date();
			Date dateDB = orderList.get(0).getOrder_expire_time();
			if((curDate.getTime()-dateDB.getTime())>=0){
				map.put("error", "pay_out");
				return map;
			}
			map.put("isExist","1");
		}else{
			map.put("error", -5);
			map.put("isExist","0");
			return map;
		}
		map.put("paymentOrder",orderList.get(0));
		return map;
	}
	
	@Override
	public Map<String, Object> checkBankData(LinkedHashMap<String, String> params,String bankName) {
		Map<String,Object> map = new HashMap<String, Object>();
		String statusReturn = "";//推送执收单位页面状态
		String statusDB = "";//更新支付平台订单表状态
		
		String orderNo = "";//订单号
		String orderStatus = "";//订单状态
		
		if(bankName.equals("ccb")){
			orderNo = params.get("ORDERID")==null?"":params.get("ORDERID");
			orderStatus = params.get("SUCCESS")==null?"":params.get("SUCCESS");
			String postId = params.get("POSID")==null?"":params.get("POSID");
			String bankId = params.get("BRANCHID")==null?"":params.get("BRANCHID");
			List<MerchantPayInfo> merPayInfoList = payService.getMerPayInfoByPostid(postId, bankId);
			//执收单位不存在
			if(merPayInfoList==null || merPayInfoList.size()<=0){
				map.put("error","no_unit");
				return map;
			}
			//获取建行公钥
			map.put("merchantPayInfo",merPayInfoList.get(0));
			String publicKey = merPayInfoList.get(0).getTg_rsa_public_key();
			//数据验证不成功
			if(!CheckBankDataUtils.checkCCBData(params, publicKey)){
				map.put("error","vail_error");
				return map;
			}
			
			if(orderStatus.equals("Y")){
				statusReturn = "1";
				statusDB = "SUCCESS";
			}else{
				statusReturn = "0";
				statusDB = "FAILED";
			}
			
		}else if(bankName.equals("boc")){
			orderNo = params.get("orderNo")==null?"":params.get("orderNo");
			orderStatus = params.get("orderStatus")==null?"":params.get("orderStatus");
			//数据验证不成功
			if(!CheckBankDataUtils.checkBOCData(params)){
				map.put("error","vail_error");
				return map;
			}
			
			if(orderStatus.equals("1")){
				statusReturn = "1";
				statusDB = "SUCCESS";
			}else{
				statusReturn = "0";
				statusDB = "FAILED";
			}
		}else if(bankName.equals("abc")){
			String msg = params.get("MSG").toString();
			orderNo ="";
			try {
				PaymentResult paymentResult = new PaymentResult(msg);
				boolean isSuccess = paymentResult.isSuccess();
				if(isSuccess){
					statusReturn = "1";
					statusDB = "SUCCESS";
					
				}else{
					statusReturn = "0";
					statusDB = "FAILED";
				}
				orderNo = paymentResult.getValue("OrderNo");
			} catch (TrxException e) {
				e.printStackTrace();
			}
			
		}
		
		List<PaymentOrder> payOrderList = payService.getPayOrderByOrderId(orderNo);
		//订单不存在
		if(payOrderList == null || payOrderList.size()<=0){
			map.put("error","no_order");
			return map;
		}
		List<PayerType> payerTypeList = payService.getPayerType(payOrderList.get(0).getProduct_name());
		//支付类型不存在
		if(payerTypeList == null || payerTypeList.size()<=0){
			map.put("error","no_pay_type");
			return map;
		}
		
		//支付类型
		List<PayWay> payWayList = payService.getPayWayByCode(payOrderList.get(0).getPay_way_code(),payOrderList.get(0).getPay_type_code());
		//商户支付配置信息
		List<MerchantPayConfig> merPayConfigList = payService.getMerPayConfig(payOrderList.get(0).getMerchant_no(),payWayList.get(0).getPay_product_code());
		//返回数据
		map.put("statusReturn",statusReturn);//返回执收单位订单状态
		map.put("statusDB",statusDB);//数据库中的订单状态
		
		map.put("paymentOrder",payOrderList.get(0));
		map.put("payerType",payerTypeList.get(0));
		map.put("payWay",payWayList.get(0));
		map.put("merchantPayConfig",merPayConfigList.get(0));
		map.put("orderId",orderNo);
		map.put("returnUrl",payOrderList.get(0).getReturn_url());
		
		return map;
	}

	@Override
	public Map<String, Object> insertZFOrderRecord(String payWayCode,
			String payTpyeCode,String returnUrl,String notifyUrl, PaymentOrder paymentOrder) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		PayWay payWay = payService.getPayWayByCode(payWayCode,payTpyeCode).get(0);
		String uuid = PageUtils.getUUID();
		Map<String,Object> map = new HashMap<String, Object>();
		Map<String,Object> returnMap = new HashMap<String, Object>();
		map.put("payerNum", paymentOrder.getMerchant_order_no());
		map.put("payWayCode", payWayCode);
		map.put("payTypeCode", payTpyeCode);
		map.put("uuid", uuid);
		map.put("creator", paymentOrder.getMerchant_no());
		map.put("productName", paymentOrder.getProduct_name());
		map.put("merchantNo", paymentOrder.getMerchant_no());
		map.put("merchantName", paymentOrder.getMerchant_name());
		map.put("payWayName", payWay.getPay_way_name());
		map.put("payTypeName", payWay.getPay_type_name());
		map.put("returnURL", returnUrl);
		map.put("notifyURL", notifyUrl);
		map.put("amt", paymentOrder.getOrder_amount());
		map.put("status", "WAITING_PAYMENT");
		String trxNO = PageUtils.getUUID();
		String bankOrderNo = format.format(new Date());
		map.put("trxNO", trxNO);
		map.put("bankOrderNo", bankOrderNo);
		//插入支付平台记录表
		int ret = paymentService.insertPaymentRecord(map);
		if(ret>0){
			//更新订单表trxNO
			Map<String,String> orderMap = new HashMap<String, String>();
			orderMap.put("trxNO", trxNO);
			orderMap.put("payerNum", paymentOrder.getMerchant_order_no());
			orderMap.put("payWayCode", payWay.getPay_way_code());
			orderMap.put("payWayName", payWay.getPay_way_name());
			orderMap.put("payTypeCode", payWay.getPay_type_code());
			orderMap.put("payTypeName", payWay.getPay_type_name());
			orderMap.put("status", "WAITING_PAYMENT");
			ret = paymentService.updateOrderTrxNO(orderMap);
			//更新缴费平台订单表
			int bankid = 0;
			//获取缴费平台银行信息
			bankid = getJFBankInfo(payWay.getPay_way_name());
			ret = updateJFPayorderState(paymentOrder.getMerchant_no(),paymentOrder.getMerchant_order_no(),1,bankid,bankOrderNo);
			returnMap.put("bankOrderNo",bankOrderNo);
		}else{
			returnMap.put("error",-1);
		}
		return returnMap;
	}

	@Override
	public int insertPayOrder(PaymentOrder paymentOrder,String payerName) {
		//插入支付平台表
		int ret = payService.insertPaymentOrder(paymentOrder);
		if(ret>0){
			//获取单位信息
			List<BaseUnit> unitList = jfPayService.getBaseUnit(paymentOrder.getMerchant_no());
			//获取缴费类型信息
			List<PayerType> payTypeList = jfPayService.getPayerType(paymentOrder.getProduct_name());
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");
			String orderDate = format.format(paymentOrder.getCreate_time());
			ret = jfPayService.insertJfPayorder(paymentOrder.getMerchant_order_no(),unitList.get(0).getCantonid(),unitList.get(0).getUnitid(),payerName,payTypeList.get(0).getPayertypeid(),paymentOrder.getBusi_type_code(),paymentOrder.getOrder_amount(),orderDate);
		}
		return ret;
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

	@Override
	public int updateOrderStatus(String orderId, String statusDB) {
		int ret = payService.updateOrderByOrderId(orderId,statusDB);
		if(ret>0){
			ret = payService.updateRecordByOrderId(orderId,statusDB);
		}
		return ret;
	}

	@Override
	public Map<String,Object> getPayOrderByOrderId(String orderId) {
		List<PaymentOrder> payOrderList = payService.getPayOrderByOrderId(orderId);
		Map<String,Object> map = new HashMap<String, Object>();
		if(payOrderList == null || payOrderList.size()<=0){
			map.put("error",-1);
			return map;
		}
		List<PayWay> payWayList = payService.getPayWayByCode(payOrderList.get(0).getPay_way_code(),payOrderList.get(0).getPay_type_code());
		if(payWayList == null || payWayList.size()<=0){
			map.put("error",-2);
			return map;
		}
		String notifyUrl = payService.getNotifyUrlByOrderId(orderId);
		
		map.put("paymentOrder",payOrderList.get(0));
		map.put("payWay",payWayList.get(0));
		map.put("notifyUrl",notifyUrl);
		return map;
	}

	
}
