package heb.pay.dao;

import heb.pay.entity.User;

import java.util.List;
import java.util.Map;


public interface JFUserDao {
	/**
	 * 快捷注册用户
	 * @param userid
	 * @param mb
	 * @param usertype
	 * @return 插入结果
	 */
	public  int insertQuickUser(Map<String,String> map);
	/**
	 * 根据mb查看用户是否存在
	 * @param mb
	 * @return
	 */
	public List<User> getUserBymb(String mb);
}
