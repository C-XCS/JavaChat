package qqClient.view;

import java.awt.BorderLayout;  
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import qqClient.Service.UserClientService;


/**
 * @author sky
 * @version 4.0
 * 
 * 	客户端 在线用户列表 界面  [主界面]
 * 		1. 拉取在线用户
 * 		2. 启动聊天窗口(私聊/群聊)
 */
public class OnlineUserFrame extends JFrame{
 
	private static final long serialVersionUID = 1L;
	private JButton btn_gain, btn_startChat;
	private JTextArea taShow;
	private JTextField txt_inputUser;
	private JLabel label_user;
	
	private UserClientService userClientService;
	
	public OnlineUserFrame(String userId, UserClientService userClientServices) {
		
		this.userClientService = userClientServices;
		
		/*  调用UserClientService类 starThread方法
		 * 	传入聊天界面, 在该方法中启动线程  
		 */
		userClientService.startThread(userId, OnlineUserFrame.this);
		
		// 拉取用户模块
		btn_gain = new JButton("拉取在线用户");
		label_user = new JLabel("在线用户列表    ");
		label_user.setFont(new Font("黑体", Font.PLAIN, 16));
		label_user.setForeground(new Color(250, 0, 0));
		
		// 启动聊天界面
		btn_startChat = new JButton("进入聊天");
		txt_inputUser = new JTextField(10);
		
		// 显示面板
		taShow = new JTextArea();
		taShow.setFont(new Font("黑体", Font.PLAIN, 16));
		taShow.setText("\n先点击 '拉取在线用户' 按钮 \n获取用户列表\n");
		
		
		JPanel top = new JPanel(new FlowLayout());
		top.add(label_user);
		top.add(btn_gain);
		this.add(top, BorderLayout.NORTH);
		
		// 流式添加组件
		JPanel buttom = new JPanel(new FlowLayout());
		buttom.add(txt_inputUser, FlowLayout.LEFT);
		buttom.add(btn_startChat);
		this.add(buttom, BorderLayout.SOUTH);
		
		// 文本域边框 滑动
		final JScrollPane sp = new JScrollPane();
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sp.setViewportView(this.taShow);
		this.taShow.setEditable(false);
		this.add(sp, BorderLayout.CENTER);
		
		this.setTitle(userId + " 聊天用户窗口");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(300, 500);
		this.setLocation(100, 200);
		this.setVisible(true);
		this.setResizable(false);
		
		// 拉取用户 按钮响应
		btn_gain.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				/*	调用 登陆注册类的 拉取在线用户方法
				 */
				userClientService.onlineFriendList(userId);
			}
		});
		
		// 开启聊天响应
		btn_startChat.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 若 启动输入框 不为空
				if(txt_inputUser != null) {
					/*  再new 一个UserClientService对象
					 * 		启动线程, 开启聊天窗口 chatFrame
					 */
					new ChatFrame(userId, txt_inputUser.getText());
				
					
					// 启动输入框 及 显示面板 置空
					txt_inputUser.setText(null);
					taShow.setText(null);
					 
				}
				
			}
		});
		
		// 客户端 退出
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				
				// 弹窗提示
				JOptionPane.showMessageDialog(null, "是否退出?", 
						"确定", JOptionPane.QUESTION_MESSAGE);
					/*	调用userClientService类 
					 * 		的 logout()方法向服务端发送退出提示  无异常退出
					*/
				userClientService.logout(userId, "在线");
				System.exit(0); // 关闭
				
			}
		});
		
		
	}
/* get set */
	public JTextArea getTaShow() {
		return taShow;
	}

	public void setTaShow(JTextArea taShow) {
		this.taShow = taShow;
	}

	public JTextField getTxt_inputUser() {
		return txt_inputUser;
	}

	public void setTxt_inputUser(JTextField txt_inputUser) {
		this.txt_inputUser = txt_inputUser;
	}

	
//	public static void main(String[] args) {
//		new OnlineUserFrame("123" ,new UserClientService());
//	}
}
