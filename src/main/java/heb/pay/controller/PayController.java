package heb.pay.controller;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import heb.pay.entity.MerchantInfo;
import heb.pay.entity.MerchantPayConfig;
import heb.pay.entity.MerchantPayInfo;
import heb.pay.entity.PayWay;
import heb.pay.entity.PayerType;
import heb.pay.entity.PaymentOrder;
import heb.pay.service.NotifyService;
import heb.pay.service.PayOpService;
import heb.pay.service.PayService;
import heb.pay.util.ApplicationUtils;
import heb.pay.util.CheckBankDataUtils;
import heb.pay.util.MD5Util;
import heb.pay.util.PageUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PayController {
	
	private Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	private PayService payService;
	
	@Autowired
	private NotifyService notifyService;
	
	@Autowired
	private PayOpService payOpService;
	
	private final String xmlHeader = "<?xml version='1.0' encoding='utf-8'?>";
	
	@RequestMapping(value="/pay/inpay")
	public ModelAndView insertOrderInfo(HttpServletRequest request,HttpServletResponse response, ModelAndView model){
		//获取参数信息
		Map<String,Object> params = PageUtils.getParameters(request);
		logger.info(params.toString());
		String payKey = params.get("payKey")==null?"":params.get("payKey").toString();
		String orderNo = params.get("payerNum")==null?"":params.get("payerNum").toString();
		DecimalFormat df = new DecimalFormat("######0.00"); 
		double amt = params.get("amt")==null?0:Double.parseDouble(params.get("amt").toString());
		int orderPeriod = Integer.parseInt(ApplicationUtils.getApplicationSettings().getProperty("order.orderPeriod","86400"));
		String returnUrl = params.get("returnUrl")==null?"":params.get("returnUrl").toString();
		String notifyUrl = params.get("notifyUrl")==null?"":params.get("notifyUrl").toString();
		String reserve1 = params.get("reserve1")==null?"":params.get("reserve1").toString();
		String reserve2 = params.get("reserve2")==null?"":params.get("reserve2").toString();
		String reserve3 = params.get("reserve3")==null?"":params.get("reserve3").toString();
		String payerName = params.get("payerName")==null?"":params.get("payerName").toString();
		String productName = params.get("payerTypeCode")==null?"":params.get("payerTypeCode").toString();
		String busiTypeCode = params.get("busiTypeCode")==null?"":params.get("busiTypeCode").toString();
		int version = Integer.parseInt(ApplicationUtils.getApplicationSettings().getProperty("order.version","0"));
		
		//验证数据
		Map<String,Object> retMap = payOpService.checkBaseInfo(payKey, productName, amt);
		if(retMap.containsKey("error")){
			int ckRet = Integer.parseInt(retMap.get("error").toString());
			switch (ckRet) {
			case -1:
				model.addObject("errorCode", "ERROR1569875421001");
				model.addObject("errorInfo", "用户不存在！");
				model.setViewName("payment_defeat");
				break;
			case -2:
				model.addObject("errorCode", "ERROR78984710025");
				model.addObject("errorInfo", "该用户下没有该缴费类型！");
				model.setViewName("payment_defeat");
				break;
			case -3:
				model.addObject("errorCode", "ERROR73901038718");
				model.addObject("errorInfo", "超出限额！");
				model.setViewName("payment_defeat");
				break;
			case -4:
				model.addObject("errorCode", "ERROR78984710025");
				model.addObject("errorInfo", "该用户下没有可用支付方式！");
				model.setViewName("payment_defeat");
				break;
			}
			return model;
		}
		
		//获取商户信息
		MerchantInfo merchantInfo = (MerchantInfo)retMap.get("merchantInfo");
		model.addObject("merchantName",merchantInfo.getUser_name());
		//获取银行信息
		@SuppressWarnings("unchecked")
		List<PayWay> payWayList = (List<PayWay>)retMap.get("payWayList");
		//获取缴费类型信息
		PayerType payerType = (PayerType)retMap.get("payerType");
		
		//验证订单号是否存在，如果存在返回订单信息。
		List<PaymentOrder> orderList = payService.getPaymentOrderByOrderNum(orderNo,merchantInfo.getUser_no(),productName,busiTypeCode,amt);
		Date curDate = null;
		if(orderList != null && orderList.size()>0){
			//如果订单已经支付，则跳转到错误页面，提示已经支付成功不可以再支付
			if(orderList.get(0).getStatus().equals("SUCCESS")){
				model.addObject("errorCode", "ERROR-ORDER"+orderList.get(0).getMerchant_order_no());
				model.addObject("errorInfo", "当前订单已支付成功，无需重复支付，请查看你的交易记录确认！");
				model.setViewName("payment_defeat");
				return model;
			}
			if(orderList.get(0).getStatus().equals("WAITING_PAYMENT")){
				model.addObject("errorCode", "ERROR-ORDER"+orderList.get(0).getMerchant_order_no());
				model.addObject("errorInfo", "订单正在支付中，请等待支付完成！");
				model.setViewName("payment_defeat");
				return model;
			}
			//判断订单是否超时
			Date dateDB = orderList.get(0).getOrder_expire_time();
			curDate = new Date();
			if((curDate.getTime()-dateDB.getTime())>=0){
				model.addObject("errorCode", "ERROR-ORDER"+orderList.get(0).getMerchant_order_no());
				model.addObject("errorInfo", "此订单已过期，请重新生成订单再支付！");
				model.setViewName("payment_defeat");
				return model;
			}
			//如果订单状态为未支付，则重新展示支付页面！
			model.addObject("payerName", payerName);
			model.addObject("amt", orderList.get(0).getOrder_amount());
			model.addObject("payerTypeCode", orderList.get(0).getProduct_name());
			model.addObject("busiTypeCode", orderList.get(0).getBusi_type_code());
			model.addObject("payerNum", orderNo);
			model.addObject("payerTypeName",payerType==null?"":payerType.getPayertypename());
			model.addObject("payKey", payKey);
			Map<String,String> payWayMap = new HashMap<String, String>();
			String originalStr = "amt="+df.format(orderList.get(0).getOrder_amount())+"&busiTypeCode="+orderList.get(0).getBusi_type_code();
			originalStr += "&payKey="+payKey+"&payWay=";
			String paySecret = payService.getSecretKeyByPaykey(payKey);
			List<Map<String,String>> bankInfo = new ArrayList<Map<String,String>>();
			for(int i =0;i<payWayList.size();i++){
				payWayMap = new HashMap<String, String>();
				String payWayCode = payWayList.get(i).getPay_way_code();
				String payTypeCode = payWayList.get(i).getPay_type_code();
				String pwdStr = originalStr + payWayCode + "|" + payTypeCode + "&payerNum=" + orderNo + "&payerTypeCode=" + orderList.get(0).getProduct_name();
				payWayMap.put("bankCode", payWayCode);
				payWayMap.put("bankType",payWayCode + "|" + payTypeCode+":"+ MD5Util.md5(pwdStr,"&paySecret=" + paySecret));
				bankInfo.add(payWayMap);
			}
			model.addObject("bankInfo", bankInfo);
			model.setViewName("payment");
			return model;
		}
		//如果订单不存在，则生成订单信息，并保存至订单表
		PaymentOrder paymentOrder = new PaymentOrder();
		String uuid = PageUtils.getUUID();
		
		curDate = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(curDate);
		c.add(Calendar.MINUTE, orderPeriod/60);
		Date periodDate = c.getTime();
		
		paymentOrder.setId(uuid);
		paymentOrder.setVersion(version);
		paymentOrder.setProduct_name(productName);
		paymentOrder.setMerchant_no(merchantInfo.getUser_no());
		paymentOrder.setMerchant_name(merchantInfo.getUser_name());
		paymentOrder.setMerchant_order_no(orderNo);
		paymentOrder.setOrder_amount(amt);
		paymentOrder.setOrder_period(orderPeriod);
		paymentOrder.setReturn_url(returnUrl);
		paymentOrder.setNotify_url(notifyUrl);
		paymentOrder.setField1(reserve1);
		paymentOrder.setField2(reserve2);
		paymentOrder.setField3(reserve3);
		paymentOrder.setStatus("CREATED");
		paymentOrder.setOrder_ip("");
		paymentOrder.setOrder_referer_url("");
		paymentOrder.setModificator(merchantInfo.getUser_no());
		paymentOrder.setModify_time(curDate);
		paymentOrder.setCreator(merchantInfo.getUser_no());
		paymentOrder.setOrder_time(curDate);
		paymentOrder.setOrder_date(curDate);
		paymentOrder.setCreate_time(curDate);
		paymentOrder.setOrder_expire_time(periodDate);
		paymentOrder.setBusi_type_code(busiTypeCode);
		
		int ret = payService.insertPaymentOrder(paymentOrder);
		if(ret<=0){
			model.addObject("errorCode", "ERROR-ORDER"+orderList.get(0).getMerchant_order_no());
			model.addObject("errorInfo", "创建订单失败！");
			model.setViewName("payment_defeat");
			return model;
		}
		//插入支付平台订单表的同时插入缴费平台订单表
		int retJF = payOpService.insertJFPayOrder(merchantInfo.getUser_no(),orderNo,payerName,productName,busiTypeCode,amt,curDate);
		if(retJF<0){
			switch (retJF) {
			case -1:
				model.addObject("errorCode", "ERROR-ORDER"+orderList.get(0).getMerchant_order_no());
				model.addObject("errorInfo", "用户不存在");
				model.setViewName("payment_defeat");
				break;
			case -2:
				model.addObject("errorCode", "ERROR-ORDER"+orderList.get(0).getMerchant_order_no());
				model.addObject("errorInfo", "缴费类型不存在！");
				model.setViewName("payment_defeat");
				break;
			case -3:
				model.addObject("errorCode", "ERROR-ORDER"+orderList.get(0).getMerchant_order_no());
				model.addObject("errorInfo", "金额超出限额标准！");
				model.setViewName("payment_defeat");
				break;
			case -4:
				model.addObject("errorCode", "ERROR-ORDER"+orderList.get(0).getMerchant_order_no());
				model.addObject("errorInfo", "创建订单失败！");
				model.setViewName("payment_defeat");
				break;
			}
			return model;
		}
		model.addObject("payerName", payerName);
		model.addObject("amt", amt);
		model.addObject("merchantName",merchantInfo.getUser_name());
		model.addObject("payerTypeCode", productName);
		model.addObject("payerTypeName",payerType==null?"":payerType.getPayertypename());
		model.addObject("busiTypeCode", busiTypeCode);
		model.addObject("payerNum", orderNo);
		model.addObject("payKey", payKey);

		Map<String,String> payWayMap = new HashMap<String, String>();
		String originalStr = "amt=" + df.format(amt) + "&busiTypeCode=" + busiTypeCode;
		originalStr += "&payKey="+payKey+"&payWay=";
		String paySecret = payService.getSecretKeyByPaykey(payKey);
		List<Map<String,String>> bankInfo = new ArrayList<Map<String,String>>();
		for(int i =0;i<payWayList.size();i++){
			payWayMap = new HashMap<String, String>();
			String payWayCode = payWayList.get(i).getPay_way_code();
			String payTypeCode = payWayList.get(i).getPay_type_code();
			String pwdStr = originalStr + payWayCode + "|" + payTypeCode + "&payerNum=" + orderNo + "&payerTypeCode=" + productName;
			payWayMap.put("bankCode", payWayCode);
			payWayMap.put("bankType",payWayCode + "|" + payTypeCode+":"+ MD5Util.md5(pwdStr,"&paySecret=" + paySecret));
			bankInfo.add(payWayMap);
		}
		model.addObject("bankInfo", bankInfo);
		model.setViewName("payment");
		return model;
	}
	
	@RequestMapping(value="/pay/orderQuery")///pay/orderQuery
	public void getOrderQuery(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> params = PageUtils.getParameters(request);
		logger.info(params.toString());
		String payKey = params.get("payKey")==null?"":params.get("payKey").toString();
		String busiTypeCode = params.get("busiTypeCode")==null?"":params.get("busiTypeCode").toString();
		String payerTypeCode = params.get("payerTypeCode")==null?"":params.get("payerTypeCode").toString();
		String payerNum = params.get("payerNum")==null?"":params.get("payerNum").toString();
		String startDate = params.get("startDate")==null?"":params.get("startDate").toString();
		String endDate = params.get("endDate")==null?"":params.get("endDate").toString();
		int pageIndex = params.get("pageIndex")==null?1:Integer.parseInt(params.get("pageIndex").toString());
		
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		
		//验证商户是否存在并且可用！如果可用返回商户对象
		List<MerchantInfo> merchantList = payService.checkMerchant(payKey);
		if(merchantList == null || merchantList.size()<=0){

			try{
				httpResponse.getWriter().write("用户不存在！");
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		List<PaymentOrder> orderList = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyymmdd");
		Date dt_start = null;
		Date dt_end = null;
		try {
			if(!startDate.equals("")){
				dt_start = simpleDateFormat.parse(startDate);
			}
			if(!endDate.equals("")){
				dt_end = simpleDateFormat.parse(endDate);
			}
		} catch (Exception e) {
			
		}
		int total = 0;
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("payerNum", payerNum);
		map.put("payKey", payKey);
		map.put("dt_start", dt_start);
		map.put("dt_end", dt_end);
		map.put("pageIndex", pageIndex);
		map.put("payerTypeCode", payerTypeCode);
		map.put("busiTypeCode",busiTypeCode);
		map.put("merchantNo",merchantList.get(0).getUser_no());
		if(!payerNum.equals("")){
			orderList = payService.getOrderSingleByOrderNum(map);
		}else{
			orderList = payService.getOrderSingleByOrderNum(map);
			total = payService.getOrderCount(map);
		}
		
		StringBuilder xmlSB = new StringBuilder();
		xmlSB.append(xmlHeader);
		if(orderList.size()>0){
			xmlSB.append("<QUERYINFO>").append("<IS_SUCCESS>T</IS_SUCCESS>");
			if(total>0){
				xmlSB.append("<TOTAL>").append(total).append("</TOTAL>");
			}
			xmlSB.append("<RESULTS>");
			for(int i =0;i<orderList.size();i++){
				String statusDB = orderList.get(i).getStatus();
				String status = "0";
				
				if(statusDB.equals("SUCCESS")){
					status = "1";
				}else if(statusDB.equals("WAITING_PAYMENT")){
					status = "2";
				}
				xmlSB.append("<RESULT>").append("<BUSITYPECODE>").append(orderList.get(i).getBusi_type_code()).append("</BUSITYPECODE>").append("<PAYERTYPECODE>").append(orderList.get(i).getProduct_name()).append("</PAYERTYPECODE>").append("<PAYERNUM>").append(orderList.get(i).getMerchant_order_no()).append("</PAYERNUM>").append("<STATUS>").append(status).append("</STATUS>").append("</RESULT>");
			}
			xmlSB.append("</RESULTS></QUERYINFO>");
		}else{
			xmlSB.append("<QUERYINFO><IS_SUCCESS>F</IS_SUCCESS><ERROR>没有符合条件的数据</ERROR></QUERYINFO>");
		}
		
		try{
			httpResponse.getWriter().write(xmlSB.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	@RequestMapping(value="/paynotify/return/{bank}")
	public ModelAndView getReturn(@PathVariable String bank,HttpServletRequest request,HttpServletResponse response,ModelAndView model){
		LinkedHashMap<String,String> params = PageUtils.getLinkedParameters(request);
		logger.info(params.toString());
		String postId = params.get("POSID")==null?"":params.get("POSID");
		String bankId = params.get("BRANCHID")==null?"":params.get("BRANCHID");
		String orderId = params.get("ORDERID")==null?"":params.get("ORDERID");
		String success = params.get("SUCCESS")==null?"":params.get("SUCCESS");
		
		List<MerchantPayInfo> merPayInfoList = payService.getMerPayInfoByPostid(postId, bankId);
		if(merPayInfoList==null || merPayInfoList.size()<=0){
			model.addObject("errorCode", "ERROR-ORDER"+orderId);
			model.addObject("errorInfo", "未找到商户信息！");
			model.setViewName("payment_defeat");
			return model;
		}
		logger.info("notice正在执行...商户验证通过...");
		String publicKey = merPayInfoList.get(0).getTg_rsa_public_key();
		if(!CheckBankDataUtils.checkCCBData(params, publicKey)){
			model.addObject("errorCode", "ERROR-ORDER"+orderId);
			model.addObject("errorInfo", "数据校验失败！");
			model.setViewName("payment_defeat");
			return model;
		}
		logger.info("notice正在执行...数据验证通过...");
		String statusReturn = "";
		String statusDB = "";
		if(success.equals("Y")){
			statusReturn = "1";
			statusDB = "SUCCESS";
		}else{
			statusReturn = "0";
			statusDB = "FAILED";
		}
		
		logger.info("notice正在执行...状态获取成功..."+statusDB);
		List<PaymentOrder> payOrderList = payService.getPayOrderByOrderId(orderId);
		if(payOrderList == null || payOrderList.size()<=0){
			model.addObject("errorCode", "ERROR-ORDER"+orderId);
			model.addObject("errorInfo", "未找到订单信息！");
			model.setViewName("payment_defeat");
			return model;
		}
		//获取缴费类型名称
		List<PayerType> payerTypeList = payService.getPayerType(payOrderList.get(0).getProduct_name());
		if(payerTypeList == null || payerTypeList.size()<=0){
			model.addObject("errorCode", "ERROR78984710025");
			model.addObject("errorInfo", "该用户下没有该缴费类型！");
			model.setViewName("payment_defeat");
			return model;
		}
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
		//更新缴费订单信息
		
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
		map.put("busiTypeCode",busiTypeCode);
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
		if(payOrderList.get(0).getReturn_url()==null || payOrderList.get(0).getReturn_url().equals("")){
			if(success.equals("Y")){
				model.addObject("payerNum", payOrderList.get(0).getMerchant_order_no());
				model.addObject("amt", payOrderList.get(0).getOrder_amount());
				model.addObject("payerTypeCode", payerTypeList.get(0).getPayertypename());
				model.setViewName("payment_ok");
			}else{
				model.addObject("errorCode", "ERROR-ORDER"+payOrderList.get(0).getMerchant_order_no());
				model.addObject("errorInfo", "订单支付失败！");
				model.setViewName("payment_defeat");
			}
		}
		return model;
}

	@RequestMapping(value="/paynotify/notify/{bank}")
	public void getNotify(@PathVariable String bank,HttpServletRequest request,HttpServletResponse response){
		LinkedHashMap<String,String> params = PageUtils.getLinkedParameters(request);
		logger.info(params.toString());
		notifyService.notice(params,0);
		
	}
}
