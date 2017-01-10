package heb.pay.util;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class WebUtil {
	
	private static Configuration pagecfg = null;
	private static StringTemplateLoader pageLoader = null;
	
	@SuppressWarnings("deprecation")
	public static void htmlToFreeMarker(HttpServletRequest request,HttpServletResponse response , String html ,String path,Map<String,Object> params ) throws Exception{
		
		response.setContentType("text/html; charset=UTF-8");
		if(pagecfg == null){		
			pageLoader = new StringTemplateLoader();
			pagecfg = new Configuration();
			pagecfg.setTemplateLoader(pageLoader);
			pagecfg.setDefaultEncoding("UTF-8");
			pagecfg.setOutputEncoding("UTF-8");
			pagecfg.setNumberFormat("#");
			pagecfg.setTemplateUpdateDelay(0);
			pagecfg.setClassicCompatible(true);
		}
		Template template =null;
		if(html != null){
			pagecfg.removeTemplateFromCache(path);
			pageLoader.putTemplate(path, html);
			template = pagecfg.getTemplate(path,"UTF-8");
		}else{
			template = pagecfg.getTemplate(path,"UTF-8");
		}
		Map<String,Object> root = new HashMap<String,Object>();
		if(params != null){
			root.putAll(params);
		}
		root.put("ctx", request.getContextPath());
		Writer out = response.getWriter();  
		template.process(root, out);  
		out.flush();
	
	}
}
