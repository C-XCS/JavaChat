package qqClient.view;

import javax.swing.*;    

import qqClient.Service.UserClientService;

import java.awt.*;
import java.awt.event.*;


/**
 * @author sky
 * @version 3.0
 * 
 * 	客户端 登录界面
 * 		1. 启动在线用户窗口OnlineUserFrame [主窗口]
 * 		2. 启动 注册窗口
 */
public class LoginFrame extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	// 顶部logo栏
	private JLabel bq_North;
	
	// 中部栏
	private JTabbedPane choose;
	private JPanel jp_Center;
	
	// 中部左侧头像栏
	private JLabel bq_head;
	
	// 中部右侧 输入组件栏
	private JPanel jp_input;
	private JLabel label_user, label_pwd, label_empty;  // 提示
	private JTextField txt_user;             // 用户名输入
	private JPasswordField txt_pwd;          // 密码输入
	private JButton btn_sweep, btn_regist;   // 清除 与 注册 按钮
	
	// 底部登录栏
	private JButton btn_login;
	private JPanel  jp_South;

	private UserClientService userClientService = null;  // 用户登录注册类

	public LoginFrame() {
	/* 顶部logo栏 */
		// 插入顶部logo
		ImageIcon logo = new ImageIcon("image/QQlogo.png");
		logo.setImage(logo.getImage().getScaledInstance(516, 170,
				Image.SCALE_DEFAULT));
		bq_North = new JLabel(logo);   // 存放上方logo
		
				
	/* 底部登录栏 */
		jp_South = new JPanel();    // 存放下方组件
		btn_login = new JButton();         // 存放登录按钮的图片
		// 下方 登录按钮 设置图片
		ImageIcon ico_login = new ImageIcon("image/loginBtn.png");
		ico_login.setImage(ico_login.getImage().getScaledInstance(350, 45,
				Image.SCALE_DEFAULT));
		
		// 设置 登录按钮
		btn_login.setIcon(ico_login);
		btn_login.setBorderPainted(false);
		btn_login.setBorder(null);
		btn_login.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		jp_South.add(btn_login);
		
		
	/* 中部左侧 头像栏 */
		// 中间右侧 头像图片
		ImageIcon headIcon = new ImageIcon("image/headshot.png");
		headIcon.setImage(headIcon.getImage().getScaledInstance(120, 144,
				Image.SCALE_DEFAULT));
		bq_head = new JLabel(headIcon);
		
		label_empty = new JLabel("              ");
		
		
	/* 中部 输入栏 */	
		// 中间栏 包含左侧头像  右侧输入面板
		jp_input = new JPanel();
		jp_Center = new JPanel();
		jp_Center.add(bq_head);
		jp_Center.add(label_empty);
		jp_Center.add(jp_input);
		jp_input.setSize(200, 300);
		
		choose = new JTabbedPane();
		choose.add("用户登录", jp_Center);
		
		
	/* 定义中间右侧 输入栏组件 */
		label_user = new JLabel("用户名", JLabel.CENTER);
		label_user.setFont(new Font("黑体", Font.PLAIN, 16));
		label_pwd = new JLabel("密  码", JLabel.CENTER);
		label_pwd.setFont(new Font("黑体", Font.PLAIN, 16));
		txt_user = new JTextField();
		txt_user.setFont(new Font("黑体", Font.PLAIN, 14));
		txt_pwd = new JPasswordField();
		
		// 清除号码 和 注册账号按钮
		btn_sweep = new JButton("清除号码");
		btn_sweep.setForeground(Color.red);
		btn_regist = new JButton("注册账号");
		btn_regist.setPreferredSize(new Dimension(20,20));
		btn_regist.setForeground(Color.blue);
	
		// 输入栏 加入组件
		jp_input.setLayout(new GridLayout(3, 3));
		jp_input.add(label_user);
		jp_input.add(txt_user);
		jp_input.add(label_pwd);
		jp_input.add(txt_pwd);
		jp_input.add(btn_sweep);
		jp_input.add(btn_regist);
		
	
	/* 设置 整个页面 方位布局 */
		add(choose, BorderLayout.CENTER);
		add(bq_North, BorderLayout.NORTH);
		add(jp_South, BorderLayout.SOUTH);
		
		btn_login.addActionListener(this);   // 登录按钮 响应
		btn_sweep.addActionListener(this);   // 清除号码 响应
		btn_regist.addActionListener(this);  // 注册事件 响应
		
		// 设置窗口
		setVisible(true);
		setBounds(500, 150, 500, 460);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("客户端登陆");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		/*
		 * 如果点击了 "登录" 按钮 
		 * 	 1. 判断帐号或者密码是否为空, 密码与确认密码是否相同
		 * 	 2. 调用UserClientService类 的 checkUser()方法 将账号密码打包成message对象发送给 服务端
		 *   3. checkUser()接收 服务端返回的message, 判断消息类是否成功, 返回boolean
		 *   4. boolean为true, 仍调用该类的startThread()方法 启动线程, 将socket存入线程, 并将socket放入集合中
		 *   5. 启动聊天窗口chatFrame
		 */
		if (e.getSource() == btn_login) {
			String userId = txt_user.getText().trim();
			String password = new String(txt_pwd.getPassword()).trim();
			
			if ("".equals(userId) || userId == null) {
				JOptionPane.showMessageDialog(null, "请输入帐号！！");
				return;
			}
			if ("".equals(password) || password == null) {
				JOptionPane.showMessageDialog(null, "请输入密码！！");
				return;
			}
			
			/* 将账号和密码 发送到 服务端 验证
			 *    
			 *    通过qqClient.Service包下的UserClientService 客户端登录服务类
			 *    方法外 已创建好 对象, 
			 *    调用checkUser()方法 验证账号 密码 是否正确
			 *    同时通过该方法 连接服务器 并启动线程 存放于集合
			 */
			
			userClientService = new UserClientService();
			if(userClientService.checkUser(userId, password)) {
				
				// 弹窗登录成功
				this.dispose();
				JOptionPane.showMessageDialog(null, "登陆成功！");
				
				// 启动在线用户窗口
				new OnlineUserFrame(userId, userClientService);
			
			} else {
				
			}
			

			// 点击 "注册" 按钮, 进入注册界面
		} else if(e.getSource() == btn_regist) {

			this.setVisible(false);
			new RegisterFrame();
			
			
		} else if (e.getSource() == btn_sweep) {
			// 点击清除号码
			txt_pwd.setText("");
		}
	}
	

}






