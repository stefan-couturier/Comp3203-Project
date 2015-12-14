package p1;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class Server implements Runnable{
	private final static int PORT_NUM = 45000;
	public static ServerSocket serv;
	public static Socket clnt;
	public static String root_dir;
	public static File current_file;

	private ServerThread clients[] = new ServerThread[50];
	private ServerSocket server = null;
	private Thread       thread = null;
	private int clientCount = 0;
	private ServerGUI gui = null;
	
	private ArrayList<FileRequest> fileRequests = new ArrayList<FileRequest>();;

	public ServerThread getThread(int x){
		return clients[x];
	}
	
	public int getClientCount(){
		return clientCount;
	}

	public Server(int port){
		File[] roots = File.listRoots();
		root_dir = roots[0].toString();
		////////
		//root_dir = "Z:\\";
		//System.out.println("!"+root_dir+"!");
		////////
		createDirectory();
		//root_dir = "C:\\Test\\";
		
		gui = new ServerGUI(this);
		try{
			System.out.println("Binding to port " + port + ", please wait  ...");
			server = new ServerSocket(port);  
			System.out.println("Server started: " + server);
			start(); 
		}
		catch(IOException ioe){
			System.out.println("Can not bind to port " + port + ": " + ioe.getMessage()); 
		}
	}

	public void run(){
		while (thread != null){
			try{
				System.out.println("Waiting for an awesome client ..."); 
				addThread(server.accept());
			}
			catch(IOException ioe){
					System.out.println("Server accept error: " + ioe);
					stop();
			}
		}
		
	}

	public void start(){
		if (thread == null){
			thread = new Thread(this); 
			thread.start();
		}
	}
	public void stop(){
		if (thread != null){
			System.out.println("SERVER:\tClosing");
			for(int x =0; x< clientCount; x++){
				clients[x].send("TERMINATE");
			}
			
			try{
				server.close();
			} catch (IOException ioe){
				System.out.println("Server STOP error: " + ioe);
			}
			
			thread.stop(); 
			thread = null;
			
		}
	}
	
	private int findClient(int ID){
		for (int i = 0; i < clientCount; i++)
			if (clients[i].getID() == ID)
				return i;
		return -1;
	}
	
	private int findClientByUsername(String username){
		for (int i = 0; i < clientCount; i++)
			if (clients[i].getUsername().equals(username))
				return i;
		return -1;
	}
	
	public synchronized void handle(int ID, String input, DataInputStream inputStream, DataOutputStream outputStream){
		if (input.equals(".bye")){
			clients[findClient(ID)].send(".bye");
			remove(ID); 
		} else {
			for (int i = 0; i < clientCount; i++)
				clients[i].send(ID + ": " + input);
		}
	}
	
	// this is to send updated lists to a client when requested from client
	public synchronized void handleUpdateLists(int ID, DataOutputStream outputStream) {
		sendArrayList(getFileList(), outputStream);
		sendArrayList(getPeerList(), outputStream);
		//sendArrayList(getFileRequests(), outputStream);
	}
	
	// this send a file to client when they request to download a file
	public synchronized void handleDownload(int ID, String filename, DataOutputStream outputStream) {
		try {
			sendFile(filename, outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// this lets server receive an upload from the client
	public synchronized void handleUpload(int ID, String filename, DataInputStream inputStream) {
		try {
			receiveFile(root_dir, inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void handleGetPeerFiles(int ID, String peerName, DataOutputStream outputStream){
		int peerID;
		peerID = findClientByUsername(peerName);
		ArrayList<String> peerFiles = new ArrayList<String>(clients[peerID].getFiles());
		try{
			outputStream.writeUTF("PeerFileList");
			sendArrayList(peerFiles,outputStream);
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public synchronized void handleGetPeerFile(int ID, String filename,
			String peerName, String ip, int port, DataOutputStream outputStream){
		int peerID;
		peerID = findClientByUsername(peerName);
		clients[peerID].send("sendPeerFile");
		clients[peerID].send(ip);
		clients[peerID].send(filename);
		clients[peerID].send(port);
	}
	
	
	public synchronized void remove(int ID){
		int pos = findClient(ID);
		if (pos >= 0){
			ServerThread toTerminate = clients[pos];
			System.out.println("Removing client thread " + ID + " at " + pos);
			if (pos < clientCount-1)
				for (int i = pos+1; i < clientCount; i++)
					clients[i-1] = clients[i];
			clientCount--;
			try{
				toTerminate.close(); 
				if(clientCount >= 1){
					clients[0].updateAllThreads();
				}
			}
			catch(IOException ioe){
				System.out.println("Error closing thread: " + ioe); 
			}
			toTerminate.stop(); 
		}
	}
	
	
	private void addThread(Socket socket){
		if (clientCount < clients.length){
			System.out.println("Client accepted: " + socket);
			clients[clientCount] = new ServerThread(this, socket);
			try	{
				clients[clientCount].open(); 
				clients[clientCount].start();  
				clientCount++; 
			}
			catch(IOException ioe){
				System.out.println("Error opening thread: " + ioe); 
			} 
		}
		else
			System.out.println("Client refused: maximum " + clients.length + " reached.");
	}


	public boolean sendFile(String name, DataOutputStream reply) throws IOException {
		boolean status = true;
		FileInputStream file_stream = null;
		BufferedInputStream buffer_stream = null;
		DataInputStream dataIn_stream = null;
		dataIn_stream = null;

		//String file_path = current_file + "\\" + name;
		String file_path = root_dir + "\\" + name;

		File file = new File(file_path);
		byte[] byte_array = new byte[(int) file.length()];

		try {
			file_stream = new FileInputStream(file);
			buffer_stream = new BufferedInputStream(file_stream);
			dataIn_stream = new DataInputStream(buffer_stream);

			dataIn_stream.readFully(byte_array, 0, byte_array.length);

			//reply.writeBoolean(true);
			reply.writeUTF(file.getName());
			reply.writeLong((long) byte_array.length);
			reply.write(byte_array, 0, byte_array.length);
			reply.flush();
		} catch (Exception e) {
			System.err.println("--error: " + e.getMessage());
			status = false;
		} finally {
			try {
				if (file_stream != null)
					file_stream.close();
				if (buffer_stream != null)
					buffer_stream.close();
				if (dataIn_stream != null)
					dataIn_stream.close();

				System.out.println("SERVER:\tFile Sent");
			} catch (Exception e) {
				System.err.println("--error: " + e.getMessage());
			}
		}
		return status;
	}



	public synchronized boolean receiveFile(String path, DataInputStream request) throws IOException {
		boolean status = true;
		int r_byt = 0;
		String f_name = null;
		long f_size = 0;
		OutputStream writer = null;

		try {
			/*if (path.equals(root_dir+"\\")){
				request.readUTF();
				f_size = request.readLong();
				request.skip(f_size);
				return false;
			}*/

			f_name = path.concat("\\" + (Paths.get(request.readUTF())).getFileName().toString());
			System.out.println(f_name);
			f_size = request.readLong();
			writer = new FileOutputStream(f_name);			
			byte[] r_buf = new byte[4096*8];

			while ((r_byt = request.read(r_buf, 0, (int) Math.min(r_buf.length, f_size))) != 1 && f_size > 0) {
				System.out.println(f_size);
				writer.write(r_buf, 0, r_byt);
				f_size -= (long) r_byt;
			}
		} catch (Exception e) {
			System.err.println("--error: " + e.getMessage());
			status = false;
		} finally {
			try {
				if (writer != null)
					writer.close();
				System.out.println("SERVER\tRecieved File "+f_name+" Fully");
			} catch (Exception e) {
				System.err.println("--error: " + e.getMessage());
			}
		}
		return status;
	}
	
	
	public void sendArrayList(ArrayList<String> list, DataOutputStream outputStream) {
		try {
			// so client knows how many file names to expect
			outputStream.writeInt(list.size());
			System.out.println("SERVER Sent:\t"+list.size());
			for (int i=0; i < list.size(); i++){
				outputStream.writeUTF(list.get(i));
				System.out.println("SERVER Sent:\t"+list.get(i));
			}
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public ArrayList<String> getPeerList() {
		ArrayList<String> peers = new ArrayList<String>();
		for (int i = 0; i < clientCount; i++)
			peers.add(clients[i].getUsername());
		return peers;
	}
	
	public ArrayList<String> getFileList() {
		File f = new File(root_dir);
		ArrayList<File> files = new ArrayList<File>(Arrays.asList(f.listFiles()));
		ArrayList<String> list = new ArrayList<String>();
		for(int x = 0; x < files.size(); x++) {
			if (!files.get(x).isDirectory()) 
				list.add(files.get(x).getName());
		}
		return list;
	}


	// verify that server doesn't already have the file that the client wants to upload
	public synchronized boolean verifyUpload(String s) {
		ArrayList<String> list = new ArrayList<String>(getFileList());
		if (s != null && !list.contains(s))
			return true;
		else return false;
	}

	private static void createDirectory(){
		//if exists
		boolean success = false;
		System.out.println("\tdirectory " + root_dir + "\n");
		File aFile = new File(root_dir + "SERVER3203");
		System.out.println("\tdirectory " + aFile.getAbsolutePath() + "\n");
		if (aFile.exists()){
			//Check permissions
			if (aFile.canWrite()){
				System.out.println("\tdirectory " + aFile.getAbsolutePath() +" already exists and can write");
			}
			else{
				System.out.println("\tdirectory " + aFile.getAbsolutePath() +" already exists and cannot write");
			}	
		}
		else{
			success = aFile.mkdir();
			if (success)
				System.out.println("\tDirectory " + aFile.getAbsolutePath() +" Created");
			else
				System.out.println("\tDirectory " + aFile.getAbsolutePath() +" failed to be created");
		}
		root_dir = root_dir + "SERVER3203" + "\\";

	}


	public static void main(String args[]){
		Server server = null;
		if (args.length != 1)
			server = new Server(PORT_NUM);
		else
			server = new Server(Integer.parseInt(args[0]));
	}
}
