package heb.pay.manage.shiro;

import org.apache.shiro.authc.AuthenticationToken;

public class QueryToken implements AuthenticationToken{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8027179971698274153L;
	private String payKey;
	private String busiTypeCode;
	private String payerTypeCode;
	private String payerNum;
	private String sign;
	private String signType;
	private String params;
	public QueryToken(String payKey, String busiTypeCode, String payerTypeCode,
			String payerNum, String sign, String signType) {
		super();
		this.payKey = payKey;
		this.busiTypeCode = busiTypeCode;
		this.payerTypeCode = payerTypeCode;
		this.payerNum = payerNum;
		this.sign = sign;
		this.signType = signType;
	}
	public String getPayKey() {
		return payKey;
	}
	public void setPayKey(String payKey) {
		this.payKey = payKey;
	}
	public String getBusiTypeCode() {
		return busiTypeCode;
	}
	public void setBusiTypeCode(String busiTypeCode) {
		this.busiTypeCode = busiTypeCode;
	}
	public String getPayerTypeCode() {
		return payerTypeCode;
	}
	public void setPayerTypeCode(String payerTypeCode) {
		this.payerTypeCode = payerTypeCode;
	}
	public String getPayerNum() {
		return payerNum;
	}
	public void setPayerNum(String payerNum) {
		this.payerNum = payerNum;
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
		
		if(payKey == null || payKey.equals("")){
			return null;
		}
		if(busiTypeCode == null || busiTypeCode.equals("")){
			return null;
		}
		if(payerTypeCode == null || payerTypeCode.equals("")){
			return null;
		}
		if(payerNum == null || payerNum.equals("")){
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

	@Override
	public Object getPrincipal() {
		return payKey;
	}

	@Override
	public Object getCredentials() {
		return sign;
	}
}
