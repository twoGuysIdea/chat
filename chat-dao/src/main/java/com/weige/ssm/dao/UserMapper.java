package com.weige.ssm.dao;

import com.weige.ssm.common.MyMapper;
import com.weige.ssm.domain.User;

public interface UserMapper extends MyMapper<User> {
	
	//@Cacheable(value = "user")
	public User findByName(String userName);
}