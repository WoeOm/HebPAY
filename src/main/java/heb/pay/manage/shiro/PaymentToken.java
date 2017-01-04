package heb.pay.manage.shiro;

import org.apache.shiro.authc.AuthenticationToken;

public class PaymentToken implements AuthenticationToken{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7273129512282858734L;
	private String payerNum;
	private String bankCode;
	private String payKey;
	private String busiTypeCode;
	private double amt;
	private String payerTypeCode;
	private String sign;
	private String signType;
	private String params;
	
	public PaymentToken(String payKey, String sign, String signType) {
		super();
		this.payKey = payKey;
		this.sign = sign;
		this.signType = signType;
	}

	public String getPayerNum() {
		return payerNum;
	}

	public void setPayerNum(String payerNum) {
		this.payerNum = payerNum;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getPayKey() {
		return payKey;
	}

	public void setPayKey(String payKey) {
		this.payKey = payKey;
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

	public String getParams() {
		
		if(payKey == null || payKey.equals("")){
			return null;
		}
		if(sign == null || sign.equals("")){
			return null;
		}
		if(signType == null || signType.equals("")){
			return null;
		}
		
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}
	
	

	public String getBusiTypeCode() {
		return busiTypeCode;
	}



	public void setBusiTypeCode(String busiTypeCode) {
		this.busiTypeCode = busiTypeCode;
	}



	public double getAmt() {
		return amt;
	}



	public void setAmt(double amt) {
		this.amt = amt;
	}



	public String getPayerTypeCode() {
		return payerTypeCode;
	}



	public void setPayerTypeCode(String payerTypeCode) {
		this.payerTypeCode = payerTypeCode;
	}



	@Override
	public Object getPrincipal() {
		return payKey;
	}

	@Override
	public Object getCredentials() {
		return sign;
	}
	
	
}
