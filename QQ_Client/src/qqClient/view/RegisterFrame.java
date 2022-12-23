package qqClient.view;

import java.awt.BorderLayout; 
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import qqClient.Service.UserClientService;


/**
 * 
 * @author sky
 * @version 2.0
 * 
 * 	用户 注册界面	
 * 		用户注册
 */
public class RegisterFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JLabel bq_North;                                 // 顶部logo  模块
	private JLabel label_user, label_pwd, label_confim_pwd;  // 用户名, 密码, 确认密码  提示
	private JButton btn_regist;                              // 注册按钮
	private JTextField txt_user;                             // 用户名 输入框
	private JPasswordField txt_pwd, txt_confim_pwd;          // 密码,确认密码输入框
	private JTabbedPane choose;                              // 蓝色边框
	private JPanel jp_Center, jp_South;                      

	
	public RegisterFrame() {
		
	/* 顶部logo栏 */
		// 插入顶部logo
		ImageIcon logo = new ImageIcon("image/QQlogo4.png");
		logo.setImage(logo.getImage().getScaledInstance(350, 285,
				Image.SCALE_DEFAULT));
		bq_North = new JLabel(logo);   // 存放上方logo
		
		
	/* 底部注册栏 */
		jp_South = new JPanel();    // 存放下方组件
		btn_regist = new JButton();         // 存放注册按钮的图片
		// 下方 注册按钮 设置图片
		ImageIcon ico_login = new ImageIcon("image/registBtn.png");
		ico_login.setImage(ico_login.getImage().getScaledInstance(220, 40,
				Image.SCALE_DEFAULT));	
		// 设置 注册按钮
		btn_regist.setIcon(ico_login);
		btn_regist.setBorderPainted(false);
		btn_regist.setBorder(null);
		btn_regist.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		jp_South.add(btn_regist);

		
     /* 中间 输入栏组件 */
		label_user = new JLabel("用户名", JLabel.CENTER);
		label_user.setFont(new Font("黑体", Font.PLAIN, 14));
		txt_user = new JTextField();
		
		label_pwd = new JLabel("密   码", JLabel.CENTER);
		label_pwd.setFont(new Font("黑体", Font.PLAIN, 14));
		txt_pwd = new JPasswordField();
		
		label_confim_pwd = new JLabel("确认密码", JLabel.CENTER);
		label_confim_pwd.setFont(new Font("黑体", Font.PLAIN, 14));
		txt_confim_pwd = new JPasswordField();
		
		jp_Center = new JPanel();
		choose = new JTabbedPane();
		choose.add("用户注册", jp_Center);
	
		// 添加组件
		jp_Center.setLayout(new GridLayout(7, 3));
		jp_Center.add(new JLabel());
		jp_Center.add(new JLabel());
		jp_Center.add(new JLabel());
		jp_Center.add(label_user);
		jp_Center.add(txt_user);
		jp_Center.add(new JLabel());
		jp_Center.add(new JLabel());
		jp_Center.add(new JLabel());
		jp_Center.add(new JLabel());
		jp_Center.add(label_pwd);
		jp_Center.add(txt_pwd);
		jp_Center.add(new JLabel());
		jp_Center.add(new JLabel());
		jp_Center.add(new JLabel());
		jp_Center.add(new JLabel());
		jp_Center.add(label_confim_pwd);
		jp_Center.add(txt_confim_pwd);
		jp_Center.add(new JLabel());
		jp_Center.add(new JLabel());
		jp_Center.add(new JLabel());
		jp_Center.add(new JLabel());
		
			
	/* 设置 整个页面 方位布局 */
		add(choose, BorderLayout.CENTER);
		add(bq_North, BorderLayout.NORTH);
		add(jp_South, BorderLayout.SOUTH);
		
		btn_regist.addActionListener(this);   // 登录按钮 响应
		
		// 设置窗口
		setVisible(true);
		setBounds(200, 50, 350, 600);
		setResizable(false);
		getContentPane().setBackground(Color.white);
		setTitle("客户端 用户注册");
	}
	
	
// 按键监听
	@Override
	public void actionPerformed(ActionEvent e) {
		/*
		 * 如果点击了 "注册" 按钮 
		 * 	 1. 判断帐号或者密码是否为空, 密码与确认密码是否相同
		 * 	 2. 调用UserClientService类 的 registUser()方法
		 */
		if (e.getSource() == btn_regist) {
			String userId = txt_user.getText().trim();
			String password = new String(txt_pwd.getPassword()).trim();
			String confimPwd = new String(txt_confim_pwd.getPassword()).trim();
			
			if ("".equals(userId) || userId == null) {
				JOptionPane.showMessageDialog(null, "请输入帐号 ！！");
				return;
			}
			if ("".equals(password) || password == null) {
				JOptionPane.showMessageDialog(null, "请输入密码 ！！");
				return;
			}
			if ("".equals(confimPwd) || confimPwd == null) {
				JOptionPane.showMessageDialog(null, "请再次确认密码 ！！");
				return;
			}
			if(password.length() < 6 || password.length() > 20) {
				JOptionPane.showMessageDialog(this, "密码长度小于6或大于20  请重新输入 ! !");
				return;
			}
			if(!confimPwd.equals(password)) {
				JOptionPane.showMessageDialog(null, "密码不一致   请重新输入 ! !");
				return;
			}
			
			/* 	初始化 UserClientService类对象
			 * 		registUser()方法 注册
			 */
			UserClientService userClientService = new UserClientService();
			if(userClientService.registUser(userId, password)) {
				
				JOptionPane.showMessageDialog(this, "注册成功");
				this.setVisible(false);
				new LoginFrame();
				
			} else {
				JOptionPane.showMessageDialog(this, "账号已存在, 请重新输入!");
			}


		}
	}
	
}




