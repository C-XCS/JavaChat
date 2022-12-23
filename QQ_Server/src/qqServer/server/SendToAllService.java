package qqServer.server;

import java.io.IOException;  
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JOptionPane;

import qqCommon.Message;
import qqCommon.MessageType;
import qqServer.Utils.MyObjectOutputStream;
import qqServer.view.ServerFrame;

/**
 * 
 * @author sky
 * @version 2.0
 * 
 * 	服务端 推送(群发)消息类
 * 		群发消息给客户端 群聊窗口
 */

public class SendToAllService {

	private ServerFrame serverFrame = null;   // 界面
	
	public SendToAllService(ServerFrame serverFrame) {
		this.serverFrame = serverFrame;
	}

		
	/*
	 * 	服务端 向用户推送消息 方法
	 * 	在ServerFrame 调用, 当点击"发送" 按钮时调用
	 */
	public void pushNews(String news) {
		
		// 1.构建一个message 群发推送消息
		Message message = new Message();
		message.setSender("\n===== 服务器 温馨提示 =====");
		message.setContent(news + "\n\n=========================\n");
		
		// 设置类型为 群发消息(同客户端的群聊)   就不用再设置 客户端 队接收代码
		message.setMessType(MessageType.MESSAGE_ToAll_MES);     
		message.setSendTime(new Date().toString());   // 发送时间
		
		println("服务器给所有人 推送消息: " + news);
		
		// 2.直接在此 循环所有通信线程   得到socket,推送消息
		// 调用线程管理类 的方法得到 集合
		HashMap<String, HashMap<String, ServerConnectThread>> map 
				= ServerConnectThreadManage.getMap();
		
		// 遍历集合 推送消息
		Iterator<String> iterator = map.keySet().iterator();
	
		while(iterator.hasNext()) {
			// 在线用户Id  
			String onlineUserId = iterator.next().toString();
			
			try {
				// get方法 状态为"群聊" 返回线程  
				ServerConnectThread thread = map.get(onlineUserId).get("群聊");
				
				if(thread != null) {
					MyObjectOutputStream oos =         
							new MyObjectOutputStream(thread.getSocket().getOutputStream());
					
					message.setGetter(onlineUserId);
					oos.writeObject(message);
				} else {
					JOptionPane.showMessageDialog(null, "推送失败 !!");
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		serverFrame.getTxt_Send().setText("");
	}
	
	
	// println语句 确保语句能输出到界面
    public void println(String s) {
        if (s != null) {
           serverFrame.getTaShow().setText(serverFrame.getTaShow().getText() + s + "\n");
            System.out.println(s + "\n");
        }
    }
}



