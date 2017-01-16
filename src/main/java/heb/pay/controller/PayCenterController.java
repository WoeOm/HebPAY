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
import heb.pay.entity.PayWay;
import heb.pay.entity.PayerType;
import heb.pay.entity.PaymentOrder;
import heb.pay.service.PayBaseService;
import heb.pay.service.PayCenterService;
import heb.pay.service.PaymentService;
import heb.pay.util.ApplicationUtils;
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
public class PayCenterController {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private PayCenterService payCenterService;
	
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private PayBaseService payBaseService;
	
	private final String xmlHeader = "<?xml version='1.0' encoding='utf-8'?>";
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/pay/inpay")
	public ModelAndView doPayIn(HttpServletRequest request,HttpServletResponse response, ModelAndView model){
		logger.info("##进入支付选择页面……");
		//获取参数信息
		Map<String,Object> params = PageUtils.getParameters(request);
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
		Map<String,Object> retMap = payBaseService.checkBaseInfo(payKey, productName, amt);
		if(retMap.containsKey("error")){
			String error = retMap.get("error").toString();
			switch (error) {
			case "no_user":
				model.addObject("errorCode", "1401");
				model.addObject("errorInfo", "用户不存在！");
				model.setViewName("payment_defeat");
				break;
			case "no_unit":
				model.addObject("errorCode", "1402");
				model.addObject("errorInfo", "用户不存在！");
				model.setViewName("payment_defeat");
				break;
			case "no_paytype":
				model.addObject("errorCode", "1403");
				model.addObject("errorInfo", "该用户下没有该缴费类型！");
				model.setViewName("payment_defeat");
				break;
			case "out_pay":
				model.addObject("errorCode", "1404");
				model.addObject("errorInfo", "超出限额！");
				model.setViewName("payment_defeat");
				break;
			case "no_pay":
				model.addObject("errorCode", "1405");
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
		List<PayWay> payWayList = (List<PayWay>)retMap.get("payWayList");
		//获取缴费类型信息
		PayerType payerType = (PayerType)retMap.get("payerType");
		
		//验证订单号是否存在，如果存在返回订单信息。
		List<PaymentOrder> orderList = payCenterService.getPaymentOrderByOrderNum(orderNo,merchantInfo.getUser_no(),productName,busiTypeCode,amt);
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
		}else{
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

			//插入缴费平台订单表和支付平台订单表
			int ret = payBaseService.insertPayOrder(paymentOrder,payerName);
			if(ret<0){
				model.addObject("errorCode", "ERROR-ORDER"+orderList.get(0).getMerchant_order_no());
				model.addObject("errorInfo", "创建订单失败！");
				model.setViewName("payment_defeat");
				return model;
			}
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
		MerchantPayConfig merchantPayConfig = payCenterService.getSecretKeyByPaykey(payKey);
		List<Map<String,String>> bankInfo = new ArrayList<Map<String,String>>();
		for(int i =0;i<payWayList.size();i++){
			payWayMap = new HashMap<String, String>();
			String payWayCode = payWayList.get(i).getPay_way_code();
			String payTypeCode = payWayList.get(i).getPay_type_code();
			String pwdStr = originalStr + payWayCode + "|" + payTypeCode + "&payerNum=" + orderNo + "&payerTypeCode=" + productName;
			payWayMap.put("bankCode", payWayCode);
			payWayMap.put("bankType",payWayCode + "|" + payTypeCode+":"+ MD5Util.md5(pwdStr,"&paySecret=" + merchantPayConfig.getPay_secret()));
			bankInfo.add(payWayMap);
		}
		model.addObject("bankInfo", bankInfo);
		model.setViewName("payment");
		return model;
	}
	
	@RequestMapping(value="/pay/orderQuery")///pay/orderQuery
	public void orderQuery(HttpServletRequest request,HttpServletResponse response){
		logger.info("##执收单位查询订单信息……");
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
		List<MerchantInfo> merchantList = payCenterService.checkMerchant(payKey);
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
			orderList = payCenterService.getOrderSingleByOrderNum(map);
		}else{
			orderList = payCenterService.getOrderSingleByOrderNum(map);
			total = payCenterService.getOrderCount(map);
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
		logger.info("##进入页面通知……");
		LinkedHashMap<String,String> params = PageUtils.getLinkedParameters(request);
		//1 获取银行页面通知参数
		Map<String,Object> checkBankData = null;
		if(bank.equals("ccb")){
			checkBankData = payBaseService.checkBankData(params,"ccb");
		}else if(bank.equals("boc")){
			checkBankData = payBaseService.checkBankData(params,"boc");
		}else if(bank.equals("abc")){
			checkBankData = payBaseService.checkBankData(params,"abc");
		}else if(bank.equals("ceb")){
			checkBankData = payBaseService.checkBankData(params,"ceb");
		}
		
		//2 根据参数判断数据有效性
		if(checkBankData.containsKey("error")){
			String errorRet = checkBankData.get("error").toString();
			switch (errorRet) {
			case "no_unit":
				model.addObject("errorCode", "1301");
				model.addObject("errorInfo", "执收单位不存在！");
				model.setViewName("payment_defeat");
				break;
			case "vail_error":
				model.addObject("errorCode", "1302");
				model.addObject("errorInfo", "数据校验失败！");
				model.setViewName("payment_defeat");
				break;
			case "no_order":
				model.addObject("errorCode", "1303");
				model.addObject("errorInfo", "订单号不存在！");
				model.setViewName("payment_defeat");
				break;
			case "no_pay_type":
				model.addObject("errorCode", "1304");
				model.addObject("errorInfo", "缴费类型不存在！");
				model.setViewName("payment_defeat");
				break;
			}
			return model;
		}
		
		//如果返回成功，获取返回结果
		String statusReturn = checkBankData.get("statusReturn").toString();
		PaymentOrder paymentOrder = (PaymentOrder)checkBankData.get("paymentOrder");
		MerchantPayConfig merchantPayConfig = (MerchantPayConfig)checkBankData.get("merchantPayConfig");
		String statusDB = checkBankData.get("statusDB").toString();
		String urlDB = checkBankData.get("returnUrl").toString();
		
		//4 拼装返回数据
		String originalStr = "";	
		String returnURL = "redirect:";
		
		model.addObject("amt", paymentOrder.getOrder_amount());
		originalStr += "amt="+paymentOrder.getOrder_amount();
		
		model.addObject("busiTypeCode",paymentOrder.getBusi_type_code());
		originalStr += "&busiTypeCode"+paymentOrder.getBusi_type_code();
		
		model.addObject("payKey",merchantPayConfig.getPay_key());
		originalStr += "&payKey"+merchantPayConfig.getPay_key();
		
		model.addObject("payerNum",paymentOrder.getMerchant_order_no());
		originalStr += "&payerNum"+paymentOrder.getMerchant_order_no();
		
		model.addObject("payerTypeCode",paymentOrder.getProduct_name());
		originalStr += "&payerTypeCode"+paymentOrder.getProduct_name();
		
		model.addObject("status",statusReturn);
		originalStr += "&status="+statusReturn;
		
		String sign = MD5Util.md5(originalStr,"&paySecret="+merchantPayConfig.getPay_secret());
		model.addObject("sign",sign);
		
		model.addObject("signType", "MD5");
		//如果执收单位返回地址为空，则返回平台页面
		if(urlDB==null || urlDB.equals("")){
			if(statusDB.equals("SUCCESS")){	
				model.setViewName("payment_ok");
			}else{
				model.addObject("errorCode", "ERROR-ORDER"+paymentOrder.getMerchant_order_no());
				model.addObject("errorInfo", "订单支付失败！");
				model.setViewName("payment_defeat");
			}
		}else{
			returnURL += urlDB;
			model.setViewName(returnURL);
		}
		//3.判断是否执行后台推送
		if(bank.equals("boc")){
			paymentService.notice(params,0,bank);
		}else if(bank.equals("abc")){
			paymentService.notice(params,0,bank);
		}
		
		logger.info("##执收单位页面推送地址："+model.getViewName());
		return model;
	}

	@RequestMapping(value="/paynotify/notify/{bank}")
	public void getNotify(@PathVariable String bank,HttpServletRequest request,HttpServletResponse response){
		logger.info("##进入服务通知……");
		LinkedHashMap<String,String> params = PageUtils.getLinkedParameters(request);
		paymentService.notice(params,0,bank);
		
	}
}
