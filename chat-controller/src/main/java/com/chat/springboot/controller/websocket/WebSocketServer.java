package com.chat.springboot.controller.websocket;

import java.io.IOException;
import java.util.List;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;

@ServerEndpoint("/webSocket/{uuid}")
@Component
public class WebSocketServer {

	private final static Logger logger = Logger.getLogger(WebSocketServer.class);

	private static JedisPool jedisPool;

	private Session session;
	// 当前用户
	private String currentUserName;

	// 匹配用户
	private String matchUserName;

	public WebSocketServer() {

	}

	@Autowired
	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	/**
	 * 连接建立调用
	 * 
	 * @param session
	 */
	@OnOpen
	public void onOpen(Session session, @PathParam("uuid") String uuid) {
		this.session = session;
		this.currentUserName = uuid;// 设置当前用户
		Jedis jedis = jedisPool.getResource();
		Pipeline pipeline = jedis.pipelined(); // redis管道技术
		pipeline.sadd("online_people", uuid);// 添加在线人数
		pipeline.incr("online_count");// 人数++
		// pipeline.hset("match_peer", uuid, "noPeer");// 匹配的伙伴 一开始 为自身 + nopeer
		List<Object> list = pipeline.syncAndReturnAll();// 发送redis管道
		logger.info("redis管道返回结果:" + list.toString());
		jedis.close();
		AllWebSocket.set.add(this);
		try {
			sendMessage("收到服务器的消息：连接成功！");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 连接关闭调用
	 */
	@OnClose
	public void onClose() {
		logger.info(currentUserName + "用户退出了聊天.....");
		Jedis jedis = jedisPool.getResource();
		Transaction transaction = jedis.multi();
		transaction.decr("online_count");// 人数--
		transaction.srem("online_people", currentUserName);// 移除用户
		// 判定一下是否匹配过用户，如果有，则通知对方用户 该用户下线，并且解除两边的匹配关系
		if (matchUserName != null) { // 解除用户匹配关系
			transaction.hdel("match_peer", currentUserName);
			transaction.hdel("match_peer", matchUserName);

			// 通知对方用户我方已经下线
			try {
				for (WebSocketServer item : AllWebSocket.set) {
					if (matchUserName.equals(item.getCurrentUserName())) {
						item.setMatchUserName(null);
						item.sendMessage("系统提示:对方退出了聊天,匹配关系解除!");
					}
				}
			} catch (Exception e) { // 如果浏览器被关闭了 会走到这里发生异常 无需关注
				e.printStackTrace();
			}

		}
		transaction.exec();
		jedis.disconnect();
		AllWebSocket.set.remove(this);
	}

	/*
	 * 
	 * A匹配B B匹配C C匹配 A
	 * 
	 * 
	 */
	@OnMessage
	public void onMessage(String message, Session session) throws Exception {
		if (matchUserName == null) { // 匹配用户为空，则进入匹配
			Jedis jedis = jedisPool.getResource();
			jedis.sadd("online_match_people", currentUserName);// 将自身写入匹配队列
			/*
			 * boolean startMatch = false; for (int i = 0; i < 3; i++) { if
			 * (jedis.scard("online_match_people") >= 2) { startMatch = true;
			 * break; } else { Thread.sleep(2000); } }
			 */
			Thread.sleep(5000);
			/*
			 * if (!startMatch) { // 告诉人太少了 jedis.srem("online_match_people",
			 * currentUserName); sendMessage("当前正在匹配的小伙伴太少...请稍后再试"); return; }
			 */
			String peer = jedis.spop("online_match_people");// 从set中随机弹出一个元素
			if (peer.equals(currentUserName)) {// 如果匹配到自身，则直接返回
				sendMessage("小伙子....你匹配到自己了..");
				return;
			}
			logger.info("自己的id是：" + currentUserName + "弹出的匹配小伙伴是: " + peer);
			// 成功弹出匹配的队友以后。用redis setnx指令 。迅速锁定。防止其他线程继续匹配
			Transaction transaction = jedis.multi();// 开始redis事务
			transaction.setnx(currentUserName, "1");// 设置自己标志
			transaction.setnx(peer, "1");// 设置对方标志
			transaction.expire(currentUserName, 10);
			transaction.expire(peer, 10);
			List<Object> result = transaction.exec();
			if (result.get(0).toString().equals("1") && result.get(1).toString().equals("1")) { // 此处说明匹配成功
				logger.info("有玩家成功通过两个标志位匹配了.....");
				jedis.hset("match_peer", currentUserName, peer);// 设置配对关系
				jedis.hset("match_peer", peer, currentUserName);
				matchUserName = peer;// 赋值小伙伴
				sendMessage("已经为您匹配小伙伴成功...对方id:" + peer);
			} else { // 配对自身指定玩家失败
				Thread.sleep(2000); // 等待匹配成功的结果
				String matchPeer = jedis.hget("match_peer", currentUserName);
				if (matchPeer != null) {// 查询是否被匹配过
					logger.info("有玩家被动匹配了.....");
					matchUserName = matchPeer;
					sendMessage("已经为您匹配小伙伴成功...对方id:" + peer);
				} else {
					sendMessage("暂未匹配到合适的小伙伴....请稍后再试");
				}
			}
			/*
			 * jedis.del(currentUserName);// 删除标志位 jedis.del(peer);
			 */
			jedis.disconnect();// 关闭连接
		} else {
			// 发送指定消息过去
			sendToMatchUser(message, matchUserName);
		}

	}

	/**
	 * 群发所有消息
	 * 
	 * @param message
	 */
	public void sendAllMessage(String message) {
		for (WebSocketServer item : AllWebSocket.set) {
			try {
				item.sendMessage(currentUserName + "用户对你说: " + message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 发送指定消息到用户
	 * 
	 * @param message
	 * @param matchUserName
	 * @throws IOException
	 */
	public void sendToMatchUser(String message, String matchUserName) throws IOException {
		for (WebSocketServer item : AllWebSocket.set) {

			if (item.getCurrentUserName().equals(matchUserName)) {
				item.sendMessage(currentUserName + "用户对你说: " + message);
			}

		}
	}

	/*
	 * @RequestMapping(value = "/pushVideoListToWeb", method =
	 * RequestMethod.POST, consumes = "application/json")
	 * 
	 * @ResponseBody public Map<String, Object> pushVideoListToWeb(@RequestBody
	 * Map<String, Object> param) { Map<String, Object> result = new
	 * HashMap<String, Object>(); try {
	 * WebSocketServer.sendInfo("有新客户呼入,sltAccountId:" +
	 * param.get("sltAccountId")); result.put("operationResult", true); } catch
	 * (IOException e) { result.put("operationResult", true); } return result; }
	 */

	/**
	 * 发生错误
	 * 
	 * @param session
	 * @param error
	 */
	@OnError
	public void onError(Session session, Throwable error) {
		error.printStackTrace();
	}

	/**
	 * 调用发送方法
	 * 
	 * @param message
	 * @throws IOException
	 */
	private void sendMessage(String message) throws IOException {
		this.session.getBasicRemote().sendText(message);
	}

	public String getCurrentUserName() {
		return currentUserName;
	}

	public void setCurrentUserName(String currentUserName) {
		this.currentUserName = currentUserName;
	}

	public String getMatchUserName() {
		return matchUserName;
	}

	public void setMatchUserName(String matchUserName) {
		this.matchUserName = matchUserName;
	}

	/**
	 * 
	 * 
	 * if (matchUserName == null) { // 匹配用户为空，则进入匹配 boolean isMatch = false;
	 * Jedis jedis = jedisPool.getResource(); String peer =
	 * jedis.srandmember("no_match_people");// 随机弹出一个未匹配的玩家 while
	 * (peer.equals(currentUserName)) {// 如果匹配到自身，则继续弹出 peer =
	 * jedis.srandmember("no_match_people"); } logger.info("自己的id是：" +
	 * currentUserName + "弹出的匹配小伙伴是: " + peer); jedis.hset("match_peer",
	 * currentUserName, peer);// 暂定两边匹配成功 for (int i = 0; i < 10; i++) {
	 * logger.info("已经进入匹配模式.....匹配次数...." + i + "次"); if
	 * (jedis.hget("match_peer", peer).equals(currentUserName)) { //
	 * 如果两边用户匹配的同一个人 isMatch = true; break; } try { Thread.sleep(1000); } catch
	 * (InterruptedException e) { e.printStackTrace(); } } if (isMatch) {
	 * jedis.srem("no_match_people", currentUserName);// 将当前用过户从未匹配set中弹出
	 * matchUserName = peer;// 匹配用户 sendMessage("匹配成功，可以开始聊天了.....匹配到的用户id是:" +
	 * peer); } else { jedis.hset("match_peer", currentUserName, "noPeer");//
	 * 解除两边的匹配模式 sendMessage("未找到合适的人选,请稍后再试....."); } jedis.disconnect();
	 * 
	 * // 此处匹配十秒 } else { // 发送指定消息过去 sendToMatchUser(message, matchUserName); }
	 * 
	 */

}
