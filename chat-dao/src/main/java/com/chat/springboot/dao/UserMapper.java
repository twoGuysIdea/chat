package com.chat.springboot.dao;

import com.chat.springboot.common.MyMapper;
import com.chat.springboot.domain.User;

public interface UserMapper extends MyMapper<User> {
	
	//@Cacheable(value = "user")
	public User findByName(String userName);
}