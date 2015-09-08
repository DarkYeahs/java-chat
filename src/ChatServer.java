
import java.net.*;
import java.util.*;
import java.io.*;

public class ChatServer {
	private static boolean flag = false;
	private ServerSocket chatserver = null;
	private ArrayList<Client> clients = new ArrayList<Client>();
	//private static ArrayList<Client> clients = new ArrayList<Client>();
	
	public static void main(String[] args) throws Exception {
		//boolean disconnect = false;
		new ChatServer().start();
	}
	
	public void start() {
		try {
			chatserver = new ServerSocket(8081);
		} catch(BindException e) {
			System.out.println("端口号被占用请使用新的端口或将占用端口号的程序关闭");
			System.exit(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Socket client = null;
		flag = true;
		try {
			while(flag) {
				client = chatserver.accept();
				Client item = new Client(client);
				clients.add(item);
				new Thread(item).start();
			}

		} catch(IOException e) {
			
		} finally {
			if(chatserver != null)
				try {
					chatserver.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		//chatserver.close();
	}
	
	class Client implements Runnable {
		private Socket client = null;
		private boolean connect = false;
		DataInputStream clientOutput = null;
		DataOutputStream clientInput = null;
		
		Client(Socket s) {
			this.client = s;
			try {
				clientOutput = new DataInputStream(client.getInputStream());
				clientInput = new DataOutputStream(client.getOutputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void send(String str) {
			try {
				clientInput.writeUTF(str);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				clients.remove(this);
				//e.printStackTrace();
			}
		}
		
		public void run() {
			connect = true;
			while(connect) {
				try {
					while(connect) {
						String getMessage = clientOutput.readUTF();
						System.out.println(getMessage);
						for(int i = 0; i < clients.size() ; i++) {
							Client item = clients.get(i);
							item.send(getMessage);
						}
						
					}
				} catch(IOException e) {
					//e.printStackTrace();
					System.out.println("client链接断开");
					connect = false;
				}
				try {
					client.close();
					clientOutput.close();
					clientInput.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}

