package qqClient.view;

import javax.swing.*;  

import qqClient.Service.FileClinetService;
import qqClient.Service.MessageClientService;
import qqClient.Service.UserClientService;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * @author sky
 * @version 3.0
 * 
 * 	客户端 聊天界面窗口
 * 		1. 聊天
 * 		2. 文件传输
 */
public class ChatFrame extends JFrame {
    
	private static final long serialVersionUID = 1L;
	
    //消息区控件
    private JTextArea txt_Chat = new JTextArea();    // 聊天输出文本
    private JTextArea txt_Send = new JTextArea();    // 聊天入文本
    private JButton btn_Send = new JButton();        // 发送 消息按钮
    
    //发送文件控件
    private JTextField txt_SendFile = new JTextField();     // 文件路径 文本
    private JButton btn_SendFile = new JButton();           // 发送文件按钮
    private JButton btn_ChooseFile = new JButton();         // 文件选择按钮
    private  JProgressBar progressBar = new JProgressBar(); // 滚动条
    
    private String sendFileName = null;       // 发送文件 的文件名                
    private MessageClientService messageClientService = null;   // 聊天类
    private FileClinetService fileClinetService = null;         // 文件传输类

    public ChatFrame(String userId, String getterId) {
        System.out.println(Thread.currentThread().getName());
        
        // 初始化文件传输类
        this.fileClinetService = new FileClinetService();
        // 初始化 聊天类
        this.messageClientService = new MessageClientService(ChatFrame.this);
       
        // 初始化 启动线程
		UserClientService userClientService = new UserClientService();	
		userClientService.startThreadChat(userId, getterId, ChatFrame.this);
		
		
     /* 消息输出 */
        // 设置聊天输出面板 滚动条
		JScrollPane spChat = new JScrollPane();
		spChat.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		spChat.setViewportView(this.txt_Chat);
		spChat.setBounds(1, 1, 660, 350);
		this.add(spChat);
		this.txt_Chat.setEditable(false);    // 聊天输出显示面板  不能输入文本
		txt_Chat.setFont(new Font("黑体", Font.PLAIN, 16));
        
		
		txt_Chat.setText("\n----------------------------- 欢迎登录网络聊天室 ------------------------------\n");
		
		
	/* 消息输入 */
        txt_Send.setBounds(1, 350, 660, 100);      
        this.add(txt_Send);                   // 聊天输入面板
        
        
    /* 消息发送 */
        // 发送消息 按钮图片
		ImageIcon ico_login = new ImageIcon("image/sendBtn.png");
		ico_login.setImage(ico_login.getImage().getScaledInstance(140, 35,
				Image.SCALE_DEFAULT));	
		// 设置 发送消息按钮
		btn_Send.setIcon(ico_login);
		btn_Send.setBorderPainted(false);
		btn_Send.setBorder(null);
		btn_Send.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn_Send.setBounds(10, 460, 140, 35); // 添加 发送消息 按钮
        this.add(btn_Send);
        btn_Send.setFont(new Font("宋体", Font.BOLD, 16));
        
    /* 发送文件 */
        // 发送文件 内容框 
        txt_SendFile.setBounds(10, 520, 400, 32);
        this.add(txt_SendFile);

        // 文件选择按钮  调出文件选择器
        btn_ChooseFile.setBounds(410, 520, 100, 30);
        btn_ChooseFile.setText("选择文件");
        this.add(btn_ChooseFile);
        btn_ChooseFile.setFont(new Font("宋体", Font.BOLD, 14));

        // 发送文件 按钮
        btn_SendFile.setBounds(520, 520, 100, 30);
        btn_SendFile.setText("发送文件");
        this.add(btn_SendFile);
        btn_SendFile.setFont(new Font("宋体", Font.BOLD, 14));
        
        
    /* 进度条 */
        progressBar.setBounds(320, 520, 200, 30);
        this.add(progressBar);
        progressBar.setMaximum(100);
        progressBar.setValue(0);

        
    /* 窗口布局 */
        setBounds(500, 100, 675, 600);
        setVisible(true);
        setResizable(false);
        
        if(getterId.equals("群聊")) {
            setTitle(userId + "    的群聊聊天室窗口");
        } else {
			setTitle(userId + "  =>  " + getterId + "  的聊天窗口");
		}
        

        // 发送消息监听事件
        btn_Send.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                	 
                if(getterId.equals("群聊")) {

	                /* 调用聊天类messageClientService 
	                 * 		的sendMessageToAll()群聊方法  向服务端发送请求 
	                 */
	                messageClientService.sendMessageToAll(userId, txt_Send.getText());
	                txt_Send.setText("");   // 换行
	                
                } else {
					messageClientService.sendMessageToOne(userId, getterId, txt_Send.getText());
					txt_Send.setText("");
				}
			
            }
        });

        
        
		// 客户端退出
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				
				// 弹窗提示
				JOptionPane.showMessageDialog(null, "是否退出?", 
						"确定", JOptionPane.QUESTION_MESSAGE);
					/*	调用userClientService类 
					 * 		的 logout()方法向服务端发送退出提示  无异常退出
					*/
					userClientService.logout(userId, getterId);
					
					this.setVisible(false);  // 点击关闭后 隐藏
				
			}

			private void setVisible(boolean b) {}
		});
        
        
        
        // 选择文件 监听事件
        btn_ChooseFile.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                //文件选择器
                JFileChooser chooser = new JFileChooser();
                
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);//文件名和文件
                
                /* JFileChooser 文件选择器
                 * 参数: 
                 *     parent: 文件选取器对话框的父组件, 对话框将会尽量显示在靠近 parent 的中心; 如果传 null, 则显示在屏幕中心。
                 * 
                 * 返回值:
                 *     JFileChooser.CANCEL_OPTION: 点击了取消或关闭
                 *     JFileChooser.APPROVE_OPTION: 点击了确认或保存
                 *     JFileChooser.ERROR_OPTION: 出现错误
                 */
                // 在获取用户选择的文件之前，通常先验证返回值是否为 APPROVE_OPTION.
                int num = chooser.showOpenDialog(null);
                
                // 若选择了文件，则打印选择的文件路径
                if(num == JFileChooser.APPROVE_OPTION)
                {
                    File file = chooser.getSelectedFile();         // 获取文件
                    sendFileName = file.getName();                 // 保存文件名
                    txt_SendFile.setText(file.getAbsolutePath());  // 输出文件路径在txt_SendFile
                }
            }
        });
        
        
        // 发送文件 监听事件
        btn_SendFile.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                
               /*  调用文件传输类 fileClinetService
                * 		sendFileToAll() 群发文件方法
                */
                if(getterId.equals("群聊")) {

                    fileClinetService.sendFileToAll(userId, txt_SendFile.getText(), 
                    		sendFileName, ChatFrame.this);
                } else {
					
                	fileClinetService.sendFileToOne(userId, getterId, txt_SendFile.getText(), 
                			sendFileName, ChatFrame.this);
				}
                
                txt_SendFile.setText("");
                sendFileName = null;
            }
        	
		});

    }
    
    
/*   get  set 方法   */
	public JTextArea getTxt_Chat() {
		return txt_Chat;
	}

	public void setTxt_Chat(JTextArea txt_Chat) {
		this.txt_Chat = txt_Chat;
	}

	public JTextArea getTxt_Send() {
		return txt_Send;
	}

	public void setTxt_Send(JTextArea txt_Send) {
		this.txt_Send = txt_Send;
	}

	public JTextField getTxt_SendFile() {
		return txt_SendFile;
	}

	public void setTxt_SendFile(JTextField txt_SendFile) {
		this.txt_SendFile = txt_SendFile;
	}
	
	public static void main(String[] args) {
		new ChatFrame("123", "123");
	}
}


