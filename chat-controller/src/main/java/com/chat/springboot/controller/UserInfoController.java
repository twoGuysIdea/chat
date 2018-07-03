package com.chat.springboot.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.chat.springboot.domain.Result;
import com.chat.springboot.domain.ResultStatus;
import com.chat.springboot.domain.UserInfo;
import com.chat.springboot.service.UserInfoService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

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
			result.setCode(ResultStatus.SUCCESS);
		} else {
			result.setCode(ResultStatus.USER_IS_REGISTER);
		}
		return result;
	}
	
	/**
	 * 用户登陆
	 * @param userInfo
	 * @param bindingResult
	 * @return
	 */
	@ApiOperation(value = "用户登陆")
	@ApiImplicitParam(name = "userInfo", value = "用户信息", required = true, dataType = "Integer")
	@RequestMapping(value = "/login", method = {RequestMethod.POST, RequestMethod.GET})
	public Result<Object> login(@Valid @RequestBody UserInfo userInfo, BindingResult bindingResult, HttpSession httpSession) {
		Result<Object> result = new Result<Object>();
		if (bindingResult.hasErrors()) { //spring-boot自带的校验
			return result.setCode(ResultStatus.LACK_PARAM).setMessage(bindingResult.getFieldError().getDefaultMessage());
		}
		if(userInfoService.login(userInfo)) { //登陆成功
			httpSession.setAttribute("userName", userInfo.getUserName());//写入session
			result.setCode(ResultStatus.SUCCESS);
		} else {
			result.setCode(ResultStatus.LOGIN_FAIL);
		}
		return result;
	}

}
