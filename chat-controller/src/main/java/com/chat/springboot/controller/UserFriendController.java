package com.chat.springboot.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.chat.springboot.common.annotation.ValidateAttribute;
import com.chat.springboot.common.annotation.ValidateSession;
import com.chat.springboot.common.response.ResponseResult;
import com.chat.springboot.common.response.ResultStatus;
import com.chat.springboot.common.response.ResultUtil;
import com.chat.springboot.domain.UserInfo;
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
@CrossOrigin
@RequestMapping("/user/friend")
public class UserFriendController {

	@Autowired
	private UserFriendService userFriendService;

	@ApiOperation(value = "用户添加好友")
	@ApiImplicitParam(name = "friendId", value = "要添加的好友id", required = true, dataType = "String", paramType = "query")
	@RequestMapping(value = "/add", method = { RequestMethod.POST, RequestMethod.GET })
	@ValidateSession
	@ValidateAttribute(attributes = {"friendId"})
	public ResponseResult<?> addFirend(String friendId, HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		return ResultUtil.setResult(userFriendService.addFriend(userId, friendId));
	}
	
	@ApiOperation(value = "查询用户好友列表")
	@ApiImplicitParam(name = "session", value = "session", required = false, dataType = "String", paramType = "query")
	@RequestMapping(value = "/list", method = { RequestMethod.POST, RequestMethod.GET })
    @ValidateSession
	public ResponseResult<List<UserInfo>> getFriendListByUid(HttpSession session) {
		String userId = (String) session.getAttribute("userId");
		List<UserInfo> userInfos = userFriendService.getFriendListByUid(userId);
		return ResultUtil.setResult(ResultStatus.SUCCESS, userInfos);
	}

}
