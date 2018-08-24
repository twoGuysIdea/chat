package com.chat.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.chat.springboot.domain.Result;

@Controller
@RequestMapping("/rest")
public class CheckCenterController {
	
	@Autowired
	private RestTemplate restTemplate;
	
	
	@GetMapping("/remote")
	public JSONObject resultInvoke() {
		JSONObject jsonObject = restTemplate.getForObject("http://localhost:8080/factory/location/generate/map", JSONObject.class);
		return jsonObject;	
	}

//    //推送数据接口
//    @ResponseBody
//    @RequestMapping("/socket/push/{cid}")
//    public ApiReturnObject pushToWeb(@PathVariable String message) {
//        try {
//            WebSocketServer.sendInfo(message,cid);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return ApiReturnUtil.error(cid+"#"+e.getMessage());
//        }
//        return ApiReturnUtil.success(cid);
//    }
}
