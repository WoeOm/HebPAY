package heb.pay.service.impl;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.abc.pay.client.TrxException;
import com.abc.pay.client.ebus.PaymentResult;

import heb.pay.dao.PaymentDAO;
import heb.pay.entity.MerchantPayConfig;
import heb.pay.entity.MerchantPayInfo;
import heb.pay.entity.PayWay;
import heb.pay.entity.PaymentOrder;
import heb.pay.manage.activemq.QueueSender;
import heb.pay.service.PayCenterService;
import heb.pay.service.PayBaseService;
import heb.pay.service.PaymentService;
import heb.pay.util.BankConfigUtils;
import heb.pay.util.CheckBankDataUtils;
import heb.pay.util.JSONUtils;
import heb.pay.util.MD5Util;

@Service
public class PaymentServiceImpl implements PaymentService {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	@Autowired
	private PaymentDAO paymentDAO;
	@Autowired
	private PayCenterService payCenterService;
	@Autowired
	private PayBaseService payOpService;
	@Autowired
	private QueueSender queueSender;

	@Override
	public int insertPaymentRecord(Map<String, Object> map) {
		return paymentDAO.insertPaymentRecord(map);
	}

	@Override
	public int updateOrderTrxNO(Map<String,String> map) {
		return paymentDAO.updateOrderTrxNO(map);
	}
	
	@Override
	public void notice(LinkedHashMap<String, String> params, int type,String bankName) {
		logger.info("##进入服务通知（notify）……");
		ModelAndView model = new ModelAndView();
		List<MerchantPayInfo> merPayInfoList = null;
		
		String orderNo = "";
		String orderStatus = "";
		
		String status = "0";
		String upStatus = "";
		if(bankName.equals("ccb")){
			//1 根据银行获取银行返回数据
			orderNo = params.get("ORDERID")==null?"":params.get("ORDERID");
			orderStatus = params.get("SUCCESS")==null?"":params.get("SUCCESS");
			
			String postId = params.get("POSID")==null?"":params.get("POSID");
			String bankId = params.get("BRANCHID")==null?"":params.get("BRANCHID");
			if(type == 0){
				if(orderStatus.equals("Y")){
					status = "1";
					upStatus = "SUCCESS";
				}else{
					status = "0";
					upStatus = "FAILED";
				}
				merPayInfoList = payCenterService.getMerPayInfoByPostid(postId, bankId);
				String publicKey = merPayInfoList.get(0).getTg_rsa_public_key();
				//如果type值为0，需要验证银行数据有效性
				if(!CheckBankDataUtils.checkCCBData(params, publicKey)){
					return;
				}
			}else if(type == 1){
				if(orderStatus.equals("1")){
					status = "1";
					upStatus = "SUCCESS";
				}else{
					status = "0";
					upStatus = "FAILED";
				}
			}
		}else if(bankName.equals("boc")){
			//1 根据银行获取银行返回数据
			orderNo = params.get("orderNo")==null?"":params.get("orderNo");
			orderStatus = params.get("orderStatus")==null?"":params.get("orderStatus");

			if(orderStatus.equals("1")){
				status = "1";
				upStatus = "SUCCESS";
			}else{
				status = "0";
				upStatus = "FAILED";
			}
		}else if(bankName.equals("abc")){
			if(type == 0){
				String msg = params.get("MSG");
				PaymentResult paymentResult = null;
				try {
					paymentResult = new PaymentResult(msg);
					boolean isSuccess = paymentResult.isSuccess();
					if(isSuccess){
						orderStatus="1";
						status = "1";
						upStatus = "SUCCESS";
						
					}else{
						orderStatus="0";
						status = "0";
						upStatus = "FAILED";
					}
					orderNo = paymentResult.getValue("OrderNo");
				} catch (TrxException e) {
					e.printStackTrace();
				}
			}else if(type == 1){
				orderNo = params.get("orderNo")==null?"":params.get("orderNo");
				orderStatus = params.get("orderStatus")==null?"":params.get("orderStatus");
				if(orderStatus.equals("1")){
					status = "1";
					upStatus = "SUCCESS";
				}else{
					status = "0";
					upStatus = "FAILED";
				}
			}
		}else if(bankName.equals("ceb")){//光大
			if(type == 0){
				String responseCode = params.get("ResponseCode").toString();
				String plain = params.get("Plain");
				String[] plains = plain.split("~|~");
				orderNo = plains[2].split("=")[1];
				if(responseCode.equals("0000")){//广大银行只要能接受到就是成功
					status = "1";
					upStatus = "SUCCESS";
				}
			}else if(type == 1){
				orderNo = params.get("orderNo")==null?"":params.get("orderNo");
				orderStatus = params.get("orderStatus")==null?"":params.get("orderStatus");
				if(orderStatus.equals("00")){
					status = "1";
					upStatus = "SUCCESS";
				}else{
					status = "0";
					upStatus = "FAILED";
				}
				
			}
		}
		
		//获取订单信息
		Map<String,Object> orderRetMap = payOpService.getPayOrderByOrderId(orderNo);
		if(orderRetMap.containsKey("error")){
			return;
		}
		
		PaymentOrder paymentOrder = (PaymentOrder)orderRetMap.get("paymentOrder");
		PayWay payWay = (PayWay)orderRetMap.get("payWay");
		//数据库订单状态
		String statusDB = paymentOrder.getStatus();
		
		//获取通知表中的条数
		String merchantNO = paymentOrder.getMerchant_no();
		String merOrderNO = paymentOrder.getMerchant_order_no();
		Map<String,Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("user_no",merchantNO);
		paramsMap.put("payerNum",merOrderNO);
		int count = payCenterService.getCount(paramsMap);
		if(count>0){
			return;
		}
		//判断不推送条件
		if(bankName.equals("ccb")){
			if(statusDB.equals("SUCCESS")||(statusDB.equals("FAILED")&& orderStatus.equals("N"))){
				return;
			}
		}else if(bankName.equals("boc")){
			if(statusDB.equals("SUCCESS")||(statusDB.equals("FAILED")&& !orderStatus.equals("1"))){
				return;
			}
		}else if(bankName.equals("abc")){
			if(statusDB.equals("SUCCESS")||(statusDB.equals("FAILED")&& !orderStatus.equals("1"))){
				return;
			}
		}
		
		//1.2更新订单表状态
		if(!statusDB.equals("SUCCESS")){
			int ret = payOpService.updateOrderStatus(orderNo,upStatus);
			if(ret<=0){
				return;
			}
		}
		
		//3 拼装数据，发送通知
		String payerTypeCode = paymentOrder.getProduct_name();
		String busiTypeCode = paymentOrder.getBusi_type_code();
		double amt = paymentOrder.getOrder_amount();
		String payProductCode = payWay.getPay_product_code();
		List<MerchantPayConfig> merPayConfigList = payCenterService.getMerPayConfig(merchantNO,payProductCode);
		String payKey = merPayConfigList.get(0).getPay_key();
		String paySecret = merPayConfigList.get(0).getPay_secret();
		String originalStr = "";
		LinkedHashMap<String,Object> map = new LinkedHashMap<String, Object>();
		model.addObject("amt",amt);
		map.put("amt", amt);
		originalStr += "amt="+amt;
		model.addObject("busiTypeCode",busiTypeCode);
		map.put("busiTypeCode", busiTypeCode);
		originalStr += "&busiTypeCode"+busiTypeCode;
		model.addObject("payKey",payKey);
		map.put("payKey", payKey);
		originalStr += "&payKey"+payKey;
		model.addObject("payerNum",merOrderNO);
		map.put("payerNum", merOrderNO);
		originalStr += "&payerNum"+merOrderNO;
		model.addObject("payerTypeCode",payerTypeCode);
		map.put("payerTypeCode", payerTypeCode);
		originalStr += "&payerTypeCode"+payerTypeCode;
		model.addObject("status",status);
		map.put("status", status);
		originalStr += "&status="+status;
		String sign = MD5Util.md5(originalStr,"&paySecret="+paySecret);
		model.addObject("sign",sign);
		map.put("sign", sign);
		model.addObject("signType", "MD5");
		map.put("signType", "MD5");
		logger.info("##服务端通知通过JMS发送……");
		queueSender.send(JSONUtils.mapToJson(map));
	}
	
	
}
