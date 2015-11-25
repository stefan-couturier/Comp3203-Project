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
		while(true) {
			try {
				client.handle(inputStream.readUTF());
			}
			catch(IOException ioe) {
				System.out.println("Listening ERROR: " + ioe.getMessage());
				return;
				//client.stop()
			}
		}
	}
	
	//Open the thread
	public void open() {
		{  try
	      {  inputStream  = new DataInputStream(socket.getInputStream());
	      }
	      catch(IOException ioe)
	      {  System.out.println("Error getting input stream: " + ioe);
	         //client.stop();
	      }
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
