package heb.pay.service.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import heb.pay.entity.MerchantPayConfig;
import heb.pay.entity.MerchantPayInfo;
import heb.pay.entity.PayWay;
import heb.pay.entity.PaymentOrder;
import heb.pay.manage.activemq.QueueSender;
import heb.pay.service.NotifyService;
import heb.pay.service.PayService;
import heb.pay.util.CheckBankDataUtils;
import heb.pay.util.JSONUtils;
import heb.pay.util.MD5Util;

@Service
public class NotifyServiceImpl implements NotifyService {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private PayService payService;
	
	@Autowired
	private QueueSender queueSender;

	@Override
	public void notice(LinkedHashMap<String, String> params, int type) {
		logger.info("notice正在执行....");
		ModelAndView model = new ModelAndView();
		String postId = params.get("POSID")==null?"":params.get("POSID");
		String bankId = params.get("BRANCHID")==null?"":params.get("BRANCHID");
		String orderId = params.get("ORDERID")==null?"":params.get("ORDERID");
		String success = params.get("SUCCESS")==null?"":params.get("SUCCESS");
		
		List<MerchantPayInfo> merPayInfoList = payService.getMerPayInfoByPostid(postId, bankId);
		if(merPayInfoList==null || merPayInfoList.size()<=0){
			return;
		}
		logger.info("notice正在执行...商户验证通过...");
		if(type==0){
			String publicKey = merPayInfoList.get(0).getTg_rsa_public_key();
			if(!CheckBankDataUtils.checkCCBData(params, publicKey)){
				return;
			}
		}
		logger.info("notice正在执行...数据验证通过...");
		String statusReturn = "";
		String statusDB = "";
		if(type==0){
			if(success.equals("Y")){
				statusReturn = "1";
				statusDB = "SUCCESS";
			}else{
				statusReturn = "0";
				statusDB = "FAILED";
			}
		}else if(type==1){
			if(success.equals("1")){
				statusReturn = "1";
				statusDB = "SUCCESS";
			}else{
				statusReturn = "0";
				statusDB = "FAILED";
			}
		}
		
		logger.info("notice正在执行...状态获取成功..."+statusDB);
		List<PaymentOrder> payOrderList = payService.getPayOrderByOrderId(orderId);
		if(payOrderList == null || payOrderList.size()<=0){
			return;
		}
		logger.info("notice正在执行...订单验证通过...");
		if(!payOrderList.get(0).getStatus().equals("SUCCESS")){
			int ret = payService.updateOrderByOrderId(orderId,statusDB);
			if(ret>0){
				int retRecord = payService.updateRecordByOrderId(orderId,statusDB);
				if(retRecord<0){
					return;
				}
			}else{
				return;
			}
			
		}
		
		logger.info("notice正在执行...订单更新通过...");
		String merchantNO = payOrderList.get(0).getMerchant_no();
		String merOrderNO = payOrderList.get(0).getMerchant_order_no();
		String payWayCode = payOrderList.get(0).getPay_way_code();
		String payTypeCode = payOrderList.get(0).getPay_type_code();
		String payerTypeCode = payOrderList.get(0).getProduct_name();
		String busiTypeCode = payOrderList.get(0).getBusi_type_code();
		double amt = payOrderList.get(0).getOrder_amount();
		List<PayWay> payWayList = payService.getPayWayByCode(payWayCode,payTypeCode);
		String payProductCode = payWayList.get(0).getPay_product_code();
		List<MerchantPayConfig> merPayConfigList = payService.getMerPayConfig(merchantNO,payProductCode);
		String payKey = merPayConfigList.get(0).getPay_key();
		String paySecret = merPayConfigList.get(0).getPay_secret();
		//插入t_jfpayorder表
		/*List<BaseUnit> unitList = payService.getBaseUnit(merchantNO);
		if(unitList == null || unitList.size()<=0){
			model.addObject("errorCode", "ERROR-ORDER"+orderId);
			model.addObject("errorInfo", "获取单位区划基本信息失败");
			model.setViewName("payment_defeat");
			return model;
		}
		int unitId = unitList.get(0).getUnitid();
		int cantonId = unitList.get(0).getCantonid();
		List<PayerType> payTypeList = payService.getPayerType(payerTypeCode); 
		if(payTypeList == null || payTypeList.size()<=0){
			model.addObject("errorCode", "ERROR-ORDER"+orderId);
			model.addObject("errorInfo", "获取缴费类型基本信息失败");
			model.setViewName("payment_defeat");
			return model;
		}
		int payerTypeId = payTypeList.get(0).getPayertypeid();*/
		logger.info("notice正在执行...即将跳转地址...");
		String returnURL = "redirect:";
		returnURL += payService.getReturnURLByOrderNum(merchantNO,merOrderNO);
		model.setViewName(returnURL);
		logger.info("notice正在执行...地址：..."+returnURL);
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
		model.addObject("status",statusReturn);
		map.put("status", statusReturn);
		originalStr += "&status="+statusReturn;
		String sign = MD5Util.md5(originalStr,"&paySecret="+paySecret);
		model.addObject("sign",sign);
		map.put("sign", sign);
		model.addObject("signType", "MD5");
		map.put("signType", "MD5");
		logger.info("notice正在执行...参数设置成功..."+map.toString()+"..即将通知后台...");
		if(type==1){
			if(payOrderList.get(0).getStatus().equals("SUCCESS")){
				return;
			}
		}
		Map<String,Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("user_no", merchantNO);
		paramsMap.put("payerNum", merOrderNO);
		int count = payService.getCount(paramsMap);
		if(count==0){
			queueSender.send(JSONUtils.mapToJson(map));
		}

	}

}
