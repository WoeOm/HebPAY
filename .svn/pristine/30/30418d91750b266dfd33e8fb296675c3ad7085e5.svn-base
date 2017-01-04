package heb.pay.manage.shiro;

import heb.pay.service.SecureService;
import heb.pay.util.MD5Util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
 
public class StatelessRealm extends AuthorizingRealm {
	
	@Autowired
	private SecureService secureService;
	
    @Override
    public boolean supports(AuthenticationToken token) {
    	//PayToken支付接口   QueryToken查询接口  ManyQueryToken批量查询接口  ReturnToken推送接口  NotifyToken推送后台接口  PaymentToken支付订单
        return token instanceof PayToken || token instanceof QueryToken ||token instanceof ManyQueryToken || token instanceof ReturnToken || token instanceof NotifyToken || token instanceof PaymentToken;
    }
    
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo authorizationInfo =  new SimpleAuthorizationInfo();
        authorizationInfo.addRole("user");
        return authorizationInfo;
    }
    
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
    	
    	
    	String payKey = "";
    	String signType = "";
    	String sign = "";
    	String params = "";
    	
    	if(token instanceof PayToken){
    		PayToken payToken = (PayToken) token;
    		payKey = payToken.getPayKey();
    		signType = payToken.getSignType();
    		sign = payToken.getSign();
    		params = payToken.getParams();
        }else if(token instanceof QueryToken){
        	QueryToken queryToken = (QueryToken) token;
        	payKey = queryToken.getPayKey();
        	signType = queryToken.getSignType();
    		sign = queryToken.getSign();
    		params = queryToken.getParams();
        }else if(token instanceof ManyQueryToken){
        	ManyQueryToken manyQueryToken = (ManyQueryToken) token;
        	payKey = manyQueryToken.getPayKey();
        	signType = manyQueryToken.getSignType();
    		sign = manyQueryToken.getSign();
    		params = manyQueryToken.getParams();
        }else if(token instanceof ReturnToken){
        	ReturnToken returnToken = (ReturnToken)token;
        	payKey = returnToken.getPayKey();
        	signType = returnToken.getSignType();
        	sign = returnToken.getSign();
        	params = returnToken.getParams();
        }else if(token instanceof NotifyToken){
        	NotifyToken notifyToken = (NotifyToken)token;
        	payKey = notifyToken.getPayKey();
        	signType = notifyToken.getSignType();
        	sign = notifyToken.getSign();
        	params = notifyToken.getParams();
        }else if(token instanceof PaymentToken){
        	PaymentToken paymentToken = (PaymentToken)token;
        	payKey = paymentToken.getPayKey();
        	signType = paymentToken.getSignType();
        	sign = paymentToken.getSign();
        	params = paymentToken.getParams();
        }
        
    	if(signType.equals("MD5")){
        	//验证payKey的是否存在
            if(!secureService.isPayKey(payKey)){
            	return null;
            }
    		//验证必要字段是否为空
            if(params != null && !params.equals("")){
            	//MD5加salt验证
    	        String salt = secureService.getPaySecret(payKey);
    	        String md5 = MD5Util.md5(params,"&paySecret="+salt);
    	        return new SimpleAuthenticationInfo(payKey,md5,getName());
            }
    	}else{
    		return null;
    	}
        
        return null;
    }
}
