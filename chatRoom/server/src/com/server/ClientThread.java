package com.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientThread extends Thread{

	public boolean flag_exit;
	public Socket clientSocket;
	public ServerThread serverThread;
	public DataInputStream dis;//用来从客户端读取数据
	public DataOutputStream dos;//用来向客户端发送数据
	public ClientThread(ServerThread serverThread, Socket socket) {
		this.serverThread = serverThread;
		this.clientSocket = socket;
		try {
			dis = new DataInputStream(clientSocket.getInputStream());
			dos = new DataOutputStream(clientSocket.getOutputStream());
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	//该方法会不断接收数据 并保存在容器中
	@Override
	public void run() {
		while(flag_exit){
			try {
				String message = dis.readUTF();
				
				if (message.contains("@login")) {
					String[] userInfo = message.split("@login");//进行字符串切割成字符数组
					//获取现成ID、
					int userID = Integer.parseInt(userInfo[1]);
					serverThread.users.remove(userID);
					//判断集合中是否存在该用户名
					if (serverThread.users.containsValue(userInfo[0])) {
						//遍历线程集合得到每个线程ID
						for (int i = 0; i < serverThread.clients.size(); i++) {
							int threadID = (int) serverThread.clients.get(i).getId();
							//判断哪个线程ID对应的用户名和userInfo[0] 一样
							if (serverThread.users.get(threadID).equals(userInfo[0])) {
								//删除该线程的信息
								serverThread.users.remove(threadID);
								//重新添加用户信息
								serverThread.users.put(threadID, userInfo[0] + "_" + threadID);
								break;
							}
						}
						//添加新的用户信息
						serverThread.users.put(userID, userInfo[0] + "_" + userID);
					}else {
						serverThread.users.put(userID, userInfo[0]);
					}
					//将在线用户显示在服务器界面右边
					message = null;
					StringBuffer sb = new StringBuffer();
					synchronized (serverThread.clients) {
						//遍历集合得到每一个线程ID ,将线程ID 对应的用户名添加到StringBuffer 中
						for (int i = 0; i < serverThread.clients.size(); i++) {
							int id = (int) serverThread.clients.elementAt(i).getId();
							//得到线程对应的用户名
							String username = serverThread.users.get(new Integer(id));
							sb.append(username + "@userlist"+id + "@userlist");//这是
						}
					}
					message = new String(sb);
					serverThread.serverFrame.setDisUser(message);//将用户显示在服务器界面
				}else {
					if (message.contains("@exit")) {//表示客户端推出聊天室
						String[] userInfo = message.split("@exit");
						int userID = Integer.parseInt(userInfo[1]);
						serverThread.users.remove(userID);
						
						message = null;
						StringBuffer sb = new StringBuffer();
						synchronized (serverThread.clients) {
							for (int i = 0; i < serverThread.clients.size(); i++) {
								//获取每个线程id
								int threadID = (int) serverThread.clients.elementAt(i).getId();
								
								if (userID == threadID) {
									serverThread.clients.removeElementAt(i);
									i--;
								}else {
									sb.append(serverThread.users.get(new Integer(threadID)) + "@userlist");
									sb.append(threadID + "@userlist");
								}
							}
						}
						
						message = new String(sb);
						if (message.equals("")) {
							serverThread.serverFrame.setDisUser("@userlist");
						}else {
							serverThread.serverFrame.setDisUser(message);
						}
						this.clientSocket.close();
					}else {
						//将聊天信息显示在服务器界面左边
						if (message.contains("@chat")) {
							String[] chat = message.split("@chat");
							StringBuffer sb = new StringBuffer();
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String date = sdf.format(new Date());
							sb.append(chat[0] + "  " + date + "\n");
							sb.append(chat[2] + "@chat");
							//将消息显示到服务器界面
							message = new String(sb);
							serverThread.serverFrame.setDisMess(message);
						}
					}
				}
				//将信息保存到容器中,如果信息为空，不用保存！
				synchronized (serverThread.message) {//同一时刻，只容许一个用户进行保存到容器
					if (message != null) {
						serverThread.message.add(message);
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
}
