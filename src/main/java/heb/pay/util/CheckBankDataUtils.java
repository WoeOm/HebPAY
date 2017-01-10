package heb.pay.util;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ipps.common.security.PKCSTool;

import CCBSign.RSASig;

public class CheckBankDataUtils {

	private static Logger logger = Logger.getLogger(CheckBankDataUtils.class.getName());

	public static boolean checkCCBData(LinkedHashMap<String, String> map,String publicKey) {
		if (map.isEmpty()) {
			return false;
		}
		Set<String> set = map.keySet();
		Iterator<String> keys = set.iterator();
		String originalStr = "";
		String signStr = "";
		while (keys.hasNext()) {
			String key = keys.next();
			if (key.equals("SIGN")) {
				signStr = map.get(key);
				continue;
			}
			originalStr += key + "=" + map.get(key) + "&";
		}
		originalStr = originalStr.substring(0, originalStr.length() - 1);
		RSASig rsa = new RSASig();
		rsa.setPublicKey(publicKey);
		return rsa.verifySigature(signStr, originalStr);
	}

	public static String bocEncode(byte[] b) {
		String path = (CheckBankDataUtils.class.getResource("/").getPath() + "cert/boc/BOC.pfx").substring(1);
		path = path.replace("%20", " ");
		PKCSTool tool = null;
		String signature = "";
		try {
			tool = PKCSTool.getSigner(path, "123456", "123456", "PKCS7");
			signature = tool.p7Sign(b);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return signature;
	}
	
	public static boolean checkBOCData(LinkedHashMap<String, String> map) {
		if (map.isEmpty()) {
			return false;
		}

		String originalStr = "";
		String signStr = map.get("signData");
		
		originalStr = map.get("merchantNo") +"|"+ map.get("orderNo") +"|"+ map.get("orderSeq") +"|"+ map.get("cardTyp") +"|"+ map.get("payTime") +"|"+ map.get("orderStatus") +"|"+ map.get("payAmount");
		
		String path = (CheckBankDataUtils.class.getResource("/").getPath() + "cert/boc/BOC.cer").substring(1);
		path = path.replace("%20", " ");
		
		PKCSTool tool = null;
		try {
			tool = PKCSTool.getVerifier(new FileInputStream(path), null);
			tool.p7Verify(signStr, originalStr.getBytes("UTF-8"));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static String getABCCertPath(){
		return (CheckBankDataUtils.class.getResource("/").getPath() + "cert/abc/").replace("%20", " ").substring(1);
	}

	public static void main(String[] args) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("merchantNo","104440170113333");
		map.put("orderNo","20170106150802");
		map.put("orderSeq","116070117");
		map.put("cardTyp","01");
		map.put("payTime","20171207150754");
		map.put("orderStatus","1");
		map.put("payAmount","0.01");
		map.put("acctNo","****");
		map.put("holderName","****");
		map.put("ibknum","****");
		map.put("orderIp","124.239.215.97");
		map.put("orderRefer","http%3A%2F%2F124.239.215.78%3A7001%2Fpay%2FpayBank");
		map.put("bankTranSeq","2017120744013333000098");
		map.put("returnActFlag","1");
		map.put("phoneNum","");
		map.put("custTranId","");
		map.put("signData","MIID0QYJKoZIhvcNAQcCoIIDwjCCA74CAQMxCTAHBgUrDgMCGjALBgkqhkiG9w0BBwGgggJsMIICaDCCAdGgAwIBAgIQfUcmGU5Dp0r66SIzTak6lDANBgkqhkiG9w0BAQUFADBaMQswCQYDVQQGEwJDTjEWMBQGA1UEChMNQkFOSyBPRiBDSElOQTEQMA4GA1UECBMHQkVJSklORzEQMA4GA1UEBxMHQkVJSklORzEPMA0GA1UEAxMGQk9DIENBMB4XDTExMDYxODEwMjYyNFoXDTIxMDQyNjEwMjYyNFowRzELMAkGA1UEBhMCQ04xFjAUBgNVBAoTDUJBTksgT0YgQ0hJTkExDTALBgNVBAsTBFRFU1QxETAPBgNVBAMeCG1Li9VVRmI3MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDRfqNZjaGwButIJvXBg2zrquSiyrERf447LX486K89akHYmOYmwrt54JNlR8QH9yVe23g8Ozrlo8SGsZ7bFojmhYbo0pcUu3cnn5OLjsB1rhTqGRiBv7HF3I1O+DS3UjNKbY+Nkn3TLG6s3EPJKr1ezGz6iVvM7ZXn5mn3WKSYQIDAQABo0IwQDAfBgNVHSMEGDAWgBR48bbzvcpEDXwv6NI2yP4GsMD5nzAdBgNVHQ4EFgQUuh0z/bgWTLx86onWP4ae5vYcdM0wDQYJKoZIhvcNAQEFBQADgYEAMi8mBiqKYmNH0mkzP3xJYP9yUaznL/MazTa+bXcputVuOHd1Ied8w/uXDoUJ0H2BBOnekHokYnDYt5MId5q/QV9Ph+7gauEkPRqvfYJNxLXS3uBDCGWxNNUj8ylYlBNgPu3sH3x4BcuYuFcHqAfTJqaWhdUT49KRn7RaendPnVgxggEvMIIBKwIBAzBuMFoxCzAJBgNVBAYTAkNOMRYwFAYDVQQKEw1CQU5LIE9GIENISU5BMRAwDgYDVQQIEwdCRUlKSU5HMRAwDgYDVQQHEwdCRUlKSU5HMQ8wDQYDVQQDEwZCT0MgQ0ECEH1HJhlOQ6dK+ukiM02pOpQwBwYFKw4DAhowCwYJKoZIhvcNAQEBBIGAkkYStNvjjIS63/Tmr9PTgzyc5u6eMM6mSwzf3TdijkfBY5FFDZ1BpHxibvQ6hMJIc1G1CCWFnT18M8D6wszVSMzt1JaLjjo2v5mo0OIsdST14eaPpQSbLsfwQbqj3ZUoGeWXXu8PV5tMJ2YOcuKdMax96fBYTteknNq+w74q7zWhHTAbBgFRMRYEFOHqc4nkzMFOXLMmla89HgvJ+pST");
		System.out.println(checkBOCData(map));
	}
}
