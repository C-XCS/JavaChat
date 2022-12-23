package qqCommon;

/**
 * 
 * @author sky
 *	
 *	表示消息类型 的接口
 */

public interface MessageType {
	/*
	 *  在接口中定义常量
	 *  不同常量的值表示不同的消息类型
	 */
	String MESSAGE_LOGIN_SUCCEED = "1";      // 表示登录成功
	String MESSAGE_LOGIN_FAIL = "2";         // 表示登录失败
	String MESSAGE_COMM_MES = "3";           // 普通消息包(私聊)
	String MESSAGE_GET_ONLINE_FRIEND = "4";  // 客户端请求返回在线消息列表
	String MESSAGE_RET_ONLINE_FRIEND = "5";  // 服务端 返回在线用户列表
	String MESSAGE_CLIENT_EXIT = "6";        // 客户端请求退出
	String MESSAGE_ToAll_MES = "7";          // 客户端取发消息
	String MESSAGE_File_MES = "8";           // 客户端 发送文件
	String MESSAGE_REGIST_REQUEST = "9";      // 客户端 请求注册账户
	String MESSAGE_REGIST_SUCCEED = "10";    // 表示注册成功
	String MESSAGE_REGIST_FAIL = "11";       // 表示注册失败
	String MESSAGE_File_MES_TOALL = "12";    // 客户端 群发文件
}

