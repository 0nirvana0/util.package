package com.common.websocket;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.java_websocket.WebSocket;

public class WebSocketPool {
	private static final Map<WebSocket, String> userconnections = new HashMap<WebSocket, String>();

	/**
	 * 获取用户名
	 * @param session
	 */
	public static String getUserByKey(WebSocket conn) {
		return userconnections.get(conn);
	}

	/**
	 * 获取在线总数
	 * @param 
	 */
	public static int getUserCount() {
		return userconnections.size();
	}

	/**
	 * 获取WebSocket
	 * @param user
	 */
	public static WebSocket getWebSocketByUser(String user) {
		WebSocket socket = userconnections.entrySet().parallelStream().filter(v -> v.getValue().equals(user)).findAny().get().getKey();
		return socket;
	}

	/**
	 * 向连接池中添加连接
	 * @param inbound
	 */
	public static void addUser(String user, WebSocket conn) {
		userconnections.put(conn, user);	//添加连接
	}

	/**
	 * 获取所有的在线用户
	 * @return
	 */
	public static Collection<String> getOnlineUser() {
		Collection<String> setUser = userconnections.values();
		return setUser;
	}

	/**
	 * 移除连接池中的连接
	 * @param inbound
	 */
	public static boolean removeUser(WebSocket conn) {
		if (userconnections.containsKey(conn)) {
			userconnections.remove(conn);	//移除连接
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 向特定的用户发送数据
	 * @param user
	 * @param message
	 */
	public static void sendMessageToUser(String user, String message) {
		WebSocket conn = getWebSocketByUser(user);
		if (null != conn) {
			conn.send(message);
		}
	}

	/**
	 * 向所有的用户发送消息
	 * @param message
	 */
	public static void sendMessage(String message) {
		userconnections.keySet().forEach(socket -> socket.send(message));
	}

}
