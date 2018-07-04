package com.chat.springboot.aop;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chat.springboot.domain.ProjectException;
import com.chat.springboot.domain.Result;
import com.chat.springboot.domain.ResultStatus;

/**
 * <pre>
 * 功       能: 统一异常处理
 * 涉及版本: V3.0.0 
 * 创  建  者: yangyiwei
 * 日       期: 2018年3月9日 下午1:45:31
 * Q    Q: 2873824885
 * </pre>
 */
@ControllerAdvice
public class ExceptionHandleAop {

	private final static Logger logger = Logger.getLogger(ExceptionHandleAop.class);

	@ResponseBody
	@ExceptionHandler(value = Exception.class)
	public Result<Object> handle(Exception e) {
		Result<Object> result = new Result<Object>();
		if (e instanceof ProjectException) {
			ProjectException projectException = (ProjectException) e;
			ResultStatus resultStatus = projectException.getResultStatus();
			if (projectException.getDetailMsg() != null) {
				result.setData(projectException.getDetailMsg());
			}
			return result.setCode(resultStatus);
		}
		logger.error("出现了系统未知的错误-----！！！！", e);
		e.printStackTrace();// 未知错误，打印出来
		return result.setCode(ResultStatus.UNKNOW_ERROR);
	}
}
