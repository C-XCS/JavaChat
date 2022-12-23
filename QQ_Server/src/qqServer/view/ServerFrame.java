package qqServer.view;

import java.awt.BorderLayout; 
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import qqServer.server.QQServer;
import qqServer.server.SendToAllService;

/**
 * @author sky
 * @version 2.0
 *
 * 	服务端界面
 *  功能: 1. 按键启动服务端
 *		  2. 显示 与客户端的信息
 *		  3. 向客户端推送消息
 */
public class ServerFrame extends JFrame{

	private static final long serialVersionUID = 1L;
	private JButton btn_Start;      // 启动服务器
	private JButton btn_Send;       // 发送信息按钮
	private JTextField txt_Send;    // 需要发送的文本信息
	private JTextArea taShow;       // 信息展示
	public QQServer server;        // 用来监听客户端连接
	
	private SendToAllService toAllService = null;   // 服务端推送新闻 线程类 

	public ServerFrame() {
		super("服务器端");
		btn_Start = new JButton("启动服务器");
		btn_Start.setForeground(new Color(200, 0, 0));
		btn_Send = new JButton("推送消息");
		
		txt_Send = new JTextField(10);
		taShow = new JTextArea();
		taShow.setFont(new Font("宋体", Font.PLAIN, 16));
		
		// 流式添加组件
		JPanel top = new JPanel(new FlowLayout());
		top.add(txt_Send);
		top.add(btn_Send);
		top.add(btn_Start);
		this.add(top, BorderLayout.SOUTH);
		
		// 文本域边框 滑动
		final JScrollPane sp = new JScrollPane();
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sp.setViewportView(this.taShow);
		this.taShow.setEditable(false);
		this.add(sp, BorderLayout.CENTER);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(400, 300);
		this.setLocation(100, 200);
		this.setVisible(true);
		
		
	/* 监听事件 */
		
		// 点击 "启动服务端" 按钮, 初始化QQServer
		btn_Start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				/*
				 *  新建线程, 在其中启动客户端(线程socket)
				 *  防止因为死循环 导致界面卡死
				 */
				new Thread() {
					@Override
					public void run() {
						// 点击 启动服务 时, 初始化QQServer 启动服务端socket 和线程
						server = new QQServer(ServerFrame.this);
							
					}
				}.start();
				
			}
		});
		
		// 向 客户端推送消息
		btn_Send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// 初始化群发消息类
				toAllService = new SendToAllService(ServerFrame.this);
				
				// 调用新闻线 pushNews方法 群发消息
				toAllService.pushNews(txt_Send.getText());
				txt_Send.setText("");
			}
		});
	
		// 客户端退出
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// 弹窗提示
				int a = JOptionPane.showConfirmDialog(null, "确定关闭吗？", "温馨提示",
						JOptionPane.YES_NO_OPTION);
				if (a == 1) {
					
					System.exit(0); // 关闭
				}
			}
		});
	}
	
//	// 运行测试
//	public static void main(String[] args) {
//		new ServerFrame();
//	}
	
    public void println(String s) {
        if (s != null) {
            this.taShow.setText(this.taShow.getText() + s + "\n");
            System.out.println(s + "\n");
        }
    }

  /* get set */
	public JTextArea getTaShow() {
		return taShow;
	}

	public void setTaShow(JTextArea taShow) {
		this.taShow = taShow;
	}

	public JTextField getTxt_Send() {
		return txt_Send;
	}

	public void setTxt_Send(JTextField txt_Send) {
		this.txt_Send = txt_Send;
	}
    
    
}
