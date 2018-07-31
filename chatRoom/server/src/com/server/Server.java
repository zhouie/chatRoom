package com.server;

public class Server {

	private ServerFrame serverFrame;
	private ServerThread serverThread;
	
	public static void main(String[] args) {
		
		Server server = new Server();
		ServerFrame serverFrame = new ServerFrame(server);
		server.setServerFrame(serverFrame);
		serverFrame.setVisible(true);
	}

	public ServerFrame getServerFrame() {
		return serverFrame;
	}

	public void setServerFrame(ServerFrame serverFrame) {
		this.serverFrame = serverFrame;
	}

	public void startServer() {
		serverThread = new ServerThread(serverFrame);
		serverThread.flag_exit=true;
		serverThread.start();
	}

	public void stopServer() {
		synchronized (serverThread.message) {
			//告诉客户端 服务器已经停止 （待会广播发送 以告诉每个客户端服务器已关闭）
			serverThread.message.add("@serverexit");
		}
		serverThread.serverFrame.setDisMess("@exit");//通过这个调用，将服务器界面消息记录清空
		serverThread.serverFrame.setDisUser("@exit");
		serverThread.stopServer();
	}

	public void close() {
//		if (serverThread != null) {
//			if (serverThread.isAlive()) {
//				serverThread.stopServer();
//			}
//		}
		System.exit(0);
	}
}
