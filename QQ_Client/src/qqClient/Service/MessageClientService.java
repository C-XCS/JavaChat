package qqClient.Service;

import java.io.IOException;  
import java.util.Date;

import qqClient.Utils.MyObjectOutputStream;
import qqClient.view.ChatFrame;
import qqCommon.Message;
import qqCommon.MessageType;

/**
 * 
 * @author sky
 * @version 1.0
 * 
 *  客户端 聊天类
 *  	实现用户 私聊 / 群聊
 */

public class MessageClientService {
	
	private ChatFrame chatFrame;   // 聊天窗口类 println方法 
	
	// 构造器初始化
	public MessageClientService(ChatFrame chatFrame) {
		super();
		this.chatFrame = chatFrame;
	}


	/** 私聊方法
	 * 
	 *  该方法向服务端发送message对象, 有服务端发送给指定用户
	 * 
	 * @param sendId   发送者Id
	 * @param getterId 接收者Id
	 * @param content  发送内容
	 */
	public void sendMessageToOne(String senderId, String getterId, String content) {
		Message message = new Message();
		
		message.setMessType(MessageType.MESSAGE_COMM_MES);    // 设置信息类型 普通消息包
		message.setSender(senderId);
		message.setGetter(getterId);
		message.setContent(content);
		message.setSendTime(new Date().toString());    // 设置发送时间, 调用util包的方法
		
		println(senderId + "(我):\t\t   " + new Date().toString());
		println(content + "\n");
		
		
		// 调用线程管理类 方法 输入发送者id,聊天对象(状态)  获取线程
		ClientConnectThread thread =
				ClientConnectThreadManage.getThread(senderId, getterId);
		// 发送给客户端
		try {
			MyObjectOutputStream oos = 
					new MyObjectOutputStream(thread.getSocket().getOutputStream());
			oos.writeObject(message);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	/**  群聊方法
	 *  
	 *  与私聊不同: 没有接收者 消息类型不同
	 * 
	 * @param senderId  发送者
	 * @param content   发送内容
	 */
	public void sendMessageToAll(String senderId, String content) {
		Message message = new Message();
		
		message.setMessType(MessageType.MESSAGE_ToAll_MES);    // 设置信息类型 群聊
		message.setSender(senderId);
		message.setContent(content);
		message.setSendTime(new Date().toString());    // 设置发送时间
		
		println(senderId + "(我):\t\t   " + new Date().toString());
		println(content + "\n");
		
		// 调用线程管理类 方法 输入发送者id  获取线程
		ClientConnectThread thread =
				ClientConnectThreadManage.getThread(senderId, "群聊");
		// 发送给客户端
		try {
			MyObjectOutputStream oos = 
					new MyObjectOutputStream(thread.getSocket().getOutputStream());
			oos.writeObject(message);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	// 将内容输出到 chatFrame窗口面板
    public void println(String s) {
        if (s != null) {
           chatFrame.getTxt_Chat().setText(chatFrame.getTxt_Chat().getText() + s + "\n");
            System.out.println(s + "\n");
        }
    }
}




