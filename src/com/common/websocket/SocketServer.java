package com.common.websocket;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.LocalTime;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * WebSocket
 * @author liuqiang
 *
 */
public class SocketServer extends WebSocketServer {

	public SocketServer(int port) {
		super(new InetSocketAddress(port));
	}

	public SocketServer(InetSocketAddress address) {
		super(address);
	}

	/**
	 * 触发连接事件
	 */
	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {

	}

	/**
	 * 触发关闭事件
	 */
	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		userLeave(conn);
	}

	@Override
	public void onError(WebSocket conn, Exception e) {
		print("onError：" + e.getMessage());
		e.printStackTrace();
	}

	/**
	 * 客户端发送消息到服务器时触发事件
	 */
	@Override
	public void onMessage(WebSocket conn, String message) {
		print("收到来自" + conn.getRemoteSocketAddress() + "的信息：" + message);
		JsonObject json = new JsonParser().parse(message).getAsJsonObject();
		String type = json.get("type").getAsString();
		String user = json.get("user").getAsString();
		if ("join".equals(type)) {
			this.userjoin(user, conn);
		}
		if ("message".equals(type)) {
			String info = json.get("message").getAsString();
			conn.send("[系统] 收到消息：" + info);// 同时向本人发送消息
			WebSocketPool.sendMessage(LocalTime.now() + "[系统]" + user + "发送消息：" + info);
		}
	}

	/**
	 * 用户加入处理
	 * @param user
	 */
	public void userjoin(String user, WebSocket conn) {
		//把当前用户加入到所有在线用户列表中
		String joinMsg = "{\"from\":\"[系统]\",\"content\":\"" + user + "上线了\",\"timestamp\":" + new Date().getTime() + ",\"type\":\"message\"}";
		WebSocketPool.sendMessage(joinMsg);						//向所有在线用户推送当前用户上线的消息
		WebSocketPool.addUser(user, conn);

		print("当前连接数：" + WebSocketPool.getUserCount());

	}

	/**
	 * 用户下线处理
	 * @param user
	 */
	public void userLeave(WebSocket conn) {
		String user = WebSocketPool.getUserByKey(conn);
		boolean b = WebSocketPool.removeUser(conn);				//在连接池中移除连接
		if (b) {
			print(user + "下线了;" + LocalTime.now());
			//向在线用户发送当前用户退出的消息
			WebSocketPool.sendMessage("[系统]" + user + "下线了;" + LocalTime.now());
		}
	}

	private static void print(String msg) {
		System.out.println(String.format("[%d] %s", System.currentTimeMillis(), msg));
	}

	private static void checkConnection() {
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				WebSocketPool.sendMessage("heart:" + LocalTime.now());
			}
		}, 0 * 1000, 10 * 1000);

	}

	public static void main(String[] args) {
		WebSocketImpl.DEBUG = false;

		int port = 8887; // 端口
		SocketServer server = new SocketServer(port);
		server.start();
		try {
			String ip = InetAddress.getLocalHost().getHostAddress();
			int serverport = server.getPort();
			print(String.format("服务已启动: %s:%d", ip, serverport));

			checkConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
