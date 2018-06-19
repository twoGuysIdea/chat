package com.chat.springboot.service.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/webSocket")
@Component
public class WebSocketServer {

    //记录在线人数
    private static int onlineCount = 0;

    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();

    private Session session;

    public WebSocketServer(){
        System.out.println("请求到服务器！");
    }

    /**
     * 连接建立调用
     * @param session
     */
    @OnOpen
    public void onOpen(Session session){
        this.session = session;
        webSocketSet.add(this);
        addOnlineCount();
        try{
            sendMessage("连接成功");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 连接关闭调用
     */
    @OnClose
    public void onClose(){
        webSocketSet.remove(this);
        subOnlineCount();
    }

    @OnMessage
    public void onMessage(String message,Session session){
        System.out.println("接受到来自网页的详细"+message);
        //群发消息
        for (WebSocketServer item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping(value="/pushVideoListToWeb",method=RequestMethod.POST,consumes = "application/json")
    @ResponseBody
    public Map<String,Object> pushVideoListToWeb(@RequestBody Map<String,Object> param) {
        Map<String,Object> result =new HashMap<String,Object>();
        try {
            WebSocketServer.sendInfo("有新客户呼入,sltAccountId:"+param.get("sltAccountId"));
            result.put("operationResult", true);
        }catch (IOException e) {
            result.put("operationResult", true);
        }
        return result;
    }

    /**
     * 群发自定义消息
     * */
    public static void sendInfo(String message) throws IOException {
        for (WebSocketServer item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                continue;
            }
        }
    }

    /**
     * 发生错误
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }


    private void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    private void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    private void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }

    /**
     * 获取在线人数
     * @return
     */
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    /**
     * 返回所有连接
     * @return
     */
    public static  CopyOnWriteArraySet<WebSocketServer> getAllSocket() {
        return webSocketSet;
    }


}
