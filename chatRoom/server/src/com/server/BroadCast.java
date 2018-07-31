package com.server;

import java.io.IOException;
/**
 * 该类用来不停向客户端广播数据
 * @author Administrator
 *
 */
public class BroadCast extends Thread{

	public boolean flag_exit;
	private ClientThread clientThread;
	private ServerThread serverThread;
	private String str;
	
	public BroadCast(ServerThread serverThread) {
		this.serverThread = serverThread;
	}
	@Override
	public void run() {
		boolean flag = true;
		while (flag_exit) {
			synchronized (serverThread.message) {//同步代码块，一个线程在操作，不容许另外的线程来操作
				if (serverThread.message.isEmpty()) {//先要判断容器中是否有数据
					continue;
				}else {
					str = serverThread.message.firstElement();//获取第一条信息
					serverThread.message.removeElement(str);//取出以后，就要删掉这条信息
					if (str.contains("@clientThread")) {//第二次握手 需要发的数据不是针对所有客户端，而是只发给那个与他建立了第一次握手的客户端
						flag = false;
					}
				}
			}
			synchronized (serverThread.clients) {
				for (int i = 0; i < serverThread.clients.size(); i++) {
					clientThread = serverThread.clients.elementAt(i);//遍历线程集合，得到每个用户对应的线程
					if (flag) {
						try {
							if (str.contains("@chat") || str.contains("@userlist") || str.contains("@serverexit")) {
								clientThread.dos.writeUTF(str);
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
					}else {
						String value = serverThread.users.get((int) clientThread.getId());
						System.out.println(value);
						if (value.contains("@login@")) {
							flag =true;
							try {
								clientThread.dos.writeUTF(str);
							} catch (IOException e) {
								
								e.printStackTrace();
							}
						}
					}
				}
			}
			if (str.contains("@serverexit")) {//这就表示服务器已关闭
				serverThread.users.clear();
				flag_exit = false;
			}
		}
	}
}
