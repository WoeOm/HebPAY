package heb.pay.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TestHttp {

	
	public static String getXML(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version='1.0' encoding='GB2312' standalone='yes' ?>");
		buffer.append("<TX>");
		buffer.append("<REQUEST_SN>"+new SimpleDateFormat("yyyyMMddhhmmss").format(new Date())+"</REQUEST_SN>");
		buffer.append("<CUST_ID>105130193990227</CUST_ID>");
		buffer.append("<USER_ID>00021802-6-aaa</USER_ID>");
		buffer.append("<PASSWORD>qwert12345</PASSWORD>");
		buffer.append("<TX_CODE>5W1002</TX_CODE>");
		buffer.append("CN");
		buffer.append("<TX_INFO>");
		/*buffer.append("<START></START>");
		buffer.append("<STARTHOUR></STARTHOUR>");
		buffer.append("<STARTMIN></STARTMIN>");
		buffer.append("<END></END>");
		buffer.append("<ENDHOUR></ENDHOUR>");
		buffer.append("<ENDMIN></ENDMIN>");*/
		buffer.append("<KIND>0</KIND>");//0:未结流水,1:已结流水
		buffer.append("<ORDER>20161227194507</ORDER>");//订单号
		/*buffer.append("<ACCOUNT></ACCOUNT>");*/
		buffer.append("<DEXCEL>1</DEXCEL>");
		/*buffer.append("<MONEY></MONEY>");*/
		buffer.append("<NORDERBY>1</NORDERBY>");//1:交易日期,2:订单号
		buffer.append("<PAGE>1</PAGE>");
		buffer.append("<POS_CODE></POS_CODE>");
		buffer.append("<STATUS>3</STATUS>");
		buffer.append("</TX_INFO>");
		buffer.append("</TX>");
		return buffer.toString();
	}
	
	public static void main(String[] args) {
		HttpClientUtil httpClientUtil = new HttpClientUtil();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("requestXml", getXML());
		try {
			String doPost = httpClientUtil.doPost("http://localhost:8888/", map, "UTF-8");
			System.out.println(doPost);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
