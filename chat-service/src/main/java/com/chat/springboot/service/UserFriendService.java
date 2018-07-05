package com.chat.springboot.service;

import com.chat.springboot.domain.ResultStatus;

/**
 * 用户好友业务逻辑层
 * @author yangyiwei
 * @date 2018年7月5日
 * @time 下午3:38:08
 */
public interface UserFriendService {

	/**
	 * 添加好友
	 * @param userId 用户id
	 * @param friendId  好友id
	 * @return
	 */
	public ResultStatus addFriend(String userId, String friendId);

}
