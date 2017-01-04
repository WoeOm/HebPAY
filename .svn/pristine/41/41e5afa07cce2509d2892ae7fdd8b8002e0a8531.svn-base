package heb.pay.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

public class JSONUtils {

    public static Map<String, Object> jsonToMap(String jsonString){ 
    	
    	if (jsonString == null || "".equals(jsonString)) { 
    		return null; 
    	}
    	HashMap<String, Object> retMap = null;
    	try {
	    	retMap = new HashMap<String, Object>();
	    	JSONObject json = JSONObject.fromObject(jsonString);
	    	Map<String, Object> tmpMap = (Map<String, Object>) JSONObject.toBean(json, Map.class);
	    	for (Map.Entry<String, Object> entry : tmpMap.entrySet()) {
		    	retMap.put(entry.getKey(), entry.getValue());
	    	}
    	}catch (Exception e) {
    		e.printStackTrace();
    	}
    	return retMap; 
    } 

    public static List<Map<String, Object>> jsonToList(String jsonString){
    	    	
    	if (jsonString == null || "".equals(jsonString)) { 
    		return null; 
    	}

    	List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();

    	JSONArray data = JSONArray.fromObject(jsonString);
    	for (int i = 0; i < data.size(); i++) {
	    	HashMap<String, Object> retMap = new HashMap<String, Object>();

	    	JSONObject json = (JSONObject) data.get(i);
	    	Map<String, Object> tmpMap = (Map<String, Object>) JSONObject.toBean(json, Map.class);

	    	for (Map.Entry<String, Object> entry : tmpMap.entrySet()) {
	    		retMap.put(entry.getKey(), entry.getValue());
	    	}
	    	retList.add(retMap);
    	}
    	return retList;
    }
    
    public static String mapToJson(Map<String, Object> map) {
    	HashMap<String, Object> retMap = new HashMap<String, Object>();
    	for (Map.Entry<String, Object> entry : map.entrySet()) {
    		retMap.put(entry.getKey(), entry.getValue());
    	}
    	JsonConfig jc = new JsonConfig();
    	return JSONObject.fromObject(retMap, jc).toString();
    }


	public static String listToJson(List<Map<String, Object>> list) {
		List<Map<String, Object>> tmpList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map : list) {
		HashMap<String, Object> retMap = new HashMap<String, Object>();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			retMap.put(entry.getKey(), entry.getValue());
		}
		tmpList.add(retMap);
		}
		JSONArray json = new JSONArray();
		json.addAll(tmpList);
		return json.toString();
	}

	
}
