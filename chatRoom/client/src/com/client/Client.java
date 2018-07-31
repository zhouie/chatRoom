package com.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends Thread{
	
	public String username;
	public Socket socket;
	public Chatframe chatframe;
	private DataInputStream dis;
	private DataOutputStream dos;
	public boolean flag_exit;
	private String chat_re;
	public int threadId;
	public static void main(String[] args) {
		
		Client client = new Client();
		StartFrame enterframe = new StartFrame(client);
		enterframe.setVisible(true);
	}

	//退出聊天界面
	public void exitLogin() {
		System.exit(0);	
	}

	//连接进入聊天室验证
	public String login(String username, String hostIp, String hostPost) {
		this.username = username;
		String message = null;
		try {
			socket = new Socket(hostIp,Integer.parseInt(hostPost));
		} catch (NumberFormatException e) {
			message = "端口号异常  只能是   1024<post<65535";
			return message;
		} catch (UnknownHostException e) {
			message = "服务器地址错误";
			return message;
		} catch (IOException e) {
			message = "与服务器连接异常";
			return message;
		}
		return "true";
	}
	
	//打开聊天界面
	public void showChatframe() {
		getDataInit();
		chatframe = new Chatframe(username,this);
		chatframe.setVisible(true);
		this.flag_exit = true;
		this.start();
	}
	
	//初始化数据
	private void getDataInit() {
		try {
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		//客户读取数据
		while (flag_exit) {
			try {
				chat_re = dis.readUTF();
				
			} catch (IOException e) {
				flag_exit = false;
				if (!chat_re.contains("serverexit")) {
					chat_re = null;
				}
			}
			if (chat_re!=null) {
				if (chat_re.contains("@clientThread")) {//如果信息中包含@clientThread 说明是第二次握手
					//切割消息内容
					int local = chat_re.indexOf("@clientThread");
					threadId = Integer.parseInt(chat_re.substring(0, local));
					try {
						dos.writeUTF(username + "@login" + threadId + "@login");//第三次握手时，将用户名信息发给服务器
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else {
					if (chat_re.contains("@userlist")) {
						chatframe.setDisUser(chat_re);
					}else {
						if (chat_re.contains("@chat")) {
							chatframe.setDisMess(chat_re);
						}else {
							if (chat_re.contains("@serverexit")) {
								chatframe.closeClient();
							}
						}
					}
				}
			}
		}
	}

	public void exitClient() {
		flag_exit = false;
		System.exit(0);
	}
	
	public void exitChat() {
		try {
			dos.writeUTF(username + "@exit" + threadId + "@exit");
			flag_exit = false;
			System.exit(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void tran(String mess) {//发送消息
		try {
			dos.writeUTF(username + "@chat" + threadId + "@chat" + mess + "@chat");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
