package com.chat.springboot.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.chat.springboot.domain.Result;
import com.chat.springboot.domain.ResultStatus;
import com.chat.springboot.domain.UserInfo;
import com.chat.springboot.service.UserInfoService;

/**
 * 用户信息控制层
 * @author yangyiwei
 * @date 2018年7月2日
 * @time 下午4:36:35
 */
@RestController
@RequestMapping("/user/info")
public class UserInfoController {
	
	@Autowired
	private UserInfoService userInfoService;
	
	/**
	 * 用户注册
	 * @param userInfo
	 * @return
	 */
	@RequestMapping(value = "/register", method = {RequestMethod.POST, RequestMethod.GET})
	public Result<Object> register(@Valid UserInfo userInfo, BindingResult bindingResult) {
		Result<Object> result = new Result<Object>();
		if (bindingResult.hasErrors()) {
			return result.setCode(ResultStatus.LACK_PARAM).setMessage(bindingResult.getFieldError().getDefaultMessage());
		}
		if (userInfoService.register(userInfo)) {
			result.setCode(ResultStatus.SUCCESS).setData("注册成功");
		} else {
			result.setCode(ResultStatus.USER_IS_REGISTER).setData("注册失败");
		}
		return result;
	}

}
