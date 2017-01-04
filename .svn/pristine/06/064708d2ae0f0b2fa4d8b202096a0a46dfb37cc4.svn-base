package heb.pay.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import heb.pay.entity.MerchantInfo;
import heb.pay.entity.MerchantPayInfo;
import heb.pay.entity.PayWay;
import heb.pay.entity.PaymentOrder;
import heb.pay.entity.User;
import heb.pay.service.PayOpService;
import heb.pay.service.PayService;
import heb.pay.service.PaymentService;
import heb.pay.service.UserService;
import heb.pay.util.AESUtils;
import heb.pay.util.ApplicationUtils;
import heb.pay.util.MD5Util;
import heb.pay.util.PageUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PaymentController {

	@Autowired
	private PayService payService;
	
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private PayOpService payOpService;
	
	@Autowired
	private UserService userservice;
	
	@RequestMapping(value="/pay/payBank")
	public ModelAndView redirectToBank(HttpServletRequest request,HttpServletResponse response,ModelAndView model){
		Map<String,Object> params = PageUtils.getParameters(request);
		String payerNum = params.get("payerNum")==null?"":params.get("payerNum").toString();
		String payWayStr = params.get("payWay")==null?"":params.get("payWay").toString();
		String payWayCode = payWayStr.split("\\|")[0];
		String payTpyeCode = payWayStr.split("\\|")[1];
		String payKey = params.get("payKey")==null?"":params.get("payKey").toString();
		String payerTypeCode = params.get("payerTypeCode")==null?"":params.get("payerTypeCode").toString();
		String busiTypeCode = params.get("busiTypeCode")==null?"":params.get("busiTypeCode").toString();
		double amt = params.get("amt")==null?0:Double.parseDouble(params.get("amt").toString());
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		//验证商户是否存在并且可用！如果可用返回商户对象
		List<MerchantInfo> merchantList = payService.checkMerchant(payKey);
		if(merchantList == null || merchantList.size()<=0){
			model.addObject("errorCode", "ERROR1569875421001");
			model.addObject("errorInfo", "用户不存在！");
			model.setViewName("payment_defeat");
			return model;
		}
		//验证银行信息
		List<PayWay> payWayList = payService.getPayWayList(payKey);
		if(payWayList == null || payWayList.size()<=0){
			model.addObject("errorCode", "ERROR78984710025");
			model.addObject("errorInfo", "该用户下没有可用支付方式！");
			model.setViewName("payment_defeat");
			return model;
		}
		//验证订单号是否存在，如果存在返回订单信息。
		List<PaymentOrder> orderList = payService.getPaymentOrderByOrderNum(payerNum,merchantList.get(0).getUser_no(),payerTypeCode,busiTypeCode,amt);
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
			//String expireTime = format.format(orderList.get(0).getOrder_expire_time());
			Date dateDB = orderList.get(0).getOrder_expire_time();
			curDate = new Date();
			if((curDate.getTime()-dateDB.getTime())>=0){
				model.addObject("errorCode", "ERROR-ORDER"+orderList.get(0).getMerchant_order_no());
				model.addObject("errorInfo", "此订单已过期，请重新生成订单再支付！");
				model.setViewName("payment_defeat");
				return model;
			}
		}
		PaymentOrder paymentOrder = orderList.get(0);
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("payerNum", payerNum);
		map.put("payWayCode", payWayCode);
		map.put("payTypeCode", payTpyeCode);

		PayWay payWay = payService.getPayWayByCode(payWayCode,payTpyeCode).get(0);
		String uuid = PageUtils.getUUID();
		int version = Integer.parseInt(ApplicationUtils.getApplicationSettings().getProperty("order.version","0"));
		map.put("uuid", uuid);
		map.put("version", version);
		map.put("creator", paymentOrder.getMerchant_no());
		map.put("productName", paymentOrder.getProduct_name());
		map.put("merchantNo", paymentOrder.getMerchant_no());
		map.put("merchantName", paymentOrder.getMerchant_name());
		map.put("payWayName", payWay.getPay_way_name());
		map.put("payTypeName", payWay.getPay_type_name());
		map.put("returnURL", "https://pay.heb.gov.cn/gateway/paynotify/notify");
		map.put("notifyURL", "https://pay.heb.gov.cn/gateway/paynotify/return");
		map.put("amt", paymentOrder.getOrder_amount());
		map.put("status", "WAITING_PAYMENT");
		String trxNO = PageUtils.getUUID();
		String bankOrderNo = format.format(new Date());
		map.put("trxNO", trxNO);
		map.put("bankOrderNo", bankOrderNo);
		//插入记录表
		int ret = paymentService.insertPaymentRecord(map);
		if(ret>0){
			//更新订单表trxNO
			Map<String,String> orderMap = new HashMap<String, String>();
			orderMap.put("trxNO", trxNO);
			orderMap.put("payerNum", payerNum);
			orderMap.put("payWayCode", payWayList.get(0).getPay_way_code());
			orderMap.put("payWayName", payWayList.get(0).getPay_way_name());
			orderMap.put("payTypeCode", payWayList.get(0).getPay_type_code());
			orderMap.put("payTypeName", payWayList.get(0).getPay_type_name());
			orderMap.put("status", "WAITING_PAYMENT");
			ret = paymentService.updateOrderTrxNO(orderMap);
			//更新缴费平台订单表
			int bankid = 0;
			//获取缴费平台银行信息
			bankid = payOpService.getJFBankInfo(payWayList.get(0).getPay_way_name());
			ret = payOpService.updateJFPayorderState(merchantList.get(0).getUser_no(),orderList.get(0).getMerchant_order_no(),1,bankid,bankOrderNo);
			
			List<MerchantPayInfo> mPayInfoList = payService.getMerchantPayInfo(paymentOrder.getMerchant_no(),payWayCode);
			if(mPayInfoList == null || mPayInfoList.size()<=0){
				model.addObject("errorCode", "ERROR10010098710");
				model.addObject("errorInfo", "该用户下无可用银行信息！");
				model.setViewName("payment_defeat");
				return model;
			}
			//BankConfigUtils.bankConfig.get("CCB-BANK")
			//String url1 = "https://ibsbjstar.ccb.com.cn/CCBIS/ccbMain?";
			String url = "https://ibsbjstar.ccb.com.cn/CCBIS/ccbMain?";
			StringBuilder originalStr = new StringBuilder();
			LinkedHashMap<String, String> returnMap = new LinkedHashMap<String, String>();
			returnMap.put("MERCHANTID", mPayInfoList.get(0).getTg_merchant_id());
			url += "MERCHANTID="+mPayInfoList.get(0).getTg_merchant_id();
			originalStr.append("MERCHANTID=").append(mPayInfoList.get(0).getTg_merchant_id());
			returnMap.put("POSID", mPayInfoList.get(0).getTg_merchant_id2());
			originalStr.append("&POSID=").append(mPayInfoList.get(0).getTg_merchant_id2());
			url += "&POSID=" + mPayInfoList.get(0).getTg_merchant_id2();
			returnMap.put("BRANCHID", mPayInfoList.get(0).getTg_merchant_id3());
			originalStr.append("&BRANCHID=").append(mPayInfoList.get(0).getTg_merchant_id3());
			url += "&BRANCHID=" + mPayInfoList.get(0).getTg_merchant_id3();
			returnMap.put("ORDERID", map.get("bankOrderNo").toString());
			originalStr.append("&ORDERID=").append(map.get("bankOrderNo").toString());
			url += "&ORDERID=" + map.get("bankOrderNo").toString();
			returnMap.put("PAYMENT", map.get("amt").toString());
			originalStr.append("&PAYMENT=").append(map.get("amt").toString());
			url += "&PAYMENT=" + map.get("amt").toString();
			returnMap.put("CURCODE", "01");
			returnMap.put("TXCODE", "520100");
			returnMap.put("REMARK1", "");
			returnMap.put("REMARK2", "");
			returnMap.put("TYPE", "1");
			originalStr.append("&CURCODE=01&TXCODE=520100&REMARK1=&REMARK2=&TYPE=1");
			url += "&CURCODE=01&TXCODE=520100&REMARK1=&REMARK2=&TYPE=1";
			String PUB = mPayInfoList.get(0).getTg_partner_secret();
			originalStr.append("&PUB=").append(PUB);
			returnMap.put("GATEWAY", "W0Z1");
			originalStr.append("&GATEWAY=");
			url += "&GATEWAY=&CLIENTIP=&REGINFO=&PROINFO=&REFERER=";
			returnMap.put("CLIENTIP", "");
			returnMap.put("REGINFO", "");
			returnMap.put("PROINFO", "");
			returnMap.put("REFERER", "");
			originalStr.append("&CLIENTIP=&REGINFO=&PROINFO=&REFERER=");
			//returnMap.put("TIMEOUT", format.format(paymentOrder.getOrder_expire_time()));
			//originalStr.append("&TIMEOUT=").append(format.format(paymentOrder.getOrder_expire_time()));
			//url += "&TIMEOUT="+format.format(paymentOrder.getOrder_expire_time());
			String MAC = MD5Util.md5(originalStr.toString());
			returnMap.put("MAC", MAC);
			url += "&MAC="+MAC;
			model.addObject("data", returnMap);
			model.addObject("url", url);
			model.setViewName("payment_bank");
			System.out.println(originalStr);
			return model;
		}
		model.setViewName("payment_defeat");
		return model;
	}
	/**
 	 * 发送短信验证码
 	 * @param user
 	 * @param request
 	 * @param response
 	 * @param model
 	 * @return
 	 */
 	@RequestMapping("/static/sendValidate")
 	@ResponseBody
    public Map<String, Object> sendValidate( HttpServletRequest request, HttpServletResponse response,Model model){   
 		Map<String, Object> listMap = new HashMap<String, Object>();
 		String kaptcha = request.getParameter("kaptcha");
 		
 		String captcha = (String) request.getSession().getAttribute(KaptchaController.DEFAULT_CAPTCHA_SESSION);
 		if(kaptcha !=null && kaptcha.equals(captcha)){
 			
 			listMap.put("code", 1);
	 		listMap.put("msg", "验证码正确");
 		}else{
 			listMap.put("code", -1);
	 		listMap.put("msg", "验证码错误");
 		}
 		return listMap;   
	}
 	/**
 	 * 验证短信验证码是否正确
 	 * @param mbcke
 	 * @param model
 	 * @return
 	 */
 	@RequestMapping("/static/validatePhone")
 	@ResponseBody
    public Map<String, Object> validatePhone( HttpServletRequest request, HttpServletResponse response,Model model){   
 		Map<String, Object> listMap = new HashMap<String, Object>();
 		String mbcke = request.getParameter("mbcke");
 		String mb = request.getParameter("mb");
 		
 		//预留接口
 		if(mbcke!=null  && mbcke!=""){
 			List<User> user = userservice.getUserBymb(AESUtils.Encode(mb));
 	 		if(user!=null && user.size()==0){
 	 			
 	 			Map<String,String> userMap = new HashMap<String, String>();
 				userMap.put("userid", PageUtils.getUUID());
 				userMap.put("mb", AESUtils.Encode(mb));
 				userMap.put("usertype", "3");
 	 			userservice.insertQuickUser(userMap);
 	 		}
 			
 			listMap.put("code", 1);
	 		listMap.put("msg", "验证码正确");
 		}else{
 			listMap.put("code", -1);
	 		listMap.put("msg", "验证码错误");
 		}
 		return listMap;   
	} 
 	
}
	