package heb.pay.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import heb.pay.dao.SecurityDao;
import heb.pay.service.SecurityService;

@Service
public class SecurityServiceImpl implements SecurityService{

	@Autowired
	private SecurityDao securityDao;
	
	@Override
	public String getPaySecret(String payKey) {
		
		return securityDao.getPaySecret(payKey);
	}

	@Override
	public boolean isPayKey(String payKey) {
		
		int count = securityDao.isPayKey(payKey);
		if(count >0){
			return true;
		}else{
			return false;
		}
	}

}
