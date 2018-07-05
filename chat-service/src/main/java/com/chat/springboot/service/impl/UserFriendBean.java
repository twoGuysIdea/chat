package com.chat.springboot.service.impl;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.chat.springboot.dao.UserFriendDao;
import com.chat.springboot.dao.UserInfoDao;
import com.chat.springboot.domain.ResultStatus;
import com.chat.springboot.domain.UserInfo;
import com.chat.springboot.service.UserFriendService;

@Service
public class UserFriendBean implements UserFriendService {

	@Resource
	private UserFriendDao userFriendDao;
	@Resource
	private UserInfoDao userInfoDao;

	@Override
	public ResultStatus addFriend(String userId, String friendId) {
		UserInfo userInfo = userInfoDao.findOne(friendId);
		if (userInfo == null) { //好友不存在
			return ResultStatus.DATA_NOT_FIND;
		}
		if (userId.equals(friendId)) { //不可添加自身
			return ResultStatus.CAN_NOT_ADD_SELF;
		}
		userInfo.setPassword(null);
		userInfo.setSalt(null);
		int count = userFriendDao.addFriend(userId, userInfo);
		if (count < 1) {
			return ResultStatus.UPDATE_FAIL;
		}
		return ResultStatus.SUCCESS;
	}

}
