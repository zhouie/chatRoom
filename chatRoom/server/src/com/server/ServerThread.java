package com.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ServerThread extends Thread{

	public ServerFrame serverFrame;
	
	public boolean flag_exit = false;
	public Vector<String> message;//保存服务器需要发送给客户端的信息
	public Vector<ClientThread> clients;//保存服务器与每个客户端的连接
	public Map<Integer,String> users;//保存每个客户端的线程Id 和 用户名
	public ServerSocket serverSocket;
	public BroadCast broadCast;
	
	/**
	 * 服务器线程类，用来和每个客户建立连接
	 * @param serverFrame
	 */
	public ServerThread(ServerFrame serverFrame) {
		this.serverFrame = serverFrame;
		message = new Vector<String>();
		clients = new Vector<ClientThread>();
		users = new HashMap<Integer,String>();
		try {
			serverSocket = new ServerSocket(5000);
		} catch (IOException e) {
			this.serverFrame.setStartAndStopUnable();//不能同时开启两个服务器
			System.exit(0);
		}
		//开启广播线程(服务端向客户端发送信息)
		broadCast = new BroadCast(this);
		broadCast.flag_exit = true;
		broadCast.start();
	}
	@Override
	public void run() {
		Socket socket;
		while(flag_exit){
			if (serverSocket.isClosed()) {
				flag_exit = false;
			}else {
				try {
					socket = serverSocket.accept();
				} catch (IOException e) {
					socket = null;
					flag_exit = false;
				}
				if (socket != null) {
					//从客户端接受信息
					ClientThread clientThread = new ClientThread(this,socket);
					clientThread.flag_exit = true;
					clientThread.start();	//开启线程
					
					synchronized (clients) {	//保存每个用户对应的clientThread对象
						clients.addElement(clientThread);
					}
					
					synchronized (message) {	//为了完成第二次握手，服务端向客户端发送的数据信息
						users.put((int)clientThread.getId(), "@login@");	//获取线程id
						message.add(clientThread.getId()+"@clientThread");	
					}
				}
			}
		}
	}

	public void stopServer() {
		try {
			if (this.isAlive()) {
				serverSocket.close();
				flag_exit = false;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
