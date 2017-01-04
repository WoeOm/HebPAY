package heb.pay.manage.shiro;

import org.apache.shiro.authc.AuthenticationToken;

public class ReturnToken implements AuthenticationToken{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7273129512282858734L;
	private String payKey;
	private String sign;
	private String signType;
	private String params;
	public ReturnToken(String payKey,String sign, String signType) {
		super();
		this.payKey = payKey;
		this.sign = sign;
		this.signType = signType;
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
	public void setParams(String params) {
		this.params = params;
	}
	public String getParams(){
		
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

	@Override
	public Object getPrincipal() {
		return payKey;
	}

	@Override
	public Object getCredentials() {
		return sign;
	}
}
