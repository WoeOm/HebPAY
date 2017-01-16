package heb.pay.dao;

public interface SecurityDao {
	
	public String getPaySecret(String payKey);

	public int isPayKey(String payKey);
	
}
