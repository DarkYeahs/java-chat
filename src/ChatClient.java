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
	
	ChatClient(String t) {
		super(t);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ChatClient chatClient = new ChatClient("ÁÄÌì¿Í»§¶Ë");
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
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				disconnect();
				System.exit(0);
			}
		});
		connect();
		new Thread(new ThreadRec(clientSocket)).start();;
	}
	
	public void connect() {
		try {
			clientSocket = new Socket("127.0.0.1",8081);
			clientOutput = new DataOutputStream(clientSocket.getOutputStream());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void disconnect() {
		try {
			clientOutput.close();
			clientSocket.close();
			clientInput.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendMessage(String s) {
		try {
			clientOutput.writeUTF(s);
			clientOutput.flush();
			//clientOutput.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
		protected boolean connect = false;
		
		ThreadRec(Socket s) {
			connect = true;
			try {
				clientInput = new DataInputStream(s.getInputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void run() {
			while(connect) {
				try {
					String str = clientInput.readUTF();
					System.out.println(str);
					chatPannel.setText(chatPannel.getText() + str + "\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					
				}
			}	
		}
	}
}




