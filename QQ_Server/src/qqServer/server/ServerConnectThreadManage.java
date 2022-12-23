package qqServer.server;

import java.util.HashMap;
import java.util.Iterator;

/**
 * 
 * @author sky
 * @version 1.0 
 * 
 * 	 服务端 管理用户线程类
 * 	
 * 	1.管理用户线程 的集合:
 * 			  用户名id        该用户的线程集合
 * 		HashMap<String, HashMap<String, ServerConnectThread>>
 * 
 * 	2.用户的 线程集合:
 * 			    状态(聊天对象)     线程
 * 		HashMap<String, ServerConnectThread>     该用户的一个聊天窗口 对于 一个线程
 */

public class ServerConnectThreadManage {


	// 管理用户线程的集合 
	// 注意需要静态
	// 总用户线程集合          userId      该用户的线程集合
	private static HashMap<String, HashMap<String, ServerConnectThread>> map = new HashMap<>();

	
	public static HashMap<String, HashMap<String, ServerConnectThread>> getMap() {
		return map;
	}


	// 存放线程 进入集合的方法
	public void addThread(String userId, String state, ServerConnectThread thread) {
		
		if(map.get(userId) != null) {
			// 若用已存在, 得到用户线程集合 再添加 
			map.get(userId).put(state, thread);
			
		} else {  // 用户不存在
			
			//     创建该用户的线程集合
			HashMap<String, ServerConnectThread> stateMaps = new HashMap<>();
			stateMaps.put(state, thread);
			// 将用户的线程集合 加入 纵用户集合
			map.put(userId, stateMaps);
		}
		
	}
	
	
	// 根据userId 返回线程
	public static ServerConnectThread getThread(String userId, String state) {
		
		return map.get(userId).get(state);
	}
	
	
	// 从集合中删除 某个线程对象
	public static void removeThread(String userId, String state) {
		//  获取该用户线程集合   删除对应线程
		map.get(userId).remove(state);
	}
	
	
	// 用户退出, 直接删除 用户整个集合
	public static void removeUserId(String userId) {
		
		map.remove(userId);
	}
	
	
	/*
	 *  返回在线用户列表的方法, 即返回userId
	 */
	public static String getOnlineUsers() {
		
		// 遍历集合 
		// 先取出所有的key 通过key取出对应的value(此处只需取key)
		Iterator<String> iterator = map.keySet().iterator();
		String onlineUserList = "  ";
		
		while(iterator.hasNext()) {
			// 取出每一个userId转为字符串
			// 由于客户端接收到字符串时会总结输出
			// 故以 "\n  " 换行符与2个空格 分割便 客户端输出
			onlineUserList += iterator.next().toString() + "\n  ";
		}
		
		return onlineUserList;
	}
	
}
