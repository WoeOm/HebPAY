package heb.pay.dao;

import java.util.List;
import java.util.Map;

public interface AutoScanDao {

	public int getAutoJob(String jobName,String groupName);

	/*
	 * 修改返回值List<String>为List<Map<String,Object>>
	 */
	public List<Map<String,Object>> getAutoScan(String status);
	
}
