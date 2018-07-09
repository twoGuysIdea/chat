package com.chat.springboot.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.chat.springboot.dao.UserFriendDao;
import com.chat.springboot.dao.UserInfoDao;
import com.chat.springboot.domain.ResultStatus;
import com.chat.springboot.domain.UserFriend;
import com.chat.springboot.domain.UserInfo;
import com.chat.springboot.service.UserFriendService;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class UserFriendBean implements UserFriendService {

	@Resource
	private UserFriendDao userFriendDao;
	@Resource
	private UserInfoDao userInfoDao;
	@Resource
	private JedisPool jedisPool;

	@Override
	public ResultStatus addFriend(String userId, String friendId) {
		UserInfo userInfo = userInfoDao.findOne(friendId);
		if (userInfo == null) { //好友不存在
			return ResultStatus.DATA_NOT_FIND;
		}
		if (userId.equals(friendId)) { //不可添加自身
			return ResultStatus.CAN_NOT_ADD_SELF;
		}
		if (userFriendDao.isRepeatAddFriend(userId, friendId) > 0) { //不可重复添加好友
			return ResultStatus.CAN_NOT_ADD_REPEAT_FRIEND;
		}
		userInfo.setPassword(null);
		userInfo.setSalt(null);
		int count = userFriendDao.addFriend(userId, userInfo);
		if (count < 1) {
			return ResultStatus.UPDATE_FAIL;
		}
		return ResultStatus.SUCCESS;
	}

	@Override
	public List<UserInfo> getFriendListByUid(String userId) {
		//根据userId 查询到匹配记录
		UserFriend userFriend = userFriendDao.findByUserId(userId);
		//获取该用户所有好友
		List<UserInfo> friends = userFriend.getFriends();
		Jedis jedis = jedisPool.getResource();
		List<String> friendsIds = new ArrayList<String>();
		for (UserInfo item : friends) { //遍历好友列表 获取所有好友id
			friendsIds.add(item.getId());
		}
		//此处去redis查询 所有好友在线状态 并设置
		List<String> friendStatus = jedis.hmget("login_online_user", friendsIds.toArray(new String[friendsIds.size()]));
		for (int i = 0; i < friendStatus.size(); i++) {
			if (friendStatus.get(i) != null) { //说明该好友登陆过，状态在线
				friends.get(i).setOnline(true);
			} else {
				friends.get(i).setOnline(false);
			}
		}
		jedis.disconnect();
		return friends;
	}

}
