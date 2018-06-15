package com.chat.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/checkcenter")
public class CheckCenterController {

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
