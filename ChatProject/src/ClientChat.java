import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClientChat extends JFrame implements ActionListener, Runnable{
	//Frame:center
	JPanel centerPane = new JPanel(new BorderLayout());
		JPanel connPane = new JPanel(new BorderLayout());
			JTextField connTf = new JTextField();
			JButton connBtn = new JButton("접속");
			
		JTextArea msgTa = new JTextArea(); 
		JScrollPane sp = new JScrollPane(msgTa);
		JPanel msgPane = new JPanel(new BorderLayout());
			JTextField msgTf = new JTextField();
			JButton sendBtn = new JButton("보내기");
	//Frame:east
	JPanel eastPane = new JPanel(new BorderLayout());
		JLabel connTitle = new JLabel("접속자 리스트                ");
		JList<String> connList = new JList<String>();
			DefaultListModel<String> model = new DefaultListModel<String>();
			JScrollPane spList = new JScrollPane(connList);
		JLabel connCount = new JLabel("현재접속: 0명");
	//통신 채널 변수 
	Socket socket;
	DataInputStream in;
	DataOutputStream out;
		
	public ClientChat() {
		super("채팅");
		//Frame:center
			connPane.add("Center",connTf);
			connPane.add("East",connBtn);
			msgPane.add("Center",msgTf);
			msgPane.add("East",sendBtn);
			centerPane.add("North",connPane);
			msgTa.setOpaque(true); msgTa.setBackground(new Color(220,210,230));
			centerPane.add("Center",sp);
			centerPane.add("South",msgPane);
			add("Center",centerPane);
		//Frame:east
			eastPane.add("North", connTitle);
			eastPane.add("Center",spList);
			eastPane.add("South", connCount);
			add("East", eastPane);
			
			setBounds(400, 100, 700, 500);
			setVisible(true);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
			connTf.addActionListener(this);
			connBtn.addActionListener(this);
			msgTf.addActionListener(this);
			sendBtn.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		Object obj = ae.getSource();
		if(obj == connTf || obj == connBtn){
			connProcess(); // 서버와 연결
		} else if(obj == msgTf || obj == sendBtn){
			sendProcess(); // 서버로 메세지 보내
		}
	}

	private void connProcess() {
		try{
			if(!connTf.getText().equals("")){ //공백문자가 아닐떄 접속 
				if(socket != null) socket.close(); //socket이 이미 접속상태이면 연결 종료 
				socket = new Socket(connTf.getText(), 1234); // 서버 연결 
				
				in = new DataInputStream(socket.getInputStream()); // 서버에서 보낸 문자 받기위한 객체 
				out = new DataOutputStream(socket.getOutputStream()); // 서버로 문자 보내기위한 객체
				
				new Thread(this).start();//연결되면 스레드 시작  
			}
		} catch(Exception e){
			
		}
		
	}

	private void sendProcess() {
		try{
			out.writeUTF(msgTf.getText());
			msgTf.setText("");
		} catch(Exception e){
			
		}
	}
	
	@Override 	// 서버에서 오는 문자 받는 스레드 생성
	public void run() {
		while(true) {
			try{
				String msg = in.readUTF(); // 서버에서 문자를 보내면 data를 읽기위해 대기 
				
				if(msg == null) return; // 넘어온 메세지가 없으면 중단시킴 
				
				if("/c".equals(msg.substring(0, 2))) { // 여기는 서버에서 접속자 수가 넘어오면 실행됨 
					connCount.setText("현재 접속 : "+msg.substring(2)+"명");
					
				} else if("/g".equals(msg.substring(0, 2))) { // 서버에서 접속자 ip받아서 모델에 추가 
					model.addElement(msg.substring(2));
				} else {
					msgTa.append(msg+"\n"); // 서버에서 받은문자 ta에 추가
					msgTa.setCaretPosition(msgTa.getText().length());
				 }          
				} catch(Exception e){
				
			}
		}
	}

	public static void main(String[] args){
		new ClientChat();
	}

	

}
