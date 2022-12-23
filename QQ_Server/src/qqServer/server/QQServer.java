package qqServer.server;

import java.io.IOException;  
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import qqCommon.Message;
import qqCommon.MessageType;
import qqCommon.User;
import qqServer.Utils.MyObjectInputStream;
import qqServer.Utils.MyObjectOutputStream;
import qqServer.view.ServerFrame;

/**
 * 
 * @author sky
 * @version 1.0
 * 
 * 	服务器 连接服务类
 * 		1. 启动服务端, 连接客户端socket
 * 		2. 存储用户数据 集合
 * 		3. 验证用户登录
 * 		4. 用户注册
 * 		5. 用户启动登录窗口
 * 		6. 启动线程维护socket, 加入集合
 */
public class QQServer {

	private ServerSocket server = null;
	private ServerFrame serverFrame = null;    // 接收界面对象 构造器初始化  定义println方法,可将输出语句打印在界面上
	
	/*
	 *  使用集合 HashMap集合 模拟数据库
	 *  		存放多个用户
	 *  		<用户id, User>
	 */
	private static ConcurrentHashMap<String, User> validUsers = new ConcurrentHashMap<>();	
	
	// 使用静态代码块  初始化 validUsers
	static {
		validUsers.put("100", new User("100", "123456"));
		validUsers.put("101", new User("101", "123456"));
		validUsers.put("102", new User("102", "123456"));
		validUsers.put("卢俊义", new User("卢俊义", "123456"));
		validUsers.put("林冲", new User("林冲", "123456"));
		validUsers.put("宋江", new User("宋江", "123456"));
		validUsers.put("鲁智深", new User("鲁智深", "123456"));
		validUsers.put("武松", new User("武松", "123456"));
	}
		
	/**	构造器监听socket 接收User 
	 * 
	 * @param serverFrame
	 */
	public QQServer(ServerFrame serverFrame) {
		
		// 初始化 界面对象
		this.serverFrame = serverFrame;
		
		
		// 1. 在构造器启动 服务端Socket
		try {
			println("服务器在9999端口监听~");
			server = new ServerSocket(9999);
			
			
		// 2. 循环监听, 可监听多个客户端socket
		// 	当和某个客户端建立连接后, 也会继续监听, 等待下一个连接
		while(true) {
			
			// 若没有客户端连接, 阻塞
			Socket socket = server.accept();   
			
			// 3. 建立连接后, 接收客户端传来的User对象
			MyObjectInputStream ois = 
					new MyObjectInputStream(socket.getInputStream());
			User user = (User)ois.readObject();   
			
			
			// 4.准备返回给客户端判断结果
			MyObjectOutputStream oos = 
					new MyObjectOutputStream(socket.getOutputStream());
			Message message = new Message();
			
			
			// 5.若 消息类型为 MESSAGE_REGIST_REQUEST 客户端请求注册
			if(user.getRegistMessageType() != null && user.getRegistMessageType().equals(MessageType.MESSAGE_REGIST_REQUEST) )  {
				
				if(registUser(user.getUserId(), user)) {   // 账号未被注册, 且将新账号加入集合
					
					println("用户 " + user.getUserId() + " 注册成功");
					
					// 给messageType消息类型设置 注册成功 
					message.setMessType(MessageType.MESSAGE_REGIST_SUCCEED);
					oos.writeObject(message);   // 发送
					
				} else {
					// 账号已存在
					println("用户 " + user.getUserId() + " 已存在, 注册失败");
					
					// 给messageType消息类型设置 注册失败
					message.setMessType(qqCommon.MessageType.MESSAGE_REGIST_FAIL);
					oos.writeObject(message);   // 发送
				}
				user = null;
				// 注册结束都要 关闭, 不不论成功与否
				socket.close();
				
				
			// 客户端 启动聊天窗口
			} else if (user.getState() != null) {
				
				// 创建线程 维护
				ServerConnectThread thread = 
						new ServerConnectThread(socket, user.getUserId(), serverFrame);
				thread.start();
				
				// 加入集合                                用户名             状态             线程
				new ServerConnectThreadManage().addThread(user.getUserId(), user.getState(), thread);		
				
				if(user.getState() == "群聊") {
					println(user.getUserId() + " 创建群聊窗口");
				} else {
					println(user.getUserId() + " 创建与 " + user.getState() + " 的聊天窗口");
				}
				
			// 客户端请求登录
			} else {
				// 验证 userId与密码  调用本类的checkUser()方法 
				if( checkUser(user.getUserId(), user.getPassword()) ) {
					// 验证通过
					//   消息方式定义为:    MESSAGE_LOGIN_SUCCEED 登录成功
					message.setMessType(qqCommon.MessageType.MESSAGE_LOGIN_SUCCEED);
					oos.writeObject(message);   // 返回
					
				
					// 创建一个 线程, 维护该socket对象  
					ServerConnectThread thread = 
							new ServerConnectThread(socket, user.getUserId(), serverFrame);
					thread.start();  // 启动
					
					// 将该线程 对象放入集合中 进行管理
					/*
					 * 	调用ServerConnectClientThreadManage 线程管理类类的  
					 * 	addThread方法 传入线程 和其对应的userId, 状态为"在线"
					 */
					 new ServerConnectThreadManage().addThread(user.getUserId(), "在线", thread);
					
					
				} else {  // 登录失败
					println("用户 " + user.getUserId() + " 登录验证失败" );
					
					// 给messageType消息类型设置 登录失败
					message.setMessType(qqCommon.MessageType.MESSAGE_LOGIN_FAIL);
					oos.writeObject(message);   // 发送
					
					// 关闭
					socket.close();
				}
			}
					
		}
		
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}  catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// 当服务端退出时, 即while循环结束
			// 关闭 ServerSocket对象
			try {
				server.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	/**  登录验证方法:
	 *   验证 用户id是否 存在于集合的方法
	 * 
	 * @param userId   用户名
	 * @param password 密码
	 * @return boolean true为验证通过
	 */
	private boolean checkUser(String userId, String password) {
		// 通过key键 userId 取出 user对象
		User user = validUsers.get(userId);
		
		// 若集合中 没有传入的userId, 则取出的user为空
		if(user == null) {
			return false;
		}
		// 传入的密码错误
		if( ! (user.getPassword().equals(password))) {
			return false;
		}
		
		return true;
	}
	
	
	/**  注册方法:
	 * 	1. 验证账号是否已存在, 存在返回fail   
	 *  2. 不存在返回true 并将user对象加入 ConcurrentHashMap
	 * 
	 * @param userId   用户名
	 * @param user     对应user
	 * @return boolean true注册成功, false用户已存在
	 */
	private boolean registUser(String userId, User user) {
		// 获取user对象
		User userVerify = validUsers.get(userId);
		// 账号已注册 返回失败
		if(userVerify != null) {
			return false;
		} else {
		
			// 未被注册, 将用户加入
			validUsers.put(userId, user);
			return true;   
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



