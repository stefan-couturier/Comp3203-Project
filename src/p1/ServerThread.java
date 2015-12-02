package p1;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;


public class ServerThread extends Thread {
	private Server       server    = null;
	private Socket           socket    = null;
	private int              ID        = -1;
	private DataInputStream  inputStream  =  null;
	private DataOutputStream outputStream = null;

	public ServerThread(Server _server, Socket _socket){
		super();
		server = _server;
		socket = _socket;
		ID     = socket.getPort();
	}
	
	public int getID() {
		return ID;
	}
	
	public void run() {
		System.out.println("Server Thread " + ID + " running.");
		String received;
		while (true) {
			try {
				//server.handle(ID, inputStream.readUTF());
				
				// serverThread does the 'receiving' from client, and then tells server what to do
				received = inputStream.readUTF();
				System.out.println("SERVERThread "+ ID + " Recieved:\t"+received);
				
				if (received.equals("update")) {
					send("update"); // lets clientThread get ready to receive updated lists
					System.out.println("SERVERThread "+ ID + " Sent:\tupdate");
					server.handleUpdateLists(ID, outputStream);
				} 
				else if (received.equals("download")) {
					String filename = inputStream.readUTF();
					send("download"); // lets the clientThread get ready to download from server
					System.out.println("SERVERThread "+ ID + " Sent:\tdownload");
					server.handleDownload(ID, filename, outputStream);
				} 
				else if (received.equals("upload")) {
					String filename = inputStream.readUTF();
					if (server.verifyUpload(filename)) {
						send("upload"); // lets the clientThread get ready to upload to server
						System.out.println("SERVERThread "+ ID + " Sent:\tupload");
						server.handleUpload(ID, filename, inputStream);
					}
					else{
						send("noupload");
						System.out.println("SERVERThread "+ ID + " Sent:\tnoupload");
					}
				}
				
			}
			catch(IOException ioe)
			{
				System.out.println(ID + " ERROR reading: " + ioe.getMessage());
				server.remove(ID);
				//stop();
			}
		}
	}
	
	public void open() throws IOException {
		inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
	}
	public void close() throws IOException {
		if (socket != null)    socket.close();
		if (inputStream != null)  inputStream.close();
		if (outputStream != null) outputStream.close();
	}


	public void send(String msg){
		try {  
			outputStream.writeUTF(msg);
			outputStream.flush();
		}
		catch(IOException ioe) {  
			System.out.println(ID + " ERROR sending: " + ioe.getMessage());
			server.remove(ID);
			//stop();
		}
	}

	/*public void sendFileList(ArrayList<String> list) {
		try {
			// so client knows how many file names to expect
			outputStream.writeInt(list.size());
			for (int i=0; i < list.size(); i++)
				outputStream.writeUTF(list.get(i));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
}
