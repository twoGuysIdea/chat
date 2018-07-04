package com.chat.springboot.domain;
/**
 * 返回一个result
 * @author yangyiwei
 * @date 2018年7月3日
 * @time 下午5:39:10
 */
public class ResultUtil {

	
	public static <T> Result<T> getResult(Class<?> clazz) {
		return new Result<T>();
	}
}
