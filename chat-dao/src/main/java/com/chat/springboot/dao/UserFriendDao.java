package com.chat.springboot.dao;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.chat.springboot.domain.UserFriend;
/**
 * 用户好友数据层
 * @author yangyiwei
 * @date 2018年7月5日
 * @time 下午4:09:44
 */
public interface UserFriendDao extends MongoRepository<UserFriend, String> {

}
