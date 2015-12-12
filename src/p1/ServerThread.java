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

	// username of corresponding client
	private String username;
	private ArrayList<String> files;
	public String getUsername() { return username; }
	public ArrayList<String> getFiles() {return files;}
	
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
				// if there are file requests to handle, do them before listening to client
				String pendingFile = server.getPendingFileRequestPoster(username);
				if (!pendingFile.equals("")) {
					send("pending");
					// ideally poster is now waiting to receive file
				}
				String ip = server.getPendingFileRequestResponder(username);
				if (!ip.equals("")) {
					send("sendP2P");
					send(ip);
				}
				
				// serverThread does the 'receiving' from client, and then tells server what to do
				received = inputStream.readUTF();
				System.out.println("SERVERThread "+ ID + " Recieved:\t"+received);
				
				if (received.equals("update")) {
					send("update"); // lets clientThread get ready to receive updated lists
					System.out.println("SERVERThread "+ ID + " Sent:\tupdate");
					
					//receive file list
					
					server.handleUpdateLists(ID, outputStream);
				} 
				else if (received.equals("download")) {
					String filename = inputStream.readUTF();
					System.out.println("SERVERThread "+ ID + " Recieved:\t"+filename);
					send("download"); // lets the clientThread get ready to download from server
					System.out.println("SERVERThread "+ ID + " Sent:\tdownload");
					server.handleDownload(ID, filename, outputStream);
				} 
				else if (received.equals("upload")) {
					String filename = inputStream.readUTF();
					System.out.println("SERVERThread "+ ID + " Recieved:\t"+filename);
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
				else if (received.equals("username")) {
					username = inputStream.readUTF();
				}
				else if (received.equals("post")) {
					server.addFileRequest(inputStream.readUTF(), inputStream.readUTF(), username);
				}
				else if (received.equals("response")) {
					server.setFileRequestToPending(inputStream.readUTF(), username);
				}
				else if (received.equals("getPeerFileList")){
					String peerName = inputStream.readUTF();
					System.out.println("SERVERThread "+ ID + " Recieved:\t"+peerName);
					server.handleGetPeerFiles(ID,peerName,outputStream);
				}
				else if (received.equals("getPeerFileList")){
					try {
						files = new ArrayList<String>();
						int listSize = inputStream.readInt();
						System.out.println("SERVERThread "+ ID + " Recieved:\t"+listSize+" files on client");
						for (int i = 0; i < listSize; i++){
							files.add(inputStream.readUTF());
							System.out.println("SERVERThread "+ ID + " Recieved:\tFilename: "+files.get(i));
						}
					} catch (IOException e) {
						e.printStackTrace();
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
