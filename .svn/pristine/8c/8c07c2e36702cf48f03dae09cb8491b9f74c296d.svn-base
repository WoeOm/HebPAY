package heb.pay.manage.shiro;

import org.apache.shiro.authc.AuthenticationToken;

public class ManyQueryToken implements AuthenticationToken{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4852654083998722095L;
	private String payKey;
	private String busiTypeCode;
	private String payerTypeCode;
	private String startDate;
	private String endDate;
	private String pageIndex;
	private String sign;
	private String signType;
	
	private String params;
	public ManyQueryToken(String payKey, String busiTypeCode,
			String payerTypeCode, String startDate, String endDate,
			String pageIndex, String sign, String signType) {
		super();
		this.payKey = payKey;
		this.busiTypeCode = busiTypeCode;
		this.payerTypeCode = payerTypeCode;
		this.startDate = startDate;
		this.endDate = endDate;
		this.pageIndex = pageIndex;
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
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(String pageIndex) {
		this.pageIndex = pageIndex;
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
		if(payerTypeCode == null || payerTypeCode.equals("")){
			return null;
		}
		if(busiTypeCode == null || busiTypeCode.equals("")){
			return null;
		}
		if(startDate == null || startDate.equals("")){
			return null;
		}
		if(endDate == null || endDate.equals("")){
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
