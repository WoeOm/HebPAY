package heb.pay.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpClientUtil {
	
    public static String doGet(String url, String charset ) throws Exception{  
        String body = "";
    	CloseableHttpClient client = HttpClients.createDefault(); 
		HttpGet httpGet = new HttpGet(url); 
        httpGet.setHeader("Content-type", "application/x-www-form-urlencoded;charset=" + charset);  
        httpGet.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)"); 
        CloseableHttpResponse response = client.execute(httpGet);  
        HttpEntity entity = response.getEntity();  
        if (entity != null) {  
            body = EntityUtils.toString(entity, charset);  
        }  
        EntityUtils.consume(entity);  
        response.close();  
        return body;  
    } 

	
    public static String doPost(String url, Map<String, Object> params, String charset ) throws Exception{  
        String body = "";
    	CloseableHttpClient client = HttpClients.createDefault(); 
		HttpPost httpPost = new HttpPost(url); 
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();           
        if (params != null) {    
            for (Map.Entry<String, Object> entry : params.entrySet()) {  
            	nvps.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue()))); 
            }  
        } 
        
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, charset));

        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded;charset=" + charset);  
        httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)"); 
        CloseableHttpResponse response = client.execute(httpPost);  
        HttpEntity entity = response.getEntity();  
        if (entity != null) {  
            body = EntityUtils.toString(entity, charset);  
        }  
        EntityUtils.consume(entity);  
        response.close();  
        return body;  
    }  
	
}
