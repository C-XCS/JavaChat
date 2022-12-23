package qqClient.Service;

import java.util.HashMap;


/**
 * 
 * @author sky
 * @version 1.0
 * 
 * 	管理  客户端连接到服务端的线程  的类
 * 
 *  客户端 线程管理类
 */
//           客户端   连接  服务  线程  管理
public class ClientConnectThreadManage {

	// 4.0 线程管理更新
	// 将多个线程 放入 HashMap集合, key为用户id  value 为线程 
	private static HashMap<String, HashMap<String, ClientConnectThread>> map = new HashMap<>();
	// 一个用户的线程集合    状态      对应线程
	private static HashMap<String, ClientConnectThread> stateMap = new HashMap<>();
	
	
	public static HashMap<String, HashMap<String, ClientConnectThread>> getMap() {
		return map;
	}

	public static HashMap<String, ClientConnectThread> getStateMap() {
		return stateMap;
	}

	// 存放线程 进入集合的方法
	public static void addThread(String userId, String state, ClientConnectThread thread) {
		stateMap.put(state, thread);
		map.put(userId, stateMap);
	}
	
	
	// 根据userId 返回线程
	public static ClientConnectThread getThread(String userId, String state) {
		
		return map.get(userId).get(state);
	}
	
	
	// 从集合中删除 某个线程对象
	public static void removeThread(String userId) {
		map.remove(userId);
	}
}
