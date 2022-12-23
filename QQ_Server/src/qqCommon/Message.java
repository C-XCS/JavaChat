package qqCommon;

import java.io.Serializable;

/**
 * 
 * @author sky
 *	
 *	表示 客户端和服务端 通信时的消息对象
 */
public class Message implements Serializable{
	// 增强兼容性
	private static final long serialVersionUID = 1L;
	
	private String sender; // 发送者
	private String getter; // 接收者 
	private String content;// 发送内容 
	private String sendTime;// 发送时间
	private String messType;// 消息类型 (可在接口定义接口类型)
	// 4.0 新增 用于私聊 一个用户多个线程
	private String state;   // 用户状态(聊天的对象: 在线 群聊 某个人)
	
	/*  扩展
	 *  和文件相关的 属性
	 */
	private byte[] fileBytes;
	private int fileLen = 0;
	private String fileName;
	
	private String destPath;   // 文件传输存入的路径
	private String srcPath;    // 源文件路径
	
	
	/* get set */
	public String getSender() {
		return sender;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public byte[] getFileBytes() {
		return fileBytes;
	}
	public void setFileBytes(byte[] fileBytes) {
		this.fileBytes = fileBytes;
	}
	public int getFileLen() {
		return fileLen;
	}
	public void setFileLen(int fileLen) {
		this.fileLen = fileLen;
	}
	public String getDestPath() {
		return destPath;
	}
	public void setDestPath(String destPath) {
		this.destPath = destPath;
	}
	public String getSrcPath() {
		return srcPath;
	}
	public void setSrcPath(String srcPath) {
		this.srcPath = srcPath;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getGetter() {
		return getter;
	}
	public void setGetter(String getter) {
		this.getter = getter;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSendTime() {
		return sendTime;
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	public String getMessType() {
		return messType;
	}
	public void setMessType(String messType) {
		this.messType = messType;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
