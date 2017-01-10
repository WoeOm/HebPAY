package heb.pay.service.impl;

import heb.pay.dao.AutoScanDao;
import heb.pay.service.AutoScanService;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AutoScanServiceImpl implements AutoScanService{

	@Autowired
	private AutoScanDao autoScanDao;
	
	@Override
	public int getAutoJob(String jobName, String groupName) {
		return autoScanDao.getAutoJob(jobName, groupName);
		
	}

	@Override
	public List<Map<String,Object>> getAutoScan() {
		return autoScanDao.getAutoScan("WAITING_PAYMENT");
	}

}
