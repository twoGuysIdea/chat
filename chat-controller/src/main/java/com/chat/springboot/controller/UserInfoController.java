package com.chat.springboot.controller;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.chat.springboot.common.DateUtils;
import com.chat.springboot.common.StringUtils;
import com.chat.springboot.common.ValidateSession;
import com.chat.springboot.domain.Result;
import com.chat.springboot.domain.ResultStatus;
import com.chat.springboot.domain.UserInfo;
import com.chat.springboot.service.UserFriendService;
import com.chat.springboot.service.UserInfoService;
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
	@Autowired
	private UserFriendService userFriendService;
	
	/**
	 * 用户注册
	 * @param userInfo
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping(value = "/register", method = {RequestMethod.POST, RequestMethod.GET})
	public Result<?> register(@Valid UserInfo userInfo, String birthdayStr, BindingResult bindingResult) throws ParseException {
		if (birthdayStr != null) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			userInfo.setBirthday(format.parse(birthdayStr)); //设置出生年月
		}
		Result<Object> result = new Result<Object>();
		if (bindingResult.hasErrors()) {
			return result.setCode(ResultStatus.LACK_PARAM).setData(bindingResult.getFieldError().getDefaultMessage());
		}
		return result.setCode(userInfoService.register(userInfo));
	}
	
	/**
	 * 用户登陆
	 * @param userInfo
	 * @param bindingResult
	 * @return
	 */
	@ApiOperation(value = "用户登陆")
	@ApiImplicitParam(name = "userInfo", value = "用户信息", required = true, dataType = "UserInfo")
	@RequestMapping(value = "/login", method = {RequestMethod.POST, RequestMethod.GET})
	public Result<?> login(@Valid UserInfo userInfo, BindingResult bindingResult, HttpSession httpSession) {
		Result<Object> result = new Result<Object>();
		if (bindingResult.hasErrors()) { //spring-boot自带的校验
			return result.setCode(ResultStatus.LACK_PARAM).setData(bindingResult.getFieldError().getDefaultMessage());
		}
		ResultStatus resultStatus = userInfoService.login(userInfo);
		if(resultStatus.getCode().intValue() == 0) { //登陆成功
			httpSession.setAttribute("userName", userInfo.getUserName());//写入session
			httpSession.setAttribute("userId", userInfo.getId());
		}
		return result.setCode(resultStatus);
	}
	
	/**
	 * 修改用户签名
	 * @return
	 */
	@ApiOperation(value = "修改用户签名")
	@ApiImplicitParam(name = "sign", value = "用户签名", required = true, dataType = "String", paramType = "query")
	@RequestMapping(value = "/edit/sign", method = {RequestMethod.POST, RequestMethod.GET})
	@ValidateSession
	public Result<?> editSign(String sign, HttpSession httpSession) {
		Result<Object> result = new Result<Object>();
		if (StringUtils.isBlank(sign)) {
			return result.setCode(ResultStatus.LACK_PARAM).setData("签名不能为空");
		}
		String userId = (String) httpSession.getAttribute("userId");
		return result.setCode(userInfoService.updateSignById(userId, sign));
	}

}
