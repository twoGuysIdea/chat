package com.chat.springboot.service;

import com.alibaba.fastjson.JSONObject;
import com.chat.springboot.domain.Result;

/**
 * redis操作
 * @author yangyiwei
 * @date 2018年6月7日
 * @time 上午9:33:02 随便调教一下
 */
public interface RedisService {

	/**
	 * 测试redis事务
	 * @return
	 */
	public Result<Object> execTransaction();
	
	/**
	 * 测试redis管道
	 * @return
	 */
	public Result<Object> execPipeLined();

	/**
	 * 执行redis hash操作
	 * @param jsonObject
	 * @return
	 */
	public Result<Object> hashOperate(JSONObject jsonObject);

	/**
	 * 模拟多线程秒杀
	 * @return
	 */
	public Result<Object> highKill();

	/**
	 * 模拟分布式锁
	 * @return
	 */
	public Result<Object> distributeLock();

}
