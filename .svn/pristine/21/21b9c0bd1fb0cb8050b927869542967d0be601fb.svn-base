package heb.pay.manage.shiro;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.shiro.authc.AuthenticationToken;

public class PayToken implements AuthenticationToken {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5830462859989325738L;

	private String payKey;
	
	private String payerTypeCode;
	private String busiTypeCode;
	private String payerName;
	private String payerIdNum;
	private String payerNum;
	private String amt;
	private String returnUrl;
	private String notifyUrl;
	private String reserve1;
	private String reserve2;
	private String reserve3;

	private String sign;
	private String signType;
	
	private String params;
	
	public PayToken(String payKey, String payerTypeCode,
			String busiTypeCode, String payerName, String payerIdNum,
			String payerNum, String amt, String returnUrl, String notifyUrl,
			String reserve1, String reserve2, String reserve3, String sign,
			String signType) {
		super();
		this.payKey = payKey;
		this.payerTypeCode = payerTypeCode;
		this.busiTypeCode = busiTypeCode;
		this.payerName = payerName;
		this.payerIdNum = payerIdNum;
		this.payerNum = payerNum;
		this.amt = amt;
		this.returnUrl = returnUrl;
		this.notifyUrl = notifyUrl;
		this.reserve1 = reserve1;
		this.reserve2 = reserve2;
		this.reserve3 = reserve3;
		this.sign = sign;
		this.signType = signType;
	}

	public String getPayKey() {
		return payKey;
	}

	public void setPayKey(String payKey) {
		this.payKey = payKey;
	}

	public String getPayerTypeCode() {
		return payerTypeCode;
	}

	public void setPayerTypeCode(String payerTypeCode) {
		this.payerTypeCode = payerTypeCode;
	}

	public String getBusiTypeCode() {
		return busiTypeCode;
	}

	public void setBusiTypeCode(String busiTypeCode) {
		this.busiTypeCode = busiTypeCode;
	}

	public String getPayerName() {
		return payerName;
	}

	public void setPayerName(String payerName) {
		this.payerName = payerName;
	}

	public String getPayerIdNum() {
		return payerIdNum;
	}

	public void setPayerIdNum(String payerIdNum) {
		this.payerIdNum = payerIdNum;
	}

	public String getPayerNum() {
		return payerNum;
	}

	public void setPayerNum(String payerNum) {
		this.payerNum = payerNum;
	}

	public String getAmt() {
		return amt;
	}

	public void setAmt(String amt) {
		this.amt = amt;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public String getReserve1() {
		return reserve1;
	}

	public void setReserve1(String reserve1) {
		this.reserve1 = reserve1;
	}

	public String getReserve2() {
		return reserve2;
	}

	public void setReserve2(String reserve2) {
		this.reserve2 = reserve2;
	}

	public String getReserve3() {
		return reserve3;
	}

	public void setReserve3(String reserve3) {
		this.reserve3 = reserve3;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getSignType() {
		return signType;
	}

	public void setSignType(String signType) {
		this.signType = signType;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getParams(){
		
		if(payKey == null || payKey.equals("") || payKey.length()>32){
			return null;
		}
		if(payerTypeCode == null || payerTypeCode.equals("") || payerTypeCode.length()>22){
			return null;
		}
		if(busiTypeCode == null || busiTypeCode.equals("") || busiTypeCode.length()>30){
			return null;
		}
		if(payerName == null || payerName.equals("") || payerName.length()>30){
			return null;
		}
		if(payerNum == null || payerNum.equals("") || payerNum.length() > 32){
			return null;
		}
		if(amt == null || amt.equals("") ||amt.length()>15 || !match("^(?!0+(?:\\.0+)?$)(?:[1-9]\\d*|0)(?:\\.\\d{1,2})?$", amt)){
			return null;
		}
		if(returnUrl == null || returnUrl.equals("") || returnUrl.length() > 150){
			return null;
		}
		if(notifyUrl == null || notifyUrl.equals("") || notifyUrl.length() > 150){
			return null;
		}
		if(sign == null || sign.equals("") || sign.length() > 32){
			return null;
		}
		if(signType == null || signType.equals("") || signType.length() >10){
			return null;
		}
		
		return params;
	}

	@Override
	public Object getPrincipal() {
		return payKey;
	}

	@Override
	public Object getCredentials() {
		return sign;
	}
	
	private static boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
}