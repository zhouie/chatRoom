package com.server;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ServerFrame extends JFrame implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Server server;
	private JButton jbt_start ;
	private JButton jbt_stop;
	private JButton jbt_exit;
	private JScrollPane jsp_mess;//消息记录
	private JScrollPane jsp_user;
	private JTextArea jta_mess;
	@SuppressWarnings("rawtypes")
	private JList jlt_user;
	
	@SuppressWarnings("rawtypes")
	public ServerFrame(Server server) {
		this.server = server;
//		try {
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InstantiationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnsupportedLookAndFeelException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		this.setTitle("QACQ服务器");
		this.setSize(449, 301);
		this.setIconImage(Toolkit.getDefaultToolkit().createImage(Server.class.getResource("server.png")));
		this.setLayout(null);
		this.setResizable(false);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				jbt_exit.doClick();
			}
		});
		
		jbt_start = new JButton("启动服务器");
		jbt_start.setFont(new Font("宋体",Font.PLAIN,14));
		jbt_start.setBounds(32, 23, 103, 34);
		jbt_start.addActionListener(this);
		this.add(jbt_start);
		
		jbt_stop = new JButton("停止服务器");
		jbt_stop.setFont(new Font("宋体",Font.PLAIN,14));
		jbt_stop.setBounds(145, 23, 103, 34);
		jbt_stop.setEnabled(false);
		jbt_stop.addActionListener(this);
		this.add(jbt_stop);
		
		jbt_exit = new JButton("退出服务器");
		jbt_exit.setFont(new Font("宋体",Font.PLAIN,14));
		jbt_exit.setBounds(258, 23, 103, 34);
		jbt_exit.addActionListener(this);
		this.add(jbt_exit);
		
		jsp_mess = new JScrollPane();
		jsp_mess.setBorder(BorderFactory.createTitledBorder("消息记录"));
		jsp_mess.setBounds(10, 64, 221, 192);
		jsp_mess.setWheelScrollingEnabled(false);//刚开始默认不显示
		this.add(jsp_mess);
		jta_mess = new JTextArea();
		jta_mess.setEditable(false);
		jsp_mess.setViewportView(jta_mess);
//		jsp_mess.setOpaque(false);  
//		jsp_mess.getViewport().setOpaque(false);
		
		jsp_user = new JScrollPane();
		jsp_user.setBorder(BorderFactory.createTitledBorder("在线用户"));
		jsp_user.setBounds(258, 65, 157, 191);
		jsp_user.setWheelScrollingEnabled(false);
		this.add(jsp_user);
		
		jlt_user = new JList();
		//jlt_user.setVisibleRowCount(4);
		jsp_user.setViewportView(jlt_user);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == jbt_start) {
			jbt_start.setEnabled(false);
			jbt_stop.setEnabled(true);//停止按钮生效
			server.startServer();
		}
		if (e.getSource() == jbt_stop) {
			int flag = JOptionPane.showConfirmDialog(this, "确定要停止服务器吗？", "提示", 
					JOptionPane.OK_CANCEL_OPTION);
			if (flag == JOptionPane.OK_OPTION) {
				server.stopServer();
				jbt_start.setEnabled(true);//启动按钮生效
				jbt_stop.setEnabled(false);
			}
		}
		if (e.getSource() == jbt_exit) {
			if (jbt_stop.isEnabled()) {
				jbt_stop.doClick();
			}
			server.close();
		}
	}

	public void setStartAndStopUnable() {
		JOptionPane.showMessageDialog(this, "不能同时开启两个服务器");
	}

	//将聊天信息显示到聊天界面
	public void setDisMess(String mess) {
		if (mess.contains("@exit")) {
			jta_mess.setText("");
		}
		if (mess.contains("@chat")) {
			int local = mess.indexOf("@chat");
			String mess_re = mess.substring(0,local);
			jta_mess.append(mess_re+"\n");
			jta_mess.setCaretPosition(jta_mess.getText().length());
		}
	}

	//将用户显示在服务器界面右侧
	@SuppressWarnings("unchecked")
	public void setDisUser(String mess) {
		if (mess.equals("@userlist")) {
			jlt_user.removeAll();
			jlt_user.setListData(new String[]{});
		}else {
			if (mess.contains("@userlist")) {
				String[] dis = mess.split("@userlist");
				String[] disUsers = new String[dis.length/2];
				int j = 0;
				for (int i = 0; i < dis.length; i++) {
					disUsers[j++] = dis[i++];
				}
				jlt_user.removeAll();
				jlt_user.setListData(disUsers);//保存所有用户名
			}
			if (mess.equals("@exit")) {
				jlt_user.setListData(new String[]{});
			}
		}
	}
}









