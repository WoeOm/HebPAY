package heb.pay.service;

import java.util.List;
import java.util.Map;

public interface AutoScanService {

	public int getAutoJob(String jobName, String groupName);
	
	public List<Map<String,Object>> getAutoScan();
	
}
