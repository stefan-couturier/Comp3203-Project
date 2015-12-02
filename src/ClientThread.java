import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {
	private Client client = null;
	private Socket socket = null;
	private DataInputStream inputStream = null;
	
	public ClientThread(Client c, Socket s){
		super();
		client = c;
		socket = s;
		open();
		start();
	}
	
	//Run thread
	public void run(){
		System.out.println("client thread Running.");
		String received;
		while(true) {
			try {
				// handle responses from server and tell client class what to do
				received = inputStream.readUTF();
				
				if (received.equals("update"))
					client.updateGUILists(inputStream);
				else if (received.equals("download"))
					client.receiveFile(inputStream);
				else if (received.equals("upload"))
					client.uploadFile();
				else if (received.equals("noupload"))
					;
				//client.handle(inputStream.readUTF());
			}
			catch(IOException ioe) {
				System.out.println("Listening ERROR: " + ioe.getMessage());
				//client.stop();
				return;
			}
		}
	}
	
	//Open the thread
	public void open() {
		try {
			inputStream  = new DataInputStream(socket.getInputStream());
		}
	    catch(IOException ioe) {
	    	System.out.println("Error getting input stream: " + ioe);
	        //client.stop();
	    }
	}
	
	//Close the thread
	public void close(){
		try {
			if (inputStream != null) inputStream.close();
		}
	    catch (IOException ioe){
	    	System.out.println("Error closing input stream: " + ioe);
	    }
	   
	}
//	public void send(){
//		
//	}
	
}
