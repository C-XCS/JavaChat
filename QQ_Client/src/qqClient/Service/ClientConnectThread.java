package qqClient.Service;

import java.io.FileNotFoundException;  
import java.io.FileOutputStream; 
import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;

import qqClient.Utils.MyObjectInputStream;
import qqClient.view.ChatFrame;
import qqClient.view.OnlineUserFrame;
import qqCommon.Message;
import qqCommon.MessageType;

/**
 * 
 * @author sky
 * @version 1.0
 *	
 *	客户端 线程类
 *		1. 接收在线用户列表
 *		2. 接收私聊内容
 *  	3. 接收群聊内容
 *  	4. 接收文件
 *  	5. 接收本线程退出提醒 -- 结束本线程!!
 */
public class ClientConnectThread extends Thread{

	// 连接线程需要socket
	private Socket socket;
	
	private ChatFrame chatFrame;
	private OnlineUserFrame onlineUserFrame = null;
	
	
	// 构造器接收Socket
	public ClientConnectThread(Socket socket, ChatFrame chatFrame) {
		super();
		this.socket = socket;
		this.chatFrame = chatFrame;
	}

	// 构造器接收  在线用户面板 对象
	public ClientConnectThread(Socket socket, OnlineUserFrame onlineUserFrame) {
		super();
		this.socket = socket;
		this.onlineUserFrame = onlineUserFrame;
	}
	
	// 通过判断message的不同 进行不同操作
	@Override
	public void run() {
	
		// 由于 客户端需要 不断与服务端 保持通信, 使用while循环
		while(true) {
			try {

				MyObjectInputStream ois = new MyObjectInputStream(socket.getInputStream());
				// 若服务端没有发送Messsage 对象, 程序会阻塞(暂停)
				Message message = (Message)ois.readObject();
				
				
				// 判断messageType类型进行不同操作
				if(message.getMessType().equals(MessageType.MESSAGE_RET_ONLINE_FRIEND)) {
				// 类型一. 服务端 返回在线用户列表
					
					/* 4.0 
					 *   接收服务端传来的 在线用户 字符串
					 *   用 "\n  " 分割, 直接输出即可
					 */
					onlineUserPrintln("拉取时间\n" + message.getSendTime() 
						+ "\n======= 在线用户 =======\n  群聊\n" + message.getContent() 
							+ "\n请在下方框内输入用户名 进入聊天\n<注意: 首尾不要有空格!!>");
					
				}  else if (message.getMessType().equals(MessageType.MESSAGE_COMM_MES)) {
				// 类型二. 客户端 接收私聊 内容(客户端1-->服务端-->本客户端)    普通消息包
					
					// 直接打印输出
					println(message.getSender() + ":\t\t\t" + message.getSendTime());
					println(message.getContent() + "\n");
					
				}  else if (message.getMessType().equals(MessageType.MESSAGE_ToAll_MES)) {
				// 类型三. 客户端 接收群聊内容   
					
					println(message.getSender() + ":\t\t\t" + message.getSendTime());
					println(message.getContent() + "\n");
					
				} else if (message.getMessType().equals(MessageType.MESSAGE_File_MES)) {
				// 类型四. 客户端文件传输   接收文件(客户端1-->服务端-->本客户端)
					 
					println(message.getSender() + "\t发送文件:\t\t" + message.getSendTime());
					println("   " + message.getFileName() + "\n");			
					
					// 弹窗提示输入 保存文件 的路径
					// 调用本类定义方法       SaveFileAddress(使用者, 发送者,发送的文件名)
					String srcPsth = saveFileAddress(message.getGetter(), message.getSender(),
							message.getFileName());
					
					if(srcPsth != null) {
						// 取出message 字节数组  输出流写入磁盘
						FileOutputStream fis = new FileOutputStream(srcPsth);
						try {
							fis.write(message.getFileBytes());
							fis.close();
							
						// 路径错误, 抛出异常并 弹窗提示
						} catch (FileNotFoundException e) {
							// TODO: handle exception
							JOptionPane.showMessageDialog(null, "保存失败 !!", message.getGetter() + " 的弹出提示", JOptionPane.INFORMATION_MESSAGE);
						}
						// 弹窗提示
						JOptionPane.showMessageDialog(null, "文件已保存",  message.getGetter() + " 的弹出提示", JOptionPane.INFORMATION_MESSAGE);
					
					}
					
					
				}  else if (message.getMessType().equals(MessageType.MESSAGE_CLIENT_EXIT)) {
				// 类型五. 接收服务端返回 本端发送的 退出系统message,  关闭socket  break退出线程
					
					// 该线程的socket 即为 将退出客户端socket, 直接关闭
					socket.close();   
					
					// 退出while循环, run方法将结束, 该线程结束
					break;   
					
				} else {
					// 暂时不做处理
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



	/**  弹窗提示用户输入 保存文件地址
	 * 
	 * @param userId   使用者
	 * @param sender   发送者
	 * @param fileName 发送过来的文件名
	 * @return srcPath 文件保存的路径
	 */
	public String saveFileAddress(String userId, String sender, String fileName) {
		
		String srcPath = JOptionPane.showInputDialog(null, 
				sender + "发送文件: " + fileName + "\n请输入保存文件的路径:\n\t形式: C:\\xxx.txt", 
				userId + "  的弹窗输入", JOptionPane.INFORMATION_MESSAGE);

		return srcPath;
	}
	
	
	
    public void println(String s) {
        if (s != null) {
            chatFrame.getTxt_Chat().setText(chatFrame.getTxt_Chat().getText() + s + "\n");
            System.out.println(s + "\n");
        }
    }
	
    public void onlineUserPrintln(String s) {
        if (s != null) {
            onlineUserFrame.getTaShow().setText(s + "\n");
            System.out.println(s + "\n");
         }
     }

	/* set get方法 */
	public Socket getSocket() {
		return socket;
	}

	public ChatFrame getChatFrame() {
		return chatFrame;
	}

	public void setChatFrame(ChatFrame chatFrame) {
		this.chatFrame = chatFrame;
	}
	
}
