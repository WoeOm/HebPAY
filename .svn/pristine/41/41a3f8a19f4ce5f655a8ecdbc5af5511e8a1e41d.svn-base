package heb.pay.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

	public static String md5(String password){
		return DigestUtils.md5Hex(password);
	}
	
	public static String md5(String password,String salt){
		return DigestUtils.md5Hex(password + salt);
	}
	
}
