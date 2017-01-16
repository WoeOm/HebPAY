package heb.pay.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import heb.pay.dao.JFUserDao;
import heb.pay.entity.User;
import heb.pay.service.JFUserService;
@Service
public class JFUserServiceImpl implements JFUserService {
	@Autowired
	private JFUserDao userdao;
	@Override
	public int insertQuickUser(Map<String,String> map) {
		// TODO Auto-generated method stub
		return userdao.insertQuickUser(map);
	}
	@Override
	public List<User> getUserBymb(String mb) {
		// TODO Auto-generated method stub
		return userdao.getUserBymb(mb);
	}

}
