package qqServer.server;

import java.io.IOException; 
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

import qqCommon.Message;
import qqCommon.MessageType;
import qqServer.Utils.MyObjectInputStream;
import qqServer.Utils.MyObjectOutputStream;
import qqServer.view.ServerFrame;

/**
 * 
 * @author sky
 * @version 1.0 
 * 
 * 服务端线程类
 * 		1.接收拉取在线用户请求, 返回message给原用户
 * 		2.接收私聊请求, 发送信息包给指定用户
 * 		3.接收群聊请求, 发送消息包给所有在线用户
 * 		4.接收私发文件请求
 * 		5.接收群发文件请求
 * 		6.接收(线程)退出请求, 将信息发送给其线程提示退出, 并break退出该线程 关闭socket
 * 				包括 聊天窗口(线程)退出 和 客户端退出
 * 	
 */

public class ServerConnectThread extends Thread{

	private ServerFrame serverFrame = null;    // 服务端界面对象
	private Socket socket;   // 类即客户端 的socket
	private String userId;   // 连接到的服务端 的对应用户id, 方便识别
	
	
	// 构造器 初始化传入
	public ServerConnectThread(Socket socket, String userId, ServerFrame serverFrame) {
		super();
		this.socket = socket;
		this.userId = userId;
		this.serverFrame = serverFrame;
	}
	
	
	// get方法
	public Socket getSocket() {
		return socket;
	}
	
	
	// 不停 接收读取 客户端发送 的数据
	@Override
	public void run() {
		// 线程处于run状态, 可以循环 方式/接收 客户端信息, 保持通信
		while(true) {
			println("服务端和 " + userId + " 保持通信, 读取数据");
		
			try {
				MyObjectInputStream ois = 
						new MyObjectInputStream(socket.getInputStream());
				// 若 客户端没有发送对象, 则重写在此阻塞(暂停)
				Message message = (Message) ois.readObject();   
				
				//	对接收的messageType 类型进行判断,进行相应操作
				if(message.getMessType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)) {
				// 一. 接到客户端 拉取在线用户列表 的请求
					
					/*
					 *  线程管理类ServerConnectClientThreadManage 存储所有userId
					 * 	  调用其 getOnlineUsers()方法  得到userId组成的字符串
					 */
					String onlineUsers = ServerConnectThreadManage.getOnlineUsers();
					
					Message message2 = new Message(); 
					message2.setContent(onlineUsers);     // 设置发送内容  在线用户
					message2.setSendTime(message.getSendTime());
					message2.setMessType(MessageType.MESSAGE_RET_ONLINE_FRIEND);    
					
					message2.setSender(message.getSender());    // 设置发送者 (为发过去的客户端)
					
					println("客户端 " + userId + " 正在拉取在线用户列表....");
					
					MyObjectOutputStream oos = 
							new MyObjectOutputStream(socket.getOutputStream());
					oos.writeObject(message2);    // 写入    
					
					   
				} else if (message.getMessType().equals(MessageType.MESSAGE_COMM_MES)) {
				// 二. 普通消息  客户端私聊
					
					
					// 调用 线程管理类的 getThread()方法 获取--接收者-- 的线程
					ServerConnectThread thread 
						= ServerConnectThreadManage.getThread( message.getGetter(), message.getSender());
					
					if(thread != null) {   // 线程存在
						MyObjectOutputStream oos = 
								new MyObjectOutputStream(thread.getSocket().getOutputStream());
						oos.writeObject(message);   // 写入
						
						println(message.getSender() + " 向 " 
								+ message.getGetter() + " 发送消息."	);
					} else {
						// 对方未启动 对应线程
						println(message.getSender() + " 向 " + message.getGetter() + " 发送消息失败!");
					}
					
					
					
				} else if (message.getMessType().equals(MessageType.MESSAGE_ToAll_MES)) {
				// 三. 客户端群聊
					
					/*
					 *  需要遍历 线程管理类的集合 得到 所有线程socket
					 *  调用 其getThread()方法 取得 该用户的线程集合
					 */
					HashMap<String, HashMap<String, ServerConnectThread>> map = 
							ServerConnectThreadManage.getMap();
					// 遍历集合
					Iterator<String> iterator = map.keySet().iterator();
					String onlineUserId;
					while(iterator.hasNext()) {
						
						onlineUserId = iterator.next().toString();  // 取出在线用户的id 即为iterator
						
						// 在线用户不是 客户端发送方
						if(! onlineUserId.equals(message.getSender())) {
							
							message.setGetter(onlineUserId);
							// 取出群聊线程
							ServerConnectThread thread = map.get(onlineUserId).get("群聊");
							
							if(thread != null) {
								MyObjectOutputStream oos =     //    取得该用户线程集合   取群聊线程    取io流     
										new MyObjectOutputStream(thread.getSocket().getOutputStream());
								oos.writeObject(message);
								
								println("用户 " + message.getSender() + " 向所有人群发消息, 用户"
										+ onlineUserId + " 接收成功.");
								
							} else {
								println(message.getSender() + " 向所有人 群发消息, 用户 "
										+ onlineUserId + " 接收失败!");
							}
						}
					}
					
					
				} else if (message.getMessType().equals(MessageType.MESSAGE_File_MES)) {
				// 四. 客户端 私聊传输文件  
					
					String getterId = message.getGetter();
					/*	线程管理类的getThread()方法 获取线程
					 */
					ServerConnectThread thread =       //      接收方    接收方聊天对象(发送方)
							ServerConnectThreadManage.getThread(getterId, userId);
					
					if(thread != null) {   // 线程存在
						MyObjectOutputStream oos = 
								new MyObjectOutputStream(thread.getSocket().getOutputStream());
						oos.writeObject(message);
						
						println(message.getSender() + " 向 " + message.getGetter() + " 发送文件...");
					} else {
						// 对方未启动 对应线程
						println(message.getSender() + " 向 " + message.getGetter() + " 发送文件失败!");
					}
					
				} else if (message.getMessType().equals(MessageType.MESSAGE_File_MES_TOALL)) {
				// 五. 客户端群发文件	
					
					// 定义消息类型 为文件传输 (客户端接收不分 群发/私发)
					message.setMessType(MessageType.MESSAGE_File_MES);
					/*
					 *  需要遍历 线程管理类的集合 得到 所有线程socket
					 *  调用 getThread()方法 取得hashmap集合
					 */
					HashMap<String, HashMap<String, ServerConnectThread>> map = 
							ServerConnectThreadManage.getMap();
					
					// 遍历集合
					Iterator<String> iterator = map.keySet().iterator();
					String onlineUserId;
					while(iterator.hasNext()) {
						
						// 取出在线用户的id 即为iterator
						onlineUserId = iterator.next().toString();
						
						// 在线用户不是 客户端发送方, 发送
						if(! onlineUserId.equals(message.getSender())) {
							
							message.setGetter(onlineUserId);
							
							ServerConnectThread thread = map.get(onlineUserId).get("群聊");
							
							if(thread != null) {
								MyObjectOutputStream oos =     //    取得该用户线程集合   取群聊线程    取io流     
										new MyObjectOutputStream(thread.getSocket().getOutputStream());
								oos.writeObject(message);
							
								// 打印文件名
								println("用户 " + message.getSender() + " 向所有人群发文件:\n" 
											+ message.getFileName() + " 接收用户: " + onlineUserId);
							} else {
								println(message.getSender() + " 向所有人 群发文件, 用户 " 
										+ onlineUserId + " 接收失败!");
							}
						}
					}
					
					
				} else if (message.getMessType().equals(MessageType.MESSAGE_CLIENT_EXIT)) {
				// 六. 接到 客户端准备退出 系统, 将message返回给客户端线程, 再关闭socket
					
					/*	客户端 调用userClientService类 的 logout()方法向服务端发送退出提示  无异常退出
					 * 
					 * 	否则 服务端会报错: java.net.SocketException:Connection reset
					 * 			报错位置: at qqServer.server.ServerConnectClientThread.run(ServerConnectClientThread.java:60)
					 * 		(服务端接收到后会将message返回给 本客户端线程-提醒其退出, 再关闭服务端对应socket)
					 */
					
					ServerConnectThread thread = ServerConnectThreadManage.
							getThread(message.getSender(), message.getGetter());
					
					MyObjectOutputStream oos = 
							new MyObjectOutputStream(thread.getSocket().getOutputStream());
					oos.writeObject(message);
					
					// 将客户端对应的线程从集合中 删除
					if(message.getGetter().equals("在线")) {
						
						// 客户端 退出, 解决删除 该用户整个 线程集合
						ServerConnectThreadManage.removeUserId(userId);
						println("客户端 " + message.getSender() + " 退出 !!");
						
					} else if (message.getGetter().equals("群聊")) {
						
						// 4.0 客户端将 状态存入getter   获取对应线程并从集合删除
						ServerConnectThreadManage.removeThread(message.getSender(), message.getGetter());
						println("客户端 " + message.getSender() + " 结束群聊..");
					} else {
						
						ServerConnectThreadManage.removeThread(message.getSender(), message.getGetter());
						println("客户端 " + message.getSender() + 
								" 结束与 " + message.getGetter() + " 的聊天..");
					}
					
					socket.close();   // 该线程的socket 即为 将退出客户端socket, 直接关闭
					break;   // 退出while循环, run方法将结束, 该线程结束!!!
					
				} else {
					
				}
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}


	// println语句 确保语句能输出到界面
    public void println(String s) {
        if (s != null) {
           serverFrame.getTaShow().setText(serverFrame.getTaShow().getText() + s + "\n");
            System.out.println(s + "\n");
        }
    }
}




