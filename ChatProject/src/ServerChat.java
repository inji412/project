import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerChat implements Runnable{
	// 접속자 목록을 저장할 컬렉션
	ArrayList<ChatService> list = new ArrayList<ChatService>();

	public ServerChat() {
		
	}
	@Override
	public void run() {
		try{ 
			ServerSocket ss = new ServerSocket(1234); 
			while(true){
				System.out.println("---접속자 대기 중---");
				Socket socket = ss.accept(); // 접속자 대기
				System.out.println("Client가 접속함");
				ChatService cs = new ChatService(socket); // 클라이언트 객체 
				list.add(cs); // 접속자를 리스트에 추가 
				cs.start(); // 클라이언트 정보가 있는 chatService객체를 Thread구현
				cs.putMessageAll("-----["+cs.userName+"님]이 입장하였습니다.-----");
				cs.putMessageAll("/c"+list.size()); // 현재 접속자 수
				cs.putMessageAll("/g"+cs.userName);// 접속자 이름 보내기 
			}
		} catch(Exception e){
			
		}
		
	}
	
	class ChatService extends Thread{
		Socket socket;
		DataInputStream in;
		DataOutputStream out;
		String userName;
		
		ChatService(Socket socket){
			try{
				this.socket = socket;
				in = new DataInputStream(socket.getInputStream()); // 서버에서 보낸 문자 받기위한 객체 
				out = new DataOutputStream(socket.getOutputStream()); // 서버로 문자 보내기위한 객체
				userName = socket.getInetAddress().getHostAddress(); // 접속자 ip를 이름으로 사용.
				
			} catch(Exception e){
				
			}
		}
		public void run(){ // 클라이언트가 보낸 문자 받기 
			try{
				while(true){
					String inStr = in.readUTF();
					if(inStr == null) return;
					putMessageAll(userName+"님]"+inStr);
				}
				
			} catch(Exception e){
				
			}
		}
		public void putMessageAll(String msg){
			for(int i = 0; i < list.size(); i++){
				try{
					ChatService cs = list.get(i); // 접속자 
					cs.out.writeUTF(msg+"\r\n");
				} catch(Exception e){ // 문자를 보내다가 에러가 생기면 접속이 끊어진 것이기 떄문에 
					list.remove(i--); // 컬렉션의 객체(유저정보)를 제거함. 
				}
			}
		}
		
	}

	public static void main(String[] args) {
		ServerChat sc = new ServerChat();
		Thread thread = new Thread(sc);
		thread.start();
	}

	
}
