package com.chat.springboot.mapper;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.chat.springboot.domain.UserFriend;
import com.chat.springboot.domain.UserInfo;

/**
 * 此接口将自动实现 UserFriendDao 并对其进行扩展
 * 
 * @author yangyiwei
 * @date 2018年6月15日
 * @time 上午10:12:27
 */
@Repository
public class UserFriendDaoImpl {
	@Resource
	private MongoTemplate mongoTemplate;

	public int addFriend(String userId, UserInfo userInfo) {
		Query query = new Query();
		query.addCriteria(new Criteria().and("user_id").is(userId));
		Update update = new Update();
		update.push("friends", userInfo);
		return mongoTemplate.updateFirst(query, update, UserFriend.class).getN();
	}
	
	/**
	 * 判断是否重复添加好友
	 * @param userId
	 * @param friendId
	 * @return
	 */
	public int isRepeatAddFriend(String userId, String friendId) {
		Query query = new Query();
		query.addCriteria(new Criteria().and("user_id").is(userId).and("friends._id").is(friendId));
		return (int) mongoTemplate.count(query, UserFriend.class);
	}

}
