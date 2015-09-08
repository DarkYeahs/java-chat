import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatClient extends Frame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TextField input = new TextField();
	private TextArea chatPannel = new TextArea();
	private Socket clientSocket = null;
	private DataOutputStream clientOutput = null;
	private DataInputStream clientInput = null;
	protected boolean connect = false;
	private Thread clientThread= null;
	ChatClient(String t) {
		super(t);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ChatClient chatClient = new ChatClient("聊天客户端");
		chatClient.launch();
	}
	
	public void launch() {
		setLocation(400,400);
		setSize(200,400);
		setVisible(true);
		add(input, BorderLayout.SOUTH);
		add(chatPannel,BorderLayout.NORTH);
		pack();
		input.addActionListener(new inputListener());
		connect();
		clientThread = new Thread(new ThreadRec(clientSocket));
		clientThread.start();
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.out.println("客户端正在尝试关闭");
				try {
					disconnect();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					//e1.printStackTrace();
				} finally {
					System.exit(0);
				}
			}
		});
	}
	
	public void connect() {
		try {
			clientSocket = new Socket("127.0.0.1",8081);
			clientOutput = new DataOutputStream(clientSocket.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void disconnect() throws Exception {
		System.out.println("进入disconnect函数");
		try {
			connect = false;
			clientThread.join(500);
			System.out.println("线程合并结束");
		} catch (InterruptedException e) {
			//e.printStackTrace();
		} finally {
			if(clientOutput != null) clientOutput.close();
			if(clientInput != null) clientInput.close();
			if(clientSocket != null) clientSocket.close();
		}
	}
	
	public void sendMessage(String s) {
		try {
			clientOutput.writeUTF(s);
			clientOutput.flush();
			//clientOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	class inputListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String s = input.getText().trim();
			input.setText("");
			sendMessage(s);
		}
		
	}

	class ThreadRec implements Runnable {
		
		ThreadRec(Socket s) {
			connect = true;
			try {
				clientInput = new DataInputStream(s.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void run() {
			System.out.println("线程开始");
			while(connect) {
				try {
					System.out.println("线程进行中");
					String str = clientInput.readUTF();
					System.out.println(str);
					chatPannel.setText(chatPannel.getText() + str + "\n");
				} catch (IOException e) {
					//e.printStackTrace();
					try {
						clientInput.close();
					} catch (IOException e1) {
						
					} finally {
						connect = false;
					}
				}
			}	
		}
	}
}




