package heb.pay.controller;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import heb.pay.entity.MerchantInfo;
import heb.pay.entity.MerchantPayInfo;
import heb.pay.entity.PaymentOrder;
import heb.pay.entity.User;
import heb.pay.service.PayOpService;
import heb.pay.service.PayService;
import heb.pay.service.PaymentService;
import heb.pay.service.UserService;
import heb.pay.util.AESUtils;
import heb.pay.util.BankConfigUtils;
import heb.pay.util.CheckBankDataUtils;
import heb.pay.util.MD5Util;
import heb.pay.util.PageUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.abc.pay.client.JSON;
import com.abc.pay.client.ebus.PaymentRequest;


@Controller
public class PaymentController {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private PayService payService;
	
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private PayOpService payOpService;
	
	@Autowired
	private UserService userservice;
	
	@SuppressWarnings({ "unchecked"})
	@RequestMapping(value="/pay/payBank")
	public ModelAndView doPayBank(HttpServletRequest request,HttpServletResponse response,ModelAndView model){
		Map<String,Object> params = PageUtils.getParameters(request);
		String payerNum = params.get("payerNum")==null?"":params.get("payerNum").toString();
		String payWayStr = params.get("payWay")==null?"":params.get("payWay").toString();
		String payWayCode = payWayStr.split("\\|")[0];
		String payTpyeCode = payWayStr.split("\\|")[1];
		String payKey = params.get("payKey")==null?"":params.get("payKey").toString();
		String payerTypeCode = params.get("payerTypeCode")==null?"":params.get("payerTypeCode").toString();
		String busiTypeCode = params.get("busiTypeCode")==null?"":params.get("busiTypeCode").toString();
		double amt = params.get("amt")==null?0:Double.parseDouble(params.get("amt").toString());
		//验证数据
		Map<String,Object> retMap = payOpService.checkBaseInfo(payKey, payerTypeCode, amt);
		if(retMap.containsKey("error")){
			String error = retMap.get("error").toString();
			switch (error) {
			case "no_user":
				model.addObject("errorCode", "1001");
				model.addObject("errorInfo", "此执收单位不存在");
				model.setViewName("payment_defeat");
				break;
			case "no_unit":
				model.addObject("errorCode", "1002");
				model.addObject("errorInfo", "此单位不存在");
				model.setViewName("payment_defeat");
				break;
			case "no_paytype":
				model.addObject("errorCode", "1003");
				model.addObject("errorInfo", "此执收单位下没有该缴费类型");
				model.setViewName("payment_defeat");
				break;
			case "out_pay":
				model.addObject("errorCode", "1004");
				model.addObject("errorInfo", "超出支付限额");
				model.setViewName("payment_defeat");
				break;
			case "no_pay":
				model.addObject("errorCode", "1005");
				model.addObject("errorInfo", "此执收单位下没有可用的支付方式");
				model.setViewName("payment_defeat");
				break;
			}
			return model;
		}

		//获取商户信息
		MerchantInfo merchantInfo = (MerchantInfo)retMap.get("merchantInfo");
		model.addObject("merchantName",merchantInfo.getUser_name());
		
		//验证订单有效性
		Map<String,Object> orderRetMap = payOpService.checkOrderExist(payerNum, merchantInfo.getUser_no(), payerTypeCode, busiTypeCode, amt);
		if(orderRetMap.containsKey("error")){
			String error = orderRetMap.get("error").toString();
			switch (error) {
			case "payed":
				model.addObject("errorCode", "1101");
				model.addObject("errorInfo", "订单（"+ payerNum +"）已支付成功，无需重复支付，请查看你的交易记录确认");
				model.setViewName("payment_defeat");
				break;
			case "paying":
				model.addObject("errorCode", "1102");
				model.addObject("errorInfo", "订单（"+ payerNum +"）正在支付中，请等待支付完成");
				model.setViewName("payment_defeat");
				break;
			case "pay_fail":
				model.addObject("errorCode", "1103");
				model.addObject("errorInfo", "订单（"+ payerNum +"）失败，请重新生成订单");
				model.setViewName("payment_defeat");
				break;
			case "pay_out":
				model.addObject("errorCode", "1104");
				model.addObject("errorInfo", "订单（"+ payerNum +"）已过期，请重新生成订单再支付");
				model.setViewName("payment_defeat");
				break;
			}
			return model;
		}
		String returnUrl = BankConfigUtils.bankConfig.get("returnUrl");
		String notifyUrl = BankConfigUtils.bankConfig.get("notifyUrl");
		if(payWayCode.equals("CCB-BANK")){
			returnUrl += "ccb";
			notifyUrl += "ccb";
		}else if(payWayCode.equals("BOC-BANK")){
			returnUrl += "boc";
			notifyUrl += "boc";
		}else if(payWayCode.equals("ABC-BANK")){
			returnUrl += "abc";
			notifyUrl += "abc";
		}
		
		PaymentOrder paymentOrder = (PaymentOrder)orderRetMap.get("paymentOrder");
		// 插入订单记录表
		Map<String,Object> recordRetMap = payOpService.insertZFOrderRecord(payWayCode, payTpyeCode,returnUrl,notifyUrl, paymentOrder);
		if(recordRetMap.containsKey("error")){
			model.addObject("errorCode", "1201");
			model.addObject("errorInfo", "订单（"+ payerNum +"）创建支付记录信息失败，请重新支付");
			model.setViewName("payment_defeat");
			return model;
		}
		//获取支付信息
		List<MerchantPayInfo> mPayInfoList = payService.getMerchantPayInfo(paymentOrder.getMerchant_no(),payWayCode);
		if(mPayInfoList == null || mPayInfoList.size()<=0){
			model.addObject("errorCode", "1202");
			model.addObject("errorInfo", "此执收单位下无可用银行信息");
			model.setViewName("payment_defeat");
			return model;
		}
		
		String url = "";//拼接跳转银行地址(包含参数，但不包含秘钥)
		StringBuilder originalStr = null;//银行参数，用于加密，包含秘钥
		DecimalFormat df = new DecimalFormat("######0.00");
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		
		if(payWayCode.equals("CCB-BANK")){//建行
			url = BankConfigUtils.bankConfig.get("CCB-BANK");
			originalStr = new StringBuilder();
			url += "MERCHANTID="+mPayInfoList.get(0).getTg_merchant_id();
			originalStr.append("MERCHANTID=").append(mPayInfoList.get(0).getTg_merchant_id());
			originalStr.append("&POSID=").append(mPayInfoList.get(0).getTg_merchant_id2());
			url += "&POSID=" + mPayInfoList.get(0).getTg_merchant_id2();
			originalStr.append("&BRANCHID=").append(mPayInfoList.get(0).getTg_merchant_id3());
			url += "&BRANCHID=" + mPayInfoList.get(0).getTg_merchant_id3();
			originalStr.append("&ORDERID=").append(recordRetMap.get("bankOrderNo").toString());
			url += "&ORDERID=" + recordRetMap.get("bankOrderNo").toString();
			originalStr.append("&PAYMENT=").append(amt);
			url += "&PAYMENT=" + amt;
			originalStr.append("&CURCODE=01&TXCODE=520100&REMARK1=&REMARK2=&TYPE=1");
			url += "&CURCODE=01&TXCODE=520100&REMARK1=&REMARK2=&TYPE=1";
			String PUB = mPayInfoList.get(0).getTg_partner_secret();
			originalStr.append("&PUB=").append(PUB);
			originalStr.append("&GATEWAY=");
			url += "&GATEWAY=&CLIENTIP=&REGINFO=&PROINFO=&REFERER=";
			originalStr.append("&CLIENTIP=&REGINFO=&PROINFO=&REFERER=");
			//originalStr.append("&TIMEOUT=").append(format.format(paymentOrder.getOrder_expire_time()));
			//url += "&TIMEOUT="+format.format(paymentOrder.getOrder_expire_time());
			String MAC = MD5Util.md5(originalStr.toString());
			url += "&MAC="+MAC;
			model.addObject("type","CCB");
		
			model.addObject("url", url);
			model.setViewName("payment_bank");
		}else if(payWayCode.equals("BOC-BANK")){//中行
			url = BankConfigUtils.bankConfig.get("BOC-BANK");
			originalStr = new StringBuilder();
			
			String orderNo = recordRetMap.get("bankOrderNo").toString();
			String orderTime = format.format(paymentOrder.getCreate_time());
			String curCode = "001";
			String orderAmount = df.format(amt);
			String merchantNo = mPayInfoList.get(0).getTg_merchant_id();
			Map<String,Object> paramsMap = new LinkedHashMap<String, Object>();
			paramsMap.put("merchantNo",merchantNo);
			paramsMap.put("payType",1);
			paramsMap.put("orderNo",orderNo);
			paramsMap.put("curCode","001");
			paramsMap.put("orderAmount",df.format(amt));
			paramsMap.put("orderTime",format.format(paymentOrder.getCreate_time()));
			paramsMap.put("orderNote","test");
			paramsMap.put("orderUrl",returnUrl);
			
			originalStr.append(orderNo).append("|");
			originalStr.append(orderTime).append("|");
			originalStr.append(curCode).append("|");
			originalStr.append(orderAmount).append("|");
			originalStr.append(merchantNo);
			logger.info(originalStr);
			String signData = "";
			try {
				signData = CheckBankDataUtils.bocEncode(originalStr.toString().getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			paramsMap.put("signData",signData);
			model.addObject("data",paramsMap);
			model.addObject("type","BOC");
			
			model.addObject("url", url);
			model.setViewName("payment_bank");
		}else if(payWayCode.equals("ABC-BANK")){//农行
			String orderNo = recordRetMap.get("bankOrderNo").toString();
			String OrderDate = new SimpleDateFormat("yyyy/MM/dd").format(paymentOrder.getCreate_time());
			String OrderTime = new SimpleDateFormat("HH:mm:ss").format(paymentOrder.getCreate_time());
			String orderAmount = df.format(amt);
			//String merchantNo = mPayInfoList.get(0).getTg_merchant_id();
			//1、生成订单对象
			PaymentRequest paymentRequest = new PaymentRequest();
			paymentRequest.dicOrder.put("PayTypeID", "ImmediatePay");
			paymentRequest.dicOrder.put("OrderDate", OrderDate);
			paymentRequest.dicOrder.put("OrderTime", OrderTime);
			paymentRequest.dicOrder.put("OrderNo", orderNo);
			paymentRequest.dicOrder.put("CurrencyCode", "156");
			paymentRequest.dicOrder.put("OrderAmount", orderAmount);
			paymentRequest.dicOrder.put("InstallmentMark", "0");
			paymentRequest.dicOrder.put("CommodityType", "0202"); 
			//3、生成支付请求对象
			paymentRequest.dicRequest.put("PaymentType", "1"); //设定支付类型
			paymentRequest.dicRequest.put("PaymentLinkType", "1");//1：internet网络接入
			paymentRequest.dicRequest.put("NotifyType", "0"); //0页面通知  1服务端通知
			paymentRequest.dicRequest.put("ResultNotifyURL", returnUrl);//设定通知URL地址
			paymentRequest.dicRequest.put("IsBreakAccount", "0");//设定交易是否分账

			JSON json = paymentRequest.postRequest();
			String ReturnCode = json.GetKeyValue("ReturnCode");
			
			if (ReturnCode.equals("0000")){
				url = json.GetKeyValue("PaymentURL");
				model.addObject("url", url);
				model.setViewName("payment_bank");
			}else{
				String ErrorMessage = json.GetKeyValue("ErrorMessage");
				model.addObject("errorCode", "2001");
				model.addObject("errorInfo", ErrorMessage);
				model.setViewName("payment_defeat");
			}
			model.addObject("type","ABC");
		}
		
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
	