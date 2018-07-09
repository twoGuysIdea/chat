package com.chat.springboot.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.chat.springboot.common.StringUtils;
import com.chat.springboot.common.ValidateSession;
import com.chat.springboot.domain.Result;
import com.chat.springboot.domain.ResultStatus;
import com.chat.springboot.service.UserFriendService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * 用户好友控制层
 * 
 * @author yangyiwei
 * @date 2018年7月5日
 * @time 下午3:36:04
 */
@RestController
@RequestMapping("/user/friend")
public class UserFriendController {

	@Autowired
	private UserFriendService userFriendService;

	@ApiOperation(value = "用户添加好友")
	@ApiImplicitParam(name = "friendId", value = "要添加的好友id", required = true, dataType = "String", paramType = "query")
	@RequestMapping(value = "/add", method = { RequestMethod.POST, RequestMethod.GET })
	@ValidateSession
	public Result<?> addFirend(String friendId, HttpSession session) {
		Result<?> result = new Result<>();
		if (StringUtils.isBlank(friendId)) {
			return result.setCode(ResultStatus.LACK_PARAM);
		}
		String userId = (String) session.getAttribute("userId");
		return result.setCode(userFriendService.addFriend(userId, friendId));
	}

}
