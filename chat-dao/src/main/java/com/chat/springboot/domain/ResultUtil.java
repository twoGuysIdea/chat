package com.chat.springboot.domain;
/**
 * 返回一个result
 * @author yangyiwei
 * @date 2018年7月3日
 * @time 下午5:39:10
 */
public class ResultUtil {

	
	/**
	 * 设置结果集，带返回对象
	 * @param resultStatus
	 * @param data
	 * @return
	 */
	public static <T> Result<T> setResult(ResultStatus resultStatus, T data) {
		return new Result<T>(resultStatus, data);
	}
	
	/**
	 * 设置结果集，不带返回对象
	 * @param resultStatus
	 * @param data
	 * @return
	 */
	public static Result<?> setResult(ResultStatus resultStatus) {
		Result<?> result = new Result<Object>();
		result.setCode(resultStatus);
		return result;
	}
	
	/**
	 * 设置出错结果集合
	 * @param resultStatus 状态码
	 * @param errorDetail 对出错情况的补充
	 * @return
	 */
	public static Result<String> setResult(ResultStatus resultStatus, String errorDetail) {
		Result<String> result = new Result<String>();
		result.setCode(resultStatus).setData(errorDetail);
		return result;
	}
	
	
}
