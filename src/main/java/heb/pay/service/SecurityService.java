package heb.pay.service;

public interface SecurityService {

	public String getPaySecret(String payKey);
	
	public boolean isPayKey(String payKey);
	
}
