package com.chat.springboot.service;

import com.chat.springboot.domain.UserInfo;

/**
 * 用户信息业务逻辑层
 * @author yangyiwei
 * @date 2018年7月2日
 * @time 下午4:39:59
 */
public interface UserInfoService {

	/**
	 * 用户信息注册
	 * @param userInfo 用户
	 * @return 
	 */
	public boolean register(UserInfo userInfo);

}
