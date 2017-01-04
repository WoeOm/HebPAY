package heb.pay.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import heb.pay.dao.AutoScanDao;
import heb.pay.service.AutoScanService;

@Service
public class AutoScanServiceImpl implements AutoScanService{

	@Autowired
	private AutoScanDao autoScanDao;
	
	@Override
	public int getAutoJob(String jobName, String groupName) {
		return autoScanDao.getAutoJob(jobName, groupName);
		
	}

	@Override
	public List<String> getAutoScan() {
		return autoScanDao.getAutoScan("WAITING_PAYMENT");
	}

}
