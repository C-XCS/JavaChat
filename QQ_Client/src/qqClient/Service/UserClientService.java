package qqClient.Service;


import java.io.IOException;   
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

import javax.swing.JOptionPane;

import qqClient.Utils.MyObjectInputStream;
import qqClient.Utils.MyObjectOutputStream;
import qqClient.view.ChatFrame;
import qqClient.view.OnlineUserFrame;
import qqCommon.Message;
import qqCommon.MessageType;
import qqCommon.User;

/**
 * 
 * @author sky
 * @version 1.0
 *	
 *	客户端 用户服务类
 * 		功能: 1. 用户登录验证
 * 			  2. 用户注册  
 * 			  3. 拉取在线用户 
 * 			  4. 无异常退出   
 * 			  5. 启动线程 加入集合
 * 
 *  <在登录验证时连接服务器 登录成功启动线程>
 *  <注册时连接服务器, 完成后关闭>
 */

public class UserClientService {

	// 使用qqCommon包 的User对象, 进行传输
	private User user = new User(); 
	
	private Socket socket;
	
	/*	验证登录方法
	 *  1. 将userId 和password 发送到服务端
	 *  2. 接收服务端返回message
	 *  3. 返回boolean
	 */
	public boolean checkUser(String userId, String password) {
		
		// 临时变量, 用于返回
		boolean loop = false;
		
		try {
			// 将数据封装至对象
			user.setUserId(userId);
			user.setPassward(password);
			 
			// 创建Socket 连接到服务端            InetAddress.getLocalHost()
				socket = new Socket(InetAddress.getLocalHost(), 9999);	
			
			// 发送user对象给服务端
			MyObjectOutputStream oos = 
					new MyObjectOutputStream(socket.getOutputStream());
			oos.writeObject(user);  
		
			// 接收服务端 回送的message 对象, 用户消息验证
			MyObjectInputStream ois = 
					new MyObjectInputStream(socket.getInputStream());
			Message message = (Message)ois.readObject();
			
			// 对接收到的message 消息对象进行验证
			if( message.getMessType().equals(qqCommon.MessageType.MESSAGE_LOGIN_SUCCEED) ) {
				
				loop = true;
			} else {     
				// 弹窗提示
				JOptionPane.showMessageDialog(null, "账号或密码错误!");
				
				// 登陆失败
				// 关闭 已启动的socket
				socket.close();
			}
			
		} catch (UnknownHostException e1) {
			JOptionPane.showMessageDialog(null, "服务器未启动");
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "服务器未启动");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return loop;   // 返回
	}
	
	
	/*  客户端注册 方法
	 * 	1. 发送账号密码给服务端, 其判断账号是否已被注册
	 *  2. 接收服务端返回的 消息类型
	 */
	public boolean registUser(String userId, String password) {
		// 临时变量, 用于返回
		boolean loop = false;
		
		try {
			// 将数据封装至对象
			user.setUserId(userId);
			user.setPassward(password);
			// 设置为请求注册
			user.setRegistMessageType(MessageType.MESSAGE_REGIST_REQUEST);   
			 
			// 创建Socket 连接到服务端            InetAddress.getLocalHost()
				socket = new Socket(InetAddress.getLocalHost(), 9999);
			
			// 发送user对象给服务端
			MyObjectOutputStream oos = 
					new MyObjectOutputStream(socket.getOutputStream());
			oos.writeObject(user);    // 发送给服务端
			
			// 接收服务端 回送的message 对象, 用户消息验证
			MyObjectInputStream ois = 
					new MyObjectInputStream(socket.getInputStream());
			
			Message message = (Message)ois.readObject();
		
			
			// 对接收到的message 消息对象进行验证
			if( message.getMessType().equals(qqCommon.MessageType.MESSAGE_REGIST_SUCCEED) ) {
				
				loop = true;   // 函数返回值
			} 
			// 无论成功与否, 关闭socket
			// 关闭 以启动的socket
			socket.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		// 返回
		return loop;
	}	
	

	/**	在线用户窗口 线程启动方法
	 * 
	 * @param userId  用户名
	 * @param onlineUserFrame  在线用户窗口(接收在线用户列表)
	 */
	public void startThread(String userId, OnlineUserFrame onlineUserFrame) {
		/*
		 *  启动线程, 封装维护socket  
		 *  使用 ClientServerThread  线程类
		 */
		ClientConnectThread thread = new ClientConnectThread(socket, onlineUserFrame);
		
		thread.start();   // 启动

		/*	将线程放入 管理线程的集合中
		 *  通过ClientConnentServerThreadManage 线程管理类
		 *  	addClientThread 方法添加线程
		 */
		// 状态标记为 在线
		ClientConnectThreadManage.addThread(userId, "在线", thread);
	}
	
	
	
	/**	4.0修改 
	 *  聊天窗口  启动线程 方法
	 * 	1. 开启一个新的 socket
	 *  2. 启动一个 新的线程
	 *  	保证 一个聊天窗口(对象) 对应 一个socket和线程
	 *  	使 一个用户可以 提示开启多个 窗口
	 * 
	 * @param userId   用户名
	 * @param getterId 聊天对象(状态)  "群聊",对方用户名 
	 * @param chatFrame	聊天窗口
	 */
	public void startThreadChat(String userId, String getterId, ChatFrame chatFrame) {
		
		/* 4.0
		 *  创建User对象 
		 *  	getterId 聊天对象 即 状态
		 *  	方便服务器 识别使用
		 */
		user.setUserId(userId);
		user.setState(getterId);   // 设置状态为 聊天对象
		 
		// 创建Socket 连接到服务端           
		try {
			socket = new Socket(InetAddress.getLocalHost(), 9999);

		
		// 发送user对象给服务端
		MyObjectOutputStream oos = 
				new MyObjectOutputStream(socket.getOutputStream());
		
		oos.writeObject(user);    // 发送
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		/*	启动线程, 封装维护socket  
		 */
		ClientConnectThread thread = new ClientConnectThread(socket, chatFrame);
		
		thread.start();  // 启动

		/* 4.0
		 * 将线程放入 管理线程的集合中
		 *  	方便 线程管理器 存放管理 
		 */
		// 状态 为 聊天对象("群聊" "对方用户名")
		ClientConnectThreadManage.addThread(userId, getterId, thread);
	}
	
	
	
	
	/**	拉取在线用户方法 
	 * 	向服务器 发送请求在线用户列表
	 * 
	 * @param senderId 用户名
	 */
	public void onlineFriendList(String senderId) {
		
		// 发送一个message对象
		// messageType 定义为MESSAGE_GET_ONLINE_FRIEND 客户端请求
		Message message = new Message();
		message.setSendTime(new Date().toString());  // 发送时间
		message.setMessType(MessageType.MESSAGE_GET_ONLINE_FRIEND);   
		message.setSender(senderId);         // 设置发送者
		
		try {
			// 线程管理类 的获取线程方法, 传入userId 和 状态"在线" 
			ClientConnectThread thread = 
					ClientConnectThreadManage.getThread(senderId, "在线");

			// 通过线程类 的getScoket方法 得到socket  发送给服务端
			MyObjectOutputStream ois = 
					new MyObjectOutputStream( thread.getSocket().getOutputStream());
			ois.writeObject(message);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	/**	无异常退出方法
	 * 	1. 该方法 向服务端发送 退出的message, 提示其关闭socket 退出线程 
	 *  2. 服务端返回给本线程, 提示本线程退出
	 * 
	 * @param userId  用户名
	 * @param state  状态(定位到对应的线程)
	 */
	public void logout(String userId, String state) {
		Message message = new Message();
		message.setSender(userId);    // 一定要指定发送者 客户端id
		message.setMessType(MessageType.MESSAGE_CLIENT_EXIT);   // 设置消息类型为 退出
		
		// 4.0
		message.setGetter(state);   // 接收者定义为状态
		
		try {
			
			// 调用线程管理类的 方法获取对应线程
			ClientConnectThread thread = 
					ClientConnectThreadManage.getThread(userId, state);
			
			// 向服务端发送 退出请求message
			MyObjectOutputStream oos = 
					new MyObjectOutputStream(thread.getSocket().getOutputStream());
			oos.writeObject(message);
			
			System.out.println(userId + "退出系统!");
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}




