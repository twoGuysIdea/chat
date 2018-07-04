package com.chat.springboot.service.impl;

import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.chat.springboot.common.SignUtil;
import com.chat.springboot.dao.UserInfoDao;
import com.chat.springboot.domain.ProjectException;
import com.chat.springboot.domain.ResultStatus;
import com.chat.springboot.domain.UserInfo;
import com.chat.springboot.service.UserInfoService;

@Service
public class UserInfoServiceBean implements UserInfoService {

	@Resource
	private UserInfoDao userInfoDao;

	@Override
	public ResultStatus register(UserInfo userInfo) {
		int count = userInfoDao.userNameIsRepeat(userInfo.getUserName());
		if (count > 0) {
			return ResultStatus.USER_IS_REGISTER;
		}
		// 通过shiro运算，获取盐和加密后的值
		String[] result = SignUtil.AddSalt(userInfo.getUserName(), userInfo.getPassword(), null);
		userInfo.setSalt(result[1]);
		userInfo.setPassword(result[0]);
		userInfo.setId(UUID.randomUUID().toString());
		System.out.println("密码盐" + userInfo.getSalt() + "密码：" + userInfo.getPassword());
		userInfoDao.insert(userInfo);
		return ResultStatus.SUCCESS;

	}

	@Override
	public ResultStatus login(UserInfo userInfo) {
		UserInfo searchUser = userInfoDao.findByUserName(userInfo.getUserName());
		if (searchUser == null) { //未找到该用户
			return ResultStatus.USER_NOT_EXIST;
		}
		String password = SignUtil.AddSalt(userInfo.getUserName(),
				userInfo.getPassword(), searchUser.getSalt())[0]; //根据用过户传输的密码以及查询出来的salt 计算值
		if (password.equals(searchUser.getPassword())) { //加密后结果 与 预期一致
			userInfo.setId(searchUser.getId());
			return ResultStatus.SUCCESS;
		}
		return ResultStatus.LOGIN_FAIL;
	}

	@Override
	public ResultStatus updateSignByUser(String userName, String sign) {
		int num = userInfoDao.updateSignByUser(userName, sign);
		if (num < 1) {
			return ResultStatus.UPDATE_FAIL;
		}
		return ResultStatus.SUCCESS;
	}

}
