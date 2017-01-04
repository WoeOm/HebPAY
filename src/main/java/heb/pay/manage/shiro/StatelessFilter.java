package heb.pay.manage.shiro;

import heb.pay.util.FileUtil;
import heb.pay.util.PageUtils;
import heb.pay.util.WebUtil;

import java.util.TreeMap;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.AccessControlFilter;

public class StatelessFilter extends AccessControlFilter {

	private static Logger logger = Logger.getLogger(StatelessFilter.class.getName());
	
	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
		return false;
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		TreeMap<String, String> treeMap = PageUtils.getTreeParameters((HttpServletRequest)request);
		
		String payKey = treeMap.get("payKey");
		String sign = treeMap.get("sign");
		String signType = treeMap.get("signType");
		
		String busiTypeCode = treeMap.get("busiTypeCode");
		String payerTypeCode = treeMap.get("payerTypeCode");
		
		AuthenticationToken token = null;
		String url = ((HttpServletRequest)request).getRequestURI();
		if(url.indexOf("inpay")>=0){
			String payerName = treeMap.get("payerName");
			String payerIdNum = treeMap.get("payerIdNum");
			String payerNum = treeMap.get("payerNum");
			String amt = treeMap.get("amt");
			String returnUrl = treeMap.get("returnUrl");
			String notifyUrl = treeMap.get("notifyUrl");
			String reserve1 = treeMap.get("reserve1");
			String reserve2 = treeMap.get("reserve2");
			String reserve3 = treeMap.get("reserve3");
			token = new PayToken(payKey, payerTypeCode, busiTypeCode, payerName, payerIdNum, payerNum, amt, returnUrl, notifyUrl, reserve1, reserve2, reserve3, sign, signType);
			((PayToken)token).setParams(getTreeMap(treeMap));
		}else if(url.indexOf("orderQuery")>=0){
			String payerNum = treeMap.get("payerNum");
			if(payerNum == null || payerNum.equals("")){
				String startDate = treeMap.get("startDate");
				String endDate = treeMap.get("endDate");
				String pageIndex = treeMap.get("pageIndex");
				token = new ManyQueryToken(payKey, busiTypeCode, payerTypeCode, startDate, endDate, pageIndex, sign, signType);
				((ManyQueryToken)token).setParams(getTreeMap(treeMap));
			}else{
				token = new QueryToken(payKey, busiTypeCode, payerTypeCode, payerNum, sign, signType);
				((QueryToken)token).setParams(getTreeMap(treeMap));
			}
		}else if(url.indexOf("return") >=0){
			token = new ReturnToken(payKey, sign, signType);
			((ReturnToken)token).setParams(getTreeMap(treeMap));
		}else if(url.indexOf("notify") >=0){
			token = new NotifyToken(payKey, sign, signType);
			((NotifyToken)token).setParams(getTreeMap(treeMap));
		}else if(url.indexOf("payBank") >=0){
			String amt = treeMap.get("amt");
			token = new PaymentToken(payKey, sign, signType);
			((PaymentToken)token).setParams(getTreeMap(treeMap));
		}
		
		try {
			getSubject(request, response).login(token);
		} catch (Exception e) {
			e.printStackTrace();
			onLoginFail(request,response);
			return false;
		}

		return true;
	}

	private void onLoginFail(ServletRequest req,ServletResponse res) throws Exception {
		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		
		String html = FileUtil.readResourceAsString("heb/pay/manage/shiro/html/payment_safe.html");
		WebUtil.htmlToFreeMarker(request, response, html, "heb/pay/manage/shiro/html/payment_safe", null);
	}
	
	private String getTreeMap(TreeMap<String, String> treeMap){
		String ret = "";
		for(String key:treeMap.keySet()){
			if(key.contains("sign")){
				continue;
			}
			ret = ret + "&" + key + "=" + treeMap.get(key);
		}
		if(treeMap.size()>0){
			return ret.substring(1);
		}else{
			return "";
		}
	}

}
