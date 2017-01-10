package heb.pay.util;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class PageUtils{

	private static Logger logger = Logger.getLogger(PageUtils.class.getName());

	public static Map<String,Object> getParameters(HttpServletRequest request){
		Map<String,Object> map = new LinkedHashMap<String,Object>();
		Enumeration<String> names = request.getParameterNames();
		
		while(names.hasMoreElements()){
			String key = names.nextElement();
			String value = request.getParameter(key);
			map.put(key, value);
		}
		return map;
	}
	
	public static TreeMap<String,String> getTreeParameters(HttpServletRequest request){
		TreeMap<String,String> map = new TreeMap<String,String>();
		Enumeration<String> names = request.getParameterNames();
		
		while(names.hasMoreElements()){
			String key = names.nextElement();
			String value = request.getParameter(key);
			logger.info(key + "=" +value);
			map.put(key, value);
		}
		return map;
	}
	
	public static LinkedHashMap<String,String> getLinkedParameters(HttpServletRequest request){
		LinkedHashMap<String,String> map = new LinkedHashMap<String,String>();
		Enumeration<String> names = request.getParameterNames();
		
		while(names.hasMoreElements()){
			String key = names.nextElement();
			String value = request.getParameter(key);
			logger.info(key + "=" +value);
			map.put(key, value);
		}
		return map;
	}

	public static String getUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	public static void main(String[] args) {
		System.out.println(getUUID());
	}
	
}
