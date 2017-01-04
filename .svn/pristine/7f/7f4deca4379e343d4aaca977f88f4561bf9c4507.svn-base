package heb.pay.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import heb.pay.dao.SecureDao;
import heb.pay.service.SecureService;

@Service
public class SecureServiceImpl implements SecureService{

	@Autowired
	private SecureDao secureDao;
	
	@Override
	public String getPaySecret(String payKey) {
		
		return secureDao.getPaySecret(payKey);
	}

	@Override
	public boolean isPayKey(String payKey) {
		
		int count = secureDao.isPayKey(payKey);
		if(count >0){
			return true;
		}else{
			return false;
		}
	}

}
