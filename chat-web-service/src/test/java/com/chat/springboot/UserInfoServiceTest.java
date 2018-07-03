package com.chat.springboot;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.chat.springboot.domain.UserInfo;
import com.chat.springboot.service.UserInfoService;

/**
 * 用户类单元测试
 * @author yangyiwei
 * @date 2018年7月3日
 * @time 上午9:36:36
 */
public class UserInfoServiceTest extends SpringBootChatApplicationTests{
	
	@Autowired
	private UserInfoService userInfoService;

	
	@Test
	public void userLogin() {
		UserInfo userInfo = new UserInfo();
		userInfo.setUserName("杨乙伟");
		userInfo.setPassword("123456");
		System.out.println(userInfoService.login(userInfo));
		
	}
}
